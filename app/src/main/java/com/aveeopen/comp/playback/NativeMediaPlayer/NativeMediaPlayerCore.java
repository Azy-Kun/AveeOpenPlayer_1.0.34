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

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.UtilsUI;
import com.aveeopen.Common.tlog;
import com.aveeopen.comp.playback.AudioFrameData;
import com.aveeopen.comp.playback.BaseEqualizerEffect;
import com.aveeopen.comp.playback.IMediaPlayerCore;
import com.aveeopen.comp.playback.Song.IMediaDataSource;
import com.aveeopen.EventsGlobal.EventsGlobalApp;

import junit.framework.Assert;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class NativeMediaPlayerCore implements
        IMediaPlayerCore,
        BaseEqualizerEffect.IEqualizerEffectListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnVideoSizeChangedListener,
        SurfaceHolder.Callback {

    final Object visualizerLock = new Object();
    Context context;
    OnNotifyListener onNotifyListener;
    int currentPlayer = 0;
    int nextPlayer = 1;
    Uri nextDataSource = null;//empty - no next song
    float volume = 1.0f;
    boolean muted = false;
    long visualizerLastTimeUsed = 0;
    NativeVisualizerDataProvider visualizerData = null;
    NativeEqualizerEffect equalizerEffect = new NativeEqualizerEffect(this);
    WeakReference<SurfaceHolder> surfaceHolderWeak = new WeakReference<>(null);
    List<Object> listenerRefHolder = new LinkedList<>();
    private PlayerEntry[] players = new PlayerEntry[2];

    public NativeMediaPlayerCore(Context context, String playerName, OnNotifyListener onNotifyListener)//, AudioFocusManager audioFocusManager)
    {
        this.context = context;
        this.onNotifyListener = onNotifyListener;


        for (int i = 0; i < players.length; i++)
            players[i] = new PlayerEntry();

        EventsGlobalApp.onUITick10.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                checkVisualizerLife();
            }
        }, listenerRefHolder);
    }

    public static int ConvertToNativeVideoScalingMode(int mode) {
        if (mode == IMediaPlayerCore.MP_VIDEO_SCALING_MODE_SCALE_TO_FIT)
            return MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT;
        else if (mode == IMediaPlayerCore.MP_VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            return MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;

        return MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT;
    }

    @Override
    public void setNotifyListener(IMediaPlayerCore.OnNotifyListener onNotifyListener) {
        this.onNotifyListener = onNotifyListener;
    }

    public void release() {
        if(equalizerEffect != null) {
            equalizerEffect.release();
            equalizerEffect = null;
        }

        SurfaceHolder surfaceHolder_ = surfaceHolderWeak.get();
        if (surfaceHolder_ != null) {
            surfaceHolder_.removeCallback(NativeMediaPlayerCore.this);
            surfaceHolderWeak = new WeakReference<>(null);
        }

        releaseMediaPlayer(currentPlayer);
        releaseMediaPlayer(nextPlayer);

    }

    void checkVisualizerLife() {
        synchronized (visualizerLock) {
            NativeVisualizerDataProvider vis = visualizerData;

            if (vis != null) {
                if ((SystemClock.elapsedRealtime() - visualizerLastTimeUsed) > 8000) {

                    vis.release();
                    visualizerData = null;
                }
            }
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        if (players[currentPlayer].player == mp) {
            if (players[currentPlayer].getStateLevels() < PlayerEntry.state_idle) {
                onNotifyListener.requestNextDataDelay();
            } else {
                onNotifyListener.requestNextDataNow();
            }
        }
        //TODO: When we are here wakelock is released(?), implement temporary wakelock(?)
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (players[currentPlayer].player == mp)
            onNotifyListener.onBufferingUpdate(true, percent);

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
//        String infoMsg = playerName+" info: " + what + "," + extra;
//        tlog.w(infoMsg);
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // The MediaPlayer has moved to the Error state, must be reset!

        if (players[currentPlayer].player == mp)
            setStateLevel(currentPlayer, PlayerEntry.state_error);
        else if (players[nextPlayer].player == mp)
            setStateLevel(nextPlayer, PlayerEntry.state_error);
        else
            Assert.fail();

        switch (what) {
            //case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
            //mHandler.sendMessageDelayed(mHandler.obtainMessage(MyPlaybackServiceDefs.SERVER_DIED), 2000);
            //    return true;
            default:
                //Log.d("MultiPlayer", "Error: " + what + "," + extra);
                break;
        }

        String errorMsg = "Error: " + what + "," + extra;

        onNotifyListener.onMpPlaystateOrMetaChanged(false, errorMsg);

        return false;//will cause the OnCompletionListener to be called.
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        float ratio = (width * 1.0f) / height;

        onNotifyListener.onNotifyVideoSizeChanged(width, height, ratio);
    }

    void setStateLevel(int index, int value) {
        players[index].setStateLevels(value);

        if (equalizerEffect != null && index == currentPlayer) {
            equalizerEffect.onAudioSessionChanged(players[index].stateTh.audioSessionId);
            equalizerEffect.onCheckEqualizerLife();
        }
    }

    void createMediaPlayer(int thisPlayer) {

        if (players[thisPlayer] == null) {
            players[thisPlayer] = new PlayerEntry();
        }

        if (players[thisPlayer].player == null) {
            MediaPlayer mp = new MediaPlayer();

            mp.setOnPreparedListener(players[thisPlayer]);
            mp.setOnSeekCompleteListener(this);
            mp.setOnCompletionListener(this);
            mp.setOnBufferingUpdateListener(this);
            mp.setOnInfoListener(this);
            mp.setOnErrorListener(this);
            mp.setOnVideoSizeChangedListener(this);

            //player[currentPlayer].setAudioStreamType(AudioManager.STREAM_MUSIC);

            mp.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);

            players[thisPlayer].player = mp;
        }

        players[thisPlayer].player.reset();
        setStateLevel(thisPlayer, PlayerEntry.state_idle);
        players[thisPlayer].playerShouldStarts = false;
    }

    void releaseMediaPlayer(int thisPlayer) {

        releaseMediaPlayer(thisPlayer, false);
    }

    void releaseMediaPlayer(int thisPlayer, boolean formCoreRelease) {

        if (thisPlayer >= players.length)
            return;

        if (players[thisPlayer].player != null) {
            if (players[thisPlayer].getStateLevels() > PlayerEntry.state_stopped)
                players[thisPlayer].player.stop();
            players[thisPlayer].player.release();

        }
        players[thisPlayer].player = null;
        setStateLevel(thisPlayer, PlayerEntry.state_null);
        players[thisPlayer].playerShouldStarts = false;
    }

    void cycleNextPlayer() {
        int old = currentPlayer;
        currentPlayer = nextPlayer;
        nextPlayer = old;
    }

    //preparePlayer
    void preparePlayer(int thisPlayer, Uri path, float fadeStartVolume, long seekPos) {
        createMediaPlayer(thisPlayer);

        try {

            players[thisPlayer].player.setDataSource(context, path);

            setStateLevel(thisPlayer, PlayerEntry.state_initialized_preparing);

            players[thisPlayer].player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //players[thisPlayer].player.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            players[thisPlayer].setVideoScalingMode(onNotifyListener.getVideoScalingMode());

            setVideoSurfaceHolder(onNotifyListener.onRequestVideoSurfaceHolder());


            players[currentPlayer].setFadeVolume(fadeStartVolume);
            players[currentPlayer].setStartFadeVolume(fadeStartVolume);

            players[thisPlayer].prepareAsync(seekPos);
        } catch (IOException ex) {
            onNotifyListener.onMpPlaystateOrMetaChanged(true, "Failed open media source");

            tlog.w(ex.getMessage());

            setStateLevel(thisPlayer, PlayerEntry.state_error);
            onCompletion(players[thisPlayer].player);

        } catch (IllegalArgumentException ex) {
            onNotifyListener.onMpPlaystateOrMetaChanged(true, "Invalid media source");

            tlog.w(ex.getMessage());

            setStateLevel(thisPlayer, PlayerEntry.state_error);
            onCompletion(players[thisPlayer].player);
        }

    }

    public void setNextDataSource(IMediaDataSource data) {
        nextDataSource = data.getContentUri();
        //TODO: Implement ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION
//        Intent i = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
//        i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
//        i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.getPackageName());
//        context.sendBroadcast(i);
    }


    @Override
    public void playNext(boolean killCurrent, boolean start, float fadeVolume, long seekPos) {
        if (!killCurrent) {
            if (players[currentPlayer].isPreparingOrAbove()) {
                cycleNextPlayer();//current becomes next , next becomes current
            }
        } else {

            if (currentPlayer != nextPlayer) {
                releaseMediaPlayer(nextPlayer);
            }
        }

        if (nextDataSource == null || nextDataSource.equals(Uri.EMPTY)) {
            tlog.w("nextDataSource is null");

            if (start)
                start(true);
            else
                pause();
            return;
        }

        preparePlayer(currentPlayer, nextDataSource, fadeVolume, seekPos);

        if (start)
            start(true);
        else
            pause();
    }

    public void start() {
        start(true);
    }

    private void start(boolean sendUpdateEvent) {

        if (players[currentPlayer].getStateLevels() < PlayerEntry.state_stopped) {
            players[currentPlayer].playerShouldStarts = true;
            return;
        }

        //opened, but stopped?
        if (players[currentPlayer].getStateLevels() < PlayerEntry.state_prepared) {
            players[currentPlayer].playerShouldStarts = true;
            //playNext(true);
            try {
                players[currentPlayer].player.prepareAsync();
            } catch (IllegalStateException ex) {
                setStateLevel(currentPlayer, PlayerEntry.state_error);
            }
            return;
        }

        if (onNotifyListener.onRequestAudioFocus()) {
            players[currentPlayer].player.start();
            setStateLevel(currentPlayer, PlayerEntry.state_started);
        }

        if (sendUpdateEvent)
            onNotifyListener.onMpPlaystateOrMetaChanged(false, null);
    }

    public void pause() {
        if (players[currentPlayer].getStateLevels() < PlayerEntry.state_paused) return;
        players[currentPlayer].player.pause();
        setStateLevel(currentPlayer, PlayerEntry.state_paused);

        onNotifyListener.onMpPlaystateOrMetaChanged(false, null);
    }

    public void stop() {
        if (players[currentPlayer].getStateLevels() < PlayerEntry.state_stopped) return;
        players[currentPlayer].player.stop();
        setStateLevel(currentPlayer, PlayerEntry.state_stopped);

        onNotifyListener.onMpPlaystateOrMetaChanged(false, null);
    }
    //
    @Override
    public boolean isPreparingOrAbove() {
        return players[currentPlayer].isPreparingOrAbove();
    }

    public boolean isPreparingOrStared() {
        return players[currentPlayer].isPreparingOrStared();
    }

    @Override
    public boolean containsVideoTrack() {
        return players[currentPlayer].containsVideoTrack();
    }

    @Override
    public void setVideoScalingMode(int mode) {
        players[currentPlayer].setVideoScalingMode(mode);
    }

    @Override
    public void setVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
        if (surfaceHolder != null) {
            surfaceHolderWeak = new WeakReference<>(surfaceHolder);
            surfaceHolder.addCallback(NativeMediaPlayerCore.this);
            this.surfaceCreated(surfaceHolder);
        } else {
            SurfaceHolder surfaceHolder_ = surfaceHolderWeak.get();
            if (surfaceHolder_ != null) {
                surfaceHolder_.removeCallback(NativeMediaPlayerCore.this);
                surfaceHolderWeak = new WeakReference<>(null);
            }
            this.surfaceDestroyed(null);
        }
    }

    public long duration() {
        if (players[currentPlayer].getStateLevels() < PlayerEntry.state_stopped) return 0;
        return players[currentPlayer].player.getDuration();//*1000;
    }

    public long position() {
        if (players[currentPlayer].getStateLevels() < PlayerEntry.state_stopped) return 0;
        return players[currentPlayer].player.getCurrentPosition();//*1000;
    }

    public void seek(long whereto) {
        //whereto = whereto/1000;
        if (players[currentPlayer].getStateLevels() < PlayerEntry.state_prepared) return;
        players[currentPlayer].setFadeVolume(1.0f);
        players[currentPlayer].player.seekTo((int) whereto);
    }

    @Override
    public void setMute(boolean state) {
        setVolume(state, volume);
        onNotifyListener.onVolumeMuteStateChanged(state);
    }

    @Override
    public boolean isMuted() {
        return muted;
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
        setVolume(muted, volume);
    }

    public void setVolume(boolean muted, float vol) {
        this.muted = muted;
        volume = vol;

        if (muted) {
            for (PlayerEntry mPlayer : players) {
                if (mPlayer != null)
                    mPlayer.setVolume(0.0f);
            }
        } else {
            for (PlayerEntry mPlayer : players) {
                if (mPlayer != null)
                    mPlayer.setVolume(volume);
            }
        }
    }

    @Override
    public void setFadeVolume(float fadeVolume, int index) {
        if (index == 0) {
            index = currentPlayer;
        } else {
            index = nextPlayer;
        }

        if (players[index] != null)
            players[index].setFadeVolume(fadeVolume);
    }

    @Override
    public boolean setFadeVolumeRelative(float fadeVolumePlus, int index) {
        if (index == 0) {
            index = currentPlayer;
        } else {
            index = nextPlayer;
        }

        return players[index] == null || players[index].setFadeVolumeRelative(fadeVolumePlus);
    }

    @Override
    public void setVolumeStereoBalance(float balance) {
        for (PlayerEntry mPlayer : players) {
            if (mPlayer != null)
                mPlayer.setVolumeStereoBalance(balance);
        }
    }

    @Override
    public void destroy(int index) {

        if (index == 0) {
            tlog.w("trying to destroy currentPlayer");
        } else {
            releaseMediaPlayer(nextPlayer);
        }
    }

    //SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        if (surfaceHolder != null) {
            players[nextPlayer].setVideoSurface(null);
            players[currentPlayer].setVideoSurface(surfaceHolder.getSurface());
        } else {
            players[nextPlayer].setVideoSurface(null);
            players[currentPlayer].setVideoSurface(null);
        }

    }

    //SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    //SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        players[nextPlayer].setVideoSurface(null);
        players[currentPlayer].setVideoSurface(null);
    }

    @Override
    public void resetVisualizer() {
        synchronized (visualizerLock) {
            NativeVisualizerDataProvider vis = visualizerData;

            if (vis == null)
                return;

            vis.reset();
        }
    }

    @Override
    public AudioFrameData getVisualizationData(AudioFrameData outResult, boolean useGlobalSession) {

        if (outResult == null) return null;

        PlayerEntry.StateTh stateTh = players[currentPlayer].stateTh;

        if (stateTh.stateLevels < 3) return null;
        if (!stateTh.isPreparingOrStared)
            return outResult;//so when paused leave last waveform on screen
        int audioSessionId = stateTh.audioSessionId;

        synchronized (visualizerLock) {
            NativeVisualizerDataProvider vis = visualizerData;

            if (vis == null) {
                vis = new NativeVisualizerDataProvider();
            }

            visualizerLastTimeUsed = SystemClock.elapsedRealtime();

            AudioFrameData result = vis.getVisData(0, outResult, audioSessionId, useGlobalSession);

            visualizerData = vis;

            return result;
        }

    }

    //IEqualizerEffectListener
    @Override
    public BaseEqualizerEffect.EqualizerSettings getEqualizerSettings(String name) {
        return onNotifyListener.getEqualizerSettings(name);
    }

    @Override
    public boolean isEqualizerEnabled(String name) {
        return onNotifyListener.getEqualizerEnabled(name);
    }

    @Override
    public void onEqualizerDescChanged(BaseEqualizerEffect.EqualizerDesc desc) {
        onNotifyListener.onEqualizerDescChanged(desc);
    }

    @Override
    public BaseEqualizerEffect.EqualizerDesc getEqualizerDesc() {
        return equalizerEffect != null ? equalizerEffect.getEqualizerDesc() : null;
    }

    @Override
    public void setEqualizerSettings(BaseEqualizerEffect.EqualizerSettings equalizerSettings) {
        if(equalizerEffect!=null)
            equalizerEffect.setEqualizerSettings(equalizerSettings);
    }

    class PlayerEntry implements MediaPlayer.OnPreparedListener {

        //stateLevels / initialization levels
        public static final int state_null = -2;
        public static final int state_error = -1;
        public static final int state_idle = 0;
        public static final int state_initialized_preparing = 1;
        public static final int state_stopped = 2;
        public static final int state_prepared = 3;
        public static final int state_paused = 4;
        public static final int state_started = 5;
        public MediaPlayer player = null;
        public boolean playerShouldStarts = false;
        public float startFadeVolume = 1.0f;
        long prepareSeekPos = 0;
        float volume = 1.0f;
        float fadeVolume = 1.0f;
        float volumeStereoBalance = 0.0f;
        volatile StateTh stateTh = new StateTh();
        private int stateLevels = -1;//initialization levels

        public void release() {
            setVideoSurfaceHolder(null);

            if (player != null) {
                player.release();
                player = null;
            }
        }

        public int getStateLevels() {
            UtilsUI.AssertIsNotUiThread();

            return stateLevels;
        }

        public void setStateLevels(int stateLevels) {
            UtilsUI.AssertIsNotUiThread();

            this.stateLevels = stateLevels;

            StateTh newStateTh = new StateTh();
            newStateTh.stateLevels = stateLevels;
            newStateTh.isPreparingOrStared = isPreparingOrStared();
            newStateTh.audioSessionId = player != null ? player.getAudioSessionId() : 0;

            stateTh = newStateTh;

        }

        public void prepareAsync(long seekPos) {
            prepareSeekPos = seekPos;
            //TODO: Can't we set seek pos now, only in onPrepared?
            //player.seekTo((int)_seekPos);
            player.prepareAsync();
        }

        public boolean isPreparingOrAbove() {
            return stateLevels > 0;
        }

        public boolean isPreparingOrStared() {
            return (stateLevels == 1 || stateLevels == 3) && playerShouldStarts || stateLevels == 5;
        }

        public boolean containsVideoTrack() {
            if (player == null) return false;
            if (getStateLevels() < PlayerEntry.state_stopped) return false;

            MediaPlayer.TrackInfo[] tracks;
            try {
                tracks = player.getTrackInfo();
                for (MediaPlayer.TrackInfo t : tracks) {
                    if (t.getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_VIDEO)
                        return true;
                }
            } catch (Exception e) {
                tlog.w(e.getMessage());
            }

            return false;
        }

        public void setVideoScalingMode(int mode) {
            if (player == null) return;
            if (getStateLevels() < PlayerEntry.state_stopped) return;

            player.setVideoScalingMode(ConvertToNativeVideoScalingMode(mode));
        }

        public void setVideoSurface(Surface surface) {
            if (player == null) return;

            if (surface != null && surface.isValid()) {
                player.setSurface(surface);
            } else {
                player.setSurface(null);
            }
        }

        public void setVolume(float volume) {
            this.volume = volume;
            updateVolume();
        }

        public void setFadeVolume(float volume) {
            fadeVolume = volume;
            updateVolume();
        }

        public boolean setFadeVolumeRelative(float volumePlus) {

            if (stateLevels < 0) return true;

            boolean reachedMax = false;

            fadeVolume += volumePlus;

            if (fadeVolume <= 0.0f) {
                fadeVolume = 0.0f;
                reachedMax = true;
            }
            if (fadeVolume >= 1.0f) {
                fadeVolume = 1.0f;
                reachedMax = true;
            }

            updateVolume();

            return reachedMax;
        }

        public void setStartFadeVolume(float startFadeVolume) {
            this.startFadeVolume = startFadeVolume;
        }

        //_balance -1.0f .. 1.0f;
        public void setVolumeStereoBalance(float balance) {
            volumeStereoBalance = balance;
            updateVolume();
        }

        public void updateVolume() {
            if (stateLevels < 0) return;

            float left = Math.min(1.0f - volumeStereoBalance, 1.0f);
            float right = Math.min(1.0f + volumeStereoBalance, 1.0f);

            player.setVolume(left * fadeVolume * volume, right * fadeVolume * volume);
        }

        @Override
        public void onPrepared(MediaPlayer player) {

            setStateLevels(PlayerEntry.state_prepared);

            player.seekTo((int) prepareSeekPos);

            setFadeVolume(startFadeVolume);

            if (playerShouldStarts)
                start(false);

            onNotifyListener.onMpPlaystateOrMetaChanged(true, null);

        }

        public class StateTh {
            public int stateLevels = -1;
            public boolean isPreparingOrStared = false;
            public int audioSessionId = 0;
        }
    }

}
