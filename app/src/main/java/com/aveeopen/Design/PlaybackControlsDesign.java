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

import com.aveeopen.comp.LibraryQueueUI.Fragment1;
import com.aveeopen.Common.Events.WeakEvent5;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.comp.Common.IItemIdentifier;
import com.aveeopen.comp.LibraryQueueUI.Fragment0;
import com.aveeopen.comp.playback.EventsPlaybackService;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.comp.PlaybackQueue.QueueCore;
import com.aveeopen.MainActivity;
import com.aveeopen.PlayerCore;

import java.util.LinkedList;
import java.util.List;

public class PlaybackControlsDesign {

    public static volatile PlaylistSong currentTrack = PlaylistSong.EmptySong;//should never be null, but PlaylistSong.EmptySong;
    public static volatile IItemIdentifier currentItemIdent = null;
    public static volatile PlaylistSong.Data fieldsongData = PlaylistSong.emptyData;
    public static volatile int fieldQueuePosition = -1;

    private List<Object> listenerRefHolder = new LinkedList<>();

    public PlaybackControlsDesign() {

        fieldQueuePosition = QueueCore.createOrGetInstance().getQueuePosition();
        Tuple2<PlaylistSong, IItemIdentifier> queueEntry = QueueCore.createOrGetInstance().getCurrentQueueEntry();
        if (queueEntry != null) {
            currentTrack = queueEntry.obj1;
            currentItemIdent = queueEntry.obj2;
            fieldsongData = currentTrack.getDataBlocking(PlayerCore.s().getAppContext());
        }

        QueueCore.onQueuePosChanged.subscribeWeak(new WeakEvent5.Handler<Tuple2<PlaylistSong, IItemIdentifier>, Integer, Boolean, Boolean, Object>() {
            @Override
            public void invoke(Tuple2<PlaylistSong, IItemIdentifier> queueEntry, Integer songIndex, Boolean playlistEnd, Boolean activeChange, Object params) {
                PlaylistSong song = null;
                IItemIdentifier itemIdentifier = null;
                if (queueEntry != null) {
                    song = queueEntry.obj1;
                    itemIdentifier = queueEntry.obj2;
                }
                if (song == null) song = PlaylistSong.EmptySong;

                PlaylistSong.Data data = song.getDataBlocking(PlayerCore.s().getAppContext());

                currentItemIdent = itemIdentifier;
                fieldQueuePosition = songIndex;
                currentTrack = song;
                fieldsongData = data;

                Fragment0 fragment0 = MainActivity.getFragment0Instance();
                if (fragment0 != null) fragment0.updateTrackInfo();

                Fragment1 fragment1 = MainActivity.getFragment1Instance();
                if (fragment1 != null) fragment1.updateTrackInfo(songIndex, song, data);


                if (activeChange) {
                    if (!playlistEnd) {
                        String dataSource = "";
                        if (queueEntry != null) {
                            if (queueEntry.obj1 != null)
                                dataSource = queueEntry.obj1.getConstrucPath();
                        }
                        EventsPlaybackService.Receive.onPlayDataSource.invoke(dataSource, true, 0L, (Long) params);
                    } else {
                        EventsPlaybackService.Receive.onAction.invoke(EventsPlaybackService.Receive.ACTION_Stop);
                    }
                }
            }
        }, listenerRefHolder);
    }

}
