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

package com.aveeopen.comp.MediaControlsUI;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.Common.Utils;
import com.aveeopen.Common.UtilsUI;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.comp.playback.PlayingMediaInfo;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.ContextData;
import com.aveeopen.R;

import java.lang.ref.WeakReference;

public class MediaControlsUI {

    public static WeakEventR<Boolean> onRequestVolumeMuteState = new WeakEventR<>();
    public static WeakEventR<Boolean> onRequestAudioEffectsActiveState = new WeakEventR<>();
    public static WeakEventR<Integer> onRequestShowState = new WeakEventR<>();
    public static WeakEvent onPlaybackPrev = new WeakEvent();
    public static WeakEvent onPlaybackNext = new WeakEvent();
    public static WeakEvent onPlaybackTogglePause = new WeakEvent();
    public static WeakEventR<Long> onRequestTrackPosition = new WeakEventR<>();
    public static WeakEventR<Tuple2<PlaylistSong.Data, PlayingMediaInfo>> onRequestTrackInfo = new WeakEventR<>();
    public static WeakEventR<Tuple2<Boolean /*isPlaying*/, Boolean /*wantsPlaying*/>> onRequestPlaystate = new WeakEventR<>();
    //volume window
    public static WeakEventR<Tuple2<Integer, Integer>> onRequestAudioVolumeState = new WeakEventR<>();//volume, volumeMax
    public static WeakEventR<Tuple2<Integer, Integer>> onRequestAudioBalanceState = new WeakEventR<>();//balance, positiveBalanceMax
    public static WeakEventR<Tuple2<Integer, Integer>> onRequestAudioEffectVirtualizerState = new WeakEventR<>();//value, max
    public static WeakEventR<Tuple2<Integer, Integer>> onRequestCrossFadeState = new WeakEventR<>();//value, max
    public static WeakEvent2<Integer /*val*/, Integer /*valMax*/> onSetAudioVolume = new WeakEvent2<>();
    public static WeakEvent2<Integer /*val*/, Integer /*valMax*/> onSetAudioStereoBalance = new WeakEvent2<>();
    public static WeakEvent2<Integer /*val*/, Integer /*valMax*/> onSetCrossFade = new WeakEvent2<>();
    public static WeakEventR<Boolean> onRequestAudioViewExpandedState = new WeakEventR<>();
    public static WeakEvent1<Boolean /*expanded*/> onSetAudioViewExpandedState = new WeakEvent1<>();
    public static WeakEvent onToggleMuteAction = new WeakEvent();
    public static WeakEvent1<ContextData /*contextData*/> onActionEq = new WeakEvent1<>();
    public static WeakEventR<Boolean> onRequestEqState = new WeakEventR<>();
    //three dot window
    public static WeakEventR<Integer> onRequestShuffleMode = new WeakEventR<>();
    public static WeakEvent1<Integer /*shuffleMode*/> onSetShuffleMode = new WeakEvent1<>();
    public static WeakEventR<Integer> onRequestRepeatMode = new WeakEventR<>();
    public static WeakEvent1<Integer /*repeatMode*/> onSetRepeatMode = new WeakEvent1<>();
    public static WeakEventR<Integer> onRequestMusicSystemIndex = new WeakEventR<>();
    public static WeakEvent1<Integer> onSelectMusicSysAction = new WeakEvent1<>();
    public static WeakEvent1<Long /*trackPosition*/> onSetTrackPosition = new WeakEvent1<>();

    private static final Object createInstanceLock = new Object();
    private static final int MSG_REFRESH = 1;
    private static volatile WeakReference<MediaControlsUI> instanceWeak = new WeakReference<>(null);
    private final Handler handler;
    private WeakReference<VolumePopupWindow> volumePopupWindowSingleton = new WeakReference<>(null);
    private WeakReference<ThreeDotPopupWindow> ctrlOverflowPopupWindowSingleton = new WeakReference<>(null);
    private WeakReference<ListPopupWindow> overflowPopupWindowSingleton = new WeakReference<>(null);

    private ImageButton buttonPause, buttonVolume, buttonOverflow;
    private ImageView overlayButtonPause;
    private TextView currentTime, lengthTime;
    private SeekBar progress;
    private TextView currentTitle;

    private ImageView overlayButtonPauseS;
    private ImageButton buttonPauseS, buttonVolumeS;
    private SeekBar progressS;
    private TextView currentTitleS;

    private long duration;
    private long posOverride = -1;
    private long lastSeekEventTime;

    private int lastShowLevel = -1;
    private float designHeight0;

