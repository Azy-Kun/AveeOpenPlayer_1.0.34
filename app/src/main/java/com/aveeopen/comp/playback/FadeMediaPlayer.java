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

import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import com.aveeopen.Common.Utils;
import com.aveeopen.comp.playback.Song.IMediaDataSource;

import java.lang.ref.WeakReference;

public class FadeMediaPlayer {

    private static final int MSG_TICK = 2;
    private static final int MSG_TICK2 = 3;
    private static final int MSG_CROSS_FADE_TICK = 4;
    private static final int MSG_START_CROSS_FADE = 5;
    private static final int MSG_START_FADE = 6;
    private static final int MSG_START = 7;

    private IMediaPlayerCore mediaPlayerCore;
    private IMediaPlayerCore.OnNotifyListener onNotifyListener;

    private int mode = 0;
    //0: nothing
    //1: fade down - stop
    //2: fade down - pause
    //3: fade up
    //4:
    //5: crossfade up

    private int mode2 = 0;
    //0: nothing
    //1: fade down - stop
    //2: fade down - pause
    //3: fade up
    //4: fade down - destroy
    //5: crossfade up
    //6: crossfade down - destroy

    private long tickMs = 80;
    private float fadeInSeconds = 0.25f;
    private float fadeSpeed = ((tickMs + 10) * 0.001f) / fadeInSeconds;//1 seconds

    private long crossFadeTickMs = 300;
    private long crossFadeDuration = -1;//1000; //ms
    private static final long prepareNextTimeReserve = 1500;//4000;

    static class MyHandler extends Handler {
        private WeakReference<FadeMediaPlayer> targetWeak;

        public MyHandler(FadeMediaPlayer target) {
            targetWeak = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {

            FadeMediaPlayer target = targetWeak.get();
            if (target == null) return;

            float crossFadeInSeconds = target.crossFadeDuration * 0.001f;
            float crossFadeSpeed = ((target.tickMs + 10) * 0.001f) / crossFadeInSeconds;//1 seconds

            switch (msg.what) {

                case MSG_TICK: {

                    if (target.mode == 1) {
                        if (target.mediaPlayerCore.setFadeVolumeRelative(-target.fadeSpeed, 0)) {
                            target.mediaPlayerCore.stop();
                            target.mode = 0;
                        }
                    } else if (target.mode == 2) {
                        if (target.mediaPlayerCore.setFadeVolumeRelative(-target.fadeSpeed, 0)) {
                            target.mediaPlayerCore.pause();
                            target.mode = 0;
                        }
                    } else if (target.mode == 3) {
                        if (target.mediaPlayerCore.setFadeVolumeRelative(target.fadeSpeed, 0)) {
                            target.mode = 0;
                        }
                    } else if (target.mode == 4) {

                    } else if (target.mode == 5) {
                        if (target.mediaPlayerCore.setFadeVolumeRelative(crossFadeSpeed, 0)) {
                            target.mode = 0;
                        }
                    }

                    if (target.mode != 0) {
                        Message msgnew = this.obtainMessage(MSG_TICK);
                        this.sendMessageDelayed(msgnew, target.tickMs);
                    }

                }
                break;


                case MSG_TICK2: {

                    if (target.mode2 == 4) {
                        if (target.mediaPlayerCore.setFadeVolumeRelative(-target.fadeSpeed, 1)) {
                            target.mediaPlayerCore.destroy(1);
                            target.mode2 = 0;
                        }
                    } else if (target.mode2 == 5) {
                        target.mode2 = 0;
                    } else if (target.mode2 == 6) {
                        if (target.mediaPlayerCore.setFadeVolumeRelative(-crossFadeSpeed, 1)) {
                            target.mediaPlayerCore.destroy(1);
                            target.mode2 = 0;
                        }
                    }

                    if (target.mode2 != 0) {
                        Message msgnew = this.obtainMessage(MSG_TICK2);
                        this.sendMessageDelayed(msgnew, target.tickMs);
                    }

                }
                break;

                case MSG_CROSS_FADE_TICK: {

                    if (target.crossFadeDuration >= 0) {//enabled?

                        long pos1 = target.mediaPlayerCore.position();
                        long duration1 = target.mediaPlayerCore.duration();
                        long toEnd1 = duration1 - pos1;

                        if (duration1 > target.crossFadeDuration + prepareNextTimeReserve) {

                            if (toEnd1 <= target.crossFadeDuration + prepareNextTimeReserve) {
                                long crossFadeAfter = (toEnd1 - target.crossFadeDuration) - 10;//little more reserve better corssfade over that silent gap
                                long crossFadeAtTime = Utils.tickCount() + crossFadeAfter;
                                target.onNotifyListener.requestNextDataAtTime(crossFadeAtTime);

                                break;
                            }
                        }

                        Message msgnew = this.obtainMessage(MSG_CROSS_FADE_TICK);
                        this.sendMessageDelayed(msgnew, target.crossFadeTickMs);
                    }


                }
                break;

                case MSG_START_CROSS_FADE: {
                    target.startCrossFade();
                    target.destroyOldCrossFade();
                }
                break;

                case MSG_START_FADE: {
                    target.startFade();
                    target.destroyOld();//just in case
                }
                break;

                case MSG_START: {
                    target.justStart();
                }
                break;

                default:
                    break;
            }
        }
    }


