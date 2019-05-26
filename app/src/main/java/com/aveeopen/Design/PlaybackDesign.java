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

package com.aveeopen.Design;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.Common.UtilsUI;
import com.aveeopen.MainActivity;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.MediaControlsUI.MediaControlsUI;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.comp.AlbumArt.AlbumArtCore;
import com.aveeopen.comp.AlbumArt.AlbumArtRequest;
import com.aveeopen.comp.AlbumArt.ImageLoadedListener;
import com.aveeopen.comp.Common.IItemIdentifier;
import com.aveeopen.comp.playback.EventsPlaybackService;
import com.aveeopen.comp.playback.MediaPlaybackService;
import com.aveeopen.comp.playback.MediaPlaybackServiceDefs;
import com.aveeopen.comp.playback.PlayingMediaInfo;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.comp.PlaybackQueue.QueueCore;
import com.aveeopen.EventsGlobal.EventsGlobalTextNotifier;

import java.util.LinkedList;
import java.util.List;

public class PlaybackDesign {

    static volatile boolean isPlaying;
    static volatile boolean wantsPlaying;
    static volatile PlaylistSong currentDisplayTrack = PlaylistSong.EmptySong;//should never be null, but PlaylistSong.EmptySong;
    static volatile PlaylistSong.Data songDisplyData = PlaylistSong.emptyData;
    static volatile PlayingMediaInfo playingMediaInfo = PlayingMediaInfo.empty;
    static volatile long trackPosition = 0;
    static volatile int repeatMode = 0;
    static volatile int volumeMax = 10;
    static volatile boolean volumeMuted = false;
    static volatile boolean timeoutEnabled = true;

    private List<Object> listenerRefHolder = new LinkedList<>();
    private Handler threadHandler = new Handler();

