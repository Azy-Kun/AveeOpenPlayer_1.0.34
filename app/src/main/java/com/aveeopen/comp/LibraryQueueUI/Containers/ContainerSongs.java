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
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.aveeopen.Common.MultiList;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.Common.Tuple3;
import com.aveeopen.Common.Utils;
import com.aveeopen.Common.UtilsUI;
import com.aveeopen.Common.tlog;
import com.aveeopen.comp.AlbumArt.AlbumArtRequest;
import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.comp.Common.IItemIdentifier;
import com.aveeopen.comp.ContextualActionBar.ActionListenerBase;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.HeaderFooterAdapterData;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.ViewAdapter;
import com.aveeopen.comp.LibraryQueueUI.Containers.Base.ContainerBase;
import com.aveeopen.comp.LibraryQueueUI.Containers.Base.FilterableMultiListContainerBase;
import com.aveeopen.comp.LibraryQueueUI.ContextualActions.ItemActionsQueue;
import com.aveeopen.comp.LibraryQueueUI.ContextualActions.ItemActionsSongs;
import com.aveeopen.comp.LibraryQueueUI.ViewHolders.ContentItemViewHolder;
import com.aveeopen.comp.LibraryQueueUI.ViewHolders.ViewHolderFactory;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.comp.PlaybackQueue.IPlaylistSongContainerIdentifier;
import com.aveeopen.ContextData;
import com.aveeopen.Design.PlaybackControlsDesign;
import com.aveeopen.R;

import junit.framework.Assert;

import java.util.List;

public class ContainerSongs extends FilterableMultiListContainerBase<PlaylistSong, IItemIdentifier> {

    private final static int primaryActionIndex = 0;
    private final static int defaultActionIndex = 1;
    private final static int primaryActionPLNowIndex = 0;
    private final static int defaultActionPLNowIndex = -1;

    private final boolean playnowList;

    ActionListenerBase[] itemListenerActionsSongs = new ActionListenerBase[]
            {
                    new ItemActionsSongs.PlayAllContainerItemAction.PlayAllContainerActionListener2() {
                        @Override
                        protected Tuple3<Integer, IPlaylistSongContainerIdentifier, Boolean> onPlayAllContainer(Context context, Object objItem, List<PlaylistSong> songsOut, IPlaylistSongContainerIdentifier _songContainerDesc, MultiList<PlaylistSong, IItemIdentifier> _queueList) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;

                            IPlaylistSongContainerIdentifier songContainerIdentifier = (IPlaylistSongContainerIdentifier) ContainerSongs.this.getSelectionContainerIdentifier();

                            if (_songContainerDesc != null && _songContainerDesc.equals(ContainerSongs.this.getSelectionContainerIdentifier())) {

                                //this logic should match same logic when containers are not same
                                PlaylistSong songSupposedToBePlayed = (item.itemPosition >= 0 && item.itemPosition < getList().size()) ? getList().get1(item.itemPosition) : null;
                                PlaylistSong songSupposedToBePlayedInQueue = (item.itemPosition >= 0 && item.itemPosition < _queueList.size()) ? _queueList.get1(item.itemPosition) : null;

                                if (songSupposedToBePlayed != null && songSupposedToBePlayed.equals(songSupposedToBePlayedInQueue))
                                    return new Tuple3<>(item.itemPosition, songContainerIdentifier, true);

                            }

                            for (Tuple2<PlaylistSong, IItemIdentifier> s : getList()) {
                                songsOut.add(s.obj1);
                            }

                            return new Tuple3<>(item.itemPosition, songContainerIdentifier, false);//position at start play
                        }
                    },

                    new ItemActionsSongs.PlayMultiItemAction.PlayMultiActionListener2() {
                        @Override
                        protected void onPlayMulti(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            songsOut.add(item.song);
                        }
                    },

                    new ItemActionsSongs.ItemActionEnqueue.EnqueueActionListener2() {
                        @Override
                        protected void onEnqueue(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            songsOut.add(item.song);
                        }
                    },

                    new ItemActionsSongs.ItemActionEnqueueNext.EnqueueNextActionListener2() {
                        @Override
                        protected void onEnqueue(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            songsOut.add(item.song);
                        }
                    },

                    new ItemActionsSongs.SendToItemAction.SendToActionListener() {
                        @Override
                        protected void onSendTo(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            songsOut.add(item.song);
                        }
                    },

