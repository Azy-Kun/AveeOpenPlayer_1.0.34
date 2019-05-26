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

package com.aveeopen.comp.LibraryQueueUI;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.aveeopen.Common.UtilsUI;
import com.aveeopen.MainActivity;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.ViewAdapter;
import com.aveeopen.comp.LibraryQueueUI.Containers.Base.IContainerData;
import com.aveeopen.comp.LibraryQueueUI.Containers.ContainerRootLocal;
import com.aveeopen.comp.GlobalSearch.SearchEntryOptions;
import com.aveeopen.R;

import junit.framework.Assert;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class Fragment0 extends LibraryQueueFragmentBase {

    private HashMap<String, ContainerScrollData> pathContainerData = new HashMap<>();
    private String currentAbsoluteLibraryAddress = "";
    private View backSwipeProgress;
    private PagerSlidingTabStrip pathTabStrip;
    private RecyclerView recyclerViewItems;
    private RecyclerView.LayoutManager layoutManager;
    private ViewAdapter libraryRootAdapter;

    public Fragment0() {
    }

    public static Fragment0 newInstance() {
        Fragment0 fragment = new Fragment0();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        currentAbsoluteLibraryAddress =
                AppPreferences.createOrGetInstance().getString(AppPreferences.PREF_String_currentAbsoluteLibraryAddress);
        if (currentAbsoluteLibraryAddress == null) currentAbsoluteLibraryAddress = "";

        navigateLibraryAddress(null, currentAbsoluteLibraryAddress);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentAbsoluteLibraryAddress =
                AppPreferences.createOrGetInstance().getString(AppPreferences.PREF_String_currentAbsoluteLibraryAddress);
        if (currentAbsoluteLibraryAddress == null) currentAbsoluteLibraryAddress = "";

        View rootView = inflater.inflate(R.layout.fragment_0, container, false);

        setStatusBarDimensions(rootView.findViewById(R.id.viewStatusBarBg));

        backSwipeProgress = rootView.findViewById(R.id.backSwipeProgress);
        navigateForBackwardProgress(0.0f);

        pathTabStrip = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabStripPath);

        pathTabStrip.setTextColor(getResources().getColor(R.color.text_color_m2));
        pathTabStrip.setTextColorSelected(getResources().getColor(R.color.text_color_m1));

        pathTabStrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String absoluteAddress = (String) v.getTag();
                navigateLibraryAddress(null, absoluteAddress);

            }
        });

        recyclerViewItems = (RecyclerView) rootView.findViewById(R.id.recyclerViewItems);
        {
            xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller fastScroller =
                    (xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller) rootView.findViewById(R.id.fast_scroller);
            // Connect the recycler to the scroller (to let the scroller scroll the list)
            fastScroller.setRecyclerView(recyclerViewItems);
            // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
            recyclerViewItems.addOnScrollListener(fastScroller.getOnScrollListener());
        }

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewItems.setLayoutManager(layoutManager);

        recyclerViewItems.addItemDecoration(new SpacesItemDecoration(1, UtilsUI.getAttrColor(recyclerViewItems, R.attr.containerBackgroundDark)));

        View tabStripPathGroup = rootView.findViewById(R.id.tabStripPathGroup);
        UtilsUI.disallowInterceptTouchEventRecursive(tabStripPathGroup, tabStripPathGroup.getParent());

        ContainerRootLocal rootData = new ContainerRootLocal(getActivity().getApplicationContext(), MainActivity.LIBRARY_PAGE_INDEX);
        libraryRootAdapter = rootData.createOrGetAdapter(getActivity().getApplicationContext());
        rootData.setLibraryContainerDataListener(new WeakReference<IContainerData.ILibraryContainerDataListener>(this));

        navigateLibraryAddress(null, currentAbsoluteLibraryAddress);
        updateTrackInfo();
        updateSearchInfo();

        return rootView;
    }

    public void updateTrackInfo() {
        if (recyclerViewItems == null) return;

        RecyclerView.Adapter adapter = recyclerViewItems.getAdapter();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public void updateLibraryItems() {
        //re-navigate = refresh
        navigateLibraryAddress(null, currentAbsoluteLibraryAddress, true);
    }

    public void refreshAdapter(IGeneralItemContainerIdentifier containerIdentifier) {
        if (recyclerViewItems == null) return;
        ViewAdapter adapter = (ViewAdapter) recyclerViewItems.getAdapter();
        if (adapter != null && adapter.getContainerData().containsContainerIdentifier(containerIdentifier))
            adapter.notifyDataSetChanged();
    }

    public void navigateForBackwardProgress(float val) {
        if (backSwipeProgress == null) return;
        backSwipeProgress.setPivotX(1);//0 doesn't work
        backSwipeProgress.setScaleX(val);
    }

    public void navigateForBackwardLibraryAddress() {
        String absoluteAddress = currentAbsoluteLibraryAddress;

        int pos;
        if (absoluteAddress.charAt(absoluteAddress.length() - 1) == '/')
            pos = absoluteAddress.lastIndexOf('/', absoluteAddress.length() - 2);
        else
            pos = absoluteAddress.lastIndexOf('/');

        String newAddress;
        if (pos <= 0) {
            newAddress = "/";
        } else {
            newAddress = absoluteAddress.substring(0, pos);
        }

        navigateLibraryAddress(null, newAddress);
    }

    public void navigateLibraryAddress(ViewAdapter currentLocationAdapter, String relativeAddress) {
        navigateLibraryAddress(currentLocationAdapter, relativeAddress, false);
    }

    public void navigateLibraryAddress(ViewAdapter currentLocationAdapter, String relativeAddress, boolean refresh) {
        if (relativeAddress.length() <= 0 || relativeAddress.charAt(0) != '/')
            relativeAddress = "/";

        if (currentLocationAdapter == null)
            currentLocationAdapter = libraryRootAdapter;

        if (!refresh) {
            if (currentLocationAdapter != null && recyclerViewItems != null) {
                ViewAdapter currentAdapter = (ViewAdapter) recyclerViewItems.getAdapter();
                if (currentAdapter != null) {
                    if (currentLocationAdapter.getContainerData().makeChildAddress(relativeAddress).equals(currentAdapter.getContainerData().getLibraryAddress()))
                        return;
                }
            }
        }

        if (relativeAddress.length() > 0 && relativeAddress.charAt(0) == '/') {

            if (currentLocationAdapter == null)
                currentLocationAdapter = libraryRootAdapter;

            pathTabStrip.myClearTabs();
            pathTabStrip.myAddTab(currentLocationAdapter.getContainerData().getDisplayName(),
                    currentLocationAdapter.getContainerData().getDispalyIconResId(), currentLocationAdapter.getContainerData().getLibraryAddress());

            relativeAddress = relativeAddress.substring(1, relativeAddress.length());
        }

        ViewAdapter newCurrentCur = navigateLibraryAddressRecursive(currentLocationAdapter, relativeAddress);
        currentAbsoluteLibraryAddress = newCurrentCur.getContainerData().getLibraryAddress();
        AppPreferences.createOrGetInstance().setString(AppPreferences.PREF_String_currentAbsoluteLibraryAddress, currentAbsoluteLibraryAddress);
        setLibraryAdapter(newCurrentCur);
    }

    public void navigateForwardLibraryAddress(ViewAdapter currentLocationAdapter, String relativeAddress) {
        if (currentLocationAdapter == null)
            currentLocationAdapter = (ViewAdapter) recyclerViewItems.getAdapter();
        if (currentLocationAdapter == null) return;

        ViewAdapter newCurrentCur = navigateLibraryAddressRecursive(currentLocationAdapter, relativeAddress);
        currentAbsoluteLibraryAddress = newCurrentCur.getContainerData().getLibraryAddress();
        AppPreferences.createOrGetInstance().setString(AppPreferences.PREF_String_currentAbsoluteLibraryAddress, currentAbsoluteLibraryAddress);
        setLibraryAdapter(newCurrentCur);
    }

    ViewAdapter navigateLibraryAddressRecursive(ViewAdapter currentLocationAdapter, String relativeAddress) {
        if (relativeAddress.isEmpty()) return currentLocationAdapter;
        int pos = relativeAddress.indexOf('/');

        String currentName;
        String subRelative;
        if (pos < 0) {
            currentName = relativeAddress;
            subRelative = "";
        } else {
            currentName = relativeAddress.substring(0, pos);
            subRelative = relativeAddress.substring(pos + 1, relativeAddress.length());
        }

        ViewAdapter newCurrentCur = currentLocationAdapter.getContainerData().createChildAdapter(getActivity().getApplicationContext(), currentName);

        if (newCurrentCur != null) {
            pathTabStrip.myAddTab(newCurrentCur.getContainerData().getDisplayName(), newCurrentCur.getContainerData().getDispalyIconResId(), newCurrentCur.getContainerData().getLibraryAddress());
            return navigateLibraryAddressRecursive(newCurrentCur, subRelative);
        }

        return currentLocationAdapter;
    }

    void setLibraryAdapter(ViewAdapter newCurrentCur) {
        ViewAdapter oldAdapter = (ViewAdapter) recyclerViewItems.getAdapter();

        boolean backwardNavigate = false;
        boolean sameNavigate = false;

        if (oldAdapter != null) {
            String oldLibraryAddress = oldAdapter.getContainerData().getLibraryAddress();
            String newLibraryAddress = newCurrentCur.getContainerData().getLibraryAddress();

            if (newLibraryAddress.length() < oldLibraryAddress.length() && oldLibraryAddress.contains(newLibraryAddress)) {//backward navigate? or same pos (<=)
                backwardNavigate = true;
            } else if (oldLibraryAddress.equals(newLibraryAddress)) {
                sameNavigate = true;
            }

            if (backwardNavigate) {
                pathContainerData.remove(oldLibraryAddress);
            } else {
                ContainerScrollData scrollData = new ContainerScrollData();
                scrollData.scrollPosition = getScrollPosition();
                pathContainerData.put(oldAdapter.getContainerData().getLibraryAddress(), scrollData);
            }
        }

        if (oldAdapter != newCurrentCur) {
            if (oldAdapter != null)
                oldAdapter.dispose();

            updateSearchInfo(newCurrentCur);

            recyclerViewItems.setAdapter(newCurrentCur);

            if (backwardNavigate || sameNavigate) {
                ContainerScrollData scrollData = pathContainerData.get(newCurrentCur.getContainerData().getLibraryAddress());
                if (scrollData != null) {
                    setScrollPosition(scrollData.scrollPosition);
                }
            }
        }
    }

    public void updateSearchQuery(Context context, String query) {
        if (recyclerViewItems == null) return;
        ViewAdapter adapter = (ViewAdapter) recyclerViewItems.getAdapter();
        if (adapter != null)
            adapter.getContainerData().updateSearchQuery(context, query);
    }

    private void updateSearchInfo(ViewAdapter adapter) {
        Context context = this.getActivity();
        Assert.assertNotNull(context);
        final SearchEntryOptions searchOptions = getSearchEntryOptions(adapter);
        onUpdateSearchOptions.invoke(MainActivity.LIBRARY_PAGE_INDEX, searchOptions.enabled, searchOptions.hint, searchOptions.containerIdentifier);
    }

    private SearchEntryOptions getSearchEntryOptions(ViewAdapter adapter) {
        Context context = this.getActivity();
        if (context == null) return SearchEntryOptions.refuse;
        return getSearchEntryOptions(context, adapter);
    }

    public SearchEntryOptions getSearchEntryOptions() {
        ViewAdapter adapter = recyclerViewItems != null ? (ViewAdapter) recyclerViewItems.getAdapter() : null;
        return getSearchEntryOptions(adapter);
    }

    public void updateSearchInfo() {
        ViewAdapter adapter = recyclerViewItems != null ? (ViewAdapter) recyclerViewItems.getAdapter() : null;
        updateSearchInfo(adapter);
    }

    int getScrollPosition() {
        int scrollPosition = 0;
        RecyclerView.LayoutManager layoutManager = recyclerViewItems.getLayoutManager();
        if (layoutManager != null && layoutManager instanceof LinearLayoutManager) {
            scrollPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        }

        return scrollPosition;
    }

    void setScrollPosition(int scrollPosition) {
        RecyclerView.LayoutManager layoutManager = recyclerViewItems.getLayoutManager();
        if (layoutManager != null) {
            if (scrollPosition != RecyclerView.NO_POSITION) {
                layoutManager.scrollToPosition(scrollPosition);
            }
        }
    }

    class ContainerScrollData {
        public int scrollPosition = 0;
    }

}