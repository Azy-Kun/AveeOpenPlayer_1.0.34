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
import android.view.View;

import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.ViewAdapter;
import com.aveeopen.ContextData;

import java.lang.ref.WeakReference;
import java.util.List;

public interface IContainerData {

    ViewAdapter createOrGetAdapter(Context context);

    ViewAdapter createOrGetAdapter(Context context, int type);

    String getDisplayName();

    int getDispalyIconResId();

    String getLibraryAddress();

    String makeChildAddress(String item);

    ViewAdapter createChildAdapter(Context context, String relativeAddressItem);

    boolean onListViewClick(int itemPosition, final Context context);

    boolean containsContainerIdentifier(IGeneralItemContainerIdentifier containerIdentifier);

    void getSearchOptions(Context contex, String[] outSearchHint, IGeneralItemContainerIdentifier[] outContainerIdentifier);

    void updateSearchQuery(Context context, String query);

    void executeItemActionHeader(ContextData contextData, int index);

    void setOnDraggingListener(IOnDraggingListener listener);

    void setContainerStatusListener(WeakReference<IContainerStatusListener> listener);

    IOnDraggingListener getOnDraggingListener();

    void setLibraryContainerDataListener(WeakReference<ILibraryContainerDataListener> listener);

    ILibraryContainerDataListener getLibraryContainerDataListener();

    int getItemCount();

    boolean getSectionOpened();

    void setSectionOpenedState(boolean state);

    boolean getShowAlbumArtValue();

    interface ILibraryContainerDataListener {
        void onNavigateForward(String currentAddress, String relativeAddress);
        String makeChildAddress(String currentAddress, String childItemAddress);
        int onRequestShuffleMode();
        void subscribeWeakShuffleModeChanged(WeakEvent1.Handler<Integer> listener, List<Object> listenerRefHolder);
        void subscribeWeakFollowCurrentValueChanged(WeakEvent1.Handler<Boolean> listener, List<Object> listenerRefHolder);
        void subscribeWeakShowAlbumArtValueChanged(WeakEvent1.Handler<Boolean> listener, List<Object> listenerRefHolder);
    }

    interface IContainerStatusListener {
        void onItemCountChanged(int itemCount, int totalTime, boolean searchingActive);
    }

    interface IOnDraggingListener {
        void onStartDragging(View itemView);
    }
}
