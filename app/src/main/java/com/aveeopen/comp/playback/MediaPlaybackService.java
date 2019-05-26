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

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.view.SurfaceHolder;

import com.AOSP.MyIntentService;
import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.Events.WeakEventR1;
import com.aveeopen.Common.MediaStoreUtils;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.Common.tlog;
import com.aveeopen.PlayerCore;
import com.aveeopen.comp.AlbumArt.AlbumArtRequest;
import com.aveeopen.comp.AlbumArt.ImageLoadedListener;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.Common.IItemIdentifier;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.comp.playback.ExoMediaPlayer.ExoMediaPlayerCore;
import com.aveeopen.comp.playback.NativeMediaPlayer.NativeMediaPlayerCore;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.comp.playback.view.MediaAppWidgetProvider;
import com.aveeopen.comp.playback.view.MediaPlaybackNotification;
import com.aveeopen.comp.PlaybackQueue.QueueCore;
import com.aveeopen.comp.PlaybackQueue.QueueItemIdentifier;
import com.aveeopen.EventsGlobal.EventsGlobalNotificationUI;
import com.aveeopen.EventsGlobal.EventsGlobalTextNotifier;
import com.aveeopen.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class MediaPlaybackService extends MyIntentService implements
        MediaPlaybackServiceDefs,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnErrorListener,
        AudioManager.OnAudioFocusChangeListener {

    public static WeakEvent1<Context /*context*/> onCreateEarly = new WeakEvent1<>();
    public static WeakEvent1<Context /*context*/> onServiceDestroying = new WeakEvent1<>();
    public static WeakEvent1<Context /*context*/> onDestroyed = new WeakEvent1<>();
    public static WeakEventR<Integer> onRequestSelectMediaPlayerCoreIndex = new WeakEventR<>();
    public static WeakEventR<Boolean> onRequestTimeoutEnabled = new WeakEventR<>();
    public static WeakEvent4<Boolean /*isPlaying*/, Boolean /*wantsPlaying*/, Integer /*bufferingPercent*/, String /*errorMsg*/> onPlayStateChanged = new WeakEvent4<>();
    public static WeakEvent4<PlaylistSong /*currentTrack*/, IItemIdentifier /*currentItemIdent*/, PlaylistSong.Data /*currentTrackData*/, PlayingMediaInfo /*playingMediaInfo*/> onDisplayMetaDataStateChanged = new WeakEvent4<>();
    public static WeakEvent1<Integer /*repeatMode*/> onRepeatModeChanged = new WeakEvent1<>();
    public static WeakEvent1<Integer /*volumeMax*/> onVolumeMaxChanged = new WeakEvent1<>();
    public static WeakEvent1<Boolean /*muted*/> onVolumeMuteChanged = new WeakEvent1<>();
    public static WeakEventR<Boolean> onRequestVolumeMuteState = new WeakEventR<>();
    public static WeakEventR<Float> onRequestVolumeStereoBalance = new WeakEventR<>();
    public static WeakEventR<Float> onRequestCrossfadeValue = new WeakEventR<>();
    public static WeakEvent onHeadsetAssistAction = new WeakEvent();
    public static WeakEvent3<Integer /*repeatMode*/, Boolean /*wantsPlaying*/, Long /*atTime*/> onPlaybackCompleted = new WeakEvent3<>();
    public static WeakEvent onUINextAction = new WeakEvent();
    public static WeakEvent onUIPrevAction = new WeakEvent();
    public static WeakEvent1<Long /*position*/> onTrackPositionChanged = new WeakEvent1<>();
    public static WeakEvent3<Integer /*width*/, Integer /*height*/, Float /*widthHeightRatio*/> onVideoSizeChanged = new WeakEvent3<>();
    public static WeakEventR<SurfaceHolder> onRequestVideoSurfaceHolder = new WeakEventR<>();
    public static WeakEventR<Integer> onRequestVideoScalingMode = new WeakEventR<>();
    public static WeakEventR1<String /*eqName*/, BaseEqualizerEffect.EqualizerSettings> onRequestEqualizerSettings = new WeakEventR1<>();
    public static WeakEventR1<String /*eqName*/, Boolean> onRequestEqualizerIsEnabled = new WeakEventR1<>();
    public static WeakEvent1<BaseEqualizerEffect.EqualizerDesc /*equalizerDesc*/> onEqualizerDescChanged = new WeakEvent1<>();
    public static WeakEvent4<AlbumArtRequest /*artRequest*/, ImageLoadedListener /*listener*/, Integer /*targetW*/, Integer /*targetH*/> onRequestAlbumArtLarge = new WeakEvent4<>();

    private static final int MEDIA_PLAYBACK_NOTIFICATION_ID = 1;
    private static volatile WeakReference<MediaPlaybackService> instanceWeak = new WeakReference<>(null);
    private final boolean taskInitializeDone[] = new boolean[1];
    private final boolean taskDeInitializeDone[] = new boolean[1];
    private boolean serviceValidState = false;
    private int lastestStartId = -1;
    private MediaEventReceiver mediaEventReceiver;
    private ComponentName mediaButtonEventReceiverName;
    private RemoteControlClient remoteControlClient;
    private ImageLoadedListener imageLoadedListener;//must hold reference
    private boolean openedSongIsPreview = false;
    private Tuple2<PlaylistSong, IItemIdentifier> openedEntry = null;
    private boolean haveAudioFocus = false;
    private boolean lostAudioFocusWhilePlaying = false;
    private boolean bufferingState = false;
    private int bufferingPercent = -1;
    private boolean wantsPlay = false;
    private RunnableNextDataSource runnableNextDataSource = new RunnableNextDataSource(0);
    private int myRunnableNextDataSourceToken = 4;
    private boolean serviceActiveState;
    private boolean timeoutEnabled = true;
    private FadeMediaPlayer fadePlayer;
    private int repeatMode = -1;//uninitialized so setRepeat works
    private int cardId;
    private AudioManager audioManager;
    private BaseEqualizerEffect.EqualizerDesc lastEqualizerDesc = null;

    private PlayerCore playerCoreReference = null;
    private Object playbackQueueRefHolder;

    String notificationChannelId = null;

    Runnable idleTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            MediaPlaybackService.this.stopService();
        }
    };
    Runnable positionRefresh = new Runnable() {
        @Override
        public void run() {
            long pos = MediaPlaybackService.this.positionSafe();
            onTrackPositionChanged.invoke(pos);
            queuePositionRefresh(100);
        }
    };

    IMediaPlayerCore.OnNotifyListener onNotifyListener = new IMediaPlayerCore.OnNotifyListener() {

        @Override
        public void requestNextDataDelay() {
            MediaPlaybackService.this.mpRequestNextDataDelay();
        }

        @Override
        public void requestNextDataNow() {
            MediaPlaybackService.this.mpRequestNextDataNow();
        }

        @Override
        public void requestNextDataAtTime(long atTime) {
            MediaPlaybackService.this.mpRequestNextDataAtTime(atTime);
        }

        @Override
        public boolean onRequestAudioFocus() {
            return MediaPlaybackService.this.requestAudioFocus();
        }

        @Override
        public void onVolumeMuteStateChanged(boolean state) {
            onVolumeMuteChanged.invoke(state);
        }

        @Override
        public void onMpPlaystateOrMetaChanged(boolean metaChanged, String errorMsg) {
            notifyPlaystateOrMetaChanged(metaChanged, errorMsg);
        }

        @Override
        public void onBufferingUpdate(boolean state, int percent) {
            notifyBufferingUpdate(state, percent);
        }

        @Override
        public void onNotifyVideoSizeChanged(final int width, final int height, final float widthHeightRatio) {
            onVideoSizeChanged.invoke(width, height, widthHeightRatio);
        }

        @Override
        public int getVideoScalingMode() {
            return onRequestVideoScalingMode.invoke(0);
        }

        @Override
        public SurfaceHolder onRequestVideoSurfaceHolder() {
            return onRequestVideoSurfaceHolder.invoke(null);
        }

        @Override
        public BaseEqualizerEffect.EqualizerSettings getEqualizerSettings(String name) {
            return onRequestEqualizerSettings.invoke(name, null);
        }

        @Override
        public boolean getEqualizerEnabled(String name) {
            return onRequestEqualizerIsEnabled.invoke(name, false);
        }

        @Override
        public void onEqualizerDescChanged(BaseEqualizerEffect.EqualizerDesc desc) {
            lastEqualizerDesc = desc;
            onEqualizerDescChanged.invoke(desc);
        }
    };

    final Runnable taskInitialize = new Runnable() {

        @Override
        public void run() {
            synchronized (this) {
                initialize();
                this.notifyAll();
                taskInitializeDone[0] = true;
            }
        }
    };
    final Runnable taskDeInitialize = new Runnable() {

        @Override
        public void run() {
            synchronized (this) {
                deInitialize();
                this.notifyAll();
                taskDeInitializeDone[0] = true;
            }
        }
    };
    public MediaPlaybackService() {
        super("MediaPlaybackService");
        setIntentRedelivery(false);

        fadePlayer = new FadeMediaPlayer(IMediaPlayerCore.Empty, onNotifyListener, 0L);
    }

    public static MediaPlaybackService getInstance() {
        return instanceWeak.get();
    }

    private void queuePositionRefresh(long delay) {
        mServiceHandler.removeCallbacks(positionRefresh);
        if (serviceValidState)
            mServiceHandler.postDelayed(positionRefresh, delay);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instanceWeak = new WeakReference<>(this);
        playerCoreReference = PlayerCore.s();

        taskInitializeDone[0] = false;
        mServiceHandler.post(taskInitialize);

        synchronized (taskInitialize) {
            if (!taskInitializeDone[0]) {
                try {
                    taskInitialize.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void initialize() {
        onCreateEarly.invoke(this);

        int selectedMediaPlayerCoreIndex = onRequestSelectMediaPlayerCoreIndex.invoke(-1);
        selectMediaPlayerCoreIndex(selectedMediaPlayerCoreIndex);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mediaEventReceiver = new MediaEventReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

        registerReceiver(mediaEventReceiver, filter);

        {
            //mediaButtonEventReceiverName = new ComponentName(getPackageName(), MediaEventReceiver.class.getName());
            mediaButtonEventReceiverName = new ComponentName(this, MediaButtonEventReceiver.class);
            //mediaButtonEventReceiverName = new ComponentName(this, MediaPlaybackService.class);

            try {
                //reported crash: java.lang.SecurityException: Must hold the MODIFY_PHONE_STATE permission.
                //but that permission is for system apps ...
                audioManager.registerMediaButtonEventReceiver(mediaButtonEventReceiverName);
            } catch (IllegalArgumentException e) {
                tlog.w("registerMediaButtonEventReceiver failed: "+e.getMessage());
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                // build the PendingIntent for the remote control client
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(mediaButtonEventReceiverName);
                PendingIntent mediaButtonPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0);
                //PendingIntent mediaButtonPendingIntent = PendingIntent.getService(this, 0, mediaButtonIntent, 0);
                // create and register the remote control client
                remoteControlClient = new RemoteControlClient(mediaButtonPendingIntent);
                audioManager.registerRemoteControlClient(remoteControlClient);

                int flags = RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
                        | RemoteControlClient.FLAG_KEY_MEDIA_NEXT
                        | RemoteControlClient.FLAG_KEY_MEDIA_PLAY
                        | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
                        | RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE
                        | RemoteControlClient.FLAG_KEY_MEDIA_STOP;
                remoteControlClient.setTransportControlFlags(flags);
            } catch(Exception e)
            {
                tlog.w("remoteControlClient failed: "+e.getMessage());
            }
        }

        onVolumeMaxChanged.invoke(getVolumeMax());

        cardId = getCardId(this);
        setRepeatMode(REPEAT_NONE, false);
        serviceValidState = true;
        reloadPreferences();
        //startForeground..
        updateServiceState();

        notifyPlaystateOrMetaChanged(true, null);
        onVolumeMuteChanged.invoke(getPlayer().isMuted());
        boolean timeoutEnabled = onRequestTimeoutEnabled.invoke(true);
        setTimeoutEnable(timeoutEnabled);

        playbackQueueRefHolder = QueueCore.createOrGetInstance();
    }

    @Override
    public void onDestroy() {
        serviceValidState = false;
        onServiceDestroying.invoke(this);
        taskDeInitializeDone[0] = false;
        mServiceHandler.post(taskDeInitialize);

        synchronized (taskDeInitialize) {
            if (!taskDeInitializeDone[0]) {
                try {
                    taskDeInitialize.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onDestroy();
        playerCoreReference = null;
    }

    void deInitialize() {
        serviceValidState = false;
        instanceWeak = new WeakReference<>(null);
        savePreferences();
        onDestroyed.invoke(this);


        this.abandonAudioFocus(true);

        if (remoteControlClient != null) {
            audioManager.unregisterRemoteControlClient(remoteControlClient);

            try {
                audioManager.unregisterMediaButtonEventReceiver(mediaButtonEventReceiverName);
            } catch (IllegalArgumentException e) {
                tlog.w("unregisterMediaButtonEventReceiver failed: "+e.getMessage());
            }
        }

        try {
            unregisterReceiver(mediaEventReceiver);
        } catch (IllegalArgumentException e) {
            // we haven't registered the receiver yet
        }

        fadePlayer.release();
        fadePlayer = new FadeMediaPlayer(IMediaPlayerCore.Empty, onNotifyListener, 0L);//avoiding null assignment

        MediaAppWidgetProvider.getInstance().notifyChange(this,
                getOpenedOrSupposedOrEmptySong().getData(),
                false,
                false,
                MediaPlaybackService.class);
    }

    public ServiceHandler getHandler() {
        return mServiceHandler;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        lastestStartId = startId;
        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = null;
        if (intent != null) action = intent.getAction();

        if (action != null) {
            //intents from notification bar / remote control / media buttons
            switch (action) {
                case SEEK_ACTION:
                    this.seek(intent.getLongExtra(EXTRA_ARG_1, 0));
                    break;
                case REPEAT_MODE_ACTION:
                    this.setRepeatMode(intent.getIntExtra(EXTRA_ARG_1, 0), true);
                    break;
                case VIDEO_SCALING_MODE_ACTION:
                    this.setVideoScalingMode(intent.getIntExtra(EXTRA_ARG_1, 0));
                    break;
                case VOLUME_PERCENTAGE_ACTION:
                    this.setVolumePercentage(intent.getFloatExtra(EXTRA_ARG_1, 20.0f));
                    break;
                case VOLUME_ACTION:
                    this.setVolume(intent.getIntExtra(EXTRA_ARG_1, 0));
                    break;
                case SET_MUTE_ACTION:
                    this.setVolumeMute(intent.getBooleanExtra(EXTRA_ARG_1, false));
                    break;
                case TOGGLE_MUTE_ACTION:
                    this.toggleVolumeMute();
                    break;
                case VOLUME_STEREO_BALANCE_ACTION:
                    this.setVolumeStereoBalance(intent.getFloatExtra(EXTRA_ARG_1, 0.0f));
                    break;
                case CROSS_FADE_VALUE_ACTION:
                    this.setCrossFadeValue(intent.getFloatExtra(EXTRA_ARG_1, -1.0f));
                    break;
                case PLAY_DATA_SOURCE_ACTION:
                    String dataSource = intent.getStringExtra(EXTRA_ARG_1);
                    PlaylistSong song = (dataSource != null) ? new PlaylistSong(-1, dataSource) : null;
                    boolean start = intent.getBooleanExtra(EXTRA_ARG_2, false);
                    long seekPos = intent.getLongExtra(EXTRA_ARG_3, 0);
                    long atTime = intent.getLongExtra(EXTRA_ARG_4, 0);

                    Tuple2<PlaylistSong, IItemIdentifier> tuple2 = (song != null) ?
                            new Tuple2<>(song, (IItemIdentifier) null) : null;
                    setWantsPlaying(start);
                    openAndPlaySongIndex(tuple2, seekPos, atTime);
                    break;
                case AUDIO_BECOMING_NOISY_ACTION:
                    pause();
                    break;
                case STOP_ACTION:
                    stop();
                    break;
                case PLAY_ACTION:
                    play();
                    break;
                case TOGGLE_PAUSE_ACTION:
                    togglePause();
                    break;
                case PAUSE_ACTION:
                    pause();
                    break;
                case PREVIOUS_ACTION:
                    onUIPrevAction.invoke();
                    break;
                case NEXT_ACTION:
                    onUINextAction.invoke();
                    break;
                case EXIT_ACTION:
                    stopService();
                    break;
                case ACTIVITY_AND_SERVICE_EXIT_ACTION:
                    EventsGlobalNotificationUI.onExitAction.invoke();
                    stopService();
                    break;
                case TIMEOUT_DISABLE_ACTION:
                    setTimeoutEnable(false);
                    break;
                case HEADSET_ASSIST_ACTION:
                    onHeadsetAssistAction.invoke();
                    break;
            }
        }

    }

    @Override
    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return true;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                boolean resumePlayingAfterAudioFocusGained =
                        AppPreferences.createOrGetInstance().preferencesGetBoolSafe(getApplicationContext(), "pref_resumePlayingAfterAudioFocusGained", true);

                if (lostAudioFocusWhilePlaying && resumePlayingAfterAudioFocusGained) {

                    if (getPlayer() != null) {

                        boolean fadePlayPause =
                                AppPreferences.createOrGetInstance().preferencesGetBoolSafe(getApplicationContext(), "pref_fadePlayPause", true);

                        if (fadePlayPause)
                            fadePlayer.startFadeAll();
                        else
                            getPlayer().start();
                    }

                    lostAudioFocusWhilePlaying = false;
                }

                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. ......playback is likely to resume

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player

                if (getPlayer() != null) {

                    boolean fadePlayPause =
                            AppPreferences.createOrGetInstance().preferencesGetBoolSafe(getApplicationContext(), "pref_fadePlayPause", true);

                    lostAudioFocusWhilePlaying = getPlayer().isPreparingOrStared();

                    if (fadePlayPause)
                        fadePlayer.pauseFadeAll();
                    else
                        getPlayer().pause();
                } else {
                    lostAudioFocusWhilePlaying = false;
                }

                updateServiceState();

                break;

        }
    }

    void mpRequestNextDataDelay() {
        mServiceHandler.removeCallbacksAndMessages(myRunnableNextDataSourceToken);
        mServiceHandler.postAtTime(runnableNextDataSource, myRunnableNextDataSourceToken, SystemClock.uptimeMillis() + 1000);
    }

    void mpRequestNextDataNow() {
        mServiceHandler.removeCallbacksAndMessages(myRunnableNextDataSourceToken);
        mServiceHandler.postAtTime(runnableNextDataSource, myRunnableNextDataSourceToken, 0);
    }

    void mpRequestNextDataAtTime(long atTime) {
        mServiceHandler.removeCallbacksAndMessages(myRunnableNextDataSourceToken);
        mServiceHandler.postAtTime(new RunnableNextDataSource(atTime), myRunnableNextDataSourceToken, 0);
    }

    public boolean requestAudioFocus() {
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            haveAudioFocus = true;
            return true;
        }

        haveAudioFocus = false;
        return false;
    }


    public void abandonAudioFocus(boolean calledFromDeinitialize) {
        if (!calledFromDeinitialize) {
            //RemoteControlClient cannot be playing when we abandon audio focus
            if (remoteControlClient != null)
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
        }
        haveAudioFocus = false;
        audioManager.abandonAudioFocus(this);
    }

    private void stopService() {
        if (getPlayer() != null) getPlayer().stop();
        stopSelf(lastestStartId);
    }

    public void stopServiceThSafe() {
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                stopService();
            }
        });
    }

    private void setTimeoutEnable(boolean timeoutEnabled) {
        this.timeoutEnabled = timeoutEnabled;
        updateServiceState();
    }

    public void setTimeoutEnableThSafe(final boolean timeoutEnabled) {
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                setTimeoutEnable(timeoutEnabled);
            }
        });
    }

    public void goIdleService() {
        tlog.notice("idle state");
        serviceActiveState = false;
        mServiceHandler.postDelayed(idleTimeoutRunnable
                , getResources().getInteger(R.integer.service_idle_timeout));

        stopForeground(false);
    }

    public void goActiveService() {
        tlog.notice("active state");

        serviceActiveState = true;
        mServiceHandler.removeCallbacks(idleTimeoutRunnable);
        PlaylistSong.Data songData = getOpenedOrSupposedOrEmptySong().getDataBlocking(this.getApplicationContext());

        if(notificationChannelId == null)
            notificationChannelId = MediaPlaybackNotification.createNotificationChannel(this);

        //must update notification, see MP_REQUEST_AUDIO_FOCUS message!
        startForeground(MEDIA_PLAYBACK_NOTIFICATION_ID, MediaPlaybackNotification.getOrCreateNotification(
                this,
                notificationChannelId,
                songData,
                isPlaying(),
                wantsPlaying(),
                MediaPlaybackService.class,
                0
        ));
    }

    private boolean updateServiceState() {
        boolean notificationUpdated = false;
        boolean resumePlayingAfterAudioFocusGained =
                AppPreferences.createOrGetInstance().preferencesGetBoolSafe(getApplicationContext(), "pref_resumePlayingAfterAudioFocusGained", true);
        boolean keepAliveForResumePlaying = lostAudioFocusWhilePlaying && resumePlayingAfterAudioFocusGained;

        if (!timeoutEnabled || (getPlayer() != null && getPlayer().isPreparingOrStared()) || keepAliveForResumePlaying) {
            //must update notification, see MP_REQUEST_AUDIO_FOCUS message!
            goActiveService();
            notificationUpdated = true;
        } else {
            abandonAudioFocus(false);
            goIdleService();
        }

        return notificationUpdated;
    }

    public void setRepeatMode(int repeatMode, boolean allowTextMessages) {
        if (this.repeatMode == repeatMode) return;
        this.repeatMode = repeatMode;

        if (allowTextMessages) {
            if (this.repeatMode == REPEAT_NONE)
                notifyMessage(getString(R.string.playback_repeat_off));
            else if (this.repeatMode == REPEAT_CURRENT)
                notifyMessage(getString(R.string.playback_repeat_current));
            else if (this.repeatMode == REPEAT_ALL)
                notifyMessage(getString(R.string.playback_repeat_all));
        }

        onRepeatModeChanged.invoke(this.repeatMode);
    }

    public int getRepeatMode() {
        return repeatMode;
    }

    public void previewOpen(List<PlaylistSong> list, int startPlayPosition) {
        previewOpenAndPlaySong(list.get(0), false);
    }

    public void openAndPlaySongIndex(Tuple2<PlaylistSong, IItemIdentifier> openEntry) {
        openAndPlaySongIndex(openEntry, 0, 0);
    }

    public void openAndPlaySongIndex(Tuple2<PlaylistSong, IItemIdentifier> openEntry, long seekPos) {
        openAndPlaySongIndex(openEntry, seekPos, 0);
    }

    public void openAndPlaySongIndex(Tuple2<PlaylistSong, IItemIdentifier> openEntry, long seekPos, long atTime) {
        queuePositionRefresh(100);
        lostAudioFocusWhilePlaying = false;
        mServiceHandler.removeCallbacksAndMessages(myRunnableNextDataSourceToken);

        openedSongIsPreview = false;
        openedEntry = openEntry;

        PlaylistSong song = getCurrentSong();
        if (song != null) {

            if (atTime <= 0) {
                fadePlayer.playNext(song.getMediaDataSource(), wantsPlay, seekPos);
            } else {

                fadePlayer.playNextAtTime(song.getMediaDataSource(), wantsPlay, seekPos, atTime);
            }

            //faster update mOpenedSongIndex to UI, so we don't wait for FadeMediaPlayer.onPrepared
            notifyPlaystateOrMetaChanged(true, null);
        } else {
            fadePlayer.stop();
        }
    }

    public void previewOpenAndPlaySong(PlaylistSong newSong, boolean openPaused) {
        openedSongIsPreview = true;
        openedEntry = new Tuple2<PlaylistSong, IItemIdentifier>(newSong, new QueueItemIdentifier(-1));

        PlaylistSong song = getCurrentSong();
        if (song != null) {
            fadePlayer.playNext(song.getMediaDataSource(), !openPaused, 0);

            //faster update mOpenedSongIndex to UI, so we don't wait for FadeMediaPlayer.onPrepared
            notifyPlaystateOrMetaChanged(true, null);
        } else {
            fadePlayer.stop();
        }
    }

    public int getSelectedMediaPlayerCoreIndex() {
        IMediaPlayerCore player = fadePlayer.getMediaPlayerCore();

        if (player instanceof NativeMediaPlayerCore) {
            return 0;
        }
        if (player instanceof ExoMediaPlayerCore) {
            return 1;
        }

        return -1;
    }

    void restartMediaPlayerCore() {
        selectMediaPlayerCoreIndex(getSelectedMediaPlayerCoreIndex(), true);
    }

    public void restartMediaPlayerCoreThSafe() {
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                restartMediaPlayerCore();
            }
        });
    }

    public void selectMediaPlayerCoreIndexThSafe(final int index) {
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                selectMediaPlayerCoreIndex(index);
            }
        });
    }

    private void selectMediaPlayerCoreIndex(int index) {
        selectMediaPlayerCoreIndex(index, false);
    }

    private void selectMediaPlayerCoreIndex(int index, boolean restart) {
        if (!restart) {
            int curretnIndex = getSelectedMediaPlayerCoreIndex();
            if (curretnIndex >= 0 && curretnIndex == index) return;
        }

        boolean muteState = onRequestVolumeMuteState.invoke(false);
        float balance = onRequestVolumeStereoBalance.invoke(0.0f);
        float crossFade = onRequestCrossfadeValue.invoke(-1.0f);

        long seekPos;

        seekPos = fadePlayer.getMediaPlayerCore().position();
        fadePlayer.release();

        if (index == 1) {
            IMediaPlayerCore player = new ExoMediaPlayerCore(this, this.getResources().getString(R.string.musicSys_exo), onNotifyListener);
            fadePlayer = new FadeMediaPlayer(player, onNotifyListener, (long) (crossFade * 1000));
        } else {
            IMediaPlayerCore player = new NativeMediaPlayerCore(this, this.getResources().getString(R.string.musicSys_native), onNotifyListener);
            fadePlayer = new FadeMediaPlayer(player, onNotifyListener, (long) (crossFade * 1000));
        }

        fadePlayer.getMediaPlayerCore().setMute(muteState);
        fadePlayer.getMediaPlayerCore().setVolumeStereoBalance(balance);

        openAndPlaySongIndex(openedEntry, seekPos);
    }

    private IMediaPlayerCore getPlayer() {
        return fadePlayer.getMediaPlayerCore();
    }

    public void play() {
        setWantsPlaying(true);
        queuePositionRefresh(100);
        lostAudioFocusWhilePlaying = false;

        if (!getPlayer().isPreparingOrAbove()) {
            //trying to restart
            openAndPlaySongIndex(openedEntry);
        }

        boolean fadePlayPause =
                AppPreferences.createOrGetInstance().preferencesGetBoolSafe(getApplicationContext(), "pref_fadePlayPause", true);

        if (fadePlayPause)
            fadePlayer.startFadeAll();
        else
            getPlayer().start();
    }

    public void pause() {
        setWantsPlaying(false);
        queuePositionRefresh(100);
        lostAudioFocusWhilePlaying = false;

        boolean fadePlayPause =
                AppPreferences.createOrGetInstance().preferencesGetBoolSafe(getApplicationContext(), "pref_fadePlayPause", true);

        if (fadePlayPause)
            fadePlayer.pauseFadeAll();
        else
            getPlayer().pause();
    }

    public void togglePause() {
        if (wantsPlaying())
            pause();
        else
            play();
    }

    public void stop() {
        setWantsPlaying(false);
        lostAudioFocusWhilePlaying = false;

        boolean fadePlayPause =
                AppPreferences.createOrGetInstance().preferencesGetBoolSafe(getApplicationContext(), "pref_fadePlayPause", true);

        if (fadePlayPause)
            fadePlayer.stopFadeAll();
        else
            fadePlayer.stop();
    }

    public boolean isPlaying() {
        return getPlayer().isPreparingOrStared();
    }

    public boolean wantsPlaying() {
        return wantsPlay;
    }

    private void setWantsPlaying(boolean state) {
        wantsPlay = state;

        final boolean isPlaying = isPlaying();
        final boolean wantsPlaying = wantsPlaying();
        onPlayStateChanged.invoke(isPlaying, wantsPlaying, bufferingPercent, null);
    }

    public PlaylistSong getCurrentSong() {
        if (openedEntry == null)
            return null;
        return openedEntry.obj1;
    }

    public PlaylistSong getOpenedSongOrEmpty() {
        PlaylistSong song = getCurrentSong();
        if (song == null)
            return PlaylistSong.EmptySong;
        return song;
    }

    public Tuple2<PlaylistSong, IItemIdentifier> getOpenedOrSupposedOrNullEntry() {
        if (openedEntry != null) {
            return openedEntry;
        }
        return null;
    }

    public PlaylistSong getOpenedOrSupposedOrEmptySong() {
        Tuple2<PlaylistSong, IItemIdentifier> playlistItem = getOpenedOrSupposedOrNullEntry();
        if (playlistItem != null)
            return playlistItem.obj1;

        return PlaylistSong.EmptySong;
    }

    public long duration() {
        IMediaPlayerCore mp = getPlayer();
        if (mp == null) return 0;
        return mp.duration();
    }

    public long position() {
        IMediaPlayerCore mp = getPlayer();
        if (mp == null) return 0;
        return mp.position();
    }

    public long positionSafe() {
        if (getPlayer() == null) return 0;
        return getPlayer().position();
    }

    public void seek(long pos) {
        lostAudioFocusWhilePlaying = false;
        if (getPlayer() == null) return;
        if (pos < 0) pos = 0;
        if (pos > getPlayer().duration()) pos = getPlayer().duration();
        getPlayer().seek(pos);
    }

    private void savePreferences() {
        SharedPreferences mPreferences = AppPreferences.createOrGetInstance().getPreferences(getApplicationContext());
        SharedPreferences.Editor ed = mPreferences.edit();

        ed.putInt("cardid", cardId);
        ed.putLong("seekpos", getPlayer().position());
        ed.putInt("repeatmode", repeatMode);

        String openedPath = "";
        if (openedEntry != null && openedEntry.obj1 != null)
            openedPath = openedEntry.obj1.getConstrucPath();
        ed.putString("openedPath", openedPath);

        ed.apply();
    }

    private void reloadPreferences() {
        SharedPreferences mPreferences = AppPreferences.createOrGetInstance().getPreferences(getApplicationContext());

        int repeatMode = AppPreferences.preferencesGetIntSafe(mPreferences, "repeatmode", REPEAT_NONE);
        if (repeatMode != REPEAT_ALL && repeatMode != REPEAT_CURRENT) {
            repeatMode = REPEAT_NONE;
        }
        setRepeatMode(repeatMode, false);

        long seekPos = mPreferences.getLong("seekpos", 0);

        String openedPath = AppPreferences.preferencesGetStringSafe(mPreferences, "openedPath", "");
        Tuple2<PlaylistSong, IItemIdentifier> entry = new Tuple2<PlaylistSong, IItemIdentifier>(new PlaylistSong(-1, openedPath), new QueueItemIdentifier());

        if (!openedSongIsPreview) { //don't replace current song if starting as preview
            setWantsPlaying(false);
            openAndPlaySongIndex(entry, seekPos);
        }
    }

    private void notifyPlaystateOrMetaChanged(boolean metaChanged, final String errorMsg) {
        boolean notificationUpdated = updateServiceState();

        PlaylistSong song = PlaylistSong.EmptySong;
        Tuple2<PlaylistSong, IItemIdentifier> openedPlaylistItem = getOpenedOrSupposedOrNullEntry();
        if (openedPlaylistItem != null)
            song = openedPlaylistItem.obj1;

        PlaylistSong.Data songData = null;
        if (!notificationUpdated) {

            songData = song.getDataBlocking(this.getApplicationContext());

            MediaPlaybackNotification.updateNotification(
                    MEDIA_PLAYBACK_NOTIFICATION_ID,
                    this,
                    notificationChannelId,
                    songData,
                    isPlaying(),
                    wantsPlaying(),
                    MediaPlaybackService.class,
                    0);
        }

        {
            if (songData == null)
                songData = song.getDataBlocking(this.getApplicationContext());

            MediaAppWidgetProvider.getInstance().notifyChange(this,
                    songData,
                    isPlaying(),
                    wantsPlaying(),
                    MediaPlaybackService.class);
        }

        {
            if (songData == null)
                songData = song.getDataBlocking(this.getApplicationContext());

            if (songData != PlaylistSong.emptyData) {

                Intent intent = new Intent("com.android.music.metachanged");
                intent.putExtra("playing", (boolean) isPlaying());
                intent.putExtra("track", (String) songData.trackName);
                intent.putExtra("album", (String) songData.albumName);
                intent.putExtra("artist", (String) songData.artistName);
                intent.putExtra("songid", (long) songData.audioId);
                intent.putExtra("albumid", (long) songData.albumId);

                intent.putExtra("id", (long) songData.albumId);

                sendBroadcast(intent);
            }
        }

        if (remoteControlClient != null) {

            remoteControlClient.setPlaybackState(isPlaying() ?
                    RemoteControlClient.PLAYSTATE_PLAYING : RemoteControlClient.PLAYSTATE_PAUSED);

            if (metaChanged) {
                if (songData == null)
                    songData = song.getDataBlocking(this.getApplicationContext());

                final RemoteControlClient.MetadataEditor ed = remoteControlClient.editMetadata(true);
                ed.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, songData.trackName);
                ed.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, songData.albumName);
                ed.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, songData.artistName);
                ed.putLong(MediaMetadataRetriever.METADATA_KEY_DURATION, duration());

                {
                    imageLoadedListener = new ImageLoadedListener() {
                        Object object1;

                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, String url00, String url0, String url1) {
                            // Create a copy of the cover art, since RemoteControlClient likes
                            // to recycle what we give it.
                            if (bitmap != null)
                                bitmap = bitmap.copy(Bitmap.Config.RGB_565, false);

                            ed.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, bitmap);
                        }

                        @Override
                        public void setUserObject1(Object obj1) {
                            object1 = obj1;
                        }
                    };

                    onRequestAlbumArtLarge.invoke(new AlbumArtRequest(songData.getVideoThumbDataSourceAsStr(),
                            songData.getAlbumArtPath0Str(),
                            songData.getAlbumArtPath1Str(),
                            songData.getAlbumArtGenerateStr()),
                            imageLoadedListener,
                            700, 700);
                }
                ed.apply();
            }

        }

        if (metaChanged) {
            if (songData == null)
                songData = song.getDataBlocking(this.getApplicationContext());

            IItemIdentifier currentItemIdent = openedPlaylistItem == null ? null : openedPlaylistItem.obj2;
            PlayingMediaInfo playingMediaInfo = new PlayingMediaInfo(duration(), getPlayer().containsVideoTrack());
            onDisplayMetaDataStateChanged.invoke(song, currentItemIdent, songData, playingMediaInfo);
        }

        final boolean isPlaying = isPlaying();
        final boolean wantsPlaying = wantsPlaying();
        onPlayStateChanged.invoke(isPlaying, wantsPlaying, bufferingPercent, errorMsg);
    }

    private void notifyBufferingUpdate(boolean state, int percent) {
        bufferingState = state;
        bufferingPercent = percent;
        if (!bufferingState) bufferingPercent = 101;
        onPlayStateChanged.invoke(isPlaying(), wantsPlaying(), bufferingPercent, null);
    }

    private void notifyMessage(final String msg) {
        EventsGlobalTextNotifier.onTextMsg.invoke(msg);
    }

    public void setVideoScalingMode(int mode) {
        fadePlayer.getMediaPlayerCore().setVideoScalingMode(mode);
        if (getSelectedMediaPlayerCoreIndex() == 1)///if Exo, we have to restart for scaling mode to take effect
            restartMediaPlayerCore();
    }

    private void setVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
        fadePlayer.getMediaPlayerCore().setVideoSurfaceHolder(surfaceHolder);
    }

    public void setVideoSurfaceHolderThSafe(final SurfaceHolder surfaceHolder) {
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                setVideoSurfaceHolder(surfaceHolder);
            }
        });
    }

    public float getVolumePercentage() {
        float max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / max;
    }

    public void setVolumePercentage(float value) {
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (max * value), 0);
    }

    public int getVolumeMax() {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public int getVolume() {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void setVolume(int value) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
        //alternative to control volume of app audio only, instead of globally
//        if(getPlayer() == null)
//            getPlayer().setVolume(_volume);
    }

    public void setVolumeMute(boolean muted) {
        if (getPlayer() != null)
            getPlayer().setMute(muted);
    }

    public void toggleVolumeMute() {
        if (getPlayer() != null)
            getPlayer().setMute(!getPlayer().isMuted());
    }

    public void setVolumeStereoBalance(float balance) {
        if (getPlayer() != null)
            getPlayer().setVolumeStereoBalance(balance);
    }

    public void setCrossFadeValue(float crossfade) {
        fadePlayer.setCrossFade((long) (crossfade * 1000));
    }

    private void resetVisualizer() {
        IMediaPlayerCore player = getPlayer();
        if (player != null)
            player.resetVisualizer();
    }

    public void resetVisualizerThSafe() {
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                resetVisualizer();
            }
        });
    }

    public AudioFrameData getVisualizationData(AudioFrameData outResult, boolean useGlobalSession) {
        IMediaPlayerCore player = getPlayer();
        if (player != null)
            return player.getVisualizationData(outResult, useGlobalSession);
        return null;
    }

    public BaseEqualizerEffect.EqualizerDesc getEqualizerDescThSafe()
    {
        //this should generate onEqualizerDescChanged event in future and caller already should be expecting result
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                IMediaPlayerCore player = getPlayer();
                if (player != null)
                    player.getEqualizerDesc();
            }
        });

        return lastEqualizerDesc;
    }

    public void setEqualizerSettingsThSafe(final BaseEqualizerEffect.EqualizerSettings equalizerSettings)
    {
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                setEqualizerSettings(equalizerSettings);
            }
        });
    }

    private void setEqualizerSettings(BaseEqualizerEffect.EqualizerSettings equalizerSettings) {
        IMediaPlayerCore player = getPlayer();
        if (player != null)
            player.setEqualizerSettings(equalizerSettings);
    }

    static int getCardId(Context context) {
        ContentResolver res = context.getContentResolver();
        Cursor c = MediaStoreUtils.querySafe(res, Uri.parse("content://media/external/fs_id"), null, null, null, null);
        int id = -1;
        if (c != null) {
            c.moveToFirst();
            id = c.getInt(0);
            c.close();
        }
        return id;
    }

    class RunnableNextDataSource implements Runnable {
        private final long atTime;

        RunnableNextDataSource(long atTime) {
            this.atTime = atTime;
        }

        @Override
        public void run() {

            if (!serviceValidState) return;

            onPlaybackCompleted.invoke(repeatMode, wantsPlay, atTime);
        }
    }
}
