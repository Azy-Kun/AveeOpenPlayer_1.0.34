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

import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.aveeopen.comp.LibraryQueueUI.Containers.ContainerPlaylist;
import com.aveeopen.comp.LibraryQueueUI.Containers.ContainerPlaylistFiles;
import com.aveeopen.comp.LibraryQueueUI.Dialog.AddLinkDialog;
import com.aveeopen.comp.LibraryQueueUI.Dialog.DirectoryPickerDialog;
import com.aveeopen.comp.LibraryQueueUI.Dialog.SongDetailsDialog;
import com.aveeopen.comp.LibraryQueueUI.Fragment1;
import com.aveeopen.comp.LibraryQueueUI.LibraryQueueFragmentBase;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.Events.WeakEventR1;
import com.aveeopen.Common.MultiList;
import com.aveeopen.comp.AlbumArt.AlbumArtCore;
import com.aveeopen.comp.AlbumArt.AlbumArtRequest;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.AppTips.TipReorderDialog;
import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.comp.Common.IItemIdentifier;
import com.aveeopen.comp.ContextualActionBar.ContextualActionBar;
import com.aveeopen.comp.ContextualActionBar.ItemSelection;
import com.aveeopen.comp.LibraryQueueUI.ContextualActions.ItemActionsSongs;
import com.aveeopen.comp.LibraryQueueUI.Fragment0;
import com.aveeopen.comp.playback.MediaPlaybackServiceDefs;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.comp.PlaybackQueue.IPlaylistSongContainerIdentifier;
import com.aveeopen.comp.PlaybackQueue.QueueCore;
import com.aveeopen.comp.Playlists.Dialog.PlaylistPickerDialog;
import com.aveeopen.ContextData;
import com.aveeopen.MainActivity;
import com.aveeopen.PlayerCore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class LibraryQueueUIDesign {

    private List<Object> listenerRefHolder = new LinkedList<>();

    public LibraryQueueUIDesign() {

        LibraryQueueFragmentBase.onRequestSectionOpenedState.subscribeWeak(new WeakEventR1.Handler<Class<?>, Boolean>() {
            @Override
            public Boolean invoke(Class<?> cls) {

                if (cls.equals(ContainerPlaylist.class))
                    return AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_uiSectionOpened0);
                else if (cls.equals(ContainerPlaylistFiles.class))
                    return AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_uiSectionOpened1);

                return true;
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onSetSectionOpened.subscribeWeak(new WeakEvent2.Handler<Boolean, Class<?>>() {
            @Override
            public void invoke(Boolean state, Class<?> cls) {

                if (cls.equals(ContainerPlaylist.class))
                    AppPreferences.createOrGetInstance().setBool(AppPreferences.PREF_Bool_uiSectionOpened0, state);
                else if (cls.equals(ContainerPlaylistFiles.class))
                    AppPreferences.createOrGetInstance().setBool(AppPreferences.PREF_Bool_uiSectionOpened1, state);

                updateLibraryItems();
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onRequestQueueList.subscribeWeak(new WeakEventR.Handler<MultiList<PlaylistSong, IItemIdentifier>>() {
            @Override
            public MultiList<PlaylistSong, IItemIdentifier> invoke() {

                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    return playbackQueue.getQueue();

                return new MultiList<>();
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onRequestSongContainerIdentifier.subscribeWeak(new WeakEventR.Handler<IPlaylistSongContainerIdentifier>() {
            @Override
            public IPlaylistSongContainerIdentifier invoke() {

                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    return playbackQueue.getSongContainerIdentifier();

                return null;
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onRequestShuffleMode.subscribeWeak(new WeakEventR.Handler<Integer>() {
            @Override
            public Integer invoke() {
                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    return playbackQueue.getShuffleMode();
                return MediaPlaybackServiceDefs.SHUFFLE_NONE;
            }
        }, listenerRefHolder);

        AddLinkDialog.onSubmitAddByLink.subscribeWeak(new WeakEvent2.Handler<ContextData, String>() {
            @Override
            public void invoke(ContextData contextData, String value) {
                Uri songUri = Uri.parse(value);
                PlaylistSong newsong = new PlaylistSong(-1, songUri);
                final List<PlaylistSong> list = new ArrayList<>();
                list.add(newsong);

                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    playbackQueue.open(list, 0, MediaPlaybackServiceDefs.FIRST, null);

            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onNavigateLibraryAddress.subscribeWeak(new WeakEvent1.Handler<String>() {
            @Override
            public void invoke(String address) {
                Fragment0 fragment0 = MainActivity.getFragment0Instance();
                if (fragment0 != null) fragment0.navigateForwardLibraryAddress(null, address);
            }
        }, listenerRefHolder);


        LibraryQueueFragmentBase.onAction.subscribeWeak(new WeakEvent2.Handler<ContextData, Integer>() {
            @Override
            public void invoke(ContextData contextData, Integer action) {

                Context context = PlayerCore.s().getAppContext();
                if (context == null) return;

                FragmentManager fragmentManager = contextData.getFragmentManager();

                switch (action) {

                    case LibraryQueueFragmentBase.ACTION_AddByLink:
                        if (fragmentManager != null) {
                            AddLinkDialog.createAndShowDialog(fragmentManager);
                        }
                        break;

                    case LibraryQueueFragmentBase.ACTION_ClearQueue:
                        //open empty
                        QueueCore playbackQueue = QueueCore.createOrGetInstance();
                        if (playbackQueue != null)
                            playbackQueue.open(new ArrayList<PlaylistSong>(), 0, MediaPlaybackServiceDefs.CLEAR, null);
                        break;

                    case LibraryQueueFragmentBase.ACTION_SaveAs: {
                        List<PlaylistSong> songs;
                        QueueCore playbackQueue2 = QueueCore.createOrGetInstance();
                        if (playbackQueue2 != null) {
                            songs = playbackQueue2.getQueue().unmodifiableList1();

                            if (fragmentManager != null)
                                PlaylistPickerDialog.createAndShowPlaylistPickerDialog(fragmentManager, songs, true);
                        }
                        break;
                    }

                    case LibraryQueueFragmentBase.ACTION_Shuffle:
                        toggleShuffle();
                        break;

                    case LibraryQueueFragmentBase.ACTION_FollowCurrent:
                        AppPreferences.createOrGetInstance().toggleBool(AppPreferences.PREF_Bool_followCurrentState);
                        break;

                    case LibraryQueueFragmentBase.ACTION_ShowAlbumArt:
                        AppPreferences.createOrGetInstance().toggleBool(AppPreferences.PREF_Bool_showAlbumArtInstead);
                        break;

                    case LibraryQueueFragmentBase.ACTION_AddFolder:
                        if (fragmentManager != null) {
                            DirectoryPickerDialog.createAndShowDialog(fragmentManager);
                        }
                        break;
                }
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onActionRemoveFolder.subscribeWeak(new WeakEvent3.Handler<Context, String, String>() {
            @Override
            public void invoke(Context context, String idhash, String path) {
                AppPreferences.createOrGetInstance().prefRemoveLibraryFolder(idhash, path, context);
                updateLibraryItems();
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onUIRequestFollowCurrentValue.subscribeWeak(new WeakEventR.Handler<Boolean>() {
            @Override
            public Boolean invoke() {
                return AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_followCurrentState);
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onRequestAlbumArtSimple.subscribeWeak(new WeakEvent2.Handler<String, ImageView>() {
            @Override
            public void invoke(String uri, ImageView imageView) {
                AlbumArtCore albumArtCore = AlbumArtCore.getInstance();
                if (albumArtCore != null) {
                    albumArtCore.loadAlbumArt(uri, imageView);
                }
            }
        }, listenerRefHolder);

        SongDetailsDialog.onRequestAlbumArt.subscribeWeak(new WeakEvent4.Handler<AlbumArtRequest, ImageView, Boolean, Boolean>() {
            @Override
            public void invoke(AlbumArtRequest albumArtRequest, ImageView imageView, Boolean fitCenterInside, Boolean preferLarge) {
                AlbumArtCore albumArtCore = AlbumArtCore.getInstance();
                if (albumArtCore != null) {
                    albumArtCore.loadAlbumArt(albumArtRequest.videoThumbDataSource,
                            albumArtRequest.path0,
                            albumArtRequest.path1,
                            albumArtRequest.genStr,
                            imageView,
                            fitCenterInside,
                            preferLarge);
                }
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onRequestAlbumArt.subscribeWeak(new WeakEvent4.Handler<AlbumArtRequest, ImageView, Boolean, Boolean>() {
            @Override
            public void invoke(AlbumArtRequest albumArtRequest, ImageView imageView, Boolean fitCenterInside, Boolean preferLarge) {
                AlbumArtCore albumArtCore = AlbumArtCore.getInstance();
                if (albumArtCore != null) {
                    albumArtCore.loadAlbumArt(albumArtRequest.videoThumbDataSource,
                            albumArtRequest.path0,
                            albumArtRequest.path1,
                            albumArtRequest.genStr,
                            imageView,
                            fitCenterInside,
                            preferLarge);

                }
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onRequestShowAlbumArtValue.subscribeWeak(new WeakEventR.Handler<Boolean>() {
            @Override
            public Boolean invoke() {
                return AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_showAlbumArtInstead);
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onActionViewDetails.subscribeWeak(new WeakEvent2.Handler<ContextData, List<ItemActionsSongs.ItemsDetails>>() {
            @Override
            public void invoke(ContextData contextData, List<ItemActionsSongs.ItemsDetails> itemDetails) {
                if (itemDetails.size() < 1)
                    return;

                ItemActionsSongs.ItemsDetails itemDetail = itemDetails.get(itemDetails.size() - 1);

                PlaylistSong song = itemDetail.getSong();
                if (song == null)
                    return;

                FragmentManager fragmentManager = contextData.getFragmentManager();
                if (fragmentManager != null) {
                    SongDetailsDialog.createAndShowDialog(fragmentManager, contextData.getContext(), song);
                }
            }
        }, listenerRefHolder);

        AppPreferences.onBoolPreferenceChanged.subscribeWeak(new WeakEvent2.Handler<Integer, Boolean>() {
            @Override
            public void invoke(Integer preference, Boolean value) {
                if (preference == AppPreferences.PREF_Bool_followCurrentState) {
                    LibraryQueueFragmentBase.onFollowCurrentValueChanged(value);
                } else if (preference == AppPreferences.PREF_Bool_showAlbumArtInstead) {
                    LibraryQueueFragmentBase.onShowAlbumArtValueChanged(value);

                    updateLibraryItems();
                    updateQueueItems();
                }
            }
        }, listenerRefHolder);

        DirectoryPickerDialog.onSubmitValue.subscribeWeak(new WeakEvent3.Handler<ContextData, String, String>() {
            @Override
            public void invoke(ContextData contextData, String folderpath1, String value) {
                AppPreferences.createOrGetInstance().prefAddLibraryFolderGenerateHash(folderpath1, contextData.getContext());

                updateLibraryItems();
            }
        }, listenerRefHolder);

        ContextualActionBar.onItemSelectionChanged.subscribeWeak(new WeakEvent2.Handler<ItemSelection.One<Object>, Boolean>() {
            @Override
            public void invoke(ItemSelection.One<Object> itemSelection, Boolean select) {
                updateContainerItems(itemSelection.getContainerIdentifier());
            }
        }, listenerRefHolder);

        ContextualActionBar.onContainerItemsDeselected.subscribeWeak(new WeakEvent1.Handler<IGeneralItemContainerIdentifier>() {
            @Override
            public void invoke(IGeneralItemContainerIdentifier containerIdentifier) {
                updateContainerItems(containerIdentifier);
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onEnqueue.subscribeWeak(new WeakEvent2.Handler<Collection<PlaylistSong>, Integer>() {
            @Override
            public void invoke(Collection<PlaylistSong> list, Integer action) {
                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    playbackQueue.enqueue(list, action);
            }
        }, listenerRefHolder);
        LibraryQueueFragmentBase.onMoveQueueItems.subscribeWeak(new WeakEvent3.Handler<Integer, Integer, List<Integer>>() {
            @Override
            public void invoke(Integer from, Integer to, List<Integer> itemOffsets) {
                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    playbackQueue.moveQueueItems(from, to, itemOffsets);
            }
        }, listenerRefHolder);
        LibraryQueueFragmentBase.onOpen2.subscribeWeak(new WeakEvent3.Handler<List<PlaylistSong>, Integer, IPlaylistSongContainerIdentifier>() {
            @Override
            public void invoke(List<PlaylistSong> list, Integer startPlayPosition, IPlaylistSongContainerIdentifier songContainerIdentifier) {
                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    playbackQueue.open(list, startPlayPosition, MediaPlaybackServiceDefs.CLEAR, songContainerIdentifier);
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onRemoveQueueItems.subscribeWeak(new WeakEvent1.Handler<List<IItemIdentifier>>() {
            @Override
            public void invoke(List<IItemIdentifier> itemIdentifiers) {
                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    playbackQueue.removeQueueItems(itemIdentifiers);
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onQueuePositionChanged.subscribeWeak(new WeakEvent1.Handler<Integer>() {
            @Override
            public void invoke(Integer position) {
                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    playbackQueue.setQueuePosition(position);
            }
        }, listenerRefHolder);
        LibraryQueueFragmentBase.onSetCurrentQueueItem.subscribeWeak(new WeakEvent1.Handler<IItemIdentifier>() {
            @Override
            public void invoke(IItemIdentifier item) {
                QueueCore playbackQueue = QueueCore.createOrGetInstance();
                if (playbackQueue != null)
                    playbackQueue.setQueueItem(item);
            }
        }, listenerRefHolder);

        QueueCore.onQueueStateChanged.subscribeWeak(new WeakEvent2.Handler<MultiList<PlaylistSong, IItemIdentifier>, IPlaylistSongContainerIdentifier>() {
            @Override
            public void invoke(MultiList<PlaylistSong, IItemIdentifier> list, IPlaylistSongContainerIdentifier songContainerIdentifier) {
                Fragment1 fragment1 = MainActivity.getFragment1Instance();
                if (fragment1 != null)
                    fragment1.updateTrackList(list);

            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onRequestShowTipState.subscribeWeak(new WeakEventR1.Handler<Integer, Boolean>() {
            @Override
            public Boolean invoke(Integer tipId) {
                return AppPreferences.createOrGetInstance().getBool(tipId);
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onActionShowReorderTip.subscribeWeak(new WeakEvent1.Handler<ContextData>() {
            @Override
            public void invoke(ContextData contextData) {
                FragmentManager fragmentManager = contextData.getFragmentManager();
                if (fragmentManager == null) return;

                TipReorderDialog.createAndShowTipReorderDialog(fragmentManager);
            }
        }, listenerRefHolder);
    }

    private void toggleShuffle() {

        int mode = MediaPlaybackServiceDefs.SHUFFLE_NONE;
        QueueCore playbackQueue = QueueCore.createOrGetInstance();
        if (playbackQueue != null)
            mode = playbackQueue.getShuffleMode();

        int shuffleMode = mode;

        if (mode == MediaPlaybackServiceDefs.SHUFFLE_NONE)
            shuffleMode = MediaPlaybackServiceDefs.SHUFFLE_NORMAL;
        else if (mode == MediaPlaybackServiceDefs.SHUFFLE_NORMAL)
            shuffleMode = MediaPlaybackServiceDefs.SHUFFLE_NONE;

        if (playbackQueue != null)
            playbackQueue.setShuffleMode(shuffleMode, true);
    }

    private void updateContainerItems(IGeneralItemContainerIdentifier containerIdentifier) {

        Fragment0 fragment0 = MainActivity.getFragment0Instance();
        if (fragment0 != null)
            fragment0.refreshAdapter(containerIdentifier);

        Fragment1 fragment1 = MainActivity.getFragment1Instance();
        if (fragment1 != null) fragment1.refreshTrackList(containerIdentifier);
    }

    private void updateLibraryItems() {
        Fragment0 fragment0 = MainActivity.getFragment0Instance();
        if (fragment0 != null) fragment0.updateLibraryItems();
    }

    private void updateQueueItems() {
        Fragment1 fragment1 = MainActivity.getFragment1Instance();
        if (fragment1 != null) fragment1.updateQueueItems();
    }

}

