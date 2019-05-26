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

import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.Common.MultiList;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.comp.ContextualActionBar.ActionListenerBase;
import com.aveeopen.comp.ContextualActionBar.ItemActionBase;
import com.aveeopen.ContextData;
import com.aveeopen.R;

import java.util.List;

public class ItemActionsPlaylist {

    public static WeakEvent4<Context /*context*/, String /*idhash*/, String /*path*/, ContextData /*contextData*/> onLibraryQueue2UI_ActionRemoveStandalonePlaylist = new WeakEvent4<>();
    public static WeakEvent4<Context /*context*/, Long /*playlistId*/, String /*currentName*/, ContextData /*mainDat*/> onLibraryQueueUI_ActionRenamePlaylist = new WeakEvent4<>();
    public static WeakEvent4<Context /*context*/, Long /*playlistId*/, String /*name*/, ContextData /*mainDat*/> onLibraryQueueUI_ActionDeletePlaylist = new WeakEvent4<>();

    public static class RenamePlaylistAction extends ItemActionBase {
        private static ItemActionBase baseInstance = new RenamePlaylistAction();

        public RenamePlaylistAction() {
            super(4, false, true, R.drawable.ic_pencil, R.string.libItemAction_rename);
        }

        @Override
        public void executeListBase(final ContextData contextData, List<Object> items, List<ActionListenerBase> listeners) {
            MultiList<Long, String> playlistOut = new MultiList<>();

            for (int i = 0; i < items.size(); i++) {
                RenamePlaylistActionListener actionListener = (RenamePlaylistActionListener) listeners.get(i);
                actionListener.onRenamePlaylist(contextData.getContext(), items.get(i), playlistOut);
            }

            Tuple2<Long, String> item = null;
            if (playlistOut.size() > 0)
                item = playlistOut.get(playlistOut.size() - 1);

            if (item != null) {
                final long playlistId = item.obj1;
                final String currentName = item.obj2;
                onLibraryQueueUI_ActionRenamePlaylist.invoke(contextData.getContext(), playlistId, currentName, contextData);
            }
        }

        public static abstract class RenamePlaylistActionListener extends ActionListenerBase {
            public RenamePlaylistActionListener() {
                super(baseInstance);
            }

            protected abstract void onRenamePlaylist(Context context, Object item, MultiList<Long, String> playlistOut);
        }
    }

    public static class DeletePlaylistAction extends ItemActionBase {

        private static ItemActionBase baseInstance = new DeletePlaylistAction();

        public DeletePlaylistAction() {
            super(4, false, true, R.drawable.ic_close, R.string.libItemAction_deletePlaylist);
        }

        @Override
        public void executeListBase(final ContextData contextData, List<Object> items, List<ActionListenerBase> listeners) {
            MultiList<Long, String> playlistOut = new MultiList<>();

            for (int i = 0; i < items.size(); i++) {
                DeletePlaylistActionListener actionListener = (DeletePlaylistActionListener) listeners.get(i);
                actionListener.onDeletePlaylist(contextData.getContext(), items.get(i), playlistOut);
            }

            Tuple2<Long, String> item = null;
            if (playlistOut.size() > 0)
                item = playlistOut.get(playlistOut.size() - 1);

            if (item != null) {
                final long playlistId = item.obj1;
                final String currentName = item.obj2;
                onLibraryQueueUI_ActionDeletePlaylist.invoke(contextData.getContext(), playlistId, currentName, contextData);
            }
        }

        public static abstract class DeletePlaylistActionListener extends ActionListenerBase {

            public DeletePlaylistActionListener() {
                super(baseInstance);
            }

            protected abstract void onDeletePlaylist(Context context, Object item, MultiList<Long, String> playlistOut);
        }
    }

    public static class RemoveStandalonePlaylistAction extends ItemActionBase {

        private static ItemActionBase baseInstance = new RemoveStandalonePlaylistAction();

        public RemoveStandalonePlaylistAction() {
            super(4, true, true, R.drawable.ic_close, R.string.libItemAction_removeStandalonePlaylist);
        }

        @Override
        public void executeListBase(final ContextData contextData, List<Object> items, List<ActionListenerBase> listeners) {
            MultiList<String, String> playlistsOut = new MultiList<>();

            for (int i = 0; i < items.size(); i++) {
                RemoveStandalonePlaylistActionListener actionListener = (RemoveStandalonePlaylistActionListener) listeners.get(i);
                actionListener.onRemoveStandalonePlaylist(contextData.getContext(), items.get(i), playlistsOut);
            }

            Tuple2<String, String> item = null;
            if (playlistsOut.size() > 0)
                item = playlistsOut.get(playlistsOut.size() - 1);

            if (item != null) {
                final String idHash = item.obj1;
                final String path = item.obj2;
                onLibraryQueue2UI_ActionRemoveStandalonePlaylist.invoke(contextData.getContext(), idHash, path, contextData);
            }
        }

        public static abstract class RemoveStandalonePlaylistActionListener extends ActionListenerBase {

            public RemoveStandalonePlaylistActionListener() {
                super(baseInstance);
            }

            protected abstract void onRemoveStandalonePlaylist(Context context, Object item, MultiList<String, String> folderOut);
        }
    }

}
