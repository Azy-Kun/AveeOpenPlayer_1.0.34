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

import android.view.SurfaceHolder;

import com.aveeopen.comp.playback.Song.IMediaDataSource;

public interface IMediaPlayerCore {

    IMediaPlayerCore Empty = new IMediaPlayerCore() {
        @Override
        public void release() {

        }

        @Override
        public void setNotifyListener(OnNotifyListener onNotifyListener) {

        }

        @Override
        public void setNextDataSource(IMediaDataSource path) {

        }

        @Override
        public void playNext(boolean killCurrent, boolean start, float fadeStartVolume, long seekPos) {

        }

        @Override
        public void start() {

        }

        @Override
        public void pause() {

        }

        @Override
        public void stop() {

        }

        @Override
        public boolean isPreparingOrAbove() {
            return false;
        }

        @Override
        public boolean isPreparingOrStared() {
            return false;
        }

        @Override
        public boolean containsVideoTrack() {
            return false;
        }

        @Override
        public void setVideoScalingMode(int mode) {

        }

        @Override
        public void setVideoSurfaceHolder(SurfaceHolder surfaceHolder) {

        }

        @Override
        public long duration() {
            return 0;
        }

        @Override
        public long position() {
            return 0;
        }

        @Override
        public void seek(long whereto) {

        }

        @Override
        public void setMute(boolean state) {

        }

        @Override
        public boolean isMuted() {
            return false;
        }

        @Override
        public void setVolume(float vol) {

        }

        @Override
        public void setFadeVolume(float volume, int index) {

        }

        @Override
        public boolean setFadeVolumeRelative(float volumePlus, int index) {
            return false;
        }

        @Override
        public void setVolumeStereoBalance(float balance) {

        }

        @Override
        public void destroy(int index) {

        }

        @Override
        public void resetVisualizer() {

        }

        @Override
        public AudioFrameData getVisualizationData(AudioFrameData outResult, boolean useGlobalSession) {
            return null;
        }

        @Override
        public BaseEqualizerEffect.EqualizerDesc getEqualizerDesc() {
            return null;
        }

        @Override
        public void setEqualizerSettings(BaseEqualizerEffect.EqualizerSettings equalizerSettings) {

        }
    };

    int MP_VIDEO_SCALING_MODE_SCALE_TO_FIT = 1;
    int MP_VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING = 2;

    void release();

    void setNotifyListener(IMediaPlayerCore.OnNotifyListener onNotifyListener);

    void setNextDataSource(IMediaDataSource path);

    void playNext(boolean killCurrent, boolean start, float fadeStartVolume, long seekPos);//ms

    void start();

    void pause();

    void stop();

    boolean isPreparingOrAbove();

    boolean isPreparingOrStared();

    boolean containsVideoTrack();

    void setVideoScalingMode(int mode);//1 - fit, 2 - fit crop

    void setVideoSurfaceHolder(SurfaceHolder surfaceHolder);

    long duration();//ms

    long position();//ms

    void seek(long whereto);//ms

    void setMute(boolean state);

    boolean isMuted();

    void setVolume(float vol);

    void setFadeVolume(float volume, int index);

    boolean setFadeVolumeRelative(float volumePlus, int index);

    void setVolumeStereoBalance(float balance);

    void destroy(int index);

    void resetVisualizer();

    AudioFrameData getVisualizationData(AudioFrameData outResult, boolean useGlobalSession);

    BaseEqualizerEffect.EqualizerDesc getEqualizerDesc();

    void setEqualizerSettings(BaseEqualizerEffect.EqualizerSettings equalizerSettings);

    interface OnNotifyListener {

        void requestNextDataDelay();

        void requestNextDataNow();

        void requestNextDataAtTime(long atTime);

        boolean onRequestAudioFocus();

        void onVolumeMuteStateChanged(boolean state);

        void onMpPlaystateOrMetaChanged(boolean metaChanged, String errorMsg);

        void onBufferingUpdate(boolean state, int percent);

        void onNotifyVideoSizeChanged(int width, int height, float widthHeightRatio);

        int getVideoScalingMode(); //1 - fit, 2 - fit crop

        SurfaceHolder onRequestVideoSurfaceHolder();

        BaseEqualizerEffect.EqualizerSettings getEqualizerSettings(String name);

        boolean getEqualizerEnabled(String name);

        void onEqualizerDescChanged(BaseEqualizerEffect.EqualizerDesc desc);
    }

}
