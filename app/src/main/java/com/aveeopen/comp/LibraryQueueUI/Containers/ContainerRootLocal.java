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

import com.aveeopen.Common.Tuple2;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.CompositeAdapterData;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.HeaderFooterAdapterData;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.ViewAdapter;
import com.aveeopen.comp.LibraryQueueUI.Containers.Base.CompositeContainer;
import com.aveeopen.comp.LibraryQueueUI.Containers.Base.ContainerBase;
import com.aveeopen.comp.LibraryQueueUI.Containers.Base.CursorContainerBase;
import com.aveeopen.comp.LibraryQueueUI.Containers.Base.IContainerData;
import com.aveeopen.comp.LibraryQueueUI.ViewHolders.ContentItemViewHolder;
import com.aveeopen.comp.LibraryQueueUI.ViewHolders.ViewHolderFactory;
import com.aveeopen.R;

public class ContainerRootLocal extends CursorContainerBase {

    public ContainerRootLocal(Context context, int pageIndex) {
        super(context, "/", "Library", R.drawable.ic_library_2, pageIndex);
        init(context);
    }

    static Cursor makeCursor() {
        String[] columns = new String[]{"_id", "", "", ""};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new String[]{"-1", "Everything*", "", "" + R.drawable.ic_playlist4});
        cursor.addRow(new String[]{"0", "Recently added", "", "" + R.drawable.ic_playlist4});
        cursor.addRow(new String[]{"1", "Albums", "", "" + R.drawable.ic_album4});
        cursor.addRow(new String[]{"2", "Artists", "", "" + R.drawable.ic_artist4});
        cursor.addRow(new String[]{"3", "Genres", "", "" + R.drawable.ic_music_book2_l});
        cursor.addRow(new String[]{"4", "Playlists", "", "" + R.drawable.ic_playlist4});
        cursor.addRow(new String[]{"5", "Folders", "", "" + R.drawable.ic_folder4});

