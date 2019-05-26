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

import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Utils;
import com.aveeopen.Common.tlog;
import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.ViewAdapter;
import com.aveeopen.ContextData;

import junit.framework.Assert;

import java.lang.ref.WeakReference;
import java.util.List;

public class CompositeContainer implements IContainerData {

    private IOnDraggingListener onDraggingListener;
    private IContainerData[] containerData;
    private ContainerStatusListenerWrap[] containerStatusListenerWraps;
    private IContainerStatusListener containerStatusListener = null;
    private LibraryContainerDataListenerWrap[] libraryContainerDataListenerWrap;
    private ILibraryContainerDataListener libraryContainerDataListener = null;
    private String dispString;
    private int dispIconResId;
    private AdapterFactory adapterFactory;

    public CompositeContainer(IContainerData[] containerData, String dispString, int dispIconResId, AdapterFactory adapterFactory) {
        this.containerData = containerData;
        this.dispString = dispString;
        this.dispIconResId = dispIconResId;
        this.adapterFactory = adapterFactory;

        containerStatusListenerWraps = new ContainerStatusListenerWrap[containerData.length];
        for (int i = 0; i < containerStatusListenerWraps.length; i++)
            containerStatusListenerWraps[i] = new ContainerStatusListenerWrap();

        libraryContainerDataListenerWrap = new LibraryContainerDataListenerWrap[containerData.length];
        for (int i = 0; i < libraryContainerDataListenerWrap.length; i++)
            libraryContainerDataListenerWrap[i] = new LibraryContainerDataListenerWrap(i);
    }

    static String makePrefix(int index) {
        return String.format(java.util.Locale.US, "%05d", index);
    }

    private void updateContainerStatusListener() {
        if (containerStatusListener == null) return;

        int itemCount = 0;
        int totalTime = 0;
        boolean searchingActive = false;

        for (ContainerStatusListenerWrap containerStatusListenerWrap : containerStatusListenerWraps) {
            itemCount += containerStatusListenerWrap.itemCount;
            totalTime += containerStatusListenerWrap.totalTime;
            if (containerStatusListenerWrap.searchingActive)
                searchingActive = true;
        }

        containerStatusListener.onItemCountChanged(itemCount, totalTime, searchingActive);
    }

    @Override
    public ViewAdapter createOrGetAdapter(Context context) {
        return createOrGetAdapter(context, -1);
    }

    @Override
    public ViewAdapter createOrGetAdapter(Context context, int type) {
        return adapterFactory.createOrGetAdapter(context, type, this);
    }

    @Override
    public String getDisplayName() {
        return dispString;
    }

    @Override
    public int getDispalyIconResId() {
        return dispIconResId;
    }

    @Override
    public String getLibraryAddress() {
        return containerData[0].getLibraryAddress();
    }

    @Override
    public String makeChildAddress(String item) {
        return containerData[0].makeChildAddress(item);
    }

    @Override
    public ViewAdapter createChildAdapter(Context context, String relativeAddressItem) {
        if (relativeAddressItem.length() > 5) {

            String prefixStr = relativeAddressItem.substring(0, 5);
            String itemWoPrefix = relativeAddressItem.substring(5);
            int prefixInt = Utils.strToIntSafe(prefixStr, -1);

            if (prefixInt >= 0 && prefixInt < containerData.length)
                return containerData[prefixInt].createChildAdapter(context, itemWoPrefix);
            else
                tlog.w("invalid prefix value " + prefixInt);
        } else {
            tlog.w("invalid relativeAddressItem <" + relativeAddressItem + ">");
        }

        return null;
    }

    @Override
    public boolean onListViewClick(int itemPosition, Context context) {
        Assert.fail();
        return false;
    }

    @Override
    public boolean containsContainerIdentifier(IGeneralItemContainerIdentifier containerIdentifier) {
        for (IContainerData aContainerData : containerData)
            if (aContainerData.containsContainerIdentifier(containerIdentifier)) return true;
        return false;
    }

    @Override
    public ILibraryContainerDataListener getLibraryContainerDataListener() {
        if(containerData.length>0)
            return containerData[0].getLibraryContainerDataListener();

        return null;
    }

