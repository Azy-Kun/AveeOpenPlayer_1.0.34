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
import android.view.View;

import com.aveeopen.comp.LibraryQueueUI.LibraryQueueFragmentBase;
import com.aveeopen.Common.Events.WeakDelegate3;
import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.comp.MediaControlsUI.MediaControlsUI;
import com.aveeopen.comp.playback.EventsPlaybackService;
import com.aveeopen.comp.PlaybackQueue.QueueCore;
import com.aveeopen.MainActivity;
import com.aveeopen.PlayerCore;

import java.util.LinkedList;
import java.util.List;

public class PlayerControlsUIDesign {

    private List<Object> listenerRefHolder = new LinkedList<>();

    public PlayerControlsUIDesign() {

        MediaControlsUI.onPlaybackPrev.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    playbackQueue.prev();
            }
        }, listenerRefHolder);

        MediaControlsUI.onPlaybackNext.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    playbackQueue.nextOrFirst();
            }
        }, listenerRefHolder);

        MediaControlsUI.onPlaybackTogglePause.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                EventsPlaybackService.Receive.onAction.invoke(EventsPlaybackService.Receive.ACTION_TogglePause);
            }
        }, listenerRefHolder);

        MediaControlsUI.onRequestTrackPosition.subscribeWeak(new WeakEventR.Handler<Long>() {
            @Override
            public Long invoke() {
                return PlaybackDesign.trackPosition;
            }
        }, listenerRefHolder);

        MediaControlsUI.onSetTrackPosition.subscribeWeak(new WeakEvent1.Handler<Long>() {
            @Override
            public void invoke(final Long trackPosition) {
                EventsPlaybackService.Receive.onSeekChanged.invoke(trackPosition);
            }
        }, listenerRefHolder);

        MediaControlsUI.onRequestShowState.subscribeWeak(new WeakEventR.Handler<Integer>() {
            @Override
            public Integer invoke() {

                MainActivity mainActivity = MainActivity.getInstance();
                int pagePosition = MainActivity.QUEUE_PAGE_INDEX;
                if (mainActivity != null)
                    pagePosition = mainActivity.currentFragmentPage;

                return getPlayerControlsShowState(true, pagePosition);
            }
        }, listenerRefHolder);


        MediaControlsUI.onRequestShuffleMode.subscribeWeak(new WeakEventR.Handler<Integer>() {
            @Override
            public Integer invoke() {
                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    return playbackQueue.getShuffleMode();
                return 0;
            }
        }, listenerRefHolder);

        MediaControlsUI.onSetShuffleMode.subscribeWeak(new WeakEvent1.Handler<Integer>() {
            @Override
            public void invoke(final Integer shuffleMode) {

                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    playbackQueue.setShuffleMode(shuffleMode, true);
            }
        }, listenerRefHolder);

        QueueCore.onShuffleModeChanged.subscribeWeak(new WeakEvent1.Handler<Integer>() {
            @Override
            public void invoke(Integer shuffleMode) {

                MediaControlsUI mediaControlsUI = MediaControlsUI.getInstance();
                if (mediaControlsUI != null)
                    mediaControlsUI.onShuffleModeChanged(shuffleMode);

                LibraryQueueFragmentBase.onShuffleModeChanged(shuffleMode);

            }
        }, listenerRefHolder);


        MainActivity.onFullscreenChanged.subscribeWeak(new WeakEvent1.Handler<Boolean>() {
            @Override
            public void invoke(Boolean fullScreen) {

                MediaControlsUI mediaControlsUI = MediaControlsUI.getInstance();

                if (!fullScreen) {

                    MainActivity mainActivity = MainActivity.getInstance();
                    int pagePosition = MainActivity.QUEUE_PAGE_INDEX;
                    if (mainActivity != null)
                        pagePosition = mainActivity.currentFragmentPage;

                    if (pagePosition == MainActivity.LIBRARY_PAGE_INDEX) {
                        if (mediaControlsUI != null) mediaControlsUI.animateShow(1);
                    } else {
                        if (mediaControlsUI != null) mediaControlsUI.animateShow(2);
                    }

                } else {
                    if (mediaControlsUI != null) mediaControlsUI.animateShow(0);
                }

            }
        }, listenerRefHolder);


        MainActivity.onCreateView.subscribeWeak(new WeakDelegate3.Handler<View, View, View>() {
            @Override
            public void invoke(View view, View viewCollapsed, View viewBg) {

                MediaControlsUI mediaControlsUI = MediaControlsUI.getInstance();
                if (mediaControlsUI != null) {
                    mediaControlsUI.onCreateView(view,
                            viewCollapsed,
                            viewBg);
                }
            }
        }, listenerRefHolder);
    }

    int getPlayerControlsShowState(boolean show, int pagePosition) {

        Context context = PlayerCore.s().getAppContext();
        if (context == null) return 2;

        if (show) {
            if (pagePosition == MainActivity.LIBRARY_PAGE_INDEX)
                return 1;
            else
                return 2;
        } else {
            return 0;
        }
    }
}
