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

import android.content.Context;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.SystemClock;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.tlog;
import com.aveeopen.comp.playback.AudioFrameData;
import com.aveeopen.comp.playback.BaseEqualizerEffect;
import com.aveeopen.comp.playback.ExoMediaPlayer.Other.MyDefaultRendererBuilder;
import com.aveeopen.comp.playback.ExoMediaPlayer.Other.MyExtractorRendererBuilder;
import com.aveeopen.comp.playback.ExoMediaPlayer.demo.player.MyDemoPlayer;
import com.aveeopen.comp.playback.IMediaPlayerCore;
import com.aveeopen.comp.playback.Song.IMediaDataSource;
import com.aveeopen.EventsGlobal.EventsGlobalApp;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.demo.EventLogger;
import com.google.android.exoplayer.demo.SmoothStreamingTestMediaDrmCallback;
import com.google.android.exoplayer.demo.WidevineTestMediaDrmCallback;
import com.google.android.exoplayer.demo.player.DashRendererBuilder;
import com.google.android.exoplayer.demo.player.DemoPlayer;
import com.google.android.exoplayer.demo.player.HlsRendererBuilder;
import com.google.android.exoplayer.demo.player.SmoothStreamingRendererBuilder;
import com.google.android.exoplayer.metadata.id3.Id3Frame;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.util.Util;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class ExoMediaPlayerCore implements
        IMediaPlayerCore,
        BaseEqualizerEffect.IEqualizerEffectListener,
        DemoPlayer.CaptionListener,
        SurfaceHolder.Callback {

    private final Object visualizerLock = new Object();
    private Context context;
    private PlayerEntry[] players = new PlayerEntry[2];
    private int currentPlayer = 0;
    private int nextPlayer = 1;
    private IMediaDataSource nextDataSource;//empty - no next song
    private float volume = 1.0f;
    private boolean muted = false;
    private WeakReference<SurfaceHolder> surfaceHolderWeak = new WeakReference<>(null);
    private long visualizerLastTimeUsed = 0;
    private ExoVisualizerDataProvider visualizerData = null;
    private List<Object> listenerRefHolder = new LinkedList<>();
    private OnNotifyListener onNotifyListener;
    private EventLogger eventLogger;
    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    private AudioCapabilities audioCapabilities;
    private ExoEqualizerEffect equalizerEffect = new ExoEqualizerEffect(this);

    IVisualizerDataCapturer visualizerDataCapturer = new IVisualizerDataCapturer() {
        boolean enabled = false;

        @Override
        public void onSetStarted(boolean b) {

            ExoVisualizerDataProvider vis = visualizerData;

            if (vis != null)
                vis.onSetStarted(b);
        }

        @Override
        public void onSetEnabled(boolean b) {
            enabled = b;
        }

        @Override
        public void onPcmData(ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo, int bufferIndex, int sampleRate, int channelCount, long positionUs) {

            if (!enabled) return;

            ExoVisualizerDataProvider vis = visualizerData;

            if (vis != null)
                vis.onPcmData(buffer, bufferInfo, bufferIndex, sampleRate, channelCount, positionUs);
        }

        @Override
        public void onAudioSessionId(int audioSessionId) {

            if (equalizerEffect != null)
                equalizerEffect.onAudioSessionChanged(audioSessionId);
        }
    };

    public ExoMediaPlayerCore(Context context, String playerName, OnNotifyListener onNotifyListener)
    {
        this.context = context;
        this.onNotifyListener = onNotifyListener;

        eventLogger = new EventLogger();
        eventLogger.startSession();

        for (int i = 0; i < players.length; i++)
            players[i] = new PlayerEntry();

        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(context, new AudioCapabilitiesReceiver.Listener() {
            @Override
            public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
                boolean audioCapabilitiesChanged = !audioCapabilities.equals(ExoMediaPlayerCore.this.audioCapabilities);
                if (/*player == null || */audioCapabilitiesChanged) {
                    ExoMediaPlayerCore.this.audioCapabilities = audioCapabilities;
                    ExoMediaPlayerCore.this.restartPlayers();
                }

            }
        });

        EventsGlobalApp.onUITick10.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                checkVisualizerLife();
            }
        }, listenerRefHolder);
    }

    public static int ConvertToExoVideoScalingMode(int mode) {
        if (mode == IMediaPlayerCore.MP_VIDEO_SCALING_MODE_SCALE_TO_FIT)
            return MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT;
        else if (mode == IMediaPlayerCore.MP_VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            return MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;

        return MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT;
    }

    @Override
    public void setNotifyListener(OnNotifyListener onNotifyListener) {
        this.onNotifyListener = onNotifyListener;
    }

    public void release() {
        if(equalizerEffect != null) {
            equalizerEffect.release();
            equalizerEffect = null;
        }

        setVideoSurfaceHolder(null);

        for (PlayerEntry mPlayer : players) {
            releasePlayer(mPlayer);
        }

        if (eventLogger != null) {
            eventLogger.endSession();
            eventLogger = null;
        }
    }

    private void restartPlayers() {
        for (PlayerEntry mPlayer : players) {
            long seekPos = 0;
            IMediaDataSource dataSource;
            if (mPlayer.player != null)
                seekPos = mPlayer.player.getCurrentPosition();
            dataSource = mPlayer.dataSource;
            releasePlayer(mPlayer);
            preparePlayer(mPlayer, dataSource, 1.0f, seekPos);
        }
    }

    void checkVisualizerLife() {
        synchronized (visualizerLock) {
            ExoVisualizerDataProvider vis = visualizerData;
            if (vis != null && (SystemClock.elapsedRealtime() - visualizerLastTimeUsed) > 8000) {
                vis.release();
                visualizerData = null;
            }
        }
    }

    public void setNextDataSource(IMediaDataSource path) {
        nextDataSource = path;

        //TODO: Implement ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION
//        Intent i = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
//        i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
//        i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.getPackageName());
//        context.sendBroadcast(i);
    }

    public void playNext(boolean killCurrent, boolean start, float fadeVolume, long seekPos) {
        if (!killCurrent) {
            if (isPreparingOrAbove()) {
                cycleNextPlayer();//current becomes next , next becomes current
            }
        } else {
            if (currentPlayer != nextPlayer) {
                releasePlayer(getNextPlayerEntry());
            }
        }

        if (nextDataSource == null || nextDataSource.getContentUri() == null || nextDataSource.getContentUri().equals(Uri.EMPTY)) {
            tlog.w("nextDataSource is null");

            if (start) {
                start();
            } else {
                getPlayerEntry().setAutoPlay(false);
                pause();
            }
            return;
        }

        preparePlayer(getPlayerEntry(), nextDataSource, fadeVolume, seekPos);

        if (start) {
            start();
        } else {
            getPlayerEntry().setAutoPlay(false);
            getPlayerEntry().pause();
        }
    }

    public void start() {
        PlayerEntry entry = getPlayerEntry();
        if (entry.player == null) return;

        if (onNotifyListener.onRequestAudioFocus()) {
            if (entry.player.getPlaybackState() == ExoPlayer.STATE_IDLE) {
                if (entry.dataSource != null)
                    entry.player.prepare(getRendererBuilder(entry.dataSource));
            }

            getPlayerEntry().player.setAutoPlay(true);
            surfaceCreated(onNotifyListener.onRequestVideoSurfaceHolder());
            getPlayerEntry().maybeStartPlayback();
        }

        onNotifyListener.onMpPlaystateOrMetaChanged(false, null);
    }

    public void pause() {
        if (getPlayer() == null) return;
        getPlayer().getPlayerControl().pause();
        onNotifyListener.onMpPlaystateOrMetaChanged(false, null);
    }

    public void stop() {
        if (getPlayer() == null) return;
        getPlayer().setPlayWhenReady(false);
        onNotifyListener.onMpPlaystateOrMetaChanged(false, null);
    }

    public boolean isPreparingOrAbove() {
        return getPlayer() != null &&
                (getPlayer().getPlaybackState() == ExoPlayer.STATE_PREPARING ||
                        getPlayer().getPlaybackState() == ExoPlayer.STATE_BUFFERING ||
                        getPlayer().getPlaybackState() == ExoPlayer.STATE_READY);
    }

    public boolean isPreparingOrStared() {
        if (getPlayer() == null) return false;

        if (getPlayer().getPlaybackState() == ExoPlayer.STATE_PREPARING ||
                getPlayer().getPlaybackState() == ExoPlayer.STATE_BUFFERING ||
                getPlayer().getPlaybackState() == ExoPlayer.STATE_READY)
            return getPlayer().getPlayWhenReady();

        return false;
    }

    @Override
    public boolean containsVideoTrack() {
        return getPlayer() != null;
    }

    @Override
    public void setVideoScalingMode(int mode) {
        getPlayerEntry().setVideoScalingMode(mode);
    }

    @Override
    public void setVideoSurfaceHolder(SurfaceHolder surfaceHolder) {

        if (surfaceHolder != null) {
            surfaceHolderWeak = new WeakReference<>(surfaceHolder);
            surfaceHolder.removeCallback(ExoMediaPlayerCore.this);
            surfaceHolder.addCallback(ExoMediaPlayerCore.this);

            this.surfaceCreated(surfaceHolder);
        } else {
            SurfaceHolder surfaceHolder_ = surfaceHolderWeak.get();
            if (surfaceHolder_ != null) {
                surfaceHolder_.removeCallback(ExoMediaPlayerCore.this);
                surfaceHolderWeak = new WeakReference<>(null);
            }
            this.surfaceDestroyed(null);
        }
    }

    public long duration() {
        if (getPlayer() == null) return 0;
        return getPlayer().getDuration();
    }

    public long position() {
        if (getPlayer() == null) return 0;
        return getPlayer().getCurrentPosition();
    }

    public void seek(long timeMillis) {
        getPlayerEntry().setFadeVolume(1.0f);
        getPlayerEntry().seekTo((int) timeMillis);
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
        setVolume(muted, volume);
    }

    public void setVolume(boolean muted, float volume) {
        this.volume = volume;
        this.muted = muted;

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
    public boolean setFadeVolumeRelative(float volumePlus, int index) {
        if (index == 0) {
            index = currentPlayer;
        } else {
            index = nextPlayer;
        }
        return players[index] == null || players[index].setFadeVolumeRelative(volumePlus);
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
            releasePlayer(players[nextPlayer]);
        }
    }

    @Override
    public void onCues(List<Cue> cues) {
        //TODO: Implement
    }

    private MyDemoPlayer getPlayer() {
        return getPlayerEntry().player;
    }

    PlayerEntry getPlayerEntry() {
        return players[currentPlayer];
    }

    PlayerEntry getNextPlayerEntry() {
        return players[nextPlayer];
    }

    void cycleNextPlayer() {
        int old = currentPlayer;
        currentPlayer = nextPlayer;
        nextPlayer = old;
    }

    private void releasePlayer(PlayerEntry playerEntry) {
        if (playerEntry.player != null) {
            playerEntry.player.release();
            playerEntry.player = null;
        }
    }

    private DemoPlayer.RendererBuilder getRendererBuilder(IMediaDataSource dataSource) {
        String userAgent = Util.getUserAgent(context, "ExoPlayerDemo");

        Uri contentUri = dataSource.getContentUri();
        int contentType = dataSource.getContentType();
        String contentId = dataSource.getContentId();

        switch (contentType) {
            case Defines.TYPE_SS:
                return new SmoothStreamingRendererBuilder(context, userAgent, contentUri.toString(),
                        new SmoothStreamingTestMediaDrmCallback());
            case Defines.TYPE_DASH:
                return new DashRendererBuilder(context, userAgent, contentUri.toString(),
                        new WidevineTestMediaDrmCallback(contentId, dataSource.getProviderDASH()));
            case Defines.TYPE_HLS:
                return new HlsRendererBuilder(context, userAgent, contentUri.toString());
            case Defines.TYPE_OTHER:
                return new MyExtractorRendererBuilder(onNotifyListener, visualizerDataCapturer, context, userAgent, contentUri);
            case Defines.TYPE_DEFAULT:
                return new MyDefaultRendererBuilder(onNotifyListener, visualizerDataCapturer, context,
                        userAgent, contentUri);
            default:
                throw new IllegalStateException("Unsupported type: " + contentType);
        }
    }

    private void preparePlayer(final PlayerEntry entry, IMediaDataSource dataSource, final float fadeStartVolume, long seekPos) {
        entry.dataSource = dataSource;

        if (entry.player == null) {
            AudioTrack.enablePreV21AudioSessionWorkaround = true;
            entry.player = new MyDemoPlayer(null);
            entry.player.addListener(entry);
            entry.player.setCaptionListener(this);
            entry.player.setMetadataListener(entry);
            entry.player.seekTo(seekPos);

            entry.player.addListener(eventLogger);
            entry.player.setInfoListener(eventLogger);
            entry.player.setInternalErrorListener(eventLogger);
        }

        entry.setFadeVolume(fadeStartVolume);
        entry.setStartFadeVolume(fadeStartVolume);

        setVideoSurfaceHolder(onNotifyListener.onRequestVideoSurfaceHolder());

        entry.player.seekTo(seekPos);
        if (entry.dataSource != null)
            entry.player.prepare(getRendererBuilder(entry.dataSource));

        entry.setFadeVolume(fadeStartVolume);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder != null) {

            getNextPlayerEntry().setVideoSurface(null);
            if (getPlayerEntry().setVideoSurface(holder.getSurface()))
                getPlayerEntry().maybeStartPlayback();

        } else {
            getNextPlayerEntry().setVideoSurface(null);
            getPlayerEntry().setVideoSurface(null);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Do nothing.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        getNextPlayerEntry().setVideoSurface(null);
        getPlayerEntry().setVideoSurface(null);
    }

    @Override
    public void resetVisualizer() {

    }

    @Override
    public AudioFrameData getVisualizationData(AudioFrameData outResult, boolean useGlobalSession) {
        if (outResult == null) return null;

        synchronized (visualizerLock) {
            ExoVisualizerDataProvider vis = visualizerData;

            if (vis == null)
                vis = new ExoVisualizerDataProvider();

            visualizerLastTimeUsed = SystemClock.elapsedRealtime();
            AudioFrameData result = vis.getVisData(outResult);
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

    class PlayerEntry implements DemoPlayer.Listener, DemoPlayer.Id3MetadataListener {

        IMediaDataSource dataSource;
        private int lastPlaybackState = 0;
        private float volume = 1.0f;
        private float fadeVolume = 0.0f;
        private float volumeStereoBalance = 0.0f;//-1.0f .. 1.0f
        private MyDemoPlayer player;
        private float startFadeVolume = 0.0f;

        public void maybeStartPlayback() {
            if (player != null)
                player.maybeStartPlayback();
        }

        public void setAutoPlay(boolean val) {
            if (player != null)
                player.setAutoPlay(val);
        }

        public void pause() {
            if (player != null)
                player.getPlayerControl().pause();
        }

        public void seekTo(long positionMs) {
            if (player != null)
                player.seekTo(positionMs);
        }

        @Override
        public void onStateChanged(boolean playWhenReady, int playbackState) {

            if (lastPlaybackState == playbackState) return;
            int lastPlaybackState_ = lastPlaybackState;
            lastPlaybackState = playbackState;

            if (equalizerEffect != null)
                equalizerEffect.onCheckEqualizerLife();

            if (lastPlaybackState_ == ExoPlayer.STATE_PREPARING) {
                //assume its prepared
                this.setFadeVolume(startFadeVolume);

                onNotifyListener.onMpPlaystateOrMetaChanged(true, null);
            }

            if (lastPlaybackState_ == ExoPlayer.STATE_BUFFERING || lastPlaybackState_ == ExoPlayer.STATE_READY) {
                if (playbackState == ExoPlayer.STATE_ENDED)
                    onCompletion(player);
            }
        }

        @Override
        public void onError(Exception e) {
            String str = e.getMessage();
            str = str.replace("java.io.IOException:", "");
            str = str.replace("java.lang.IllegalStateException", "");
            if (str.length() < 5) str = "Error " + str;

            onNotifyListener.onMpPlaystateOrMetaChanged(false, str);
        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

            float w = width;
            float h = height;

            if (width < height) {
                w = height;
                h = width;
            }

            float widthHeightRatio = 1.0f;
            if (w > 0.0f & h > 0.0f)
                widthHeightRatio = (w * pixelWidthHeightRatio) / h;

            onNotifyListener.onNotifyVideoSizeChanged(width, height, widthHeightRatio);
        }

        @Override
        public void onId3Metadata(List<Id3Frame> id3Frames) {

        }

        public void onCompletion(DemoPlayer mp) {
            if (mp == null) return;

            if (ExoMediaPlayerCore.this.getPlayer() == mp) {
                onNotifyListener.requestNextDataNow();
            }
        }

        public void setVideoScalingMode(int mode) {

        }

        //return true - surface set
        public boolean setVideoSurface(Surface surface) {
            if (player == null) return false;

            if (surface != null && surface.isValid()) {
                player.setSelectedTrack(DemoPlayer.TYPE_VIDEO, DemoPlayer.TRACK_DEFAULT);
                player.setSurface(surface);
                return true;
            } else {
                player.setSelectedTrack(DemoPlayer.TYPE_VIDEO, DemoPlayer.TRACK_DISABLED);
                player.blockingClearSurface();
                return false;
            }
        }

        public void setVolume(float volume) {
            this.volume = volume;
            updateVolume();
        }

        public void setFadeVolume(float fadeVolume) {
            this.fadeVolume = fadeVolume;
            updateVolume();
        }

        public boolean setFadeVolumeRelative(float fadeVolumePlus) {

            boolean reachedMax = false;

            fadeVolume += fadeVolumePlus;

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

        void updateVolume() {
            float left = Math.min(1.0f - volumeStereoBalance, 1.0f);
            float right = Math.min(1.0f + volumeStereoBalance, 1.0f);

            if (player != null)
                player.audioTrackRendererSetVolume(left * fadeVolume * volume, right * fadeVolume * volume);
        }
    }
}


