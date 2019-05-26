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

import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.ViewAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class FilterableListContainerBase<T> extends ListContainerBase<T> implements ViewAdapter.IAdapterDataProvider {
    
    protected List<T> originalList;
    protected List<T> visibleList;
    private FilterComparable<T> filter = null;
    private String queryText = null;
    
    protected FilterableListContainerBase(Context context, List<T> list, String libraryAddress, String displayName, int displayIconResId, int pageIndex) {
        super(context, list, libraryAddress, displayName, displayIconResId, pageIndex);

        originalList = list;
        updateFiltered();
    }

    @Override
    protected void setDataAndNotifyDataSetChanged(List<T> list, String itemsIdent) {
        if (checkItemIdent(itemsIdent)) return;
        originalList = list;
        updateFiltered();
    }

    public void setFilter(String queryText, FilterComparable<T> filter) {

        if (this.queryText.equals(queryText) && this.filter == filter) return;

        if (queryText == null || queryText.isEmpty() || filter == null) {
            this.filter = null;
            this.queryText = null;
        } else {
            this.filter = filter;
            this.queryText = filter.preProcessQuery(queryText);
        }

        updateFiltered();
    }

    protected void updateFiltered() {

        if (queryText == null) {
            visibleList = null;
            super.setDataAndNotifyDataSetChanged(originalList, "");
        } else {

            if (visibleList == null)
                visibleList = new ArrayList<>();
            else
                visibleList.clear();

            for (T item : originalList)
                filter.preProcessItem(item);

            for (T item : originalList) {
                if (filter.compare(queryText, item))
                    visibleList.add(item);
            }

            super.setDataAndNotifyDataSetChanged(visibleList, queryText);
        }
    }

    public interface FilterComparable<T> {
        String preProcessQuery(String text);
        void preProcessItem(T item);
        boolean compare(String text, T item);
    }
}
