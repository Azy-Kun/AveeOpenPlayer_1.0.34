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

package com.aveeopen.comp.playback.ExoMediaPlayer.demo.player;

import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.demo.player.DemoPlayer;
import com.google.android.exoplayer.upstream.BandwidthMeter;

public class MyDemoPlayer extends DemoPlayer {

    private boolean autoPlay = false;
    private TrackRenderer audioRenderer;

    public MyDemoPlayer(RendererBuilder rendererBuilder) {
        super(fixRenderBuilder(rendererBuilder));
    }

    private static RendererBuilder fixRenderBuilder(RendererBuilder rendererBuilder) {
        if (rendererBuilder == null)
            rendererBuilder = new RendererBuilderDummy();
        return rendererBuilder;
    }

    @Override
    public void prepare() {
        audioRenderer = null;
        super.prepare();
    }

    public void prepare(RendererBuilder rendererBuilder) {//###
        this.rendererBuilder = rendererBuilder;//###
        prepare();
    }

    @Override
    public void onRenderers(TrackRenderer[] renderers, BandwidthMeter bandwidthMeter) {
        super.onRenderers(renderers, bandwidthMeter);
        this.audioRenderer = renderers[TYPE_AUDIO];
    }

    public void setAutoPlay(boolean val) {
        autoPlay = val;
    }

    public void maybeStartPlayback() {

        if (autoPlay) {
            if ((this.surface != null && this.surface.isValid())
                    || this.getSelectedTrack(DemoPlayer.TYPE_VIDEO) == DemoPlayer.TRACK_DISABLED) {
                this.setPlayWhenReady(true);
                autoPlay = false;
            }
        }
    }

    public void stop() {
        player.stop();
    }

    public void audioTrackRendererSetVolume(float volumeL, float volumeR) {

        if (audioRenderer == null) return;

        player.sendMessage(
                audioRenderer, MediaCodecAudioTrackRenderer.MSG_SET_VOLUME, volumeR);//0.0 to 1.0

        //STEREO BALANCE EXOPLAYER
        //player.sendMessage(
        //        audioRenderer, MyMediaCodecAudioTrackRendererCapture.MY_MSG_SET_VOLUME, new float[]{volumeL, volumeR});//0.0 to 1.0
    }

    static class RendererBuilderDummy implements RendererBuilder {

        @Override
        public void buildRenderers(DemoPlayer player) {
        }

        @Override
        public void cancel() {
        }
    }
}
