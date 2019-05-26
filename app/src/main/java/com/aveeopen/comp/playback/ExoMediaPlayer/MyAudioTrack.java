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

import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioTrack;

//STEREO BALANCE EXOPLAYER
//public class MyAudioTrack extends AudioTrack {
//
//    private float volumeLeft = 1.0f;
//    private float volumeRight = 1.0f;
//
//    public MyAudioTrack(AudioCapabilities audioCapabilities, int streamType) {
//        super(audioCapabilities, streamType);
//    }
//
//    @Override
//    public void setVolume(float volume) {
//        super.setVolume(volume);
//        setAudioTrackVolume();
//    }
//
//    @Override
//    protected void setAudioTrackVolume() {
//        if (isInitialized()) {
//            audioTrack.setStereoVolume(volumeLeft, volumeRight);
//        }
//    }
//
//    public void mySetVolume(float volumeLeft, float volumeRight) {
//        this.volumeLeft = volumeLeft;
//        this.volumeRight = volumeRight;
//        setAudioTrackVolume();
//    }
//}
