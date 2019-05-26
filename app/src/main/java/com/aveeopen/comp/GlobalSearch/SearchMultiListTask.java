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

import com.aveeopen.Common.MultiList;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.Common.tlog;
import com.aveeopen.comp.LibraryQueueUI.Containers.Base.FilterableMultiListContainerBase;

import java.lang.ref.WeakReference;

public class SearchMultiListTask<T1, T2> extends AsyncTask<String, Object, Void> {

    private MultiList<T1, T2> originalList;
    private FilterableMultiListContainerBase.FilterComparable<T1> searchFilter;
    private WeakReference<IResultReceiver<T1, T2>> receiver;
    private int fileCounter = 0;

    SearchMultiListTask.IResultReceiverInternal<T1, T2> resultReceiver0 = new IResultReceiverInternal<T1, T2>() {

        @Override
        public void onItemDirFound(final Tuple2<T1, T2> itemDir) {

        }

        @Override
        public void onItemFileFound(final Tuple2<T1, T2> itemFile) {
            fileCounter++;
            SearchMultiListTask.this.publishProgress(itemFile);
        }

        @Override
        public boolean isCancelled() {
            return SearchMultiListTask.this.isCancelled();
        }
    };

    public SearchMultiListTask(Context context,
                               MultiList<T1, T2> originalList,
                               FilterableMultiListContainerBase.FilterComparable<T1> searchFilter,
                               WeakReference<IResultReceiver<T1, T2>> receiver) {

        this.originalList = originalList;
        this.searchFilter = searchFilter;
        this.receiver = receiver;
    }

    @Override
    public void onPreExecute() {
        IResultReceiver rcv = receiver.get();
        if (rcv != null) rcv.onSearchStarted(SearchMultiListTask.this);
    }

    @Override
    public void onPostExecute(Void result) {
        IResultReceiver rcv = receiver.get();
        if (rcv != null) rcv.onSearchFinished(SearchMultiListTask.this, true);
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        IResultReceiver<T1, T2> rcv = receiver.get();
        if (rcv != null) rcv.onItemFileFound(SearchMultiListTask.this, (Tuple2<T1, T2>)values[0]);
    }

    @Override
    protected void onCancelled(Void aVoid) {
        IResultReceiver rcv = receiver.get();
        if (rcv != null) rcv.onSearchFinished(SearchMultiListTask.this, false);
    }

    @Override
    protected Void doInBackground(String... params) {
        String _query = params[0];
        String query = null;
        if (searchFilter == null) return null;

        try {
            if (_query != null && !_query.isEmpty()) {
                query = searchFilter.preProcessQuery(_query);
            }

            for (Tuple2<T1, T2> item : originalList) {
                if (resultReceiver0.isCancelled()) break;
                searchFilter.preProcessItem(item.obj1);
            }

            for (Tuple2<T1, T2> item : originalList) {
                if (resultReceiver0.isCancelled()) break;
                if (searchFilter.compare(query, item.obj1))
                    resultReceiver0.onItemFileFound(item);
            }
        } catch (Exception e)
        {
            tlog.w("doInBackground Exception: "+e.getMessage());
        }

        return null;
    }

    public interface IResultReceiver<T1, T2> {
        void onSearchStarted(AsyncTask task);

        void onSearchFinished(AsyncTask task, boolean allFinished);

        void onItemDirFound(AsyncTask task, Tuple2<T1, T2> itemDir);

        void onItemFileFound(AsyncTask task, Tuple2<T1, T2> itemFile);
    }

    private interface IResultReceiverInternal<T1, T2> {
        void onItemDirFound(Tuple2<T1, T2> itemDir);

        void onItemFileFound(Tuple2<T1, T2> itemFile);

        boolean isCancelled();
    }

}