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

package com.aveeopen.Common;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.aveeopen.Design.SortDesign;

public class MediaStoreUtils {

    public static Cursor emptyCursor = new Cursor() {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public int getPosition() {
            return 0;
        }

        @Override
        public boolean move(int offset) {
            return false;
        }

        @Override
        public boolean moveToPosition(int position) {
            return false;
        }

        @Override
        public boolean moveToFirst() {
            return false;
        }

        @Override
        public boolean moveToLast() {
            return false;
        }

        @Override
        public boolean moveToNext() {
            return false;
        }

        @Override
        public boolean moveToPrevious() {
            return false;
        }

        @Override
        public boolean isFirst() {
            return false;
        }

        @Override
        public boolean isLast() {
            return false;
        }

        @Override
        public boolean isBeforeFirst() {
            return false;
        }

        @Override
        public boolean isAfterLast() {
            return false;
        }

        @Override
        public int getColumnIndex(String columnName) {
            return 0;
        }

        @Override
        public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
            return 0;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return null;
        }

        @Override
        public String[] getColumnNames() {
            return new String[0];
        }

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public byte[] getBlob(int columnIndex) {
            return new byte[0];
        }

        @Override
        public String getString(int columnIndex) {
            return null;
        }

        @Override
        public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {

        }

        @Override
        public short getShort(int columnIndex) {
            return 0;
        }

        @Override
        public int getInt(int columnIndex) {
            return 0;
        }

        @Override
        public long getLong(int columnIndex) {
            return 0;
        }

        @Override
        public float getFloat(int columnIndex) {
            return 0;
        }

        @Override
        public double getDouble(int columnIndex) {
            return 0;
        }

        @Override
        public int getType(int columnIndex) {
            return 0;
        }

        @Override
        public boolean isNull(int columnIndex) {
            return false;
        }

        @Override
        public void deactivate() {

        }

        @Override
        public boolean requery() {
            return false;
        }

        @Override
        public void close() {

        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public void registerContentObserver(ContentObserver observer) {

        }

        @Override
        public void unregisterContentObserver(ContentObserver observer) {

        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void setNotificationUri(ContentResolver cr, Uri uri) {

        }

        @Override
        public Uri getNotificationUri() {
            return null;
        }

        @Override
        public boolean getWantsAllOnMoveCalls() {
            return false;
        }

        @Override
        public Bundle getExtras() {
            return null;
        }

        @Override
        public void setExtras(Bundle extras) {

        }

        @Override
        public Bundle respond(Bundle extras) {
            return null;
        }
    };

    public static String getOrderBy(SortDesign.SortDesc sortDesc) {

        String orderBy = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        if (sortDesc == null) return orderBy;

        boolean descending = sortDesc.sortDescending;

        orderBy = MediaStore.Audio.Media.DATA;

        switch (sortDesc.sortModeIndex) {
            case SortDesign.Sort_Mode_Title:
                orderBy = MediaStore.Audio.Media.TITLE;
                break;
            case SortDesign.Sort_Mode_Album:
                orderBy = MediaStore.Audio.Media.ALBUM;
                break;
            case SortDesign.Sort_Mode_Artist:
                orderBy = MediaStore.Audio.Media.ARTIST;
                break;

            case SortDesign.Sort_Mode_Path:
                orderBy = MediaStore.Audio.Media.DATA;
                break;
            case SortDesign.Sort_Mode_DateAdded:
                orderBy = MediaStore.Audio.Media.DATE_ADDED;
                descending = !descending;
                break;
            case SortDesign.Sort_Mode_DateModified:
                orderBy = MediaStore.Audio.Media.DATE_ADDED;
                descending = !descending;
                break;
            case SortDesign.Sort_Mode_Duration:
                orderBy = MediaStore.Audio.Media.DURATION;
                descending = !descending;
                break;
            case SortDesign.Sort_Mode_Size:
                orderBy = MediaStore.Audio.Media.SIZE;
                descending = !descending;
                break;
            default:
        }

        if (descending)
            orderBy = orderBy + " DESC";

        return orderBy;
    }

    public static String CursorGetStringSafe(Cursor c, int columnIndex) {
        if (c == null) return "";
        try {
            return c.getString(columnIndex);
        } catch (Exception e) {
            return "";
        }
    }

    public static Cursor querySafe(ContentResolver cr, Uri uri, String[] projection,
                                   String selection, String[] selectionArgs, String sortOrder) {
        try {
            return cr.query(uri, projection, selection, selectionArgs, sortOrder);//, null);
        } catch (Exception e) {
            tlog.w(e.getMessage());
            return null;
        }
    }

    public static Cursor querySafeEmpty(ContentResolver cr, Uri uri, String[] projection,
                                        String selection, String[] selectionArgs, String sortOrder) {
        try {
            return cr.query(uri, projection, selection, selectionArgs, sortOrder);//, null);
        } catch (Exception e) {
            tlog.w(e.getMessage());
            return emptyCursor;
        }
    }

    public static void closeCursor(Cursor cursor) {
        if (cursor == null) return;
        if (!cursor.isClosed())
            cursor.close();
    }

}
