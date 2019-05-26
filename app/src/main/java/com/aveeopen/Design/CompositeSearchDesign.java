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

import android.os.AsyncTask;

import com.aveeopen.Common.Events.WeakEventR2;
import com.aveeopen.comp.LibraryQueueUI.Fragment1;
import com.aveeopen.comp.LibraryQueueUI.LibraryQueueFragmentBase;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.Utils;
import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.comp.Common.ISearchEntry;
import com.aveeopen.comp.GlobalSearch.GlobalSearchCore;
import com.aveeopen.comp.GlobalSearch.SearchEntry;
import com.aveeopen.comp.GlobalSearch.SearchEntryOptions;
import com.aveeopen.comp.GlobalSearch.SearchTaskManager;
import com.aveeopen.comp.LibraryQueueUI.Fragment0;
import com.aveeopen.MainActivity;

import java.util.LinkedList;
import java.util.List;

public class CompositeSearchDesign {

    private static SearchTaskManager searchTaskManager = new SearchTaskManager();
    private List<Object> listenerRefHolder = new LinkedList<>();

    public CompositeSearchDesign() {

        GlobalSearchCore.ICompositeSearch_onCurrentSearchEntryChanged.subscribeWeak(new WeakEvent4.Handler<Integer, Integer, ISearchEntry, Boolean>() {
            @Override
            public void invoke(Integer currentIndex, Integer index, ISearchEntry
                    searchEntry, Boolean queryChangedToo) {

                if (searchEntry == null || searchEntry.getQuery() == null || searchEntry.getQuery().isEmpty())
                    searchTaskManager.clearTaskIfMatch(index);

                MainActivity mainActivity = MainActivity.getInstance();
                if (mainActivity == null) return;

                if (currentIndex.equals(index))
                    mainActivity.updateSearchView(searchEntry, false);

                if (queryChangedToo &&searchEntry != null) {
                    if (index == MainActivity.LIBRARY_PAGE_INDEX) {
                        Fragment0 fragment0 = MainActivity.getFragment0Instance();
                        if (fragment0 != null)
                            fragment0.updateSearchQuery(mainActivity, searchEntry.getQuery());
                    } else if (index == MainActivity.QUEUE_PAGE_INDEX) {
                        Fragment1 fragment1 = MainActivity.getFragment1Instance();
                        if (fragment1 != null)
                            fragment1.updateSearchQuery(mainActivity, searchEntry.getQuery());
                    }
                }
            }
        }
        , listenerRefHolder);

        MainActivity.onUISearchQueryTextChange.subscribeWeak(new WeakEvent2.Handler<Integer, java.lang.String>() {
            @Override
            public void invoke(Integer index, String query) {
                GlobalSearchCore globalSearchCore = GlobalSearchCore.getInstance();
                if (globalSearchCore == null) return;

                globalSearchCore.onSearchQueryTextChange(query);
            }
        }, listenerRefHolder);

        SearchTaskManager.onUISearchQueryTextChangeWithIndex.subscribeWeak(new WeakEvent2.Handler<Integer, java.lang.String>() {
            @Override
            public void invoke(Integer index, String query) {
                GlobalSearchCore globalSearchCore = GlobalSearchCore.getInstance();
                if (globalSearchCore == null) return;

                globalSearchCore.onSearchQueryTextChange(index, query);
            }
        }, listenerRefHolder);

        MainActivity.onUISearchQueryStateChange.subscribeWeak(new WeakEvent1.Handler<Boolean>() {
            @Override
            public void invoke(Boolean enabled) {
                GlobalSearchCore globalSearchCore = GlobalSearchCore.getInstance();
                if (globalSearchCore == null) return;

                globalSearchCore.onSearchQueryTextChange(null);
            }
        }, listenerRefHolder);

        MainActivity.onSetCurrentSearchIndex.subscribeWeak(new WeakEvent1.Handler<Integer>() {
            @Override
            public void invoke(final Integer index) {

                GlobalSearchCore globalSearchCore = GlobalSearchCore.getInstance();
                if (globalSearchCore == null) return;

                SearchEntryOptions searchOptions = SearchEntryOptions.refuse;

                if (index == MainActivity.LIBRARY_PAGE_INDEX) {
                    Fragment0 fragment0 = MainActivity.getFragment0Instance();
                    if (fragment0 != null) searchOptions = fragment0.getSearchEntryOptions();

                } else if (index == MainActivity.QUEUE_PAGE_INDEX) {
                    Fragment1 fragment1 = MainActivity.getFragment1Instance();
                    if (fragment1 != null) searchOptions = fragment1.getSearchEntryOptions();
                }

                if (searchOptions != SearchEntryOptions.refuse) {
                    if (searchOptions != null)
                        globalSearchCore.onUpdateSearchOptions(index, searchOptions.enabled, searchOptions.hint, searchOptions.containerIdentifier);
                    else
                        globalSearchCore.onUpdateSearchOptions(index, false, "", null);
                }

                globalSearchCore.onSetCurrentSearchIndex(index);
            }
        }, listenerRefHolder);

        MainActivity.onRequestCurrentSearchEntry.subscribeWeak(new WeakEventR.Handler<ISearchEntry>() {
            @Override
            public ISearchEntry invoke() {
                GlobalSearchCore globalSearchCore = GlobalSearchCore.getInstance();
                if (globalSearchCore == null) return null;

                return globalSearchCore.getCurrentSearchEntry();
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onRequestSearchQuery.subscribeWeak(new WeakEventR2.Handler<Integer, IGeneralItemContainerIdentifier, String>() {
            @Override
            public String invoke(Integer pageIndex, IGeneralItemContainerIdentifier containerIdentifier) {
                GlobalSearchCore globalSearchCore = GlobalSearchCore.getInstance();
                if (globalSearchCore == null) return null;

                SearchEntry entry = globalSearchCore.getSearchEntry(pageIndex);
                if (entry == null) return null;

                if (Utils.compareNullEqual(entry.getContainerIdentifier(), containerIdentifier))
                    return entry.getQuery();
                else
                    return null;
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onUpdateSearchOptions.subscribeWeak(new WeakEvent4.Handler<Integer, Boolean, String, IGeneralItemContainerIdentifier>() {
            @Override
            public void invoke(Integer index, Boolean enabled, String hint, IGeneralItemContainerIdentifier containerIdentifier) {
                GlobalSearchCore globalSearchCore = GlobalSearchCore.getInstance();
                if (globalSearchCore == null) return;

                globalSearchCore.onUpdateSearchOptions(index, enabled, hint, containerIdentifier);
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onCompareSearchTask.subscribeWeak(new WeakEventR2.Handler<AsyncTask, Integer, Boolean>() {
            @Override
            public Boolean invoke(AsyncTask task, Integer pageIndex) {
                return searchTaskManager.compareTask(task, pageIndex);
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onStartingSearchTask.subscribeWeak(new WeakEvent3.Handler<AsyncTask, Integer, Object>() {
            @Override
            public void invoke(AsyncTask task, Integer pageIndex, Object param) {
                searchTaskManager.setTask(task, pageIndex);
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onContainerDataSetChanged.subscribeWeak(new WeakEvent1.Handler<Integer>() {
            @Override
            public void invoke(Integer pageIndex) {
                searchTaskManager.clearTaskIfMatch(pageIndex);
            }
        }, listenerRefHolder);
    }

}
