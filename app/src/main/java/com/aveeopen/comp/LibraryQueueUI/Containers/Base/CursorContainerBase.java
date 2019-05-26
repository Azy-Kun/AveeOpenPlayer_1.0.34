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
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.aveeopen.Common.MediaStoreUtils;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.IAdapter;
import com.aveeopen.comp.LibraryQueueUI.Containers.Adapter.ViewAdapter;
import com.aveeopen.comp.LibraryQueueUI.ViewHolders.ViewHolderFactory;

import junit.framework.Assert;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class CursorContainerBase extends ContainerBase implements ViewAdapter.IAdapterDataProvider {

    private WeakReference<IAdapter> associatedAdapter = new WeakReference<>(null);
    private Cursor cursor;

    protected CursorContainerBase(Context context,
                                  String libraryAddress,
                                  String displayName,
                                  int displayIconResId,
                                  int pageIndex) {
        super(context, libraryAddress, displayName, displayIconResId, pageIndex);
    }

    public void dispose() {
        Assert.fail();
        MediaStoreUtils.closeCursor(cursor);
        cursor = null;
    }

    protected void init(Context context) {
        Tuple2<Cursor, String> newdata = createOrGetCursor(context);
        setDataAndNotifyDataSetChanged(newdata.obj1, newdata.obj2);
    }

    @Override
    public void updateSearchQuery(Context context, String query) {
        Tuple2<Cursor, String> newdata = createOrGetCursor(context, query);
        if (newdata != null) {
            setDataAndNotifyDataSetChanged(newdata.obj1, newdata.obj2);
        }
    }

    public Tuple2<Cursor, String> createOrGetCursor(Context context, String query) {
        //TODO: search query implementation(?)
        return createOrGetCursor(context);
    }

    public abstract Tuple2<Cursor, String> createOrGetCursor(Context context);

    protected IAdapter getAssociatedAdapter() {
        return associatedAdapter.get();
    }

    @Override
    public void onAdapterInitialized(IAdapter adapter) {
        associatedAdapter = new WeakReference<>(adapter);
    }

    @Override
    public void onAdapterDispose() {

    }

    @Override
    public abstract int getItemViewType(int position);

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


    public Cursor getItem(int position) {
        cursor.moveToPosition(position);
        return cursor;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return ViewHolderFactory.newInstance(viewGroup.getContext(), viewGroup, viewType);
    }

    @Override
    public long getItemId(int position) {
        return RecyclerView.NO_ID;
    }

    @Override
    public void onItemsMoved(int from, int to, List<Integer> itemOffsets) {

    }

    @Override
    public int dataPositionToPosition(int position) {
        return position;
    }

    public int findRowAndMove(String columnName, String columnValue) {
        return findRowAndMove(cursor.getColumnIndex(columnName), columnValue);
    }

    public int findRowAndMove(int columnIndex, String columnValue) {
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            if (cursor.getString(columnIndex).equals(columnValue))
                return cursor.getPosition();
        }
        return -1;
    }

    protected void setDataAndNotifyDataSetChanged(Cursor newCursor, String itemsIdent) {
        if (checkItemIdent(itemsIdent)) return;

        MediaStoreUtils.closeCursor(cursor);
        Assert.assertNotNull(newCursor);

        cursor = newCursor;
        IAdapter adapter = getAssociatedAdapter();
        if (adapter != null)
            adapter.myNotifyDataSetChanged();
    }
}
