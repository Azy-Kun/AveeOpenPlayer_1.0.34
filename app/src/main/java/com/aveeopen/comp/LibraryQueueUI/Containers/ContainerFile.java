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
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.aveeopen.comp.LibraryQueueUI.FileSortingUtils;
import com.aveeopen.Common.MultiList;
import com.aveeopen.Common.Tuple3;
import com.aveeopen.Common.UtilsMusic;
import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.comp.Common.IItemIdentifier;
import com.aveeopen.comp.ContextualActionBar.ActionListenerBase;
import com.aveeopen.comp.GlobalSearch.SearchFilesTask;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.HeaderFooterAdapterData;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.IAdapter;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.ViewAdapter;
import com.aveeopen.comp.LibraryQueueUI.Containers.Base.ContainerBase;
import com.aveeopen.comp.LibraryQueueUI.Containers.Base.FilterableMultiListContainerBase;
import com.aveeopen.comp.LibraryQueueUI.ContextualActions.ItemActionsSongs;
import com.aveeopen.comp.LibraryQueueUI.ViewHolders.ContentItemViewHolder;
import com.aveeopen.comp.LibraryQueueUI.ViewHolders.ViewHolderFactory;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.comp.PlaybackQueue.IPlaylistSongContainerIdentifier;
import com.aveeopen.ContextData;
import com.aveeopen.Design.SortDesign;
import com.aveeopen.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContainerFile extends ContainerBase implements ViewAdapter.IAdapterDataProvider {

    private static final int primaryActionDirectoryIndex = -1;
    private static final int defaultActionDirectoryIndex = 0;
    private static final int primaryActionSongIndex = 0;
    private static final int defaultActionSongIndex = 1;

    ActionListenerBase[] directoryItemListenerActions = new ActionListenerBase[]
            {

                    new ItemActionsSongs.PlaySingleItemAction.PlaySingleActionListener2() {
                        @Override
                        protected void onPlaySingle(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            getTrackList(context, pageIndex, getSelectionContainerIdentifier(), "" + item.getId(), songsOut);
                        }
                    },

                    new ItemActionsSongs.PlayMultiItemAction.PlayMultiActionListener2() {
                        @Override
                        protected void onPlayMulti(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            getTrackList(context, pageIndex, getSelectionContainerIdentifier(), "" + item.getId(), songsOut);
                        }
                    },

                    new ItemActionsSongs.ItemActionEnqueue.EnqueueActionListener2() {
                        @Override
                        protected void onEnqueue(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            getTrackList(context, pageIndex, getSelectionContainerIdentifier(), "" + item.getId(), songsOut);
                        }
                    },

                    new ItemActionsSongs.ItemActionEnqueueNext.EnqueueNextActionListener2() {
                        @Override
                        protected void onEnqueue(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            getTrackList(context, pageIndex, getSelectionContainerIdentifier(), "" + item.getId(), songsOut);
                        }
                    },

                    new ItemActionsSongs.SendToItemAction.SendToActionListener() {
                        @Override
                        protected void onSendTo(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            getTrackList(context, pageIndex, getSelectionContainerIdentifier(), "" + item.getId(), songsOut);
                        }
                    },
            };

    private int pageIndex;
    private File rootFile;
    private String currentName;
    private String currentAbsolutePath, currentRelativePath;
    private boolean currentIsDirectory;
    private List<Item> items;

    ActionListenerBase[] itemListenerActionsSongs = new ActionListenerBase[]
            {

                    new ItemActionsSongs.PlayAllContainerItemAction.PlayAllContainerActionListener2() {
                        @Override
                        protected Tuple3<Integer, IPlaylistSongContainerIdentifier, Boolean> onPlayAllContainer(Context context, Object objItem, List<PlaylistSong> songsOut, IPlaylistSongContainerIdentifier songContainerDesc, MultiList<PlaylistSong, IItemIdentifier> queueList) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;

                            IPlaylistSongContainerIdentifier songContainerIdentifier = (IPlaylistSongContainerIdentifier) ContainerFile.this.getSelectionContainerIdentifier();

                            if (songContainerDesc != null && songContainerDesc.equals(ContainerFile.this.getSelectionContainerIdentifier())) {

                                //this logic should match same logic when containers are not same
                                PlaylistSong songSupposedToBePlayed = item.item.getSong();
                                int songIndex = findSongInItemsNotCountingNulls(items, songSupposedToBePlayed, 0);
                                PlaylistSong songSupposedToBePlayedInQueue = null;
                                if (songIndex >= 0 && songIndex < queueList.size())
                                    songSupposedToBePlayedInQueue = queueList.get1(songIndex);

                                if (songSupposedToBePlayed != null && songSupposedToBePlayed.equals(songSupposedToBePlayedInQueue))
                                    return new Tuple3<>(songIndex, songContainerIdentifier, true);

                            }

                            int start = songsOut.size();
                            getSongsFromFileItems2(context, items, songsOut);

                            int startPos = 0;

                            PlaylistSong startPlayAtSong = item.item.getSong();
                            if (startPlayAtSong != null) {
                                startPos = UtilsMusic.findSongInList(songsOut, startPlayAtSong, start);
                                if (startPos < 0) startPos = 0;
                            }

                            return new Tuple3<>(startPos, songContainerIdentifier, false);//position at start play
                        }
                    },

                    new ItemActionsSongs.PlayMultiItemAction.PlayMultiActionListener2() {
                        @Override
                        protected void onPlayMulti(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            PlaylistSong song = item.item.getSong();
                            if (song == null) return;
                            songsOut.add(song);
                        }
                    },

                    new ItemActionsSongs.ItemActionEnqueue.EnqueueActionListener2() {
                        @Override
                        protected void onEnqueue(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            PlaylistSong song = item.item.getSong();
                            if (song == null) return;
                            songsOut.add(song);
                        }
                    },

                    new ItemActionsSongs.ItemActionEnqueueNext.EnqueueNextActionListener2() {
                        @Override
                        protected void onEnqueue(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            PlaylistSong song = item.item.getSong();
                            if (song == null) return;
                            songsOut.add(song);
                        }
                    },

                    new ItemActionsSongs.SendToItemAction.SendToActionListener() {
                        @Override
                        protected void onSendTo(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            PlaylistSong song = item.item.getSong();
                            if (song == null) return;
                            songsOut.add(song);
                        }
                    },

                    new ItemActionsSongs.ViewDetailsItemAction.ViewDetailsActionListener2() {

                        @Override
                        protected ItemActionsSongs.ItemsDetails onDetails(Context context, Object objItem) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            PlaylistSong song = item.item.getSong();
                            if (song == null) return new ItemActionsSongs.ItemsDetails(null);
                            return new ItemActionsSongs.ItemsDetails(song);
                        }
                    }

            };
    ActionListenerBase[] itemListenerActionsHeader = new ActionListenerBase[]
            {

                    new ItemActionsSongs.PlayAllContainerItemAction.PlayAllContainerActionListener2() {
                        @Override
                        protected Tuple3<Integer, IPlaylistSongContainerIdentifier, Boolean> onPlayAllContainer(Context context, Object objItem, List<PlaylistSong> songsOut, IPlaylistSongContainerIdentifier songContainerDesc, MultiList<PlaylistSong, IItemIdentifier> queueList) {
                            IPlaylistSongContainerIdentifier songContainerIdentifier = (IPlaylistSongContainerIdentifier) ContainerFile.this.getSelectionContainerIdentifier();
                            getSongsFromFileItems2(context, items, songsOut);
                            return new Tuple3<>(0, songContainerIdentifier, false);
                        }
                    },

                    new ItemActionsSongs.EnqueueAllContainerItemAction.EnqueueAllContainerActionListener2() {

                        @Override
                        protected void onEnqueue(Context context, Object item, List<PlaylistSong> songsOut) {
                            getSongsFromFileItems2(context, items, songsOut);
                        }

                    }

            };
    private WeakReference<IAdapter> associatedAdapter = new WeakReference<>(null);
    SearchFilesTask.IResultReceiver resultReceiver = new SearchFilesTask.IResultReceiver() {

        public boolean compareTask(AsyncTask task) {
            return onCompareSearchTask.invoke(task, pageIndex, false);
        }

        @Override
        public void onSearchStarted(AsyncTask task) {
            if (checkItemIdentEquals("")) return;
            if (!compareTask(task)) {
                return;
            }

            ContainerFile.this.setSearchActive(true);
            ContainerFile.this.clearDataAndNotify();
        }

        @Override
        public void onSearchFinished(AsyncTask task, boolean allFinished) {
            if (checkItemIdentEquals("")) return;
            if (!compareTask(task)) {
                return;
            }
            ContainerFile.this.setSearchActive(false);
            ContainerFile.this.dataChangedNotify();
        }

        @Override
        public void onItemDirFound(AsyncTask task, ContainerFile.Item itemDir) {
            if (checkItemIdentEquals("")) return;
            if (!compareTask(task)) return;
            ContainerFile.this.setSearchActive(true);
        }

        @Override
        public void onItemFileFound(AsyncTask task, ContainerFile.Item itemFile) {
            if (checkItemIdentEquals("")) return;
            if (!compareTask(task)) {
                return;
            }

            ContainerFile.this.addDataAndNotify(itemFile);
            ContainerFile.this.setSearchActive(true);
        }
    };

    public ContainerFile(Context context, File f, String libraryAddress, int pageIndex) {
        super(context, libraryAddress, f.getName(), 0, pageIndex);

        this.pageIndex = pageIndex;
        rootFile = f;
        items = new ArrayList<>();

        currentName = rootFile.getName();
        currentAbsolutePath = rootFile.getAbsolutePath();
        currentRelativePath = rootFile.getPath();
        currentIsDirectory = rootFile.isDirectory();
        if (rootFile.isDirectory()) {
            if (currentAbsolutePath.length() > 0 && currentAbsolutePath.charAt(currentAbsolutePath.length() - 1) != '/')
                currentAbsolutePath = currentAbsolutePath + "/";
        }

        final IGeneralItemContainerIdentifier containerIdentifier = getSelectionContainerIdentifier();
        String searchText = onRequestSearchQuery.invoke(pageIndex, containerIdentifier, "");
        updateSearchQuery(context, searchText);
    }

    public static int findSongInItemsNotCountingNulls(List<Item> items, PlaylistSong song, int start) {
        int indexWONulls = 0;
        for (int i = start; i < items.size(); i++) {
            PlaylistSong s = items.get(i).getSong();
            if (s != null) {
                if (s.compare(song)) return indexWONulls;
                indexWONulls++;
            }
        }
        return -1;
    }

    //searches files
    //excludeDirs: false -  searches subdirectories
    static List<Item> makeItemsSearch(Context context, File f, boolean excludeDirs, String query, FilterableMultiListContainerBase.FilterComparable searchFilter) {
        String queryProcessed;
        if (query == null || query.isEmpty() || searchFilter == null) {
            queryProcessed = null;
            searchFilter = null;
        } else {
            queryProcessed = searchFilter.preProcessQuery(query);
        }

        List<Item> itemDirsDest = new ArrayList<>();
        List<Item> itemFlsDest = new ArrayList<>();
        makeItemsRecursive(context, itemDirsDest, itemFlsDest, f, excludeDirs, queryProcessed, searchFilter, null);

        for (int i = 0; i < itemFlsDest.size(); i++) {
            itemFlsDest.get(i).index = i;
        }

        if (itemDirsDest.size() > 0) {
            itemDirsDest.addAll(itemFlsDest);
            return itemDirsDest;
        } else {
            return itemFlsDest;
        }
    }

    static List<Item> makeItems(Context context, final int pageIndex, final IGeneralItemContainerIdentifier containerId, File f, boolean excludeDirs) {
        List<Item> itemDirsDest = new ArrayList<>();
        List<Item> itemFlsDest = new ArrayList<>();

        FilterInclude includeTest = new FilterInclude() {
            @Override
            public boolean shouldInclude(final File item) {
                return onRequestFilterFileResult.invoke(pageIndex, containerId, item, true);
            }
        };

        makeItemsRecursive(context, itemDirsDest, itemFlsDest, f, excludeDirs, null, null, includeTest);

        SortDesign.SortDesc sortDesc = onRequestCurrentSortDesc.invoke(pageIndex, containerId, null);
        Collections.sort(itemDirsDest);

        Comparator<Item> comparator = FileSortingUtils.getSortComparator(sortDesc);
        if (comparator != null)
            Collections.sort(itemFlsDest, comparator);
        else
            Collections.sort(itemFlsDest);


        for (int i = 0; i < itemFlsDest.size(); i++) {
            itemFlsDest.get(i).index = i;
        }

        if (itemDirsDest.size() > 0) {
            itemDirsDest.addAll(itemFlsDest);
            return itemDirsDest;
        } else {
            return itemFlsDest;
        }
    }

    static void makeItemsRecursive(Context context, List<Item> itemDirsDest, List<Item> itemFlsDest, File f, boolean excludeDirs, String query, FilterableMultiListContainerBase.FilterComparable searchFilter, FilterInclude filterInclude) {

        File[] dirs = f.listFiles();

        try {
            for (File ff : dirs) {

                if (ff.isDirectory()) {

                    if (!excludeDirs) {

                        boolean include = true;

                        if (filterInclude != null)
                            include = filterInclude.shouldInclude(ff);

                        if (include) {
                            if (searchFilter == null) {
                                File[] fbuf = ff.listFiles();
                                int buf;
                                if (fbuf != null) {
                                    buf = fbuf.length;
                                } else buf = 0;

                                itemDirsDest.add(new Item(true, ff.getName(), buf, ff.getCanonicalPath(), ff.lastModified()));
                            } else {
                                //sub dir search
                                makeItemsRecursive(context, itemDirsDest, itemFlsDest, ff, excludeDirs, query, searchFilter, filterInclude);
                            }
                        }
                    }
                } else {
                    //search by meta data
                    //if(_searchFilter==null || _searchFilter.compare(_query, playlistsong)) {

                    //search by filename - faster
                    boolean include = true;

                    if (filterInclude != null)
                        include = filterInclude.shouldInclude(ff);

                    if (include) {
                        if (searchFilter != null)
                            include = ff.getName().toLowerCase().contains(query);
                    }

                    if (include) {
                        PlaylistSong playlistsong = new PlaylistSong(ff);
                        itemFlsDest.add(new Item(false,
                                ff.getName(),
                                ff.length(),
                                ff.getCanonicalPath(),
                                ff.lastModified(),
                                playlistsong
                        ));
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static List<PlaylistSong> getTrackList(Context context, int pageIndex, IGeneralItemContainerIdentifier containerIdentifier, String absolutePathDir, List<PlaylistSong> dest) {
        return getTrackList(context, pageIndex, containerIdentifier, absolutePathDir, false, dest);
    }

    //TODO: Implement includeSubDirs
    public static List<PlaylistSong> getTrackList(Context context, int pageIndex, IGeneralItemContainerIdentifier containerIdentifier, String absolutePathDir, boolean includeSubDirs, List<PlaylistSong> dest) {
        List<Item> files = makeItems(context, pageIndex, containerIdentifier, new File(absolutePathDir), true);
        return getSongsFromFileItems2(context, files, dest);
    }

    static List<PlaylistSong> getSongsFromFileItems2(Context context, List<Item> files, List<PlaylistSong> dest) {
        if (dest == null)
            dest = new ArrayList<>(files.size());

        for (Item item : files) {
            if (item.getSong() != null)
                dest.add(item.getSong());
        }

        return dest;
    }

    protected IAdapter getAssociatedAdapter() {
        return associatedAdapter.get();
    }

    @Override
    public void onAdapterInitialized(IAdapter adapter) {
        associatedAdapter = new WeakReference<>(adapter);
    }

    @Override
    public void onAdapterDispose() {
    }

    @Override
    public ViewAdapter createAdapter(Context context, int type) {
        ViewAdapter.IAdapterDataProvider adapterDataProvider = new HeaderFooterAdapterData(this, this, ViewHolderFactory.VIEW_HOLDER_songFilesItem, ViewHolderFactory.VIEW_HOLDER_footer1);
        return new ViewAdapter(adapterDataProvider, this);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return ViewHolderFactory.newInstance(viewGroup.getContext(), viewGroup, viewType);
    }

    @Override
    public String getItemPositionToItemAddress(int position) {
        final Item item = this.getItem(position);
        if (item.isDirectory())//path
            return item.getName();//name
        else
            return "";//no address, no navigable
    }

    @Override
    public ViewAdapter createChildAdapter(Context context, String relativeAddress) {
        if (relativeAddress.length() > 0) {
            ContainerFile adapter = new ContainerFile(context,
                    new File(currentAbsolutePath + relativeAddress),
                    makeChildAddress(relativeAddress),
                    pageIndex
            );
            adapter.setLibraryContainerDataListener(libraryContainerDataListenerWeak);
            return adapter.createOrGetAdapter(context);
        } else
            return null;//no children
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        final Item item = this.getItem(position);
        ContentItemViewHolder holder = (ContentItemViewHolder) viewHolder;
        holder.itemPosition = position;

        getView(item, position, holder);
    }

    public void getView(final Item item, int position, final ContentItemViewHolder holder) {
        PlaylistSong song = item.getSong();
        if (song != null) {
            ContainerSongs.getViewStatic(this, new ThisItemIdentifier(item, position), song, null, item.index, position, holder, itemListenerActionsSongs, primaryActionSongIndex, defaultActionSongIndex);
            return;
        }

        holder.setToDefault(this, new ThisItemIdentifier(item, position), this.getSelectionContainerIdentifier());
        boolean selected = onRequestContainsItemSelection.invoke(holder.itemSelection, false);
        holder.viewItemBg.setSelected(selected);
        holder.setItemActions2(directoryItemListenerActions, primaryActionDirectoryIndex, defaultActionDirectoryIndex, this);
        holder.imgArt.setVisibility(View.VISIBLE);
        holder.imgArt.setColorFilter(colorM1);
        holder.setImgResource(R.drawable.ic_folder4);
        holder.txtNum.setVisibility(View.GONE);
        holder.txtItemLine1.setText(item.getName());
        holder.txtItemLine1.setTextColor(this.color);
        holder.txtItemLine2.setVisibility(View.GONE);
        holder.txtItemDuration.setText("" + item.getCountOrSize());
    }

    protected void setDataAndNotifyDataSetChanged(List<Item> items, String itemsIdent) {
        onContainerDataSetChanged.invoke(pageIndex);
        if (checkItemIdent(itemsIdent)) return;

        this.items = items;

        IAdapter adapter = getAssociatedAdapter();
        if (adapter != null)
            adapter.myNotifyDataSetChanged();
    }

    private void clearDataAndNotify() {
        clearItemIdent();
        items.clear();
        IAdapter adapter = getAssociatedAdapter();
        if (adapter != null)
            adapter.myNotifyDataSetChanged();
    }

    private void addDataAndNotify(Item item) {
        clearItemIdent();
        items.add(item);
        IAdapter adapter = getAssociatedAdapter();
        if (adapter != null) {
            //doesn't work right, don't use for now
            //adapter.myNotifyItemInserted(items.size() - 1);

            adapter.myNotifyDataSetChanged();
        }
    }

    private void dataChangedNotify() {
        IAdapter adapter = getAssociatedAdapter();
        if (adapter != null)
            adapter.myNotifyDataSetChanged();
    }

    @Override
    public void getSearchOptions(Context context, String[] outSearchHint, IGeneralItemContainerIdentifier[] outContainerIdentifier) {
        outSearchHint[0] = context.getResources().getString(R.string.libContainer_Folders_search);
        outContainerIdentifier[0] = getSelectionContainerIdentifier();
    }

    @Override
    public void updateSearchQuery(Context context, String query) {
        if (query == null || query.isEmpty()) {
            this.setSearchActive(false);
            this.setDataAndNotifyDataSetChanged(makeItems(context, pageIndex, getSelectionContainerIdentifier(), rootFile, false), "");//""
        } else {
            clearItemIdent();
            this.setSearchActive(true);
            startSearch(context, query);
        }
    }

    @Override
    public void executeItemActionHeader(ContextData contextData, int index) {
        if (index < itemListenerActionsHeader.length)
            itemListenerActionsHeader[index].execute(contextData, null);
    }

    void startSearch(Context context, String query) {
        SearchFilesTask searchTask = new SearchFilesTask(context,
                rootFile,
                new ContainerSongs.SearchFilter(context),
                new WeakReference<>(resultReceiver));

        onStartSearchTask.invoke(searchTask, pageIndex, query);
        searchTask.execute(query);
    }

    @Override
    public int getItemViewType(int position) {
        return ViewHolderFactory.VIEW_HOLDER_libContent;
    }

    @Override
    public void onItemsMoved(int from, int to, List<Integer> itemOffsets) {

    }

    @Override
    public int dataPositionToPosition(int position) {
        return position;
    }

    public interface FilterInclude {
        boolean shouldInclude(File item);
    }

    static class ThisItemIdentifier {
        public int itemPosition;
        public Item item;
        public ThisItemIdentifier(Item item, int itemPosition) {
            this.itemPosition = itemPosition;
            this.item = item;
        }

        public String getId() {
            return item.getPath();
        }

        @Override
        public int hashCode() {
            return itemPosition;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ThisItemIdentifier && itemPosition == ((ThisItemIdentifier) o).itemPosition;
        }
    }

    public static class Item implements Comparable<Item> {
        private int index = 0;//optional, for songs
        private final PlaylistSong song;
        private final boolean isDir;
        private final String name;
        private final long countOrSize;
        private final String path;
        private final long lastModified;

        public Item(boolean dir, String n, long countOrSize, String p, long lastModified) {
            this(dir, n, countOrSize, p, lastModified, null);
        }

        public Item(boolean dir, String n, long countOrSize, String p, long lastModified, PlaylistSong s) {
            isDir = dir;
            name = n;
            this.countOrSize = countOrSize;
            path = p;
            this.lastModified = lastModified;

            song = s;
        }

        public void setIndex(int val) {
            index = val;
        }

        public int getIndex() {
            return index;
        }

        public boolean isDirectory() {
            return isDir;
        }

        public String getName() {
            return name;
        }

        public long getCountOrSize() {
            return countOrSize;
        }

        public String getPath() {
            return path;
        }

        public long getLastModified() {
            return lastModified;
        }

        public PlaylistSong getSong() {
            return song;
        }

        public int compareTo(@NonNull Item o) {
            if (this.name != null)
                return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
            else
                throw new IllegalArgumentException();
        }
    }
}
