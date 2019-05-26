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

package com.aveeopen.comp.GlobalSearch;

import android.content.Context;
import android.os.AsyncTask;

import com.aveeopen.comp.LibraryQueueUI.Containers.Base.FilterableMultiListContainerBase;
import com.aveeopen.comp.LibraryQueueUI.Containers.ContainerFile;
import com.aveeopen.comp.playback.Song.PlaylistSong;

import java.io.File;
import java.lang.ref.WeakReference;

public class SearchFilesTask extends AsyncTask<String, Object, Void> {

    private Context context;
    private File rootFile;
    private FilterableMultiListContainerBase.FilterComparable searchFilter;
    private WeakReference<IResultReceiver> receiver;
    private int fileCounter = 0;

    SearchFilesTask.IResultReceiverInternal resultReceiver0 = new IResultReceiverInternal() {

        @Override
        public void onItemDirFound(final ContainerFile.Item itemDir) {

        }

        @Override
        public void onItemFileFound(final ContainerFile.Item itemFile) {
            itemFile.setIndex(fileCounter);
            fileCounter++;

            SearchFilesTask.this.publishProgress(itemFile);

        }

        @Override
        public boolean isCancelled() {
            return SearchFilesTask.this.isCancelled();
        }
    };

    public SearchFilesTask(Context context, File rootFile, FilterableMultiListContainerBase.FilterComparable<PlaylistSong> searchFilter, WeakReference<IResultReceiver> receiver) {

        this.context = context;
        this.rootFile = rootFile;
        this.searchFilter = searchFilter;
        this.receiver = receiver;

    }

    //searches files
    //excludeDirs: false -  searches subdirectories
    static boolean getItemsSearch(Context context, IResultReceiverInternal result, File f, boolean excludeDirs, String query, FilterableMultiListContainerBase.FilterComparable searchFilter) {
        String queryProcessed;
        if (query == null || query.isEmpty() || searchFilter == null) {
            queryProcessed = null;
            searchFilter = null;
        } else {
            queryProcessed = searchFilter.preProcessQuery(query);
        }

        return getItemsRecursive(context, result, f, excludeDirs, queryProcessed, searchFilter);//true: finished to end
    }

    static boolean getItemsRecursive(Context context,
                                     IResultReceiverInternal result,
                                     File f,
                                     boolean excludeDirs,
                                     String query,
                                     FilterableMultiListContainerBase.FilterComparable searchFilter) {

        File[] dirs = f.listFiles();

        try {
            for (File ff : dirs) {
                if (result.isCancelled()) return false;

                if (ff.isDirectory()) {

                    if (!excludeDirs) {

                        if (searchFilter == null) {
                            File[] fbuf = ff.listFiles();
                            int buf;
                            if (fbuf != null) {
                                buf = fbuf.length;
                            } else buf = 0;

                            result.onItemDirFound(new ContainerFile.Item(true, ff.getName(), buf, ff.getCanonicalPath(), ff.lastModified()));
                        } else {
                            //sub dir search
                            if (!getItemsRecursive(context, result, ff, false, query, searchFilter))
                                return false;
                        }
                    }
                } else {
                    //search by meta data
                    //if(_searchFilter==null || _searchFilter.compare(_query, playlistsong)) {

                    //search by filename - faster
                    boolean include = true;
                    if (searchFilter != null)
                        include = ff.getName().toLowerCase().contains(query);

                    if (include) {

                        PlaylistSong playlistsong = new PlaylistSong(ff);
                        result.onItemFileFound(new ContainerFile.Item(false,
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

        return true;
    }

    @Override
    public void onPreExecute() {
        IResultReceiver rcv = receiver.get();
        if (rcv != null) rcv.onSearchStarted(SearchFilesTask.this);
    }


    @Override
    public void onPostExecute(Void result) {
        IResultReceiver rcv = receiver.get();
        if (rcv != null) rcv.onSearchFinished(SearchFilesTask.this, true);
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        IResultReceiver rcv = receiver.get();
        if (rcv != null)
            rcv.onItemFileFound(SearchFilesTask.this, (ContainerFile.Item) values[0]);
    }

    @Override
    protected void onCancelled(Void aVoid) {
        IResultReceiver rcv = receiver.get();
        if (rcv != null) rcv.onSearchFinished(SearchFilesTask.this, false);
    }

    @Override
    protected Void doInBackground(String... params) {
        String searchQuery = params[0];
        getItemsSearch(context, resultReceiver0, rootFile, false, searchQuery, searchFilter);
        return null;
    }

    public interface IResultReceiver {
        void onSearchStarted(AsyncTask task);

        void onSearchFinished(AsyncTask task, boolean allFinished);

        void onItemDirFound(AsyncTask task, ContainerFile.Item itemDir);

        void onItemFileFound(AsyncTask task, ContainerFile.Item itemFile);
    }


    private interface IResultReceiverInternal {
        void onItemDirFound(ContainerFile.Item itemDir);

        void onItemFileFound(ContainerFile.Item itemFile);

        boolean isCancelled();
    }
}