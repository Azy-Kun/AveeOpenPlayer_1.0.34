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

package com.aveeopen.comp.LibraryQueueUI.Containers.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.aveeopen.comp.LibraryQueueUI.Containers.Base.IContainerData;
import com.aveeopen.comp.LibraryQueueUI.ViewHolders.BaseViewHolder;
import com.aveeopen.comp.LibraryQueueUI.ViewHolders.ViewHolderFactory;

import java.lang.ref.WeakReference;
import java.util.List;

public class HeaderFooterAdapterData implements ViewAdapter.IAdapterDataProvider {

    private WeakReference<IAdapter> adapter = new WeakReference<>(null);
    private ViewAdapter.IAdapterDataProvider adapterData;
    private int headerViewType;
    private int footerViewType;
    private IContainerData containerData;
    private IAdapter adapterWrap;//must hold reference

    public HeaderFooterAdapterData(ViewAdapter.IAdapterDataProvider adapterData,
                                   IContainerData containerData,
                                   int headerViewType,
                                   int footerViewType) {

        this.containerData = containerData;
        this.adapterData = adapterData;
        this.headerViewType = headerViewType;
        this.footerViewType = footerViewType;

        adapterWrap = new IAdapter() {

            @Override
            public void myNotifyDataSetChanged() {
                IAdapter adapter = getAdapter();
                if (adapter != null) adapter.myNotifyDataSetChanged();
            }

            @Override
            public void myNotifyItemChanged(int position) {
                IAdapter adapter = getAdapter();
                if (adapter != null) adapter.myNotifyItemChanged(position + 1);
            }

            @Override
            public void myNotifyItemRangeChanged(int positionStart, int itemCount) {
                IAdapter adapter = getAdapter();
                if (adapter != null) adapter.myNotifyItemRangeChanged(positionStart + 1, itemCount);
            }

            @Override
            public void myNotifyItemInserted(int position) {
                IAdapter adapter = getAdapter();
                if (adapter != null) adapter.myNotifyItemInserted(position + 1);
            }

            @Override
            public void myNotifyItemMoved(int fromPosition, int toPosition) {
                IAdapter adapter = getAdapter();
                if (adapter != null) adapter.myNotifyItemMoved(fromPosition + 1, toPosition + 1);
            }

            @Override
            public void myNotifyItemRangeInserted(int positionStart, int itemCount) {
                IAdapter adapter = getAdapter();
                if (adapter != null)
                    adapter.myNotifyItemRangeInserted(positionStart + 1, itemCount);
            }

            @Override
            public void myNotifyItemRemoved(int position) {
                IAdapter adapter = getAdapter();
                if (adapter != null) adapter.myNotifyItemRemoved(position + 1);
            }

            @Override
            public void myNotifyItemRangeRemoved(int positionStart, int itemCount) {
                IAdapter adapter = getAdapter();
                if (adapter != null) adapter.myNotifyItemRangeRemoved(positionStart + 1, itemCount);
            }

        };

        adapterData.onAdapterInitialized(adapterWrap);
    }

    protected IAdapter getAdapter() {
        return adapter.get();
    }

    @Override
    public void onAdapterInitialized(IAdapter adapter) {
        this.adapter = new WeakReference<>(adapter);
    }

    @Override
    public void onAdapterDispose() {
        adapterData.onAdapterDispose();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return headerViewType;
        else if (position == adapterData.getItemCount() + 1)
            return footerViewType;
        else
            return adapterData.getItemViewType(position - 1);
    }

    @Override
    public int getItemCount() {
        return adapterData.getItemCount() + 2;
    }

    @Override
    public long getItemId(int position) {
        if (position > 0 && position < adapterData.getItemCount() + 1)
            return adapterData.getItemId(position - 1);
        else
            return RecyclerView.NO_ID;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == headerViewType)
            return ViewHolderFactory.newInstance(viewGroup.getContext(), viewGroup, viewType);
        else if (viewType == footerViewType)
            return ViewHolderFactory.newInstance(viewGroup.getContext(), viewGroup, viewType);
        else
            return adapterData.onCreateViewHolder(viewGroup, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position > 0 && position < adapterData.getItemCount() + 1) {
            adapterData.onBindViewHolder(viewHolder, position - 1);
            return;
        }

        BaseViewHolder holder = (BaseViewHolder) viewHolder;
        holder.onBind(containerData, position);
    }

    @Override
    public void onItemsMoved(int from, int to, List<Integer> itemOffsets) {
        adapterData.onItemsMoved(from - 1, to - 1, itemOffsets);
    }

    @Override
    public int dataPositionToPosition(int position) {
        return position + 1;
    }
}
