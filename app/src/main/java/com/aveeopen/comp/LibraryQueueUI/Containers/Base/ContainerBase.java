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

package com.aveeopen.comp.LibraryQueueUI.Containers.Base;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.Events.WeakEventR1;
import com.aveeopen.Common.Events.WeakEventR2;
import com.aveeopen.Common.Events.WeakEventR3;
import com.aveeopen.Common.Utils;
import com.aveeopen.comp.AlbumArt.AlbumArtRequest;
import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.comp.ContextualActionBar.ItemSelection;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.ViewAdapter;
import com.aveeopen.comp.PlaybackQueue.IPlaylistSongContainerIdentifier;
import com.aveeopen.ContextData;
import com.aveeopen.Design.SortDesign;
import com.aveeopen.R;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

public abstract class ContainerBase implements IContainerData {

    public static WeakEvent2<String /*url*/, ImageView /*imageView*/> onRequestAlbumArtSimple = new WeakEvent2<>();
    public static WeakEvent4<AlbumArtRequest /*artRequest*/, ImageView /*imageView*/, Boolean /*fitCenterInside*/, Boolean /*preferLarge*/> onRequestAlbumArt = new WeakEvent4<>();
    public static WeakEventR<Boolean> onRequestShowAlbumArtValue = new WeakEventR<>();
    public static WeakEventR2<Integer /*pageIndex*/, IGeneralItemContainerIdentifier /*containerIdentifier*/, String> onRequestSearchQuery = new WeakEventR2<>();
    public static WeakEventR2<Integer /*pageIndex*/, IGeneralItemContainerIdentifier /*containerIdentifier*/, SortDesign.SortDesc> onRequestCurrentSortDesc = new WeakEventR2<>();
    public static WeakEventR3<Integer /*pageIndex*/, IGeneralItemContainerIdentifier /*containerIdentifier*/, File /*file,*/, Boolean> onRequestFilterFileResult = new WeakEventR3<>();
    public static WeakEventR1<Class<?> /*cls*/, Boolean> onRequestSectionOpenedState = new WeakEventR1<>();
    public static WeakEvent2<Boolean /*state*/, Class<?> /*cls*/> onSetSectionOpened = new WeakEvent2<>();
    public static WeakEvent3<Integer /*from*/, Integer /*to*/, List<Integer> /*itemOffsets*/> onMoveQueueItems = new WeakEvent3<>();
    public static WeakEventR1<ItemSelection.One /*itemSelection*/, Boolean> onRequestContainsItemSelection = new WeakEventR1<>();
    public static WeakEventR2<AsyncTask /*task*/, Integer /*pageIndex*/, Boolean> onCompareSearchTask = new WeakEventR2<>();
    public static WeakEvent3<AsyncTask /*task*/, Integer /*pageIndex*/, Object /*taskParam*/> onStartSearchTask = new WeakEvent3<>();
    public static WeakEvent1<Integer /*pageIndex*/> onContainerDataSetChanged = new WeakEvent1<>();

    protected IOnDraggingListener onDraggingListener;
    protected WeakReference<IContainerStatusListener> containerStatusListener = new WeakReference<>(null);
    protected WeakReference<ILibraryContainerDataListener> libraryContainerDataListenerWeak = new WeakReference<>(null);
    protected String displayName;
    protected int displayIconResId;
    protected String libraryAddress;
    protected int color;
    protected int colorM1;
    protected int colorM2;
    protected IGeneralItemContainerIdentifier selectionContainerIdentifier;
    protected int pageIndex = -1;
    private String itemsIdent = null;
    private boolean isSearchActive = false;

    protected ContainerBase(Context context,
                            String libraryAddress,
                            String displayName,
                            int displayIconResId,
                            int pageIndex) {

        this.pageIndex = pageIndex;

        this.displayName = displayName;
        this.displayIconResId = displayIconResId;
        this.libraryAddress = libraryAddress;

        color = context.getResources().getColor(R.color.black_alpha_1);
        colorM1 = context.getResources().getColor(R.color.text_color_m1);
        colorM2 = context.getResources().getColor(R.color.text_color_m2);

        selectionContainerIdentifier = makeContainerIdentifier(libraryAddress);
    }

    protected static IGeneralItemContainerIdentifier makeContainerIdentifier(String libraryAddr)
    {
        return new SelectionContainerAddressIdentifier(libraryAddr);
    }

