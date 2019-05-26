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

import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.IAdapter;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.ViewAdapter;

import junit.framework.Assert;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class ListContainerBase<T> extends ContainerBase implements ViewAdapter.IAdapterDataProvider {

    private WeakReference<IAdapter> associatedAdapter = new WeakReference<>(null);
    private List<T> list;

    protected ListContainerBase(Context context,
                                List<T> list,
                                String libraryAddress,
                                String displayName,
                                int displayIconResId,
                                int pageIndex) {
        super(context,
                libraryAddress,
                displayName,
                displayIconResId,
                pageIndex);

        Assert.assertNotNull(list);
        this.list = list;
    }

    protected IAdapter getAssociatedAdapter() {
        return associatedAdapter.get();
    }

    @Override
    public void onAdapterInitialized(IAdapter adapter) {
        associatedAdapter = new WeakReference<>(adapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected List<T> getList() {
        return list;
    }

    protected void setDataAndNotifyDataSetChanged(List<T> list, String itemsIdent) {

        if (checkItemIdent(itemsIdent)) return;

        Assert.assertNotNull(list);
        this.list = list;

        IAdapter adapter = getAssociatedAdapter();
        if (adapter != null)
            adapter.myNotifyDataSetChanged();
    }

}