                    new ItemActionsSongs.ViewDetailsItemAction.ViewDetailsActionListener2() {

                        @Override
                        protected ItemActionsSongs.ItemsDetails onDetails(Context context, Object objItem) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            return new ItemActionsSongs.ItemsDetails(item.song);
                        }
                    }

            };
    ActionListenerBase[] itemListenerActionsSongsHeader = new ActionListenerBase[]
            {
                    new ItemActionsSongs.PlayAllContainerItemAction.PlayAllContainerActionListener2() {
                        @Override
                        protected Tuple3<Integer, IPlaylistSongContainerIdentifier, Boolean> onPlayAllContainer(Context context, Object objItem, List<PlaylistSong> songsOut, IPlaylistSongContainerIdentifier _songContainerDesc, MultiList<PlaylistSong, IItemIdentifier> _queueList) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            IPlaylistSongContainerIdentifier songContainerIdentifier = (IPlaylistSongContainerIdentifier) ContainerSongs.this.getSelectionContainerIdentifier();

                            for (Tuple2<PlaylistSong, IItemIdentifier> s : getList()) {
                                songsOut.add(s.obj1);
                            }

                            return new Tuple3<>(0, songContainerIdentifier, false);
                        }
                    },


                    new ItemActionsSongs.EnqueueAllContainerItemAction.EnqueueAllContainerActionListener2() {

                        @Override
                        protected void onEnqueue(Context context, Object item, List<PlaylistSong> songsOut) {
                            for (Tuple2<PlaylistSong, IItemIdentifier> s : getList()) {
                                songsOut.add(s.obj1);
                            }
                        }
                    }
            };
    //PLNow //Queue
    ActionListenerBase[] itemListenerActionsPLNow = new ActionListenerBase[]
            {
                    new ItemActionsQueue.PlayQueueItemAction.PlayQueueItemActionListener2() {
                        @Override
                        protected IItemIdentifier onPlay(Context context, Object objItem) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            if (item.itemIdent == null)
                                tlog.w("ThisItemIdentifier is null, in Queue");
                            return item.itemIdent;
                        }
                    },

                    new ItemActionsSongs.PlayMultiItemAction.PlayMultiActionListener2() {
                        @Override
                        protected void onPlayMulti(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            songsOut.add(item.song);
                        }
                    },

                    new ItemActionsSongs.ItemActionEnqueue.EnqueueActionListener2() {
                        @Override
                        protected void onEnqueue(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            songsOut.add(item.song);

                        }
                    },


                    new ItemActionsSongs.ItemActionEnqueueNext.EnqueueNextActionListener2() {
                        @Override
                        protected void onEnqueue(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            songsOut.add(item.song);

                        }
                    },

                    new ItemActionsSongs.SendToItemAction.SendToActionListener() {
                        @Override
                        protected void onSendTo(Context context, Object objItem, List<PlaylistSong> songsOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;
                            songsOut.add(item.song);
                        }
                    },

                    new ItemActionsQueue.RemoveQueueItemAction.RemoveQueueItemActionListener2() {
                        @Override
                        protected void onRemove(Context context, Object objItem, List<Integer> itemIndexesOut, List<IItemIdentifier> itemIdentifiersOut) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objItem;

                            itemIndexesOut.add(item.itemPosition);
                            itemIdentifiersOut.add(item.itemIdent);
                        }
                    },

                    new ItemActionsQueue.TipReorderItemAction.TipReorderItemActionListener2() {
                    },

                    new ItemActionsSongs.ViewDetailsItemAction.ViewDetailsActionListener2() {

                        @Override
                        protected ItemActionsSongs.ItemsDetails onDetails(Context context, Object objitem) {
                            ThisItemIdentifier item = (ThisItemIdentifier) objitem;
                            return new ItemActionsSongs.ItemsDetails(item.song);
                        }
                    }
            };

    public ContainerSongs(Context context, MultiList<PlaylistSong, IItemIdentifier> list, String libraryAddress, String displayName, boolean playnowlist, int pageIndex) {
        super(context,
                list,
                libraryAddress,
                displayName,
                0,
                new ContainerSongs.SearchFilter(context),
                pageIndex);

        playnowList = playnowlist;
    }

    public ContainerSongs(Context context, List<PlaylistSong> list, String libraryAddress, String displayName, int displayIconResId, int pageIndex, boolean playnowlist) {
        super(context,
                MultiList.<PlaylistSong,
                        IItemIdentifier>fromList1FillWith2(list, null),
                libraryAddress,
                displayName,
                displayIconResId,
                new ContainerSongs.SearchFilter(context),
                pageIndex);

        playnowList = playnowlist;
    }

    public static void getViewStatic(ContainerBase adapter,
                                     Object itemIdentifier,
                                     final PlaylistSong item,
                                     IItemIdentifier queueItemIdent,
                                     int songIndex,
                                     int itemPosition,
                                     ContentItemViewHolder holder,
                                     ActionListenerBase[] itemActions,
                                     int primaryActionIndex,
                                     int defaultActionIndex) {
        getViewStatic(adapter, itemIdentifier, item, queueItemIdent, songIndex, itemPosition, holder, itemActions, primaryActionIndex, defaultActionIndex, false);
    }

    public static void getViewStatic(ContainerBase adapter,
                                     Object itemIdentifier,
                                     final PlaylistSong item,
                                     IItemIdentifier queueItemIdent,
                                     final int songIndex,
                                     final int itemPosition,
                                     final ContentItemViewHolder holder,
                                     ActionListenerBase[] itemActions,
                                     int primaryActionIndex,
                                     int defaultActionIndex,
                                     final boolean playnowlist) {

        boolean showAlbumArtInstead = ContainerBase.onRequestShowAlbumArtValue.invoke(false);
        holder.setToDefault(adapter, itemIdentifier, adapter.getSelectionContainerIdentifier());
        holder.dataId = item.getConstrucPath();
        boolean selected = onRequestContainsItemSelection.invoke(holder.itemSelection, false);
        holder.viewItemBg.setSelected(selected);
        holder.setItemActions2(itemActions, primaryActionIndex, defaultActionIndex, adapter, playnowlist);

        if (showAlbumArtInstead) {
            holder.txtNum.setVisibility(View.GONE);
        } else {
            holder.txtNum.setVisibility(View.VISIBLE);
            holder.txtNum.setText(songIndex + 1 + ".");
        }

        if (playnowlist) {
            //we don't use PlaybackControlsDesign.currentTrack
            // so we don't mark multiple songs as playing and
            // due to way we update playlist ..see updateTrackInfo
            IItemIdentifier currentItemIdent = PlaybackControlsDesign.currentItemIdent;
            if (currentItemIdent != null && currentItemIdent.equals(queueItemIdent)) {
                holder.viewItemBg.setBackgroundResource(UtilsUI.getAttrDrawableRes(holder.viewItemBg, R.attr.listItemBackgroundSelected));
            } else {
                holder.viewItemBg.setBackgroundResource(UtilsUI.getAttrDrawableRes(holder.viewItemBg, R.attr.listItemBackground));
            }


        } else {

            if (PlaybackControlsDesign.currentTrack.compare(item))
            {
                holder.viewItemBg.setBackgroundResource(UtilsUI.getAttrDrawableRes(holder.viewItemBg, R.attr.listItemBackgroundSelected));
            } else {
                holder.viewItemBg.setBackgroundResource(UtilsUI.getAttrDrawableRes(holder.viewItemBg, R.attr.listItemBackground));
            }

        }

        PlaylistSong.Data songData = item.getData(new PlaylistSong.OnDataReadyListener() {
            @Override
            public void onDataReady(PlaylistSong.Data data, Object userData1, Object userData2) {

                boolean showAlbumArtInstead2 = ContainerBase.onRequestShowAlbumArtValue.invoke(false);

                ContentItemViewHolder holder2 = (ContentItemViewHolder) userData1;

                if (holder.dataId != null && holder.dataId.equals(userData2)) {
                    updateHolderFromData(holder2, data, showAlbumArtInstead2);
                }

            }
        }, holder, holder.dataId);

        if (songData == PlaylistSong.emptyData) {

            if (showAlbumArtInstead) {
                holder.imgArt.setVisibility(View.VISIBLE);
                holder.imgArt.setColorFilter(0xffffff);
                holder.setImgResource(R.drawable.placeholderart4);
            } else {
                holder.imgArt.setVisibility(View.GONE);
                holder.setImageDrawable(null);
            }

            holder.txtItemLine1.setText("...");
            holder.txtItemLine2.setVisibility(View.GONE);
            holder.txtItemDuration.setText("");

            //view not updated
            return;
        }

        updateHolderFromData(holder, songData, showAlbumArtInstead);
    }

    static void updateHolderFromData(ContentItemViewHolder holder, PlaylistSong.Data songData, boolean showAlbumArtInstead) {
        if (showAlbumArtInstead) {

            holder.imageLoaded = songData.audioId;
            holder.imgArt.setVisibility(View.VISIBLE);
            holder.imgArt.setColorFilter(0xffffff);

            String path0 = songData.getAlbumArtPath0Str();
            String path1 = songData.getAlbumArtPath1Str();
            String videoThumbDataSource = songData.getVideoThumbDataSourceAsStr();

            onRequestAlbumArt.invoke(
                    new AlbumArtRequest(videoThumbDataSource, path0, path1, songData.getAlbumArtGenerateStr()),
                    holder.imgArt,
                    true,
                    false
            );

        } else {

            holder.imgArt.setVisibility(View.GONE);
            holder.setImageDrawable(null);
        }

        if (songData.isArtistKnownOrSecondName()) {
            holder.txtItemLine1.setText(songData.trackName);

            holder.txtItemLine2.setVisibility(View.VISIBLE);

            if (songData.isAlbumKnown())
                holder.txtItemLine2.setText(songData.artistName + "   |   " + songData.albumName);
            else
                holder.txtItemLine2.setText(songData.artistName);

        } else {
            holder.txtItemLine1.setText(songData.trackName);

            holder.txtItemLine2.setVisibility(View.GONE);
        }

        holder.txtItemDuration.setText(Utils.getDurationStringHMSS(songData.duration / 1000));
    }

    @Override
    public ViewAdapter createAdapter(Context context, int type) {
        if (type == 0) {
            ViewAdapter.IAdapterDataProvider adapterDataProvider = new HeaderFooterAdapterData(this, this, ViewHolderFactory.VIEW_HOLDER_songsItem, ViewHolderFactory.VIEW_HOLDER_footer1);
            return new ViewAdapter(adapterDataProvider, this);
        } else if (type == 1) {

        } else {
            Assert.fail();
            return null;
        }

        ViewAdapter.IAdapterDataProvider adapterDataProvider = new HeaderFooterAdapterData(this, this, ViewHolderFactory.VIEW_HOLDER_queue, ViewHolderFactory.VIEW_HOLDER_footer1);
        return new ViewAdapter(adapterDataProvider, this);
    }

    @Override
    public int getItemViewType(int position)
    {
        return ViewHolderFactory.VIEW_HOLDER_libContent;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Tuple2<PlaylistSong, IItemIdentifier> mitem = getList().get(position);

        ContentItemViewHolder holder = (ContentItemViewHolder) viewHolder;
        holder.itemPosition = position;

        if (playnowList) {
            int queueIndex = getList().get2(position).getQueueIndex();
            getViewStatic(this, new ThisItemIdentifier(mitem.obj1, mitem.obj2, position), mitem.obj1, mitem.obj2, queueIndex, position, holder, itemListenerActionsPLNow, primaryActionPLNowIndex, defaultActionPLNowIndex, true);
        } else {
            getViewStatic(this, new ThisItemIdentifier(mitem.obj1, mitem.obj2, position), mitem.obj1, null, position, position, holder, itemListenerActionsSongs, primaryActionIndex, defaultActionIndex, false);
        }
    }

    @Override
    public String getItemPositionToItemAddress(int position) {
        return "";//no children
    }

    @Override
    public ViewAdapter createChildAdapter(Context context, String item) {
        return null;//no children
    }

    @Override
    public void onItemsMoved(final int from, final int to, final List<Integer> itemOffsets) {
        onMoveQueueItems.invoke(from, to, itemOffsets);
    }

    @Override
    public void getSearchOptions(Context context, String[] outSearchHint, IGeneralItemContainerIdentifier[] outContainerIdentifier) {
        if (playnowList)
            outSearchHint[0] = context.getResources().getString(R.string.libContainer_Queue_search);
        else
            outSearchHint[0] = context.getResources().getString(R.string.libContainer_Songs_search);

        outContainerIdentifier[0] = getSelectionContainerIdentifier();
    }

    @Override
    public void updateSearchQuery(Context context, String query) {
        this.updateSearchQuery(context, query, new ContainerSongs.SearchFilter(context));
    }

    @Override
    public void executeItemActionHeader(ContextData contextData, int index) {
        if (index < itemListenerActionsSongsHeader.length)
            itemListenerActionsSongsHeader[index].execute(contextData, null);
    }

    static class ThisItemIdentifier {

        public final int itemPosition;
        public final PlaylistSong song;
        private IItemIdentifier itemIdent;

        public ThisItemIdentifier(PlaylistSong song, IItemIdentifier itemIdent, int itemPosition) {
            this.song = song;
            this.itemIdent = itemIdent;
            this.itemPosition = itemPosition;
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

    public static class SearchFilter implements FilterableMultiListContainerBase.FilterComparable<PlaylistSong> {

        private Context context;

        public SearchFilter(Context context) {
            this.context = context;
        }

        @Override
        public String preProcessQuery(String text) {
            return text.toLowerCase();
        }

        @Override
        public void preProcessItem(PlaylistSong item) {
            PlaylistSong.Data data = item.getData();
        }

        @Override
        public boolean compare(String text, PlaylistSong item) {
            PlaylistSong.Data data = item.getDataBlocking(context);

            if (data != null) {
                if (data.artistName.toLowerCase().contains(text)) return true;
                if (data.trackName.toLowerCase().contains(text)) return true;
            }
            return false;
        }
    }
}