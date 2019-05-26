/*
 * Copyright 2019 Avee Player. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aveeopen.comp.playback.ExoMediaPlayer;

import android.media.MediaCodec;

import com.aveeopen.Common.tlog;
import com.aveeopen.comp.playback.AudioFrameData;
import com.google.android.exoplayer.C;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class ExoVisualizerDataProvider
{
    private static final int MAX_BUFFER_COUNT = 20;
    private static final int MAX_REUSE_BUFFER_COUNT = 2;

    private boolean validBuffers;
    private boolean isPlaying = false;
    private long positionUs = 0;
    private long timeInMillis = 0;
    private int newestBufferIndex = 0;
    private Lock lockBuffersList = new ReentrantLock();
    private List<BufferEntry> buffersList = new ArrayList<>();
    private Queue<BufferEntry> buffersReuse = new ArrayDeque<>();
    private int lastIndex = 0;

    public ExoVisualizerDataProvider() {
    }

    public void release() {
    }

    void putReuseBuffer(BufferEntry e) {
        if (buffersReuse.size() >= MAX_REUSE_BUFFER_COUNT) return;
        buffersReuse.add(e);
    }

    BufferEntry getReuseBuffer() {
        return buffersReuse.poll();
    }

    public ByteBuffer clone(ByteBuffer original) {
        ByteBuffer clone = ByteBuffer.allocate(original.capacity());
        original.rewind();//copy from the beginning
        clone.put(original);
        original.rewind();
        clone.flip();
        return clone;
    }

    public ByteBuffer clone(ByteBuffer original, ByteBuffer avalible) {
        ByteBuffer dest = avalible;
        if (dest == null || dest.capacity() != original.capacity())
            dest = ByteBuffer.allocate(original.capacity());
        original.rewind();//copy from the beginning
        dest.put(original);
        original.rewind();
        dest.flip();
        return dest;
    }

    public void onSetStarted(boolean b) {
        isPlaying = b;
    }


    public void onPcmData(ByteBuffer buffer,
                          android.media.MediaCodec.BufferInfo bufferInfo,
                          int bufferIndex,
                          int sampleRate,
                          int channelCount,
                          long positionUs) {
        timeInMillis = System.currentTimeMillis();
        this.positionUs = positionUs;

        if (lastIndex == bufferIndex) return;
        lastIndex = bufferIndex;

        long newestPresentationTimeUs = bufferInfo.presentationTimeUs;


        try {
            if (lockBuffersList.tryLock(3, TimeUnit.SECONDS)) {
                try {

                    for (Iterator<BufferEntry> iterator = buffersList.iterator(); iterator.hasNext(); ) {
                        BufferEntry e = iterator.next();

                        if ((e.bufferInfo.presentationTimeUs < (positionUs - 50 * 1000)) ||
                                (e.bufferInfo.presentationTimeUs > newestPresentationTimeUs)) {

                            putReuseBuffer(e);
                            iterator.remove();
                        }
                    }

                    if (buffersList.size() >= MAX_BUFFER_COUNT) {
                        putReuseBuffer(buffersList.get(0));
                        buffersList.remove(0);
                    }

                    {
                        BufferEntry newEntry = getReuseBuffer();
                        //get
                        if (newEntry == null)
                            newEntry = new BufferEntry();

                        newEntry.outputBuffer = clone(buffer, newEntry.outputBuffer);
                        newEntry.bufferInfo.size = bufferInfo.size;
                        newEntry.bufferInfo.offset = bufferInfo.offset;
                        newEntry.bufferInfo.presentationTimeUs = bufferInfo.presentationTimeUs;
                        newEntry.sampleRate = sampleRate;
                        newEntry.channelCount = channelCount;
                        buffersList.add(newEntry);
                    }

                    validBuffers = true;
                    newestBufferIndex = bufferIndex;

                } finally {
                    lockBuffersList.unlock();
                }
            } else {
                tlog.w("thread lock timeout 1");
            }
        } catch (Exception ignored) {
        }
    }

    private long bytesToFrames(long byteCount, int channelCount) {
        final int frameSize = 2 * channelCount; // 2 bytes per 16 bit sample * number of channels.;
        return byteCount / frameSize;
    }

    private long framesToDurationUs(long frameCount, int samplerate) {
        return (frameCount * C.MICROS_PER_SECOND) / samplerate;
    }

    private void getBufferData2(short[] outPcmData,
                                long startTimeInBuf,
                                int bufferIndex,
                                float[] rms) {
        final int captureSkips = 1;
        final int captureSkips2 = 1;

        int left_vali;
        int right_vali;
        int cntr = 0;
        int buffinx = bufferIndex;
        BufferEntry entry;

        if (buffinx >= buffersList.size()) {
            //warped around
            //warning
            tlog.w("warped arround 0");
            return;
        }

        entry = buffersList.get(buffinx);
        double samplesInTime = entry.sampleRate / 1000000.0;
        int startSample = (int) (startTimeInBuf * samplesInTime);
        int scnt = startSample;

        while (true) {
            entry = buffersList.get(buffinx);

            int s = entry.bufferInfo.offset + ((scnt * captureSkips) * entry.channelCount * 2);

            if (s < entry.bufferInfo.size) {

                ByteBuffer buf = entry.outputBuffer;

                if (entry.channelCount == 1) {
                    //mono
                    left_vali = (buf.get(s + 1) * 256) + (buf.get(s) & 0xFF);
                    outPcmData[cntr] = (short) (left_vali / (256));
                } else {
                    //stereo
                    left_vali = (buf.get(s + 1) * 256) + (buf.get(s) & 0xFF);
                    right_vali = (buf.get(s + 3) * 256) + (buf.get(s + 2) & 0xFF);
                    outPcmData[cntr] = (short) ((left_vali + right_vali) / (2 * 256));//stereo to mono
                }

                rms[0] += outPcmData[cntr] * outPcmData[cntr];


                scnt += captureSkips2;
                cntr++;
                if (cntr >= outPcmData.length)
                    break;

            } else {
                scnt = 0;
                buffinx = buffinx + 1;
                if (buffinx >= buffersList.size()) {
                    //warped around
                    tlog.w("warped around");
                    break;
                }
            }

        }
    }

    public AudioFrameData getVisData(AudioFrameData outResult) {
        if (!isPlaying)
            return getVisData(positionUs, outResult);
        long timePassedMs = System.currentTimeMillis() - timeInMillis;
        return getVisData(positionUs + (timePassedMs * 1000), outResult);
    }

    public AudioFrameData getVisData(long positionUs, AudioFrameData outResult) {
        final long captureOffsetTimeUs = -10 * 1000;

        if (!validBuffers) {
            outResult.valid = false;
            return outResult;
        }

        positionUs += captureOffsetTimeUs;

        float[] rms_ = new float[1];
        rms_[0] = 0.0f;

        try {
            if (lockBuffersList.tryLock(2, TimeUnit.SECONDS)) {
                try {

                    for (int i = 0; i < buffersList.size(); i++) {
                        BufferEntry entry = buffersList.get(i);

                        long presentationTimeUs = entry.bufferInfo.presentationTimeUs;
                        long bufferStartTime = presentationTimeUs
                                - framesToDurationUs(bytesToFrames(entry.bufferInfo.size, entry.channelCount), entry.sampleRate);

                        long timePastPresentation0 = positionUs - bufferStartTime;//buffers.get(j).bufferInfo.presentationTimeUs;

                        if (timePastPresentation0 >= 0) {

                            outResult.sampleRate = entry.sampleRate;

                            getBufferData2(outResult.pcmBuffer,
                                    timePastPresentation0,
                                    i,
                                    rms_);
                        }
                    }

                } finally {
                    lockBuffersList.unlock();
                }
            }
        } catch (Exception ignored) {
        }

        outResult.rms = (float) Math.sqrt((1.0f / (float) outResult.pcmBuffer.length) * (rms_[0]));
        outResult.valid = true;

        return outResult;
    }

    class BufferEntry {
        public ByteBuffer outputBuffer = ByteBuffer.allocate(0);
        public android.media.MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        public int sampleRate;
        public int channelCount;
    }
}