    @Override
    public void getSearchOptions(Context context, String[] outSearchHint, IGeneralItemContainerIdentifier[] outContainerIdentifier) {
        containerData[0].getSearchOptions(context, outSearchHint, outContainerIdentifier);
    }

    @Override
    public void updateSearchQuery(Context context, String query) {
        for (IContainerData aContainerData : containerData)
            aContainerData.updateSearchQuery(context, query);
    }

    @Override
    public void executeItemActionHeader(ContextData contextData, int index) {
        for (IContainerData aContainerData : containerData)
            aContainerData.executeItemActionHeader(contextData, index);
    }

    @Override
    public void setOnDraggingListener(IOnDraggingListener listener) {
        onDraggingListener = listener;
    }

    @Override
    public void setContainerStatusListener(WeakReference<IContainerStatusListener> listener) {
        containerStatusListener = listener.get();
        for (int i = 0; i < containerData.length; i++)
            containerData[i].setContainerStatusListener(new WeakReference<IContainerStatusListener>(containerStatusListenerWraps[i]));
    }

    @Override
    public IOnDraggingListener getOnDraggingListener() {
        return onDraggingListener;
    }

    @Override
    public void setLibraryContainerDataListener(WeakReference<ILibraryContainerDataListener> listener) {
        libraryContainerDataListener = listener.get();
        for (int i = 0; i < containerData.length; i++)
            containerData[i].setLibraryContainerDataListener(new WeakReference<ILibraryContainerDataListener>(libraryContainerDataListenerWrap[i]));
    }

    @Override
    public int getItemCount() {
        int items = 0;
        for (IContainerData aContainerData : containerData)
            items += aContainerData.getItemCount();

        return items;
    }

    @Override
    public boolean getSectionOpened() {
        return true;
    }

    @Override
    public void setSectionOpenedState(boolean state) {
    }

    @Override
    public boolean getShowAlbumArtValue() {
        return containerData[0].getShowAlbumArtValue();
    }

    public interface AdapterFactory {
        ViewAdapter createOrGetAdapter(Context context, int type, IContainerData libraryContainerData);
    }

    class ContainerStatusListenerWrap implements IContainerStatusListener {
        int itemCount = 0;
        int totalTime = 0;
        boolean searchingActive = false;

        @Override
        public void onItemCountChanged(int itemCount, int totalTime, boolean searchingActive) {
            this.itemCount = itemCount;
            this.totalTime = totalTime;
            this.searchingActive = searchingActive;

            CompositeContainer.this.updateContainerStatusListener();
        }
    }

    class LibraryContainerDataListenerWrap implements ILibraryContainerDataListener {
        int index;

        public LibraryContainerDataListenerWrap(int index) {
            this.index = index;
        }

        @Override
        public void onNavigateForward(String currentAddress, String relativeAddress) {
            libraryContainerDataListener.onNavigateForward(currentAddress, makePrefix(index) + relativeAddress);
        }

        @Override
        public String makeChildAddress(String currentAddress, String childItemAddress) {
            return libraryContainerDataListener.makeChildAddress(currentAddress, makePrefix(index) + childItemAddress);
        }

        @Override
        public int onRequestShuffleMode() {
            return libraryContainerDataListener.onRequestShuffleMode();
        }

        @Override
        public void subscribeWeakShuffleModeChanged(WeakEvent1.Handler<Integer> listener, List<Object> listenerRefHolder) {
            libraryContainerDataListener.subscribeWeakShuffleModeChanged(listener, listenerRefHolder);
        }

        @Override
        public void subscribeWeakFollowCurrentValueChanged(WeakEvent1.Handler<Boolean> listener, List<Object> listenerRefHolder) {
            libraryContainerDataListener.subscribeWeakFollowCurrentValueChanged(listener, listenerRefHolder);
        }

        @Override
        public void subscribeWeakShowAlbumArtValueChanged(WeakEvent1.Handler<Boolean> listener, List<Object> listenerRefHolder) {
            libraryContainerDataListener.subscribeWeakShowAlbumArtValueChanged(listener, listenerRefHolder);
        }
    }
}
