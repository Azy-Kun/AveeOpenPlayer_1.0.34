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
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.view.Surface;
import android.view.WindowManager;

import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.comp.AlbumArt.AlbumArtCore;
import com.aveeopen.comp.AlbumArt.AlbumArtRequest;
import com.aveeopen.comp.AlbumArt.ImageLoadedListener;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.LibraryQueueUI.Fragment0;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.comp.PlaybackQueue.QueueCore;
import com.aveeopen.ContextData;
import com.aveeopen.EventsGlobal.EventsGlobalNotificationUI;
import com.aveeopen.MainActivity;

import java.util.LinkedList;
import java.util.List;

public class MainUIDesign {

    List<Object> listenerRefHolder = new LinkedList<>();

    public MainUIDesign() {

        MainActivity.onMainUIAction.subscribeWeak(new WeakEvent2.Handler<Integer, ContextData>() {
            @Override
            public void invoke(Integer id, ContextData contextData) {
                if (id == 2) toggleLockOrient();
            }
        }, listenerRefHolder);

        MainActivity.onRequestAlbumArtLarge.subscribeWeak(new WeakEvent4.Handler<AlbumArtRequest, ImageLoadedListener, Integer, Integer>() {
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

        MainActivity.onRequestLockOrientState.subscribeWeak(new WeakEventR.Handler<Boolean>() {
            @Override
            public Boolean invoke() {
                int lockState = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_lockOrient);
                return lockState != 0;
            }
        }, listenerRefHolder);

        AppPreferences.onIntPreferenceChanged.subscribeWeak(new WeakEvent3.Handler<Integer, Integer, Boolean>() {
            @Override
            public void invoke(Integer preference, Integer value, Boolean userForce) {
                if (preference == AppPreferences.PREF_Int_lockOrient) {
                    updateLockOrient(value);
                }
            }
        }, listenerRefHolder);

        MainActivity.onPreviewOpen.subscribeWeak(new WeakEvent2.Handler<List<PlaylistSong>, Integer>() {
            @Override
            public void invoke(List<PlaylistSong> list, Integer startPlayPosition) {
                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    playbackQueue.previewOpen(list, startPlayPosition);
            }
        }, listenerRefHolder);

        EventsGlobalNotificationUI.onExitAction.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                MainActivity mainActivity = MainActivity.getInstance();
                if (mainActivity != null)
                    mainActivity.doExitFromService();
            }
        }, listenerRefHolder);

        MainActivity.onViewPagerPageSelected.subscribeWeak(new WeakEvent2.Handler<Integer, Activity>() {
            @Override
            public void invoke(Integer page, Activity activity) {

                AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_mainPageIndex, page);
            }
        }, listenerRefHolder);


        MainActivity.onViewPagerSwipeOutAtStart.subscribeWeak(new WeakEvent1.Handler<Context>() {
            @Override
            public void invoke(Context context) {
                boolean libUseSwipeBack = AppPreferences.preferencesGetBoolSafe(AppPreferences.createOrGetInstance().getPreferences(context), "pref_libUseSwipeBack", true);

                if (libUseSwipeBack) {
                    Fragment0 fragment0 = MainActivity.getFragment0Instance();
                    if (fragment0 != null) fragment0.navigateForBackwardLibraryAddress();
                }
            }
        }, listenerRefHolder);


        MainActivity.onViewPagerSwipeProgressUpdate.subscribeWeak(new WeakEvent2.Handler<Float, Context>() {
            @Override
            public void invoke(Float val, Context context) {
                boolean libUseSwipeBack =
                        AppPreferences.preferencesGetBoolSafe(AppPreferences.createOrGetInstance().getPreferences(context), "pref_libUseSwipeBack", true);
                if (libUseSwipeBack) {
                    Fragment0 fragment0 = MainActivity.getFragment0Instance();
                    if (fragment0 != null) fragment0.navigateForBackwardProgress(val);
                }
            }
        }, listenerRefHolder);

        MainActivity.onRequestTrackInfo.subscribeWeak(new WeakEventR.Handler<PlaylistSong.Data>() {
            @Override
            public PlaylistSong.Data invoke() {
                return PlaybackDesign.songDisplyData;
            }
        }, listenerRefHolder);

    }

    private void toggleLockOrient() {
        int lockState = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_lockOrient);

        if (lockState == 0) {
            MainActivity mainActivity = MainActivity.getInstance();

            if (mainActivity != null) {
                lockState = getLockOrient(mainActivity);
            }

        } else {
            lockState = 0;
        }

        AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_lockOrient, lockState);
    }


    private void updateLockOrient(int value) {

        MainActivity mainActivity = MainActivity.getInstance();

        if (mainActivity != null) {

            mainActivity.invalidateOptionsMenu();

            if (value != 0) {

                if (mainActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {

                    //as of SCREEN_ORIENTATION_NOSENSOR and SCREEN_ORIENTATION_LOCKED(api18)
                    //wont lock to current orientation, use workaround..
                    lockOrient(mainActivity, value);
                }
            } else {
                mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        }
    }

    void lockOrient(MainActivity mainActivity, int rotation) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //this is more reliable, but locked position wont be saved
            switch (rotation) {
                case 0:
                    mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                    mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    break;
                default:
                    mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }

        } else {
            switch (rotation) {
                case 0:
                    mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    break;
                case 1:
                    mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case 2:
                    mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case 3:
                    mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case 4:
                    mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
                default:
                    mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        }
    }

    int getLockOrient(MainActivity mainActivity) {

        final int rotation = ((WindowManager) mainActivity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();

        if (mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            switch (rotation) {
                case Surface.ROTATION_0:
                    return 1;//portrait
                case Surface.ROTATION_90:
                    return 3;//reverse portrait
                case Surface.ROTATION_180:
                    return 3;//reverse portrait
                case Surface.ROTATION_270:
                    return 1;//portrait
            }

        } else if (mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            switch (rotation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_90:
                    return 2;//land
                case Surface.ROTATION_180:
                case Surface.ROTATION_270:
                    return 4;//reverse land
            }
        }

        return 0;
    }

}
