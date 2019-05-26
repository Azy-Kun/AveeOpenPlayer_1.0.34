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

import android.content.Context;
import android.os.Handler;

import com.aveeopen.AppPermissions;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.MultiList;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.Common.tlog;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.Common.IItemIdentifier;
import com.aveeopen.comp.PlaybackQueue.IPlaylistSongContainerIdentifier;
import com.aveeopen.comp.playback.EventsPlaybackService;
import com.aveeopen.comp.playback.MediaPlaybackService;
import com.aveeopen.comp.PlaybackQueue.QueueCore;
import com.aveeopen.MainActivity;
import com.aveeopen.PlayerCore;
import com.aveeopen.comp.playback.Song.PlaylistSong;

import java.util.LinkedList;
import java.util.List;

public class GeneralDesign {

    public static boolean isFirstLaunch = false;
    public static boolean shouldLoadInitalSongs = false;
    public static boolean shouldLoadInitalSongMediaService = false;
    private List<Object> listenerRefHolder = new LinkedList<>();
    private Handler threadHandler = new Handler();
    private boolean gotOnContext = false;

    public GeneralDesign() {

//        SettingsActivity.onSendGeneralFeedbackAction.subscribeWeak(new WeakEvent1.Handler<ContextData>() {
//            @Override
//            public void invoke(ContextData contextData) {
//                Intent Email = new Intent(Intent.ACTION_SEND);
//                Email.setType("text/email");
//                Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@email.com"});
//                Email.putExtra(Intent.EXTRA_SUBJECT, "" + contextData.getContext().getResources().getString(R.string.app_name));
//                Email.putExtra(Intent.EXTRA_TEXT, "Hi, " + "");
//            }
//        }, listenerRefHolder);

        MainActivity.onStart.subscribeWeak(new WeakEvent1.Handler<Context>() {
            @Override
            public void invoke(Context context) {
            }
        }, listenerRefHolder);

        MainActivity.onCreateEarly.subscribeWeak(new WeakEvent1.Handler<Context>() {
            @Override
            public void invoke(Context context) {
                onContext(context);
            }
        }, listenerRefHolder);

        MediaPlaybackService.onCreateEarly.subscribeWeak(new WeakEvent1.Handler<Context>() {
            @Override
            public void invoke(final Context context) {
                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onContext(context);
                    }
                });
            }
        }, listenerRefHolder);

        QueueCore.onRequestShouldReloadInitalSongs.subscribeWeak(new WeakEventR.Handler<Boolean>() {
            @Override
            public Boolean invoke() {
                if (!gotOnContext)
                    onContext(PlayerCore.s().getAppContext());

                boolean result = shouldLoadInitalSongs;
                shouldLoadInitalSongs = false;
                return result;
            }
        }, listenerRefHolder);

        QueueCore.onQueueStateChanged.subscribeWeak(new WeakEvent2.Handler<MultiList<PlaylistSong, IItemIdentifier>, IPlaylistSongContainerIdentifier>() {
            @Override
            public void invoke(MultiList<PlaylistSong, IItemIdentifier> list, IPlaylistSongContainerIdentifier songContainerIdentifier) {

                if(shouldLoadInitalSongMediaService) {
                    Tuple2<PlaylistSong, IItemIdentifier> queueEntry = list.size() > 0 ? list.get(0) : null;
                    if (queueEntry != null) {
                        if (queueEntry.obj1 != null) {
                            String dataSource = queueEntry.obj1.getConstrucPath();
                            EventsPlaybackService.Receive.onPlayDataSource.invoke(dataSource, false, 0L, (Long) null);
                        }
                    }

                    shouldLoadInitalSongMediaService = false;
                }
            }
        }, listenerRefHolder);

        MainActivity.onRequestPermissionsResult.subscribeWeak(new WeakEvent1.Handler<Integer>() {
            @Override
            public void invoke(Integer request) {
                EventsPlaybackService.Receive.onRestartMediaPlayerCore.invoke();
                shouldLoadInitalSongMediaService = shouldLoadInitalSongs = isFirstLaunch;
                if (request == AppPermissions.REQUEST_STORAGE)
                    QueueCore.createOrGetInstance().reloadQueue();
            }
        }, listenerRefHolder);
    }

    private void onContext(Context context) {

        gotOnContext = true;

        if (context != null) {
            AppPreferences appPreferences = AppPreferences.createOrGetInstance();
            appPreferences.load(context);

            if (!isFirstLaunch) {
                isFirstLaunch = appPreferences.getBool(AppPreferences.PREF_Bool_firstLaunch);
                tlog.notice("isFirstLaunch: " + isFirstLaunch);
                appPreferences.setBool(AppPreferences.PREF_Bool_firstLaunch, false);

                shouldLoadInitalSongMediaService = shouldLoadInitalSongs = isFirstLaunch;
            }
        }

    }
}
