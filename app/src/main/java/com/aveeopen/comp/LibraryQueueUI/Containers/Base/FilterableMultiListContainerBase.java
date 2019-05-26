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

import com.aveeopen.Common.MultiList;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.PlayerCore;
import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.comp.GlobalSearch.SearchMultiListTask;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.IAdapter;

import java.lang.ref.WeakReference;

public abstract class FilterableMultiListContainerBase<T1, T2> extends MultiListContainerBase<T1, T2> {

    protected MultiList<T1, T2> originalList;
    protected MultiList<T1, T2> visibleList;

    SearchMultiListTask.IResultReceiver<T1, T2> resultReceiver = new SearchMultiListTask.IResultReceiver<T1, T2>() {

        boolean compareTask(AsyncTask task) {
            return onCompareSearchTask.invoke(task, pageIndex, false);
        }

        @Override
        public void onSearchStarted(AsyncTask task) {
            if (visibleList == null) return;
            if (!compareTask(task)) return;
            FilterableMultiListContainerBase.this.setSearchActive(true);
            FilterableMultiListContainerBase.this.clearDataAndNotify();
        }

        @Override
        public void onSearchFinished(AsyncTask task, boolean allFinished) {
            FilterableMultiListContainerBase.this.setSearchActive(false);
            if (visibleList == null) return;
            if (!compareTask(task)) return;
            FilterableMultiListContainerBase.this.dataChangedNotify();
        }

        @Override
        public void onItemDirFound(AsyncTask task, Tuple2<T1, T2> itemDir) {
            if (visibleList == null) return;
            if (!compareTask(task)) return;
            FilterableMultiListContainerBase.this.setSearchActive(true);
        }

        @Override
        public void onItemFileFound(AsyncTask task, Tuple2<T1, T2> itemFile) {
            if (visibleList == null) return;
            if (!compareTask(task)) return;
            FilterableMultiListContainerBase.this.addDataAndNotify(itemFile);
            FilterableMultiListContainerBase.this.setSearchActive(true);
        }
    };

    protected FilterableMultiListContainerBase(Context context,
                                               MultiList<T1, T2> list,
                                               String libraryAddress,
                                               String displayName,
                                               int displayIconResId,
                                               FilterComparable<T1> filter,
                                               int pageIndex) {
        super(context, list.unmodifiableList(), libraryAddress, displayName, displayIconResId, pageIndex);

        originalList = list.unmodifiableList();
        final IGeneralItemContainerIdentifier containerIdentifier = getSelectionContainerIdentifier();
        String searchText = onRequestSearchQuery.invoke(pageIndex, containerIdentifier, "");
        updateSearchQuery(context, searchText, filter);
    }


    @Override
    public void setDataAndNotifyDataSetChanged(MultiList<T1, T2> list, String itemsIdent) {
        onContainerDataSetChanged.invoke(pageIndex);
        originalList = list.unmodifiableList();
        super.setDataAndNotifyDataSetChanged(originalList, itemsIdent);
    }

    public void updateSearchQuery(Context context, String query, FilterComparable<T1> filter) {
        if (query == null || query.isEmpty() || filter == null) {
            visibleList = null;
            this.setSearchActive(false);
            super.setDataAndNotifyDataSetChanged(originalList, "");
        } else {
            visibleList = new MultiList<>();
            this.setSearchActive(true);
            super.setDataAndNotifyDataSetChanged(visibleList, query);
            startSearch(query, filter);
        }

    }

    void startSearch(String query, FilterComparable<T1> filter) {
        Context context = PlayerCore.s().getAppContext();
        if (context == null) return;

        SearchMultiListTask<T1, T2> searchTask = new SearchMultiListTask<>(context,
                originalList,
                filter,
                new WeakReference<>(resultReceiver));

        onStartSearchTask.invoke(searchTask, pageIndex, query);
        searchTask.execute(query);
    }

    protected void clearDataAndNotify() {
        clearItemIdent();
        visibleList.clear();
        IAdapter adapter = getAssociatedAdapter();
        if (adapter != null)
            adapter.myNotifyDataSetChanged();
    }

    protected void addDataAndNotify(Tuple2<T1, T2> item) {
        clearItemIdent();
        visibleList.add(item);
        IAdapter adapter = getAssociatedAdapter();
        if (adapter != null) {
            //doesn't work right, don't use for now
            //adapter.myNotifyItemInserted(visibleList.size() - 1);
            adapter.myNotifyDataSetChanged();
        }
    }

    protected void dataChangedNotify() {
        IAdapter adapter = getAssociatedAdapter();
        if (adapter != null)
            adapter.myNotifyDataSetChanged();
    }

    public interface FilterComparable<T1> {
        String preProcessQuery(String text);
        void preProcessItem(T1 item);
        boolean compare(String text, T1 item);
    }

}