    private MyHandler mHandler = new MyHandler(this);

    public FadeMediaPlayer(IMediaPlayerCore mediaPlayerCore, IMediaPlayerCore.OnNotifyListener onNotifyListener, long crossFadeDurationMs) {
        this.mediaPlayerCore = mediaPlayerCore;
        this.onNotifyListener = onNotifyListener;
        setCrossFade(crossFadeDurationMs);

        mediaPlayerCore.setNotifyListener(new IMediaPlayerCore.OnNotifyListener() {

            @Override
            public void requestNextDataDelay() {

                if (mHandler.hasMessages(MSG_START_CROSS_FADE) || mHandler.hasMessages(MSG_START_FADE) || mHandler.hasMessages(MSG_START)) {
                    return;
                }

                FadeMediaPlayer.this.onNotifyListener.requestNextDataDelay();
            }

            @Override
            public void requestNextDataNow() {

                if (mHandler.hasMessages(MSG_START_CROSS_FADE) || mHandler.hasMessages(MSG_START_FADE) || mHandler.hasMessages(MSG_START)) {
                    return;
                }

                FadeMediaPlayer.this.onNotifyListener.requestNextDataNow();
            }

            @Override
            public void requestNextDataAtTime(long atTime) {
                FadeMediaPlayer.this.onNotifyListener.requestNextDataAtTime(atTime);
            }

            @Override
            public boolean onRequestAudioFocus() {
                return FadeMediaPlayer.this.onNotifyListener.onRequestAudioFocus();
            }

            @Override
            public void onVolumeMuteStateChanged(boolean state) {
                FadeMediaPlayer.this.onNotifyListener.onVolumeMuteStateChanged(state);
            }

            @Override
            public void onMpPlaystateOrMetaChanged(boolean metaChanged, String errorMsg) {
                FadeMediaPlayer.this.onNotifyListener.onMpPlaystateOrMetaChanged(metaChanged, errorMsg);
            }

            @Override
            public void onBufferingUpdate(boolean state, int percent) {
                FadeMediaPlayer.this.onNotifyListener.onBufferingUpdate(state, percent);
            }

            @Override
            public void onNotifyVideoSizeChanged(int width, int height, float widthHeightRatio) {
                FadeMediaPlayer.this.onNotifyListener.onNotifyVideoSizeChanged(width, height, widthHeightRatio);
            }

            @Override
            public int getVideoScalingMode() {
                return FadeMediaPlayer.this.onNotifyListener.getVideoScalingMode();
            }

            @Override
            public SurfaceHolder onRequestVideoSurfaceHolder() {
                return FadeMediaPlayer.this.onNotifyListener.onRequestVideoSurfaceHolder();
            }

            @Override
            public void onEqualizerDescChanged(BaseEqualizerEffect.EqualizerDesc desc) {
                FadeMediaPlayer.this.onNotifyListener.onEqualizerDescChanged(desc);
            }

            @Override
            public BaseEqualizerEffect.EqualizerSettings getEqualizerSettings(String name) {
                return FadeMediaPlayer.this.onNotifyListener.getEqualizerSettings(name);
            }

            @Override
            public boolean getEqualizerEnabled(String name) {
                return FadeMediaPlayer.this.onNotifyListener.getEqualizerEnabled(name);
            }
        });
    }

    public IMediaPlayerCore getMediaPlayerCore() {
        return mediaPlayerCore;
    }

    public void release() {
        mHandler.removeCallbacksAndMessages(null);
        mediaPlayerCore.release();
        mediaPlayerCore = null;
    }

    public void stopFadeAll() {
        stopFade();
        destroyOldFadeDown();
    }

    public void pauseFadeAll() {
        pauseFade();
        destroyOldFadeDown();
    }

    public void startFadeAll() {
        startFade();
        destroyOldFadeDown();//just in case
    }

    public void stop() {
        mediaPlayerCore.stop();
    }

    private void stopFade() {
        mode = 1;
        mHandler.removeMessages(MSG_TICK);
        Message msg = mHandler.obtainMessage(MSG_TICK);
        mHandler.sendMessageDelayed(msg, tickMs);
    }


    private void pauseFade() {
        mode = 2;
        mHandler.removeMessages(MSG_TICK);
        Message msg = mHandler.obtainMessage(MSG_TICK);
        mHandler.sendMessageDelayed(msg, tickMs);
    }

    private void startFade() {
        mode = 3;
        mediaPlayerCore.start();
        mHandler.removeMessages(MSG_TICK);
        Message msg = mHandler.obtainMessage(MSG_TICK);
        mHandler.sendMessageDelayed(msg, tickMs);
    }

    private void justStart() {
        mediaPlayerCore.start();
    }

