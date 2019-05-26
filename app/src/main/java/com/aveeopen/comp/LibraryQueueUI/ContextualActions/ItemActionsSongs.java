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

package com.aveeopen.comp.LibraryQueueUI.ContextualActions;

import android.content.Context;

import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.MultiList;
import com.aveeopen.Common.Tuple3;
import com.aveeopen.ContextData;
import com.aveeopen.comp.ContextualActionBar.ActionListenerBase;
import com.aveeopen.comp.ContextualActionBar.ItemActionBase;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.comp.Common.IItemIdentifier;
import com.aveeopen.comp.playback.MediaPlaybackServiceDefs;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.comp.PlaybackQueue.IPlaylistSongContainerIdentifier;
import com.aveeopen.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemActionsSongs {

    public static WeakEventR<IPlaylistSongContainerIdentifier> onRequestSongContainerIdentifier = new WeakEventR<>();
    public static WeakEventR<MultiList<PlaylistSong, IItemIdentifier>> onRequestQueueList = new WeakEventR<>();
    public static WeakEvent2<ContextData /*contextData*/, List<ItemsDetails> /*itemDetails*/> onActionViewDetails = new WeakEvent2<>();
    public static WeakEvent1<Integer /*position*/> onQueuePositionChanged = new WeakEvent1<>();
    public static WeakEvent2<Collection<PlaylistSong> /*list*/, Integer /*action*/> onEnqueue = new WeakEvent2<>();
    public static WeakEvent3<List<PlaylistSong> /*list*/, Integer /*startPlayPosition*/, IPlaylistSongContainerIdentifier /*songContainerIdentifier*/> onOpen2 = new WeakEvent3<>();
    public static WeakEvent4<Context /*context*/, List<PlaylistSong> /*songs*/, Boolean /*overwritePL*/, ContextData /*contextData*/> onLibraryQueueUI_ActionSongSendToPlaylist = new WeakEvent4<>();

    public static class ItemsDetails {
        PlaylistSong song;
        public ItemsDetails(PlaylistSong song) {
            this.song = song;
        }

        public PlaylistSong getSong() {
            return song;
        }
    }

    public static class ItemActionEnqueue extends ItemActionBase {
        public static ItemActionBase itemAction2Enqueue = new ItemActionEnqueue();

        public ItemActionEnqueue() {
            super(3, true, R.drawable.ic_playlist4, R.string.libItemAction_enqueue);
        }

        @Override
        public void executeListBase(ContextData contextData, List<Object> items, List<ActionListenerBase> listeners) {
            List<PlaylistSong> songsOut = new ArrayList<>();

            for (int i = 0; i < items.size(); i++) {
                EnqueueActionListener2 actionListener = (EnqueueActionListener2) listeners.get(i);
                actionListener.onEnqueue(contextData.getContext(), items.get(i), songsOut);
            }
            onEnqueue.invoke(songsOut, MediaPlaybackServiceDefs.LAST);
        }

        public static abstract class EnqueueActionListener2 extends ActionListenerBase {
            public EnqueueActionListener2() {
                super(itemAction2Enqueue);
            }

            protected abstract void onEnqueue(Context context, Object item, List<PlaylistSong> songsOut);
        }
    }

    public static class ItemActionEnqueueNext extends ItemActionBase {

        public static ItemActionBase itemAction2EnqueueNext = new ItemActionEnqueueNext();

        public ItemActionEnqueueNext() {
            super(3, true, R.drawable.ic_playlist4, R.string.libItemAction_enqueueNext);
        }

        @Override
        public void executeListBase(ContextData contextData, List<Object> items, List<ActionListenerBase> listeners) {
            final List<PlaylistSong> songsOut = new ArrayList<>();

            for (int i = 0; i < items.size(); i++) {
                EnqueueNextActionListener2 actionListener = (EnqueueNextActionListener2) listeners.get(i);
                actionListener.onEnqueue(contextData.getContext(), items.get(i), songsOut);
            }
            onEnqueue.invoke(songsOut, MediaPlaybackServiceDefs.NEXT);
        }

        public static abstract class EnqueueNextActionListener2 extends ActionListenerBase {
            public EnqueueNextActionListener2() {
                super(itemAction2EnqueueNext);
            }

            protected abstract void onEnqueue(Context context, Object item, List<PlaylistSong> songsOut);
        }
    }

    public static class EnqueueAllContainerItemAction extends ItemActionBase {
        public static ItemActionBase enqueueContainerItemAction = new EnqueueAllContainerItemAction();

        public EnqueueAllContainerItemAction() {
            super(3, true, R.drawable.ic_playlist4, R.string.libItemAction_enqueueAll);
        }

        @Override
        public void executeListBase(ContextData contextData, List<Object> items, List<ActionListenerBase> listeners) {
            List<PlaylistSong> songsOut = new ArrayList<>();

            for (int i = 0; i < items.size(); i++) {
                EnqueueAllContainerActionListener2 actionListener = (EnqueueAllContainerActionListener2) listeners.get(i);
                actionListener.onEnqueue(contextData.getContext(), items.get(i), songsOut);
            }
            onEnqueue.invoke(songsOut, MediaPlaybackServiceDefs.LAST);
        }

        public static abstract class EnqueueAllContainerActionListener2 extends ActionListenerBase {
            public EnqueueAllContainerActionListener2() {
                super(enqueueContainerItemAction);
            }

            protected abstract void onEnqueue(Context context, Object item, List<PlaylistSong> songsOut);
        }
    }

    public static class SendToItemAction extends ItemActionBase {
        public static ItemActionBase itemActionSendTo = new SendToItemAction();

        public SendToItemAction() {
            super(4, true, R.drawable.ic_add2, R.string.libItemAction_sendTo);
        }

        @Override
        public void executeListBase(final ContextData contextData, List<Object> items, List<ActionListenerBase> listeners) {
            List<PlaylistSong> songsOut = new ArrayList<>();

            for (int i = 0; i < items.size(); i++) {
                SendToActionListener actionListener = (SendToActionListener) listeners.get(i);
                actionListener.onSendTo(contextData.getContext(), items.get(i), songsOut);
            }
            onLibraryQueueUI_ActionSongSendToPlaylist.invoke(contextData.getContext(), songsOut, false, contextData);
        }

        public static abstract class SendToActionListener extends ActionListenerBase {
            public SendToActionListener() {
                super(itemActionSendTo);
            }

            protected abstract void onSendTo(Context context, Object item, List<PlaylistSong> songsOut);
        }
    }

    public static class ViewDetailsItemAction extends ItemActionBase {
        public static ItemActionBase itemActionViewDetails = new ViewDetailsItemAction();

        public ViewDetailsItemAction() {
            super(6, false, true, R.drawable.ic_gear, R.string.libItemAction_details);
        }

        @Override
        public void executeListBase(final ContextData contextData, List<Object> items, List<ActionListenerBase> listeners) {
            final List<ItemsDetails> allDetails = new ArrayList<>();

            for (int i = 0; i < items.size(); i++) {
                ViewDetailsActionListener2 actionListener = (ViewDetailsActionListener2) listeners.get(i);
                allDetails.add(actionListener.onDetails(contextData.getContext(), items.get(i)));
            }
            onActionViewDetails.invoke(contextData, allDetails);

        }

        public static abstract class ViewDetailsActionListener2 extends ActionListenerBase {

            public ViewDetailsActionListener2() {
                super(itemActionViewDetails);
            }

            protected abstract ItemsDetails onDetails(Context context, Object item);
        }
    }


    public static class PlayMultiItemAction extends ItemActionBase {
        public static ItemActionBase playMultiItemAction = new PlayMultiItemAction();
        public PlayMultiItemAction() {
            super(2, true, false, R.drawable.ic_playlist4, R.string.libItemAction_playAllMulti);
        }

        @Override
        public void executeListBase(ContextData contextData, List<Object> items, List<ActionListenerBase> listeners) {
            List<PlaylistSong> songsOut = new ArrayList<>();

            for (int i = 0; i < items.size(); i++) {
                PlayMultiActionListener2 actionListener = (PlayMultiActionListener2) listeners.get(i);
                actionListener.onPlayMulti(contextData.getContext(), items.get(i), songsOut);
            }
            onOpen2.invoke(songsOut, 0, null);
        }

        public static abstract class PlayMultiActionListener2 extends ActionListenerBase {
            public PlayMultiActionListener2() {
                super(playMultiItemAction);
            }

            protected abstract void onPlayMulti(Context context, Object item, List<PlaylistSong> songsOut);
        }
    }

    public static class PlaySingleItemAction extends ItemActionBase {
        public static ItemActionBase playSingleItemAction = new PlaySingleItemAction();

        public PlaySingleItemAction() {
            super(1, false, true, R.drawable.ic_playlist4, R.string.libItemAction_play);
        }

        @Override
        public void executeListBase(ContextData contextData, List<Object> items, List<ActionListenerBase> listeners) {
            List<PlaylistSong> songsOut = new ArrayList<>();

            for (int i = 0; i < items.size(); i++) {
                PlaySingleActionListener2 actionListener = (PlaySingleActionListener2) listeners.get(i);
                actionListener.onPlaySingle(contextData.getContext(), items.get(i), songsOut);
            }
            onOpen2.invoke(songsOut, 0, null);
        }

        public static abstract class PlaySingleActionListener2 extends ActionListenerBase {
            public PlaySingleActionListener2() {
                super(playSingleItemAction);
            }

            protected abstract void onPlaySingle(Context context, Object item, List<PlaylistSong> songsOut);
        }
    }

    public static class PlayAllContainerItemAction extends ItemActionBase {
        private static ItemActionBase playAllContainerItemAction = new PlayAllContainerItemAction();
        public PlayAllContainerItemAction() {
            super(2, false, R.drawable.ic_playlist4, R.string.libItemAction_playAll);
        }

        @Override
        public void executeListBase(ContextData contextData, List<Object> items, List<ActionListenerBase> listeners) {
            List<PlaylistSong> songsOut = new ArrayList<>();

            IPlaylistSongContainerIdentifier currentSongContainerDesc = onRequestSongContainerIdentifier.invoke(null);
            //PlaylistSong currentSong = IPlaybackService.bus.fields.fields.currentTrack;
            MultiList<PlaylistSong, IItemIdentifier> queueList = onRequestQueueList.invoke(new MultiList<PlaylistSong, IItemIdentifier>());

            int positionAtStart = 0;
            IPlaylistSongContainerIdentifier songContainerIdentifier = null;
            boolean sameContainer = false;

            for (int i = 0; i < items.size(); i++) {
                PlayAllContainerActionListener2 actionListener = (PlayAllContainerActionListener2) listeners.get(i);
                Tuple3<Integer, IPlaylistSongContainerIdentifier, Boolean> ret = actionListener.onPlayAllContainer(contextData.getContext(), items.get(i), songsOut, currentSongContainerDesc, queueList);
                positionAtStart = ret.obj1;
                songContainerIdentifier = ret.obj2;
                sameContainer = ret.obj3;
            }

            if (items.size() > 1) {//on multi select its not allowed
                positionAtStart = 0;
                songContainerIdentifier = null;
                sameContainer = false;
            }

            if (sameContainer) {
                onQueuePositionChanged.invoke(positionAtStart);
            } else {
                onOpen2.invoke(songsOut, positionAtStart, songContainerIdentifier);
            }
        }

        public static abstract class PlayAllContainerActionListener2 extends ActionListenerBase {
            public PlayAllContainerActionListener2() {
                super(playAllContainerItemAction);
            }

            protected abstract Tuple3<Integer /*positionAtStart*/, IPlaylistSongContainerIdentifier, Boolean> onPlayAllContainer(
                    Context context,
                    Object item,
                    List<PlaylistSong> songsOut,
                    IPlaylistSongContainerIdentifier songContainerIdentifier,
                    MultiList<PlaylistSong, IItemIdentifier> queueList);
        }
    }

}
