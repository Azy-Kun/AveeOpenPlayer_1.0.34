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

package com.aveeopen.comp.LibraryQueueUI.Containers;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.aveeopen.Common.MultiList;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.comp.ContextualActionBar.ActionListenerBase;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.HeaderFooterAdapterData;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.ViewAdapter;
import com.aveeopen.comp.LibraryQueueUI.Containers.Base.CursorContainerBase;
import com.aveeopen.comp.LibraryQueueUI.ContextualActions.ItemActionsFolders;
import com.aveeopen.comp.LibraryQueueUI.ContextualActions.ItemActionsSongs;
import com.aveeopen.comp.LibraryQueueUI.ViewHolders.ContentItemViewHolder;
import com.aveeopen.comp.LibraryQueueUI.ViewHolders.ViewHolderFactory;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.R;

import java.io.File;
import java.util.List;

public class ContainerFolders extends CursorContainerBase {

    private static final int primaryActionIndex = -1;
    private static final int defaultActionIndex = 0;

    ActionListenerBase[] itemListenerActions = new ActionListenerBase[]
            {

                    new ItemActionsSongs.PlaySingleItemAction.PlaySingleActionListener2() {
                        @Override
                        protected void onPlaySingle(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            getChildItems(context, "" + item.id, songsOut);
                        }
                    },

                    new ItemActionsSongs.PlayMultiItemAction.PlayMultiActionListener2() {
                        @Override
                        protected void onPlayMulti(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            getChildItems(context, "" + item.id, songsOut);
                        }
                    },

                    new ItemActionsSongs.ItemActionEnqueue.EnqueueActionListener2() {
                        @Override
                        protected void onEnqueue(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            getChildItems(context, "" + item.id, songsOut);
                            //_songsOut.add(item.song);
                        }
                    },

                    new ItemActionsSongs.ItemActionEnqueueNext.EnqueueNextActionListener2() {
                        @Override
                        protected void onEnqueue(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            getChildItems(context, "" + item.id, songsOut);
                        }
                    },

                    new ItemActionsSongs.SendToItemAction.SendToActionListener() {
                        @Override
                        protected void onSendTo(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            getChildItems(context, "" + item.id, songsOut);
                        }
                    },

                    new ItemActionsFolders.RemoveFolderAction.RemoveFolderActionListener() {
                        @Override
                        protected void onRemoveFolder(Context context, Object objItem, MultiList<String, String> folderOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;

                            folderOut.add(item.id, item.path);
                        }
                    },
            };


    public ContainerFolders(Context context, String libraryAddress, String displayName, int displayIconResId, int pageIndex) {
        super(context, libraryAddress, displayName, displayIconResId, pageIndex);
        init(context);
    }

    static Tuple2<Cursor, String> makeCursor(Context context, final IGeneralItemContainerIdentifier containerIdentifier) {
        MultiList<String, String> libFolders = AppPreferences.createOrGetInstance().prefGetLibraryFolders(context);

        String[] columns = new String[]{"_id", "", ""};
        MatrixCursor cursor = new MatrixCursor(columns);

        for (Tuple2<String, String> s : libFolders) {
            cursor.addRow(new String[]{s.obj1, s.obj2, "" + R.drawable.ic_folder4});
        }

        return new Tuple2<Cursor, String>(cursor, "");
    }

    @Override
    public ViewAdapter createAdapter(Context context, int type) {
        ViewAdapter.IAdapterDataProvider adapterDataProvider = new HeaderFooterAdapterData(this, this, ViewHolderFactory.VIEW_HOLDER_folders, ViewHolderFactory.VIEW_HOLDER_footer1);
        return new ViewAdapter(adapterDataProvider, this);
    }

    List<PlaylistSong> getChildItems(Context context, String path, List<PlaylistSong> dest) {
        return ContainerFile.getTrackList(context, pageIndex, getSelectionContainerIdentifier(), path, dest);
    }

    @Override
    public String getItemPositionToItemAddress(int position) {
        final Cursor item = this.getItem(position);
        return item.getString(0);//id
    }

    @Override
    public ViewAdapter createChildAdapter(Context context, String relativeAddress) {
        String absolutePath = "";
        int cursorPos = findRowAndMove("_id", relativeAddress);
        if (cursorPos >= 0) {
            absolutePath = this.getItem(cursorPos).getString(1);
        }

        if (absolutePath.length() > 0) {
            ContainerFile adapter = new ContainerFile(context,
                    new File(absolutePath),
                    makeChildAddress(relativeAddress),
                    pageIndex);
            adapter.setLibraryContainerDataListener(libraryContainerDataListenerWeak);
            return adapter.createOrGetAdapter(context);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position)
    {
        return ViewHolderFactory.VIEW_HOLDER_libContent;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final Cursor item = this.getItem(position);
        ContentItemViewHolder holder = (ContentItemViewHolder) viewHolder;
        holder.itemPosition = position;
        getView(item, position, holder);
    }

    public void getView(final Cursor item, int position, final ContentItemViewHolder holder) {
        holder.setToDefault(this, new ThisItemIdentifier(this.getItem(position).getString(0), this.getItem(position).getString(1)), this.getSelectionContainerIdentifier());

        boolean selected = onRequestContainsItemSelection.invoke(holder.itemSelection, false);
        holder.viewItemBg.setSelected(selected);
        holder.setItemActions2(itemListenerActions, primaryActionIndex, defaultActionIndex, this);
        holder.imgArt.setVisibility(View.VISIBLE);
        holder.imgArt.setColorFilter(colorM1);
        holder.setImgResource(item.getInt(2));
        holder.txtNum.setVisibility(View.GONE);
        String path = item.getString(1);

        int index = path.lastIndexOf("/");
        String pathEnd = path;
        try {
            pathEnd = path.substring(index + 1);
        } catch (Exception ignored) {
        }

        holder.txtItemLine1.setText(pathEnd);
        holder.txtItemLine1.setTextColor(this.color);
        holder.txtItemLine2.setVisibility(View.VISIBLE);
        holder.txtItemLine2.setText(path);
        holder.txtItemDuration.setText("");
    }

    @Override
    public Tuple2<Cursor, String> createOrGetCursor(Context context, String query) {
        return makeCursor(context, getSelectionContainerIdentifier());
    }

    @Override
    public Tuple2<Cursor, String> createOrGetCursor(Context context) {
        return makeCursor(context, getSelectionContainerIdentifier());
    }

    static class ThisItemIdentifier {
        final String id;
        final String path;
        public ThisItemIdentifier(String idhash, String path) {
            this.id = idhash;
            this.path = path;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ThisItemIdentifier && id.equals(((ThisItemIdentifier) o).id);
        }
    }
}
