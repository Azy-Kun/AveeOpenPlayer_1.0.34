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

package com.aveeopen.comp.playback;


public class AudioFrameData {

    public boolean valid = false;
    public short[] pcmBuffer;//16 bit mono
    public int sampleRate;
    public float rms;

    private AudioFrameData(int bufferSize) {
        valid = false;
        pcmBuffer = new short[bufferSize];
        sampleRate = 44100;
        rms = 0.0f;
    }

    public static AudioFrameData createReuse(AudioFrameData old, int bufferSize) {
        if (old == null || old.pcmBuffer.length != bufferSize) {
            old = new AudioFrameData(bufferSize);
        }
        return old;
    }
}
