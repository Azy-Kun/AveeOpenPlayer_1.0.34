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

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.aveeopen.comp.LibraryQueueUI.LibraryQueueFragmentBase;
import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.Common.Events.WeakEvent5;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.MultiList;
import com.aveeopen.Common.UtilsFileSys;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.GlobalSearch.SearchTaskManager;
import com.aveeopen.comp.LibraryQueueUI.Fragment0;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.comp.Playlists.Dialog.CreatePlaylistDialog;
import com.aveeopen.comp.Playlists.Dialog.PlaylistPickerDialog;
import com.aveeopen.comp.Playlists.Dialog.RenamePlaylistDialog;
import com.aveeopen.comp.Playlists.Dialog.ScanPlaylistFilesDialog;
import com.aveeopen.comp.Playlists.Files.PlaylistFilesType;
import com.aveeopen.comp.Playlists.Files.PlaylistFilesUtils;
import com.aveeopen.comp.Playlists.Platform.PlaylistPlatformUtils;
import com.aveeopen.comp.Playlists.ScanPlaylistFilesTask;
import com.aveeopen.ContextData;
import com.aveeopen.EventsGlobal.EventsGlobalTextNotifier;
import com.aveeopen.MainActivity;
import com.aveeopen.PlayerCore;
import com.aveeopen.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PlaylistsDesign {

    private SearchTaskManager taskManager = new SearchTaskManager();
    private PlaylistScanningStatus playlistScanningStatus = new PlaylistScanningStatus();
    private ScanResultReceiver resultReceiver = new ScanResultReceiver();
    private List<Object> listenerRefHolder = new ArrayList<>();

    public PlaylistsDesign() {

        ScanPlaylistFilesDialog.onRequestPlaylistScanStatus.subscribeWeak(new WeakEventR.Handler<PlaylistScanningStatus>() {
            @Override
            public PlaylistScanningStatus invoke() {
                return playlistScanningStatus;
            }
        }, listenerRefHolder);

        ScanPlaylistFilesDialog.onStopPlaylistScan.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                taskManager.clearTaskIfMatch(0);
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onLibraryQueue2UI_ActionScanStandalonePlaylist.subscribeWeak(new WeakEvent1.Handler<ContextData>() {
            @Override
            public void invoke(ContextData contextData) {

                Context context = PlayerCore.s().getAppContext();
                if (context == null) return;

                resultReceiver.contextData = contextData;

                {
                    ScanPlaylistFilesTask task = ScanPlaylistFilesTask.createScanPlaylistFilesTask(context,
                            new File("/storage/"),
                            new SearchFilter(),
                            new WeakReference<ScanPlaylistFilesTask.IResultReceiver>(resultReceiver)
                    );

                    taskManager.setTask(task, 0);
                    task.start();
                }

                FragmentManager fragmentManager = contextData.getFragmentManager();
                if (fragmentManager != null)
                    ScanPlaylistFilesDialog.createAndShowScanPlaylistFilesDialog(fragmentManager);
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onLibraryQueue2UI_ActionRemoveStandalonePlaylist.subscribeWeak(new WeakEvent4.Handler<Context, String, String, ContextData>() {
            @Override
            public void invoke(Context context, String idhash, String path, ContextData mainDat) {
                onLibraryQueue2UI_SubmitRemoveStandalonePlaylist(context, idhash, path);
            }
        }, listenerRefHolder);


        LibraryQueueFragmentBase.onActionCreatePlaylist.subscribeWeak(new WeakEvent3.Handler<long[], List<String>, ContextData>() {
            @Override
            public void invoke(long[] addSongsNativePL, List<String> addSongDataSources, ContextData contextData) {
                FragmentManager fragmentManager = contextData.getFragmentManager();

                if (fragmentManager != null)
                    CreatePlaylistDialog.createAndShowCreatePlaylistDialog(fragmentManager, addSongsNativePL, addSongDataSources);
            }
        }, listenerRefHolder);

        PlaylistPickerDialog.onActionCreatePlaylist.subscribeWeak(new WeakEvent3.Handler<long[], List<String>, ContextData>() {
            @Override
            public void invoke(long[] addSongsNativePL, List<String> addSongDataSources, ContextData contextData) {
                FragmentManager fragmentManager = contextData.getFragmentManager();

                if (fragmentManager != null)
                    CreatePlaylistDialog.createAndShowCreatePlaylistDialog(fragmentManager, addSongsNativePL, addSongDataSources);
            }
        }, listenerRefHolder);

        CreatePlaylistDialog.onSubmitCreatePlaylist.subscribeWeak(new WeakEvent3.Handler<String, long[], List<String>>() {
            @Override
            public void invoke(String name, long[] addSongsNativePL, List<String> addSongDataSources) {
                if (name != null && name.length() > 0) {

                    Context context = PlayerCore.s().getAppContext();

                    if (context != null) {
                        int numInserted = PlaylistPlatformUtils.createPlaylist(context, name, addSongsNativePL);

                        if (numInserted > 0) {
                            final String message = context.getResources().getQuantityString(
                                    R.plurals.x_items_saved_in_playlist, numInserted, numInserted);
                            EventsGlobalTextNotifier.onTextMsg.invoke(message);
                        }
                    }

                    updateLibraryItems();

                }
            }
        }, listenerRefHolder);

        CreatePlaylistDialog.onSubmitCreateStandalonePlaylist.subscribeWeak(new WeakEvent5.Handler<String, String, PlaylistFilesType, List<String>, Boolean>() {
            @Override
            public void invoke(String destPath, String name, PlaylistFilesType playlistType, List<String> addSongDataSources, Boolean useRelativePaths) {

                Context context = PlayerCore.s().getAppContext();
                if (context == null) return;

                if (destPath == null || destPath.length() <= 0)
                    destPath = AppPreferences.preferencesGetStringSafe(AppPreferences.createOrGetInstance().getPreferences(context), "pref_playlistDefaultPath", "\\Playlists\\");

                if (name != null && name.length() > 0) {

                    String playlistPath = PlaylistFilesUtils.makePlaylistPath(destPath, name, playlistType);
                    int numInserted = PlaylistFilesUtils.s().createPlaylist(playlistPath, playlistType, addSongDataSources, useRelativePaths);

                    if (numInserted > 0) {
                        final String message = context.getResources().getQuantityString(
                                R.plurals.x_items_saved_in_playlist, numInserted, numInserted);
                        EventsGlobalTextNotifier.onTextMsg.invoke(message);
                    }

                    AppPreferences.createOrGetInstance().prefAddStandalonePlaylistGenerateHash(context, playlistPath, true);
                    updateLibraryItems();

                }
            }
        }, listenerRefHolder);
        LibraryQueueFragmentBase.onLibraryQueueUI_ActionSongSendToPlaylist.subscribeWeak(new WeakEvent4.Handler<Context, List<PlaylistSong>, Boolean, ContextData>() {
            @Override
            public void invoke(Context context, List<PlaylistSong> songs, Boolean overwritePL, ContextData contextData) {

                FragmentManager fragmentManager = contextData.getFragmentManager();

                if (fragmentManager != null)
                    PlaylistPickerDialog.createAndShowPlaylistPickerDialog(fragmentManager, songs, overwritePL);
            }
        }, listenerRefHolder);

        PlaylistPickerDialog.onLibraryQueueUI_SubmitSongSendToPlaylist.subscribeWeak(new WeakEvent5.Handler<Context, Long, long[], List<String>, Boolean>() {
            @Override
            public void invoke(Context context, Long playlistId, long[] songsIds, List<String> songDataSources, Boolean overwritePL) {
                int numInserted = PlaylistPlatformUtils.addToPlaylist(context, playlistId, songsIds, overwritePL);

                updateLibraryItems();

                if (overwritePL) {
                    final String message = context.getResources().getQuantityString(
                            R.plurals.x_items_saved_in_playlist, numInserted, numInserted);
                    EventsGlobalTextNotifier.onTextMsg.invoke(message);

                } else {
                    final String message = context.getResources().getQuantityString(
                            R.plurals.x_items_added_to_playlist, numInserted, numInserted);
                    EventsGlobalTextNotifier.onTextMsg.invoke(message);

                }
            }
        }, listenerRefHolder);

        PlaylistPickerDialog.onLibraryQueueUI_SubmitSongSendToStandalonePlaylist.subscribeWeak(new WeakEvent5.Handler<String, String, List<String>, Boolean, Boolean>() {
            @Override
            public void invoke(String playlistIdHash, String playlistPath, List<String> songDataSources, Boolean overwritePL, Boolean useRelativePaths) {
                int numInserted = PlaylistFilesUtils.s().addDataSourceToPlaylistFile(playlistPath, songDataSources, overwritePL, useRelativePaths);

                updateLibraryItems();

                Context context = PlayerCore.s().getAppContext();

                if (context != null) {
                    if (overwritePL) {
                        final String message = context.getResources().getQuantityString(
                                R.plurals.x_items_saved_in_playlist, numInserted, numInserted);
                        EventsGlobalTextNotifier.onTextMsg.invoke(message);

                    } else {
                        final String message = context.getResources().getQuantityString(
                                R.plurals.x_items_added_to_playlist, numInserted, numInserted);
                        EventsGlobalTextNotifier.onTextMsg.invoke(message);

                    }
                }
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onLibraryQueueUI_ActionRenamePlaylist.subscribeWeak(new WeakEvent4.Handler<Context, Long, String, ContextData>() {
            @Override
            public void invoke(Context context, Long playlistId, String currentName, ContextData contextData) {
                FragmentManager fragmentManager = contextData.getFragmentManager();

                if (fragmentManager != null)
                    RenamePlaylistDialog.createAndShowCreateRenamePlaylistDialog(fragmentManager, playlistId, currentName);
            }
        }, listenerRefHolder);

        RenamePlaylistDialog.onSubmitRenamePlaylist.subscribeWeak(new WeakEvent3.Handler<Context, Long, String>() {
            @Override
            public void invoke(Context context, Long playlistId, String newName) {
                PlaylistPlatformUtils.renamePlaylist(context, playlistId, newName);

                updateLibraryItems();
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onLibraryQueueUI_ActionDeletePlaylist.subscribeWeak(new WeakEvent4.Handler<Context, Long, String, ContextData>() {
            @Override
            public void invoke(Context context, Long playlistId, String name, ContextData mainDat) {
                onLibraryQueueUI_SubmitDeletePlaylist(context, playlistId);
            }
        }, listenerRefHolder);

        PlaylistPickerDialog.onRequestStandalonePlaylists.subscribeWeak(new WeakEventR.Handler<MultiList<String, String>>() {
            @Override
            public MultiList<String, String> invoke() {
                Context context = PlayerCore.s().getAppContext();
                if (context == null) return null;

                return AppPreferences.createOrGetInstance().prefGetStandalonePlaylists(context);
            }
        }, listenerRefHolder);

    }

    private void onScanCompleteResults(ContextData contextData, List<String> resultItems) {
        Context context = contextData.getContext();
        if (context == null) return;

        //idhash, path
        MultiList<String, String> savedPlaylists = AppPreferences.createOrGetInstance().prefGetStandalonePlaylists(context);

        List<String> newPlaylists = new ArrayList<>();

        for (String playlistPath : resultItems) {

            if (savedPlaylists.contains2(playlistPath)) continue;

            newPlaylists.add(playlistPath);
        }

        DialogInterface.OnClickListener clickListener = new ScanCompleteDialogClickListener(newPlaylists);

        int numCount = newPlaylists.size();
        final String message = context.getResources().getQuantityString(
                R.plurals.x_items_found_playlist_scan, numCount, numCount);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_playlist_scan_completed);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.dialog_ok, clickListener);

        builder.create().show();
    }

    private void updateLibraryItems() {
        Fragment0 fragment0 = MainActivity.getFragment0Instance();
        if (fragment0 != null) fragment0.updateLibraryItems();
    }

    private void onLibraryQueue2UI_SubmitScanStandalonePlaylist(List<String> playlistPaths) {

            Context context = PlayerCore.s().getAppContext();
            if (context == null) return;

            AppPreferences.createOrGetInstance().prefAddStandalonePlaylistGenerateHash(context, playlistPaths, true);
            updateLibraryItems();
    }

    private void onLibraryQueue2UI_SubmitRemoveStandalonePlaylist(Context context, String idhash, String path) {
            AppPreferences.createOrGetInstance().prefRemoveStandalonePlaylist(idhash, path, context);
            updateLibraryItems();
    }

    private void onLibraryQueueUI_SubmitDeletePlaylist(Context context, Long playlistId) {
            int num = PlaylistPlatformUtils.deletePlaylist(context, playlistId);

            if (num > 0) {
                final String message = context.getResources().getString(R.string.playlist_deleted);
                EventsGlobalTextNotifier.onTextMsg.invoke(message);
            }

            updateLibraryItems();
    }

    public static class PlaylistScanningStatus {
        public String text;
        public boolean active;

    }

    public static class SearchFilter implements ScanPlaylistFilesTask.FilterComparable<File> {
        public SearchFilter() {
        }

        @Override
        public String preProcessQuery(String text) {
            return text.toLowerCase();
        }

        @Override
        public void preProcessItem(File item) {

        }

        @Override
        public boolean compare(String text, File item) {
            String ext = UtilsFileSys.extractFilenameExt(item);
            return PlaylistFilesType.isPlaylistFileExtension(ext);
        }
    }

    class ScanCompleteDialogClickListener implements DialogInterface.OnClickListener {

        List<String> resultItems;

        public ScanCompleteDialogClickListener(List<String> resultItems) {
            this.resultItems = resultItems;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    onLibraryQueue2UI_SubmitScanStandalonePlaylist(resultItems);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    }

    class ScanResultReceiver implements ScanPlaylistFilesTask.IResultReceiver {

        ContextData contextData = null;

        @Override
        public void onStarted(AsyncTask task) {
            if (!taskManager.compareTask(task, 0)) return;

            playlistScanningStatus.active = true;
            playlistScanningStatus.text = "Starting";

            ScanPlaylistFilesDialog.updatePlaylistScanStatus(playlistScanningStatus);
        }


        @Override
        public void onFinished(AsyncTask task, boolean allFinished, List<String> resultItems) {
            if (!taskManager.compareTask(task, 0)) return;

            playlistScanningStatus.active = false;
            playlistScanningStatus.text = "Finished";

            ScanPlaylistFilesDialog.updatePlaylistScanStatus(playlistScanningStatus);

            if (allFinished && resultItems != null) {
                if (contextData != null)
                    onScanCompleteResults(contextData, resultItems);
            }
        }

        @Override
        public void onItem(AsyncTask task, String itemPath) {
            taskManager.compareTask(task, 0);
        }

        @Override
        public void onStatusUpdate(AsyncTask task, String statusText) {
            if (!taskManager.compareTask(task, 0)) return;

            playlistScanningStatus.active = true;
            playlistScanningStatus.text = statusText;

            ScanPlaylistFilesDialog.updatePlaylistScanStatus(playlistScanningStatus);
        }
    }
}