    public PlaybackDesign() {

        MediaPlaybackService.onServiceDestroying.subscribeWeak(new WeakEvent1.Handler<Context>() {
            @Override
            public void invoke(final Context context) {

                UtilsUI.AssertIsUiThread();

                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    playbackQueue.onDataSaveTime(context);

            }
        }, listenerRefHolder);

        MediaPlaybackService.onRequestTimeoutEnabled.subscribeWeak(new WeakEventR.Handler<Boolean>() {
            @Override
            public Boolean invoke() {
                return timeoutEnabled;
            }
        }, listenerRefHolder);
        MediaPlaybackService.onRequestSelectMediaPlayerCoreIndex.subscribeWeak(new WeakEventR.Handler<Integer>() {
            @Override
            public Integer invoke() {
                return AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_playbackEngine);
            }
        }, listenerRefHolder);
        MediaPlaybackService.onPlayStateChanged.subscribeWeak(new WeakEvent4.Handler<Boolean, Boolean, Integer, String>() {
            @Override
            public void invoke(final Boolean isPlaying, final Boolean wantsPlaying, Integer bufferingPercent, final String errorMsg) {

                PlaybackDesign.isPlaying = isPlaying;
                PlaybackDesign.wantsPlaying = wantsPlaying;

                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (errorMsg != null && errorMsg.length() > 0) {
                            EventsGlobalTextNotifier.onTextMsg.invoke(errorMsg);
                        }

                        MediaControlsUI mediaControlsUI = MediaControlsUI.getInstance();
                        if (mediaControlsUI != null)
                            mediaControlsUI.updatePlaystate(isPlaying, wantsPlaying);

                        MainActivity mainActivity = MainActivity.getInstance();
                        if (mainActivity != null) {
                            mainActivity.setScreenLock(mainActivity.currentFragmentPage == MainActivity.VISUAL_PAGE_INDEX  && isPlaying);
                        }
                    }
                });
            }
        }, listenerRefHolder);
        MediaPlaybackService.onDisplayMetaDataStateChanged.subscribeWeak(new WeakEvent4.Handler<PlaylistSong, IItemIdentifier, PlaylistSong.Data, PlayingMediaInfo>() {
            @Override
            public void invoke(PlaylistSong currentTrack, IItemIdentifier currentItemIdent, final PlaylistSong.Data currentTrackData, PlayingMediaInfo playingMediaInfo) {

                PlaybackDesign.currentDisplayTrack = currentTrack;
                PlaybackDesign.songDisplyData = currentTrackData;
                PlaybackDesign.playingMediaInfo = playingMediaInfo;

                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity mainActivity = MainActivity.getInstance();
                        if (mainActivity != null)
                            mainActivity.updateActionBar(currentTrackData);

                        MediaControlsUI mediaControlsUI = MediaControlsUI.getInstance();
                        if (mediaControlsUI != null)
                            mediaControlsUI.updateTrackInfo(PlaybackDesign.songDisplyData, PlaybackDesign.playingMediaInfo);
                    }
                });

            }
        }, listenerRefHolder);
        MediaPlaybackService.onTrackPositionChanged.subscribeWeak(new WeakEvent1.Handler<Long>() {
            @Override
            public void invoke(Long position) {
                PlaybackDesign.trackPosition = position;
            }
        }, listenerRefHolder);
        MediaPlaybackService.onRepeatModeChanged.subscribeWeak(new WeakEvent1.Handler<Integer>() {
            @Override
            public void invoke(Integer rptMode) {
                PlaybackDesign.repeatMode = rptMode;

                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        MediaControlsUI mediaControlsUI = MediaControlsUI.getInstance();
                        if (mediaControlsUI != null)
                            mediaControlsUI.onRepeatModeChanged(PlaybackDesign.repeatMode);

                    }
                });
            }
        }, listenerRefHolder);
        MediaPlaybackService.onPlaybackCompleted.subscribeWeak(new WeakEvent3.Handler<Integer, Boolean, Long>() {
            @Override
            public void invoke(final Integer rptMode, Boolean wntsPlaying, final Long atTime) {

                if (!wntsPlaying) return;

                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (rptMode == MediaPlaybackServiceDefs.REPEAT_CURRENT) {
                            QueueCore playbackQueue = QueueCore.createOrGetInstance();
                            if (playbackQueue != null)
                                playbackQueue.playCurrent(atTime);

                        } else if (rptMode == MediaPlaybackServiceDefs.REPEAT_ALL) {
                            QueueCore playbackQueue = QueueCore.createOrGetInstance();
                            if (playbackQueue != null) {
                                if (playbackQueue.isNextPlaylistEnd())
                                    playbackQueue.playFirst(atTime);
                                else
                                    playbackQueue.next(atTime);
                            }
                        } else {
                            QueueCore playbackQueue = QueueCore.createOrGetInstance();
                            if (playbackQueue != null) {
                                playbackQueue.next(atTime);//tests playlistEnd internally
                            }
                        }
                    }
                });

            }
        }, listenerRefHolder);

        MediaPlaybackService.onDestroyed.subscribeWeak(new WeakEvent1.Handler<Context>() {
            @Override
            public void invoke(Context context) {
                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        MediaControlsUI mediaControlsUI = MediaControlsUI.getInstance();
                        if (mediaControlsUI != null)
                            mediaControlsUI.updatePlaystate(false, false);

                    }
                });
            }
        }, listenerRefHolder);

        MediaPlaybackService.onVolumeMaxChanged.subscribeWeak(new WeakEvent1.Handler<Integer>() {
            @Override
            public void invoke(Integer volumeMax) {
                PlaybackDesign.volumeMax = volumeMax;
            }
        }, listenerRefHolder);
        MediaPlaybackService.onVolumeMuteChanged.subscribeWeak(new WeakEvent1.Handler<Boolean>() {
            @Override
            public void invoke(Boolean muted) {
                PlaybackDesign.volumeMuted = muted;
            }
        }, listenerRefHolder);
        MediaPlaybackService.onRequestVolumeMuteState.subscribeWeak(new WeakEventR.Handler<Boolean>() {
            @Override
            public Boolean invoke() {
                return AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_audioMuteState);
            }
        }, listenerRefHolder);
        MediaPlaybackService.onRequestVolumeStereoBalance.subscribeWeak(new WeakEventR.Handler<Float>() {
            @Override
            public Float invoke() {
                return AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_volumeStereoBalance) * 0.01f;
            }
        }, listenerRefHolder);

        MediaPlaybackService.onRequestCrossfadeValue.subscribeWeak(new WeakEventR.Handler<Float>() {
            @Override
            public Float invoke() {
                return AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_crossfadeValue) * 0.001f;
            }
        }, listenerRefHolder);
        MediaPlaybackService.onHeadsetAssistAction.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {

                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        QueueCore playbackQueue = QueueCore.createOrGetInstance();
                        if (playbackQueue != null) {
                            if (playbackQueue.getShuffleMode() == MediaPlaybackServiceDefs.SHUFFLE_NONE)
                                playbackQueue.setShuffleMode(MediaPlaybackServiceDefs.SHUFFLE_NORMAL, true);
                            else
                                playbackQueue.setShuffleMode(MediaPlaybackServiceDefs.SHUFFLE_NONE, true);
                        }
                    }
                });

            }
        }, listenerRefHolder);

        MediaPlaybackService.onUIPrevAction.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        QueueCore.createOrGetInstance().prev();
                    }
                });
            }
        }, listenerRefHolder);

        MediaPlaybackService.onUINextAction.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {

                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        QueueCore.createOrGetInstance().nextOrFirst();
                    }
                });
            }
        }, listenerRefHolder);

        MediaPlaybackService.onRequestAlbumArtLarge.subscribeWeak(new WeakEvent4.Handler<AlbumArtRequest, ImageLoadedListener, Integer, Integer>() {
            @Override
            public void invoke(AlbumArtRequest albumArtRequest, ImageLoadedListener imageLoadedListener, Integer targetW, Integer targetH) {
                AlbumArtCore albumArtCore = AlbumArtCore.getInstance();
                if(albumArtCore != null)
                    albumArtCore.loadAlbumArtLarge(
                            albumArtRequest.videoThumbDataSource,
                            albumArtRequest.path0,
                            albumArtRequest.path1,
                            albumArtRequest.genStr,
                            imageLoadedListener,
                            targetW,
                            targetH);
            }
        }, listenerRefHolder);

        MediaControlsUI.onSelectMusicSysAction.subscribeWeak(new WeakEvent1.Handler<Integer>() {
            @Override
            public void invoke(Integer index) {
                if (index == 0)
                    AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_playbackEngine, 0);
                else if (index == 1)
                    AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_playbackEngine, 1);
            }
        }, listenerRefHolder);

        MediaControlsUI.onToggleMuteAction.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                AppPreferences.createOrGetInstance().toggleBool(AppPreferences.PREF_Bool_audioMuteState);
            }
        }, listenerRefHolder);

        MediaControlsUI.onRequestMusicSystemIndex.subscribeWeak(new WeakEventR.Handler<Integer>() {
            @Override
            public Integer invoke() {
                return AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_playbackEngine);
            }
        }, listenerRefHolder);

        MediaControlsUI.onRequestVolumeMuteState.subscribeWeak(new WeakEventR.Handler<Boolean>() {
            @Override
            public Boolean invoke() {
                return AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_audioMuteState);
            }
        }, listenerRefHolder);

        MediaControlsUI.onRequestAudioEffectsActiveState.subscribeWeak(new WeakEventR.Handler<Boolean>() {
            @Override
            public Boolean invoke() {
                return requestAudioEffectsActiveState();
            }
        }, listenerRefHolder);

        MediaControlsUI.onRequestAudioVolumeState.subscribeWeak(new WeakEventR.Handler<Tuple2<Integer, Integer>>() {
            @Override
            public Tuple2<Integer, Integer> invoke() {
                int volume = EventsPlaybackService.Receive.getVolume.invoke(0);

                return new Tuple2<>(volume, volumeMax);
            }
        }, listenerRefHolder);

        MediaControlsUI.onRequestAudioBalanceState.subscribeWeak(new WeakEventR.Handler<Tuple2<Integer, Integer>>() {
            @Override
            public Tuple2<Integer, Integer> invoke() {
                int val = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_volumeStereoBalance) / 20;
                return new Tuple2<>(val + 5, 10);//0 ..5 ..10
            }
        }, listenerRefHolder);

        MediaControlsUI.onRequestAudioEffectVirtualizerState.subscribeWeak(new WeakEventR.Handler<Tuple2<Integer, Integer>>() {
            @Override
            public Tuple2<Integer, Integer> invoke() {
                return null;
            }
        }, listenerRefHolder);

        MediaControlsUI.onRequestCrossFadeState.subscribeWeak(new WeakEventR.Handler<Tuple2<Integer, Integer>>() {
            @Override
            public Tuple2<Integer, Integer> invoke() {
                int val = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_crossfadeValue);
                return new Tuple2<>((val / 1000) + 1, 10);
            }
        }, listenerRefHolder);

        MediaControlsUI.onSetAudioVolume.subscribeWeak(new WeakEvent2.Handler<Integer, Integer>() {
            @Override
            public void invoke(final Integer val, Integer valMax) {
                EventsPlaybackService.Receive.setVolume.invoke(val);
            }
        }, listenerRefHolder);

        MediaControlsUI.onSetAudioStereoBalance.subscribeWeak(new WeakEvent2.Handler<Integer, Integer>() {
            @Override
            public void invoke(Integer val, Integer valMax) {
                AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_volumeStereoBalance, (val - 5) * 20);//0 ..5 ..10
            }
        }, listenerRefHolder);

        MediaControlsUI.onSetCrossFade.subscribeWeak(new WeakEvent2.Handler<Integer, Integer>() {
            @Override
            public void invoke(Integer val, Integer valMax) {
                AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_crossfadeValue, (val - 1) * 1000);
            }
        }, listenerRefHolder);

        MediaControlsUI.onRequestAudioViewExpandedState.subscribeWeak(new WeakEventR.Handler<Boolean>() {
            @Override
            public Boolean invoke() {
                return AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_uiSectionOpened2);
            }
        }, listenerRefHolder);

        MediaControlsUI.onSetAudioViewExpandedState.subscribeWeak(new WeakEvent1.Handler<Boolean>() {
            @Override
            public void invoke(Boolean state) {
                AppPreferences.createOrGetInstance().setBool(AppPreferences.PREF_Bool_uiSectionOpened2, state);
            }
        }, listenerRefHolder);

        MediaControlsUI.onRequestRepeatMode.subscribeWeak(new WeakEventR.Handler<Integer>() {
            @Override
            public Integer invoke() {
                return repeatMode;
            }
        }, listenerRefHolder);

        MediaControlsUI.onSetRepeatMode.subscribeWeak(new WeakEvent1.Handler<Integer>() {
            @Override
            public void invoke(final Integer repeatMode) {
                EventsPlaybackService.Receive.onRepeatModeChange.invoke(repeatMode);
            }
        }, listenerRefHolder);

        MediaControlsUI.onRequestTrackInfo.subscribeWeak(new WeakEventR.Handler<Tuple2<PlaylistSong.Data, PlayingMediaInfo>>() {
            @Override
            public Tuple2<PlaylistSong.Data, PlayingMediaInfo> invoke() {
                return new Tuple2<>(songDisplyData, playingMediaInfo);
            }
        }, listenerRefHolder);

        MediaControlsUI.onRequestPlaystate.subscribeWeak(new WeakEventR.Handler<Tuple2<Boolean, Boolean>>() {
            @Override
            public Tuple2<Boolean, Boolean> invoke() {
                return new Tuple2<>(isPlaying, wantsPlaying);
            }
        }, listenerRefHolder);

        MainActivity.onExit.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                EventsPlaybackService.Receive.onRequestCloseService.invoke();
            }
        }, listenerRefHolder);

        MainActivity.onStart.subscribeWeak(new WeakEvent1.Handler<Context>() {
            @Override
            public void invoke(Context context) {
                timeoutEnabled = false;
                EventsPlaybackService.Receive.onRequestStateRefresh.invoke();
                EventsPlaybackService.Receive.onTimeoutChange.invoke(false);
            }
        }, listenerRefHolder);

        MainActivity.onStop.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                timeoutEnabled = true;
                EventsPlaybackService.Receive.onTimeoutChange.invoke(true);
            }
        }, listenerRefHolder);

        MainActivity.onViewPagerPageSelected.subscribeWeak(new WeakEvent2.Handler<Integer, Activity>() {
            @Override
            public void invoke(Integer page, Activity activity) {
                MainActivity mainActivity = MainActivity.getInstance();
                if (mainActivity != null) {
                    mainActivity.setScreenLock(page == MainActivity.VISUAL_PAGE_INDEX  && isPlaying);
                }
            }
        }, listenerRefHolder);
        AppPreferences.onIntPreferenceChanged.subscribeWeak(new WeakEvent3.Handler<Integer, Integer, Boolean>() {
            @Override
            public void invoke(Integer preference, final Integer value, Boolean userForce) {
                if (preference == AppPreferences.PREF_Int_playbackEngine) {

                    EventsPlaybackService.Receive.PlaybackControls_selectMediaPlayerCoreIndex.invoke(value);

                    MediaControlsUI mediaControlsUI = MediaControlsUI.getInstance();
                    if (mediaControlsUI != null)
                        mediaControlsUI.onMusicSysChanged(value);

                } else if (preference == AppPreferences.PREF_Int_volumeStereoBalance) {

                    MediaControlsUI mediaControlsUI = MediaControlsUI.getInstance();
                    if (mediaControlsUI != null)
                        mediaControlsUI.onAudioEffectsActiveChanged(requestAudioEffectsActiveState());

                    EventsPlaybackService.Receive.setVolumeStereoBalance.invoke(((float)value) * 0.01f);

                } else if (preference == AppPreferences.PREF_Int_crossfadeValue) {
                    EventsPlaybackService.Receive.setCrossfadeValue.invoke(((float)value) * 0.001f);
                }
            }
        }, listenerRefHolder);

        AppPreferences.onBoolPreferenceChanged.subscribeWeak(new WeakEvent2.Handler<Integer, Boolean>() {
            @Override
            public void invoke(Integer preference, final Boolean value) {
                if (preference == AppPreferences.PREF_Bool_audioMuteState) {

                    MediaControlsUI mediaControlsUI = MediaControlsUI.getInstance();
                    if (mediaControlsUI != null)
                        mediaControlsUI.onVolumeMuteChanged(value);

                    EventsPlaybackService.Receive.setVolumeMute.invoke(value);
                }
            }
        }, listenerRefHolder);

    }

    private boolean requestAudioEffectsActiveState() {
        int val1 = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_volumeStereoBalance);
        return (val1 != 0);
    }

}