    private void justFadeUp() {
        mode = 3;
        mHandler.removeMessages(MSG_TICK);
        Message msg = mHandler.obtainMessage(MSG_TICK);
        mHandler.sendMessageDelayed(msg, tickMs);
    }

    private void startCrossFade() {
        mode = 5;
        mediaPlayerCore.start();
        mHandler.removeMessages(MSG_TICK);
        Message msg = mHandler.obtainMessage(MSG_TICK);
        mHandler.sendMessageDelayed(msg, tickMs);
    }

    private void justCrossFadeUp() {
        mode = 5;
        mHandler.removeMessages(MSG_TICK);
        Message msg = mHandler.obtainMessage(MSG_TICK);
        mHandler.sendMessageDelayed(msg, tickMs);
    }

    public void destroyOldFadeDown() {
        mHandler.removeMessages(MSG_TICK2);
        mode2 = 4;
        Message msg = mHandler.obtainMessage(MSG_TICK2);
        mHandler.sendMessageDelayed(msg, tickMs);
    }

    private void destroyOld() {
        mediaPlayerCore.destroy(1);
    }

    public void destroyOldCrossFade() {
        mHandler.removeMessages(MSG_TICK2);
        mode2 = 6;
        Message msg = mHandler.obtainMessage(MSG_TICK2);
        mHandler.sendMessageDelayed(msg, tickMs);
    }

    public void playNextAtTime(IMediaDataSource path, final boolean start, long seekPos, long atTime) {
        mHandler.removeMessages(MSG_START_CROSS_FADE);
        mHandler.removeMessages(MSG_START_FADE);
        mHandler.removeMessages(MSG_START);

        long delay = atTime - Utils.tickCount();
        if (atTime <= 0 || delay <= 0) {
            playNext(path, start, seekPos);
        } else {

            if (crossFadeDuration >= 0 && crossFadeDuration > crossFadeTickMs) {//enabled? and for gapless its as disabled here

                startPlayNext(path, false, false, 0.0f, seekPos);

                if (start) {
                    Message msg = mHandler.obtainMessage(MSG_START_CROSS_FADE);
                    mHandler.sendMessageAtTime(msg, atTime);
                }

            } else if (crossFadeDuration >= 0) {
                startPlayNext(path, false, false, 1.0f, seekPos);
                if (start) {
                    Message msg = mHandler.obtainMessage(MSG_START);
                    mHandler.sendMessageAtTime(msg, atTime);
                }
            } else {
                startPlayNext(path, true, false, 1.0f, seekPos);
                if (start) {
                    Message msg = mHandler.obtainMessage(MSG_START);
                    mHandler.sendMessageAtTime(msg, atTime);
                }
            }
        }
    }

    public void playNext(IMediaDataSource path, final boolean start, long seekPos) {

        mHandler.removeMessages(MSG_START_CROSS_FADE);
        mHandler.removeMessages(MSG_START_FADE);
        mHandler.removeMessages(MSG_START);

        if (crossFadeDuration >= 0 && crossFadeDuration > crossFadeTickMs) {//cross fade enabled? for gapless its as disabled
            startPlayNext(path, false, start, 0.0f, seekPos);
            justCrossFadeUp();
            destroyOldCrossFade();
        } else {
            startPlayNext(path, true, start, 1.0f, seekPos);
            destroyOld();//just in case
        }
    }

    private void startPlayNext(IMediaDataSource path, final boolean killOld, final boolean start, float fadeStartVolume, long seekPos) {
        mHandler.removeMessages(MSG_CROSS_FADE_TICK);
        Message msg0 = mHandler.obtainMessage(MSG_CROSS_FADE_TICK);
        mHandler.sendMessageDelayed(msg0, crossFadeTickMs);

        mHandler.removeMessages(MSG_TICK2);
        mode2 = 0;

        mode = 0;
        mHandler.removeMessages(MSG_TICK);

        mediaPlayerCore.setNextDataSource(path);
        mediaPlayerCore.playNext(killOld, start, fadeStartVolume, seekPos);
    }

    private void startPlayNextFadeUp(IMediaDataSource path, boolean killOld, boolean start, long seekPos) {
        mHandler.removeMessages(MSG_CROSS_FADE_TICK);
        Message msg0 = mHandler.obtainMessage(MSG_CROSS_FADE_TICK);
        mHandler.sendMessageDelayed(msg0, crossFadeTickMs);


        mediaPlayerCore.setNextDataSource(path);
        mediaPlayerCore.playNext(killOld, start, 0.0f, seekPos);

        mHandler.removeMessages(MSG_TICK2);
        mode2 = 0;

        mHandler.removeMessages(MSG_TICK);
        mode = 3;
        Message msg = mHandler.obtainMessage(MSG_TICK);
        mHandler.sendMessageDelayed(msg, tickMs);
    }

    //crossFadeDurationMs: -1  disabled
    public void setCrossFade(long crossFadeDurationMs) {
        this.crossFadeDuration = crossFadeDurationMs;
    }

}
