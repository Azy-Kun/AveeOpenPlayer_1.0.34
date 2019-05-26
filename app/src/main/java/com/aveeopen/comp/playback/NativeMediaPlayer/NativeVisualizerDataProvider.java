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

package com.aveeopen.comp.playback.NativeMediaPlayer;

import android.media.audiofx.Virtualizer;
import android.media.audiofx.Visualizer;

import com.aveeopen.comp.playback.AudioFrameData;

public class NativeVisualizerDataProvider {

    private boolean disableVisualizer;
    private byte[] bytesBuffer = new byte[1];
    private Visualizer visualizer;
    private int currentAudioSessionId = -1;
    private Virtualizer virtualizerEffect = null;

    public NativeVisualizerDataProvider() {
        disableVisualizer = false;
    }

    static int pow2roundup(int x) {
        if (x < 0)
            return 0;
        --x;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return x + 1;
    }

    public void release() {
        if (visualizer != null) {
            visualizer.setEnabled(false);
            visualizer.release();
        }
        if (virtualizerEffect != null) {
            virtualizerEffect.setEnabled(true);
            virtualizerEffect.setEnabled(false);
            virtualizerEffect.release();
        }
    }

    void reset() {
        disableVisualizer = false;
    }

    public AudioFrameData getVisData(long positionUs, AudioFrameData outResult, int audioSessionId, boolean useGlobalSession) {

        if (disableVisualizer)
            return null;

        if (visualizer == null || currentAudioSessionId != audioSessionId) {
            currentAudioSessionId = audioSessionId;

            try {
                if (!useGlobalSession) {
                    visualizer = new Visualizer(currentAudioSessionId);
                } else {
                    //when we use visualizer on audioSession - 0(global session)
                    // it wont corrupt audio if visualizer is buggy on device, but need additional premission
                    visualizer = new Visualizer(0);
                }

                visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);

            } catch (RuntimeException e) {
                disableVisualizer = true;
            }
        }

        if (visualizer == null) {
            return null;
        }

        int capSize = visualizer.getCaptureSize();

        int targetCapSize = pow2roundup(outResult.pcmBuffer.length);

        if (targetCapSize != capSize) {
            int[] capRanges = Visualizer.getCaptureSizeRange();
            if (targetCapSize >= capRanges[0] && targetCapSize <= capRanges[1]) {

                visualizer.setEnabled(false);
                visualizer.setCaptureSize(targetCapSize);
                capSize = targetCapSize;
            }
        }

        if (bytesBuffer.length != capSize) {
            bytesBuffer = new byte[capSize];
        }

        if (!visualizer.getEnabled()) {
            visualizer.setEnabled(true);
        }

        try {
            visualizer.getWaveForm(bytesBuffer);
        } catch (IllegalStateException ignored) {
        }

        float rms_ = 0.0f;

        int min = Math.min(bytesBuffer.length, outResult.pcmBuffer.length);
        for (int i = 0; i < min; i++) {
            short v1 = (short) (((bytesBuffer[i] & 0xFF) - 128));
            v1 = (short) (v1 * 2);

            outResult.pcmBuffer[i] = v1;
            rms_ += (outResult.pcmBuffer[i] / 255.0f);
        }

        rms_ /= (float) outResult.pcmBuffer.length;
        outResult.rms = rms_;
        outResult.sampleRate = 44100;
        outResult.valid = true;

        return outResult;
    }
}
