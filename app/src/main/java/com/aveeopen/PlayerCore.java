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

package com.aveeopen;

import android.content.Context;

import com.aveeopen.Common.UtilsUI;
import com.aveeopen.Design.AppThemesDesign;
import com.aveeopen.Design.AudioEffectsDesign;
import com.aveeopen.Design.CompositeSearchDesign;
import com.aveeopen.Design.ContextualActionModeDesign;
import com.aveeopen.Design.GeneralDesign;
import com.aveeopen.Design.LibraryQueueUIDesign;
import com.aveeopen.Design.MainUIDesign;
import com.aveeopen.Design.PlaybackControlsDesign;
import com.aveeopen.Design.PlayerControlsUIDesign;
import com.aveeopen.Design.PlaylistsDesign;
import com.aveeopen.Design.SleepTimerDesign;
import com.aveeopen.Design.SortDesign;
import com.aveeopen.Design.VisualizerDesign;
import com.aveeopen.Design.WidgetAndNotificationDesign;
import com.aveeopen.comp.ContextualActionBar.ContextualActionBar;
import com.aveeopen.comp.MediaControlsUI.MediaControlsUI;
import com.aveeopen.Common.tlog;
import com.aveeopen.comp.AlbumArt.AlbumArtCore;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.GlobalSearch.GlobalSearchCore;
import com.aveeopen.comp.playback.MediaPlaybackService;
import com.aveeopen.comp.PlaybackQueue.QueueCore;
import com.aveeopen.comp.SleepTimer.SleepTimer;
import com.aveeopen.Design.AdsDesign;
import com.aveeopen.Design.PlaybackDesign;

public class PlayerCore {

    static PlayerCore instance = new PlayerCore();

    private  AppPreferences appPreferences;
    private AlbumArtCore albumArtCore;
    private GlobalSearchCore globalSearchCore;
    private ContextualActionBar contextualActionBar;
    private SleepTimer sleepTimer;
    private QueueCore playbackQueue;
    private MediaControlsUI mediaControlsUI;
    private Object[] design = new Object[16];

    private PlayerCore() {
        instance = this;//when we are here, instance isn't assigned yet, but needed later
        init();
    }

    public static PlayerCore s() {
        return instance;
    }

    private void init() {
        UtilsUI.AssertIsUiThread();

        appPreferences = AppPreferences.createOrGetInstance();

        design[11] = new GeneralDesign();//first!
        design[0] = new SleepTimerDesign();
        design[1] = new LibraryQueueUIDesign();
        design[2] = new VisualizerDesign();
        design[3] = new PlaybackControlsDesign();
        design[4] = new PlaybackDesign();
        design[5] = new MainUIDesign();
        design[6] = new CompositeSearchDesign();
        design[7] = new SortDesign();
        design[8] = new PlaylistsDesign();
        design[9] = new PlayerControlsUIDesign();
        design[10] = new ContextualActionModeDesign();
        design[12] = new AdsDesign();
        design[13] = new AudioEffectsDesign();
        design[14] = new AppThemesDesign();
        design[15] = WidgetAndNotificationDesign.createInstance();

        playbackQueue = QueueCore.createOrGetInstance();
        contextualActionBar = ContextualActionBar.createInstance(MainActivity.getInstance());
        sleepTimer = SleepTimer.createOrGetInstance();
        albumArtCore = AlbumArtCore.createInstance();
        globalSearchCore = GlobalSearchCore.createInstance();
        mediaControlsUI = MediaControlsUI.createOrGetInstance();

    }

    public Context getAppContext() {
        Context context = null;
        MainActivity mainActivity = MainActivity.getInstance();
        if (mainActivity != null)
            context = mainActivity.getApplicationContext();
        if (context != null)
            return context;

        MediaPlaybackService mediaPlaybackService = MediaPlaybackService.getInstance();
        if (mediaPlaybackService != null)
            context = mediaPlaybackService.getApplicationContext();
        if (context != null)
            return context;

        tlog.w("app context is null");

        return null;
    }

}