    public ViewAdapter createOrGetAdapter(Context context) {
        return createOrGetAdapter(context, -1);
    }

    public ViewAdapter createOrGetAdapter(Context context, int type) {
        return createAdapter(context, type);
    }

    protected abstract ViewAdapter createAdapter(Context context, int type);

    protected boolean checkItemIdent(String ident) {

        if (checkItemIdentEquals(ident)) return true;
        itemsIdent = ident == null ? "" : new String(ident);

        return false;
    }

    protected boolean checkItemIdentEquals(String ident) {
        if (itemsIdent != null && ident != null)
            if (Utils.compareNullStrings(itemsIdent, ident)) return true;

        return false;
    }

    protected void clearItemIdent() {
        itemsIdent = null;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDispalyIconResId() {
        return displayIconResId;
    }

    public String getLibraryAddress() {
        return libraryAddress;
    }

    public String makeChildAddress(String item) {
        ILibraryContainerDataListener libraryContainerDataListener = libraryContainerDataListenerWeak.get();

        if (libraryContainerDataListener != null)
            return libraryContainerDataListener.makeChildAddress(getLibraryAddress(), item);

        return getLibraryAddress();
    }

    protected abstract String getItemPositionToItemAddress(int position);

    public abstract ViewAdapter createChildAdapter(Context context, String ite);//, ViewAdapter _adapter);

    public boolean onListViewClick(int itemPosition, final Context context) {

        ILibraryContainerDataListener libraryContainerDataListener = libraryContainerDataListenerWeak.get();

        if (libraryContainerDataListener != null)
            libraryContainerDataListener.onNavigateForward(getLibraryAddress(), getItemPositionToItemAddress(itemPosition));

        return false;
    }

    @Override
    public boolean containsContainerIdentifier(IGeneralItemContainerIdentifier containerIdentifier) {
        return selectionContainerIdentifier.equals(containerIdentifier);
    }

    public IGeneralItemContainerIdentifier getSelectionContainerIdentifier() {
        return selectionContainerIdentifier;
    }

    public void getSearchOptions(Context contex, String[] outSearchHint, IGeneralItemContainerIdentifier[] outContainerIdentifier) {
        outSearchHint[0] = null;
        outContainerIdentifier[0] = null;
    }

    public abstract void updateSearchQuery(Context context, String query);

    public void executeItemActionHeader(ContextData contextData, int index) {

    }

    public void setOnDraggingListener(IOnDraggingListener listener) {
        onDraggingListener = listener;
    }

    public void setContainerStatusListener(WeakReference<IContainerStatusListener> listenerWeak) {
        containerStatusListener = listenerWeak;
        IContainerStatusListener listener = containerStatusListener.get();
        if (listener != null) {
            listener.onItemCountChanged(getItemCount(), 0, isSearchActive);
        }
    }

    public void setLibraryContainerDataListener(WeakReference<ILibraryContainerDataListener> listener) {
        libraryContainerDataListenerWeak = listener;
    }

    @Override
    public ILibraryContainerDataListener getLibraryContainerDataListener() {
        return libraryContainerDataListenerWeak.get();
    }

    public IOnDraggingListener getOnDraggingListener() {
        return onDraggingListener;
    }

    public abstract int getItemCount();

    protected boolean getIsSearchActive() {
        return false;
    }

    protected void setSearchActive(boolean isSearchActive) {
        this.isSearchActive = isSearchActive;
        IContainerStatusListener listener = containerStatusListener.get();
        if (listener != null) {
            listener.onItemCountChanged(getItemCount(), 0, this.isSearchActive);
        }
    }

    @Override
    public boolean getSectionOpened() {
        return true;
    }

    @Override
    public void setSectionOpenedState(final boolean state) {

    }

    @Override
    public boolean getShowAlbumArtValue() {
        return onRequestShowAlbumArtValue.invoke(true);
    }

    static class SelectionContainerAddressIdentifier implements IPlaylistSongContainerIdentifier, IGeneralItemContainerIdentifier {
        private String strid;

        public SelectionContainerAddressIdentifier(String str) {
            strid = str;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof SelectionContainerAddressIdentifier && strid.compareTo(((SelectionContainerAddressIdentifier) o).strid) == 0;
        }

        @Override
        public int hashCode() {
            return strid.hashCode();
        }
    }
}
