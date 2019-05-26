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
import android.media.MediaFormat;

import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaFormatHolder;
import com.google.android.exoplayer.audio.AudioCapabilities;

public class MyMediaCodecAudioTrackRendererCapture extends MediaCodecAudioTrackRenderer {

    //public static final int MY_MSG_SET_VOLUME = 12301;//hopefully never will collide with any exoplayer message //STEREO BALANCE EXOPLAYER

    private int outputSampleRate = 44100;
    private int outputChannelCount = 2;
    private String outputMimeType = "audio/raw";
    private int inputSampleRate = 44100;
    private int inputChannelCount = 2;
    private String inputMimeType = "audio/mpeg";

    public MyMediaCodecAudioTrackRendererCapture(IVisualizerDataCapturer visualizerData, com.google.android.exoplayer.SampleSource source,
                                                 MediaCodecSelector mediaCodecSelector,
                                                 com.google.android.exoplayer.drm.DrmSessionManager drmSessionManager,
                                                 boolean playClearSamplesWithoutKeys,
                                                 android.os.Handler eventHandler,
                                                 com.google.android.exoplayer.MediaCodecAudioTrackRenderer.EventListener eventListener,
                                                 AudioCapabilities audioCapabilities,
                                                 int streamType) {
        super(source,
                mediaCodecSelector,
                drmSessionManager,
                playClearSamplesWithoutKeys,
                eventHandler,
                eventListener,
                audioCapabilities,
                streamType
        ); //new MyAudioTrack(audioCapabilities, streamType) //STEREO BALANCE EXOPLAYER

        VisualizerDataCapturerLimiter.assignInstance(this, visualizerData);
    }

    @Override
    protected void onStarted() {
        IVisualizerDataCapturer visualizerData = VisualizerDataCapturerLimiter.getListener(this);
        if (visualizerData != null) visualizerData.onSetStarted(true);
        super.onStarted();
    }

    @Override
    protected void onStopped() {
        IVisualizerDataCapturer visualizerData = VisualizerDataCapturerLimiter.getListener(this);
        if (visualizerData != null) visualizerData.onSetStarted(false);
        super.onStopped();
    }

    @Override
    protected void onEnabled(int track, long positionUs, boolean joining) throws ExoPlaybackException {
        IVisualizerDataCapturer visualizerData = VisualizerDataCapturerLimiter.getListener(this);
        if (visualizerData != null) visualizerData.onSetEnabled(true);
        super.onEnabled(track, positionUs, joining);
    }

    @Override
    protected void onDisabled() throws ExoPlaybackException {
        IVisualizerDataCapturer visualizerData = VisualizerDataCapturerLimiter.getListener(this);
        if (visualizerData != null) visualizerData.onSetEnabled(false);
        super.onDisabled();
    }

    @Override
    protected boolean processOutputBuffer(long positionUs,
                                          long elapsedRealtimeUs,
                                          android.media.MediaCodec codec,
                                          java.nio.ByteBuffer buffer,
                                          android.media.MediaCodec.BufferInfo bufferInfo,
                                          int bufferIndex,
                                          boolean shouldSkip)
            throws com.google.android.exoplayer.ExoPlaybackException {

        //TODO: Should we react on - shouldSkip?

        IVisualizerDataCapturer visualizerData = VisualizerDataCapturerLimiter.getListener(this);

        if (visualizerData != null)
        {
            if (bufferIndex >= 0 && bufferIndex < 128 && buffer != null)
                visualizerData.onPcmData(buffer, bufferInfo, bufferIndex, outputSampleRate, outputChannelCount, positionUs);
        }

        return super.processOutputBuffer(positionUs,
                elapsedRealtimeUs,
                codec,
                buffer,
                bufferInfo,
                bufferIndex,
                shouldSkip);
    }

    @Override
    protected void onOutputFormatChanged(MediaCodec codec, MediaFormat outputFormat) {

        outputSampleRate = 44100;
        outputChannelCount = 2;
        outputMimeType = "audio/raw";

        if (outputFormat.containsKey(MediaFormat.KEY_SAMPLE_RATE))
            outputSampleRate = outputFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        if (outputFormat.containsKey(MediaFormat.KEY_CHANNEL_COUNT))
            outputChannelCount = outputFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        if (outputFormat.containsKey(MediaFormat.KEY_MIME))
            outputMimeType = outputFormat.getString(MediaFormat.KEY_MIME);

        if (AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_fixAssumeMonoOutputFromMonoInput)) {

            //fix when mono source is played twice as fast
            //codecs is feed with mono source, output format reads as stereo,
            //but in fact is mono output,
            //we change format to mono so AudioTrack get configured correctly as mono

            if (inputChannelCount == 1 && inputMimeType.equals("audio/mpeg")) {

                if (outputChannelCount == 2) {
                    outputChannelCount = 1;
                    outputFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, outputChannelCount);
                }
            }
        }

        super.onOutputFormatChanged(codec, outputFormat);
    }

    @Override
    protected void onInputFormatChanged(MediaFormatHolder formatHolder) throws ExoPlaybackException {
        inputSampleRate = formatHolder.format.sampleRate;
        inputChannelCount = formatHolder.format.channelCount;
        inputMimeType = formatHolder.format.mimeType;
        super.onInputFormatChanged(formatHolder);
    }

    //STEREO BALANCE EXOPLAYER
//    @Override
//    public void handleMessage(int messageType, Object message) throws ExoPlaybackException {
//        if (messageType == MY_MSG_SET_VOLUME) {
//            float[] msg = (float[]) message;
//
//            if (audioTrack instanceof MyAudioTrack) {
//                ((MyAudioTrack) audioTrack).mySetVolume(msg[0], msg[1]);
//            } else {
//                tlog.w("audioTrack isn't MyAudioTrack");
//            }
//
//        } else if (messageType == MSG_SET_VOLUME) {
//            //nothing
//        } else {
//            super.handleMessage(messageType, message);
//        }
//    }

    @Override
    protected void onAudioSessionId(int audioSessionId) {
        super.onAudioSessionId(audioSessionId);

        IVisualizerDataCapturer visualizerData = VisualizerDataCapturerLimiter.getListener(this);
        if (visualizerData != null)
            visualizerData.onAudioSessionId(audioSessionId);
    }
}