        return cursor;
    }

    @Override
    public ViewAdapter createAdapter(Context context, int type) {
        ViewAdapter.IAdapterDataProvider adapterDataProvider = new HeaderFooterAdapterData(this, this, ViewHolderFactory.VIEW_HOLDER_plain_gone, ViewHolderFactory.VIEW_HOLDER_footer1);
        return new ViewAdapter(adapterDataProvider, this);
    }

    @Override
    public String getItemPositionToItemAddress(int position) {
        final Cursor item = this.getItem(position);
        return item.getString(0);
    }

    @Override
    public ViewAdapter createChildAdapter(final Context context, String relativeAddress) {
        String dispName = "";
        int iconResId = 0;
        int cursorPos = findRowAndMove("_id", relativeAddress);
        if (cursorPos >= 0) {
            dispName = this.getItem(cursorPos).getString(1);
            iconResId = this.getItem(cursorPos).getInt(3);
        }

        switch (relativeAddress) {
            case "-1": {
                ContainerBase containerBase = new ContainerAllSongs(context, makeChildAddress(relativeAddress), dispName, iconResId, pageIndex);
                containerBase.setLibraryContainerDataListener(libraryContainerDataListenerWeak);
                return containerBase.createOrGetAdapter(context, 0);

            }
            case "0": {
                ContainerBase containerBase = new ContainerRecently(context, makeChildAddress(relativeAddress), dispName, iconResId, pageIndex);
                containerBase.setLibraryContainerDataListener(libraryContainerDataListenerWeak);
                return containerBase.createOrGetAdapter(context, 0);

            }
            case "1": {
                ContainerBase containerBase = new ContainerAlbums(context, makeChildAddress(relativeAddress), dispName, iconResId, pageIndex);
                containerBase.setLibraryContainerDataListener(libraryContainerDataListenerWeak);
                return containerBase.createOrGetAdapter(context);

            }
            case "2": {
                ContainerBase containerBase = new ContainerArtists(context, makeChildAddress(relativeAddress), dispName, iconResId, pageIndex);
                containerBase.setLibraryContainerDataListener(libraryContainerDataListenerWeak);
                return containerBase.createOrGetAdapter(context);

            }
            case "3": {
                ContainerBase containerBase = new ContainerGenres(context, makeChildAddress(relativeAddress), dispName, iconResId, pageIndex);
                containerBase.setLibraryContainerDataListener(libraryContainerDataListenerWeak);
                return containerBase.createOrGetAdapter(context);

            }
            case "4":
                final ContainerPlaylist containerData0 = new ContainerPlaylist(context, makeChildAddress(relativeAddress), pageIndex);
                final ContainerPlaylistFiles containerData1 = new ContainerPlaylistFiles(context, makeChildAddress(relativeAddress), pageIndex);

                CompositeContainer compositeContainer =
                        new CompositeContainer(new IContainerData[]{containerData0, containerData1},
                                context.getResources().getString(R.string.libContainer_Playlists),
                                R.drawable.ic_playlist4,
                                new CompositeContainer.AdapterFactory() {
                                    @Override
                                    public ViewAdapter createOrGetAdapter(Context context, int type, IContainerData libraryContainerData) {
                                        ViewAdapter.IAdapterDataProvider data = new CompositeAdapterData(
                                                new CompositeAdapterData.SectionDesc[]{
                                                        new CompositeAdapterData.SectionDesc(ViewHolderFactory.VIEW_HOLDER_section),
                                                        new CompositeAdapterData.SectionDesc(ViewHolderFactory.VIEW_HOLDER_section)},
                                                new ViewAdapter.IAdapterDataProvider[]{containerData0, containerData1},
                                                new IContainerData[]{containerData0, containerData1}
                                        );

                                        ViewAdapter.IAdapterDataProvider adapterDataProvider = new HeaderFooterAdapterData(data, libraryContainerData, ViewHolderFactory.VIEW_HOLDER_playLists, ViewHolderFactory.VIEW_HOLDER_footer1);
                                        return new ViewAdapter(adapterDataProvider, libraryContainerData);
                                    }
                                }
                        );


                compositeContainer.setLibraryContainerDataListener(libraryContainerDataListenerWeak);

                return compositeContainer.createOrGetAdapter(context);

            case "5": {
                ContainerBase containerBase = new ContainerFolders(context, makeChildAddress(relativeAddress), dispName, iconResId, pageIndex);
                containerBase.setLibraryContainerDataListener(libraryContainerDataListenerWeak);
                return containerBase.createOrGetAdapter(context);
            }
        }

        return null;
    }

    public void getView(final Cursor item, int position, final ContentItemViewHolder holder) {
        holder.setToDefault(this, this.getSelectionContainerIdentifier());

        holder.setImgResource(item.getInt(3));
        holder.imgArt.setColorFilter(colorM1);
        holder.txtNum.setVisibility(View.GONE);
        holder.txtItemLine1.setText(item.getString(1));
        holder.txtItemLine1.setTextColor(this.color);
        holder.txtItemLine2.setVisibility(View.GONE);
        holder.txtItemDuration.setText("");
    }

    @Override
    public Tuple2<Cursor, String> createOrGetCursor(Context context) {
        return new Tuple2<>(makeCursor(), null);
    }

    @Override
    public int getItemViewType(int position)
    {
        return ViewHolderFactory.VIEW_HOLDER_libContent;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Cursor _item = this.getItem(position);
        ContentItemViewHolder holder = (ContentItemViewHolder) viewHolder;
        holder.itemPosition = position;
        getView(_item, position, holder);
    }

    @Override
    public boolean getSectionOpened() {
        return onRequestSectionOpenedState.invoke(ContainerRootLocal.class, false);
    }

    @Override
    public void setSectionOpenedState(final boolean state) {
        onSetSectionOpened.invoke(state, ContainerRootLocal.class);
    }

}