    private View viewRootL;
    private View viewRootBg;
    private View viewRootS;//is null if not available, eg land orient

    private RotateAnimation rotateAnimation;
    private RotateAnimation rotateAnimationS;

    private Runnable viewRootLHide = new Runnable() {
        @Override
        public void run() {
            viewRootL.setVisibility(View.INVISIBLE);
        }
    };

    private Runnable viewRootSHide = new Runnable() {
        @Override
        public void run() {
            if (viewRootS != null)
                viewRootS.setVisibility(View.INVISIBLE);
        }
    };


    private SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {

        public void onStartTrackingTouch(SeekBar bar) {
            lastSeekEventTime = 0;
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) return;

            posOverride = duration * progress / 1000;

            long now = SystemClock.elapsedRealtime();
            if ((now - lastSeekEventTime) > 250) {
                lastSeekEventTime = now;
                onSetTrackPosition.invoke(posOverride);
            }
        }

        public void onStopTrackingTouch(SeekBar bar) {
            onSetTrackPosition.invoke(posOverride);
            posOverride = -1;
        }
    };

    public MediaControlsUI() {
        rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(6000);
        rotateAnimation.setRepeatCount(-1);

        rotateAnimationS = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAnimationS.setInterpolator(new LinearInterpolator());
        rotateAnimationS.setDuration(6000);
        rotateAnimationS.setRepeatCount(-1);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {

                    case MSG_REFRESH:
                        long next = refreshNow();
                        queueNextRefresh(next);
                        break;
                    default:
                        break;
                }

                return false;
            }
        });

    }

    public static MediaControlsUI createOrGetInstance() {
        MediaControlsUI inst0 = instanceWeak.get();
        if (inst0 != null) return inst0;

        synchronized (createInstanceLock) {
            MediaControlsUI inst = instanceWeak.get();
            if (inst == null) {
                inst = new MediaControlsUI();
                instanceWeak = new WeakReference<>(inst);
            }

            return inst;
        }
    }

    public static MediaControlsUI getInstance() {
        return instanceWeak.get();
    }

    private boolean isViewCreated() {
        return viewRootBg != null;
    }

    public void onCreateView(View view, View viewCollapsed, View viewBg) {
        View.OnClickListener volumeButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextData contextData = new ContextData(v);
                LayoutInflater layoutInflater = contextData.getLayoutInflater();

                VolumePopupWindow popup = volumePopupWindowSingleton.get();
                UtilsUI.dismissSafe(popup);
                volumePopupWindowSingleton =
                        new WeakReference<>(new VolumePopupWindow(layoutInflater, v));
            }
        };

        View.OnClickListener overflowButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreeDotPopupWindow popup = ctrlOverflowPopupWindowSingleton.get();
                UtilsUI.dismissSafe(popup);
                ctrlOverflowPopupWindowSingleton =
                        new WeakReference<>(new ThreeDotPopupWindow(v));
            }
        };

        designHeight0 = view.getResources().getDimension(R.dimen.design_height_0);

        viewRootL = view;
        viewRootBg = viewBg;
        viewRootS = viewCollapsed;

        UtilsUI.disallowInterceptTouchEventRecursive(viewRootL, viewRootL.getParent());

        //L
        {
            currentTime = (TextView) view.findViewById(R.id.txtSongCurrentTime);
            lengthTime = (TextView) view.findViewById(R.id.txtSongDuration);
            //mTotalTime = (TextView) rootView.findViewById(R.id.totaltime);
            progress = (SeekBar) view.findViewById(R.id.seekBarSongProgress);
            currentTitle = (TextView) view.findViewById(R.id.txtSongTitle);
            progress.setOnSeekBarChangeListener(seekListener);

            ImageButton btn1 = (ImageButton) view.findViewById(R.id.btnPrev);
            btn1.setImageResource(R.drawable.ic_ctrl_fb);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPlaybackPrev.invoke();
                }
            });

            btn1 = (ImageButton) view.findViewById(R.id.btnNext);
            btn1.setImageResource(R.drawable.ic_ctrl_ff);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPlaybackNext.invoke();
                }
            });

            buttonPause = (ImageButton) view.findViewById(R.id.btnPause);
            buttonPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPlaybackTogglePause.invoke();
                }
            });
            overlayButtonPause = (ImageView) view.findViewById(R.id.viewOverlayPause);


            buttonVolume = (ImageButton) view.findViewById(R.id.btnVolume);
            buttonVolume.setOnClickListener(volumeButtonClick);


            buttonOverflow = (ImageButton) view.findViewById(R.id.btnMediaControlsOverflow);
            buttonOverflow.setOnClickListener(overflowButtonClick);
        }

        //S
        if (viewRootS != null) {
            progressS = (SeekBar) viewRootS.findViewById(R.id.seekBarSongProgress);
            currentTitleS = (TextView) viewRootS.findViewById(R.id.txtSongTitle);
            progressS.setOnSeekBarChangeListener(seekListener);

            buttonPauseS = (ImageButton) viewRootS.findViewById(R.id.btnPause);
            buttonPauseS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPlaybackTogglePause.invoke();
                }
            });
            overlayButtonPauseS = (ImageView) viewRootS.findViewById(R.id.viewOverlayPause);

            buttonVolumeS = (ImageButton) viewRootS.findViewById(R.id.btnVolume);
            buttonVolumeS.setOnClickListener(volumeButtonClick);

            ImageButton buttonOverflowS = (ImageButton) viewRootS.findViewById(R.id.btnMediaControlsOverflow);
            buttonOverflowS.setOnClickListener(overflowButtonClick);
        }

        int showState = onRequestShowState.invoke(2);
        lastShowLevel = -1;
        animateShow(showState);

        Tuple2<Boolean /*isPlaying*/, Boolean /*wantsPlaying*/> playState = onRequestPlaystate.invoke(new Tuple2<>(false, false));
        updatePlaystate(playState.obj1, playState.obj2);
        Tuple2<PlaylistSong.Data /*songData*/, PlayingMediaInfo /*playingMediaInfo*/> trackInfo = onRequestTrackInfo.invoke(new Tuple2<>(PlaylistSong.emptyData, PlayingMediaInfo.empty));
        updateTrackInfo(trackInfo.obj1, trackInfo.obj2);

        boolean volumeMuteState = onRequestVolumeMuteState.invoke(false);
        boolean audioEffectsActiveState = onRequestAudioEffectsActiveState.invoke(false);

        onVolumeMuteChanged(volumeMuteState, audioEffectsActiveState);

        queueNextRefresh(100);
    }

    private void updatePauseButtonImage(boolean isPlaying, boolean wantsPlaying) {

        if (wantsPlaying) {

            if (isPlaying) {
                if (overlayButtonPause.getAnimation() == null || !overlayButtonPause.getAnimation().hasStarted())
                    overlayButtonPause.startAnimation(rotateAnimation);
            } else {
                overlayButtonPause.clearAnimation();
            }

            overlayButtonPause.setVisibility(View.VISIBLE);
            buttonPause.setImageResource(R.drawable.ic_ctrl_pause_s);

            if (overlayButtonPauseS != null && buttonPauseS != null) {
                if (isPlaying) {
                    if (overlayButtonPauseS.getAnimation() == null || !overlayButtonPauseS.getAnimation().hasStarted())
                        overlayButtonPauseS.startAnimation(rotateAnimationS);
                } else {
                    overlayButtonPauseS.clearAnimation();
                }

                overlayButtonPauseS.setVisibility(View.VISIBLE);
                buttonPauseS.setImageResource(R.drawable.ic_ctrl_pause_vs);
            }

        } else {

            overlayButtonPause.clearAnimation();
            overlayButtonPause.setVisibility(View.INVISIBLE);

            buttonPause.setImageResource(R.drawable.ic_ctrl_play);

            if (overlayButtonPauseS != null && buttonPauseS != null) {
                overlayButtonPauseS.clearAnimation();
                overlayButtonPauseS.setVisibility(View.INVISIBLE);

                buttonPauseS.setImageResource(R.drawable.ic_ctrl_play_s);
            }
        }

    }

    private void updatePauseButtonImageTrackUpdate() {
        if (overlayButtonPause.getAnimation() != null && overlayButtonPause.getAnimation().hasStarted()) {
            overlayButtonPause.clearAnimation();
            overlayButtonPause.startAnimation(rotateAnimation);
        }

        if (overlayButtonPauseS != null) {
            if (overlayButtonPauseS.getAnimation() != null && overlayButtonPauseS.getAnimation().hasStarted()) {
                overlayButtonPauseS.clearAnimation();
                overlayButtonPauseS.startAnimation(rotateAnimationS);
            }
        }
    }

    private void updateButtonVolume(boolean volumeMuteState, boolean audioEffectsActiveState) {
        int buttonColor = UtilsUI.getAttrColor(buttonVolume, R.attr.buttonColorLight);

        if (volumeMuteState) {
            int color = UtilsUI.getAttrColor(buttonVolume, R.attr.highlight_color_1);
            buttonVolume.setImageResource(R.drawable.ic_mute_s);
            buttonVolume.setColorFilter(color);

            if (buttonVolumeS != null) {
                buttonVolumeS.setImageResource(R.drawable.ic_mute_s);
                buttonVolumeS.setColorFilter(color);
            }
        } else if (audioEffectsActiveState) {
            buttonVolume.setImageResource(R.drawable.ic_speaker_asterisk_s);
            buttonVolume.setColorFilter(buttonColor);

            if (buttonVolumeS != null) {
                buttonVolumeS.setImageResource(R.drawable.ic_speaker_asterisk_s);
                buttonVolumeS.setColorFilter(buttonColor);
            }
        } else {
            buttonVolume.setImageResource(R.drawable.ic_speaker_s);
            buttonVolume.setColorFilter(buttonColor);

            if (buttonVolumeS != null) {
                buttonVolumeS.setImageResource(R.drawable.ic_speaker_s);
                buttonVolumeS.setColorFilter(buttonColor);
            }
        }
    }

    public void updatePlaystate(boolean isPlaying, boolean wantsPlaying) {
        if (!isViewCreated()) return;
        updatePauseButtonImage(isPlaying, wantsPlaying);
    }

    public void updateTrackInfo(PlaylistSong.Data songData, PlayingMediaInfo playingMediaInfo) {
        if (!isViewCreated()) return;

        duration = playingMediaInfo.duration;
        lengthTime.setText(Utils.getDurationStringHMSS((int) (duration / 1000)));

        currentTitle.setText(songData.trackName);
        currentTitle.setSelected(true);
        currentTitle.setMovementMethod(new MyTitleScrollingMovementMethod());
        currentTitle.setClickable(false);
        currentTitle.setLongClickable(false);

        if (currentTitleS != null) {
            currentTitleS.setText(songData.trackName);
            currentTitleS.setSelected(true);
            currentTitleS.setMovementMethod(new MyTitleScrollingMovementMethod());
            currentTitleS.setClickable(false);
            currentTitleS.setLongClickable(false);
        }

        queueNextRefresh(100);

        updatePauseButtonImageTrackUpdate();
    }

    private void queueNextRefresh(long delay) {
        Message msg = handler.obtainMessage(MSG_REFRESH);
        handler.removeMessages(MSG_REFRESH);
        handler.sendMessageDelayed(msg, delay);
    }

    private long refreshNow() {
        final long trackPosition = onRequestTrackPosition.invoke((long) -1);
        Tuple2<Boolean /*isPlaying*/, Boolean /*wantsPlaying*/> playState = onRequestPlaystate.invoke(new Tuple2<>(false, false));

        if (trackPosition < 0)
            return 500;

        try {
            long pos = posOverride < 0 ? trackPosition : posOverride;
            if ((pos >= 0) && (duration >= 0)) {
                currentTime.setText(Utils.getDurationStringHMSS((int) (pos / 1000)));
                int progress = (int) (1000 * pos / duration);
                this.progress.setProgress(progress);
                if (progressS != null) progressS.setProgress(progress);

                if (playState.obj1) {//isPlaying
                    currentTime.setVisibility(View.VISIBLE);
                } else {
                    int vis = currentTime.getVisibility();
                    currentTime.setVisibility(vis == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
                    return 500;
                }
            } else {
                currentTime.setText("--:--");
                progress.setProgress(1000);
                if (progressS != null) progressS.setProgress(1000);
            }
            // calculate the number of milliseconds until the next full second, so
            // the counter can be updated at just the right time
            long remaining = 1000 - (pos % 1000);

            // approximate how often we would need to refresh the slider to
            // move it smoothly
            int width = progress.getWidth();
            if (width == 0) width = 320;
            long smoothrefreshtime = duration / width;

            if (smoothrefreshtime > remaining) return remaining;
            if (smoothrefreshtime < 20) return 20;
            return smoothrefreshtime;
        } catch (Exception ignored) {
        }

        return 500;
    }

    public void animateShow(int showLevel) {
        if (!isViewCreated()) return;

        if (showLevel == 1 && viewRootS == null)
            showLevel = 2;

        if (lastShowLevel != showLevel) {
            VolumePopupWindow popup = volumePopupWindowSingleton.get();
            if (popup != null) {
                UtilsUI.dismissSafe(popup);
                volumePopupWindowSingleton.clear();
            }
            ThreeDotPopupWindow popup3 = ctrlOverflowPopupWindowSingleton.get();
            if (popup3 != null) {
                UtilsUI.dismissSafe(popup3);
                ctrlOverflowPopupWindowSingleton.clear();
            }

            ListPopupWindow popup2 = overflowPopupWindowSingleton.get();
            UtilsUI.dismissSafe(popup2);
        }
        lastShowLevel = showLevel;

        {
            View view1 = viewRootL;
            int shortAnimTime = view1.getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
            float bgHeight = viewRootBg.getResources().getDimension(R.dimen.player_controls_height);

            if (showLevel == 0) {

                viewRootBg.animate()
                        .translationY((viewRootBg.getHeight()))
                        .setDuration(shortAnimTime);

                view1.animate().cancel();
                view1.animate().withEndAction(viewRootLHide)
                        .translationY(bgHeight).alpha(0.0f)
                        .setDuration(shortAnimTime);

                if (viewRootS != null) {
                    viewRootS.animate().cancel();
                    viewRootS.animate().withEndAction(viewRootSHide)
                            .translationY(designHeight0).alpha(0.0f)
                            .setDuration(shortAnimTime);
                }


            } else if (showLevel == 1) {

                viewRootBg.animate()
                        .translationY(bgHeight - designHeight0)
                        .setDuration(shortAnimTime);

                view1.animate().cancel();
                view1.animate().withEndAction(viewRootLHide);
                view1.animate()
                        .translationY(bgHeight).alpha(0.0f)
                        .setDuration(shortAnimTime);

                if (viewRootS != null) {
                    viewRootS.animate().cancel();
                    viewRootS.animate().withEndAction(null);
                    viewRootS.setVisibility(View.VISIBLE);
                    viewRootS.animate()
                            .translationY(0).alpha(1.0f)
                            .setDuration(shortAnimTime);
                }


            } else if (showLevel == 2) {

                viewRootBg.animate()
                        .translationY(0)
                        .setDuration(shortAnimTime);

                view1.animate().cancel();
                view1.setVisibility(View.VISIBLE);
                view1.animate().withEndAction(null);
                view1.animate()
                        .translationY(0).alpha(1.0f)
                        .setDuration(shortAnimTime);

                if (viewRootS != null) {
                    viewRootS.animate().cancel();
                    viewRootS.animate().withEndAction(viewRootSHide);
                    viewRootS.animate()
                            .translationY(-designHeight0).alpha(0.0f)
                            .setDuration(shortAnimTime);
                }
            }
        }
    }

    public void onVolumeMuteChanged(boolean volumeMuteState) {
        if (!isViewCreated()) return;

        boolean audioEffectsActiveState = onRequestAudioEffectsActiveState.invoke(false);

        onVolumeMuteChanged(volumeMuteState, audioEffectsActiveState);
    }

    public void onAudioEffectsActiveChanged(boolean state) {
        if (!isViewCreated()) return;

        boolean muteState = onRequestVolumeMuteState.invoke(false);

        onVolumeMuteChanged(muteState, state);
    }

    private void onVolumeMuteChanged(boolean volumeMuteState, boolean audioEffectsActiveState) {
        VolumePopupWindow popup3 = volumePopupWindowSingleton.get();

        if (popup3 != null && popup3.isShowing())
            popup3.onVolumeMuteChanged(volumeMuteState, audioEffectsActiveState);

        updateButtonVolume(volumeMuteState, audioEffectsActiveState);
    }

    public void onRepeatModeChanged(int repeatMode) {
        ThreeDotPopupWindow popup3 = ctrlOverflowPopupWindowSingleton.get();

        if (popup3 != null && popup3.isShowing())
            popup3.onRepeatModeChanged(repeatMode);
    }

    public void onShuffleModeChanged(int shuffleMode) {
        ThreeDotPopupWindow popup3 = ctrlOverflowPopupWindowSingleton.get();

        if (popup3 != null && popup3.isShowing())
            popup3.onShuffleModeChanged(shuffleMode);
    }

    public void onMusicSysChanged(int musicSysIndex) {
        ThreeDotPopupWindow popup3 = ctrlOverflowPopupWindowSingleton.get();

        if (popup3 != null && popup3.isShowing())
            popup3.onMusicSysChanged(musicSysIndex);
    }

    public void onEqStateChanged(boolean eqState) {
        VolumePopupWindow popup3 = volumePopupWindowSingleton.get();

        if (popup3 != null && popup3.isShowing())
            popup3.onEqStateChanged(eqState);
    }
}
