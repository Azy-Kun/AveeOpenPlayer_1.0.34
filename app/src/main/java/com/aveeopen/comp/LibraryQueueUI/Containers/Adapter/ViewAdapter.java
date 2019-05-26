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

import java.util.List;

public class ViewAdapter extends RecyclerView.Adapter implements IAdapter  {

    private IAdapterDataProvider dataProvider;
    private IContainerData data;

    public ViewAdapter(
            IAdapterDataProvider dataProvider,
            IContainerData data) {

        this.data = data;

        //we cant guarantee stable ids, due to search function
        this.setHasStableIds(false);
        this.dataProvider = dataProvider;
        dataProvider.onAdapterInitialized(this);
    }

    public void dispose() {
        dataProvider.onAdapterDispose();
    }

    @Override
    public int getItemViewType(int position) {
        return dataProvider.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return dataProvider.getItemCount();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return dataProvider.onCreateViewHolder(viewGroup, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        dataProvider.onBindViewHolder(viewHolder, position);
    }

    public void onItemsMoved(int from, int to, List<Integer> itemOffsets) {
        dataProvider.onItemsMoved(from, to, itemOffsets);
    }

    public int dataPositionToPosition(int position) {
        return dataProvider.dataPositionToPosition(position);
    }

    public IContainerData getContainerData() {
        return data;
    }

    public void myNotifyDataSetChanged() {
        this.notifyDataSetChanged();
    }

    public void myNotifyItemChanged(int position) {
        this.notifyItemChanged(position);
    }

    public void myNotifyItemRangeChanged(int positionStart, int itemCount) {
        this.notifyItemRangeChanged(positionStart, itemCount);
    }

    public void myNotifyItemInserted(int position) {
        this.notifyItemInserted(position);
    }


    public void myNotifyItemMoved(int fromPosition, int toPosition) {
        this.notifyItemMoved(fromPosition, toPosition);
    }

    public void myNotifyItemRangeInserted(int positionStart, int itemCount) {
        this.notifyItemRangeInserted(positionStart, itemCount);
    }

    public void myNotifyItemRemoved(int position) {
        this.notifyItemRemoved(position);
    }

    public void myNotifyItemRangeRemoved(int positionStart, int itemCount) {
        this.notifyItemRangeRemoved(positionStart, itemCount);
    }

    public  interface IAdapterDataProvider {

        void onAdapterInitialized(IAdapter adapter);

        void onAdapterDispose();

        int getItemViewType(int position);

        int getItemCount();

        long getItemId(int position);

        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType);

        void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position);

        void onItemsMoved(int from, int to, List<Integer> itemOffsets);

        int dataPositionToPosition(int position);
    }
}
