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
import com.aveeopen.Common.Tuple2;
import com.aveeopen.Common.tlog;
import com.aveeopen.comp.LibraryQueueUI.ViewHolders.BaseViewHolder;
import com.aveeopen.comp.LibraryQueueUI.ViewHolders.ViewHolderFactory;

import java.lang.ref.WeakReference;
import java.util.List;

public class CompositeAdapterData implements ViewAdapter.IAdapterDataProvider {

    private WeakReference<IAdapter> adapter = new WeakReference<>(null);
    private SectionDesc[] sectionDesc;
    private ViewAdapter.IAdapterDataProvider[] adapterData;
    private IContainerData[] containerData;
    private IAdapter[] adapterWrap;//must hold reference

    public CompositeAdapterData(SectionDesc[] sectionDesc,
                                ViewAdapter.IAdapterDataProvider[] adapterData,
                                IContainerData[] containerData) {

        this.containerData = containerData;
        this.sectionDesc = sectionDesc;
        this.adapterData = adapterData;

        adapterWrap = new IAdapter[adapterData.length];

        for (int i = 0; i < adapterData.length; i++) {
            adapterWrap[i] = new AdapterWrap(i);
            adapterData[i].onAdapterInitialized(adapterWrap[i]);
        }
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
        for (ViewAdapter.IAdapterDataProvider anAdapterData : adapterData)
            anAdapterData.onAdapterDispose();
    }

    @Override
    public int getItemViewType(int position) {

        Tuple2<Integer, Integer> pos = positionToDataPosition(position);

        if (pos.obj1 < 0)
            return sectionDesc[pos.obj2].getViewType();
        else
            return adapterData[pos.obj2].getItemViewType(pos.obj1);
    }

    @Override
    public int getItemCount() {
        int total = 0;
        for (int i = 0; i < adapterData.length; i++) {

            if (containerData[i].getSectionOpened())
                total += adapterData[i].getItemCount() + 1;
            else
                total += 1;
        }
        return total;
    }

    @Override
    public long getItemId(int position) {

        Tuple2<Integer, Integer> pos = positionToDataPosition(position);

        if (pos.obj1 < 0)
            return RecyclerView.NO_ID;
        else
            return adapterData[pos.obj2].getItemId(pos.obj1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return ViewHolderFactory.newInstance(viewGroup.getContext(), viewGroup, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        Tuple2<Integer, Integer> pos = positionToDataPosition(position);

        if (pos.obj1 < 0) {
            BaseViewHolder holder = (BaseViewHolder) viewHolder;
            holder.onBind(containerData[pos.obj2], pos.obj2);

        } else {
            adapterData[pos.obj2].onBindViewHolder(viewHolder, pos.obj1);
        }

    }

    @Override
    public void onItemsMoved(int from, int to, List<Integer> itemOffsets) {

        Tuple2<Integer, Integer> posFrom = positionToDataPosition(from);
        Tuple2<Integer, Integer> posTo = positionToDataPosition(to);

        if (!posFrom.obj2.equals(posFrom.obj1)) {
            tlog.w("Assert posFrom.obj2 != posFrom.obj1");
            adapterData[posTo.obj2].onItemsMoved(posFrom.obj1, -1, itemOffsets);
            return;
        }

        adapterData[posTo.obj2].onItemsMoved(posFrom.obj1, posTo.obj1, itemOffsets);
    }

    @Override
    public int dataPositionToPosition(int position) {
        //not supported
        return 0;
    }

    public int dataPositionToPosition(int positionInSection, int section) {

        section = Math.min(section, adapterData.length);

        int total = 0;
        int totalLast = 0;
        for (int i = 0; i < section; i++) {

            if (containerData[i].getSectionOpened())
                total += adapterData[i].getItemCount() + 1;
            else
                total += 0 + 1;

            totalLast = total;
        }


        return positionInSection + total + 1;
    }

    public Tuple2<Integer, Integer> positionToDataPosition(int position) {

        int total = 0;
        int totalLast = 0;
        for (int i = 0; i < adapterData.length; i++) {

            if (containerData[i].getSectionOpened())
                total += adapterData[i].getItemCount() + 1;
            else
                total += 0 + 1;

            if (position < total)
                return new Tuple2<>((position - totalLast) - 1, i);

            totalLast = total;
        }


        return new Tuple2<>(-1, -1);
    }

    public static class SectionDesc {
        private final int viewType;

        public SectionDesc(int viewType)///, String _name, boolean _sectionOpened)
        {
            this.viewType = viewType;
        }

        public int getViewType() {
            return viewType;
        }

    }

    class AdapterWrap implements IAdapter {
        int sectionIndex;

        public AdapterWrap(int sectionIndex) {
            this.sectionIndex = sectionIndex;
        }

        @Override
        public void myNotifyDataSetChanged() {
            IAdapter adapter = getAdapter();
            if (adapter != null) adapter.myNotifyDataSetChanged();
        }

        @Override
        public void myNotifyItemChanged(int position) {
            IAdapter adapter = getAdapter();
            if (adapter != null)
                adapter.myNotifyItemChanged(dataPositionToPosition(position, sectionIndex));
        }

        @Override
        public void myNotifyItemRangeChanged(int positionStart, int itemCount) {
            IAdapter adapter = getAdapter();
            if (adapter != null)
                adapter.myNotifyItemRangeChanged(dataPositionToPosition(positionStart, sectionIndex), itemCount);
        }

        @Override
        public void myNotifyItemInserted(int position) {
            IAdapter adapter = getAdapter();
            if (adapter != null)
                adapter.myNotifyItemInserted(dataPositionToPosition(position, sectionIndex));
        }

        @Override
        public void myNotifyItemMoved(int fromPosition, int toPosition) {
            IAdapter adapter = getAdapter();
            if (adapter != null)
                adapter.myNotifyItemMoved(dataPositionToPosition(fromPosition, sectionIndex), dataPositionToPosition(toPosition, sectionIndex));
        }

        @Override
        public void myNotifyItemRangeInserted(int positionStart, int itemCount) {
            IAdapter adapter = getAdapter();
            if (adapter != null)
                adapter.myNotifyItemRangeInserted(dataPositionToPosition(positionStart, sectionIndex), itemCount);
        }

        @Override
        public void myNotifyItemRemoved(int position) {
            IAdapter adapter = getAdapter();
            if (adapter != null)
                adapter.myNotifyItemRemoved(dataPositionToPosition(position, sectionIndex));
        }

        @Override
        public void myNotifyItemRangeRemoved(int positionStart, int itemCount) {
            IAdapter adapter = getAdapter();
            if (adapter != null)
                adapter.myNotifyItemRangeRemoved(dataPositionToPosition(positionStart, sectionIndex), itemCount);
        }

    }
}
