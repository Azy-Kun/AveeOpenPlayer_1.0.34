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

package com.aveeopen.comp.Playlists.Platform;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.aveeopen.Common.MediaStoreUtils;
import com.aveeopen.Common.UtilsMusic;

import java.util.ArrayList;
import java.util.List;

public class PlaylistPlatformUtils {

    public static int createPlaylist(Context context, String name) {
        return createPlaylist(context, name, null);
    }

    public static int createPlaylist(Context context, String name, long[] songsIds) {

        ContentResolver resolver = context.getContentResolver();
        int id = UtilsMusic.playlistIdForPlaylist(context, name);
        Uri uri;
        if (id >= 0) {
            //playlist already exists
            return -1;
        } else {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.NAME, name);
            uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
            if (uri == null) return 0;

            int numInserted = 0;
            if (songsIds != null) {
                int base = 0;
                ContentValues[] sContentValuesCache = null;
                for (int i = 0; i < songsIds.length; i += 1000) {
                    sContentValuesCache = makeInsertItems(sContentValuesCache, songsIds, i, 1000, base);
                    numInserted += resolver.bulkInsert(uri, sContentValuesCache);
                }
            }
            return numInserted;
        }
    }

    public static int deletePlaylist(Context context, long playlsitId) {
        Uri uri = ContentUris.withAppendedId(
                MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, playlsitId);
        int numRows = context.getContentResolver().delete(uri, null, null);
        return numRows > 0 ? 1 : 0;
    }


    public static void renamePlaylist(Context context, long playlsitId, String newname) {
        renamePlaylist(context, "" + playlsitId, newname);
    }

    public static void renamePlaylist(Context context, String playlsitId, String newname) {

        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Audio.Playlists.NAME, newname);
        resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                values,
                MediaStore.Audio.Playlists._ID + "=?",
                new String[]{playlsitId});
    }

    private static ContentValues[] makeInsertItems(ContentValues[] sContentValuesCache, long[] ids, int offset, int len, int base) {
        // adjust 'len' if would extend beyond the end of the source array
        if (offset + len > ids.length) {
            len = ids.length - offset;
        }

        List<ContentValues> contentValuesList = new ArrayList<>();
        // fill in the ContentValues array with the right values for this pass
        for (int i = 0; i < len; i++) {
            long audioId = ids[offset + i];
            if (audioId == 0) continue;

            ContentValues newValue = new ContentValues();
            newValue.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + offset + i);
            newValue.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
            contentValuesList.add(newValue);
        }

        // allocate the ContentValues array, or reallocate if it is the wrong size
        if (sContentValuesCache == null || sContentValuesCache.length != contentValuesList.size()) {
            sContentValuesCache = new ContentValues[contentValuesList.size()];
        }

        sContentValuesCache = contentValuesList.toArray(sContentValuesCache);

        return sContentValuesCache;
    }

    public static int addToPlaylist(Context context, long playlistId, long[] songsIds, boolean deleteCurrentContent) {

        int size = songsIds.length;// ids.length;
        ContentResolver resolver = context.getContentResolver();
        // need to determine the number of items currently in the playlist,
        // so the play_order field can be maintained.
        String[] cols = new String[]{
                "count(*)"
        };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);

        if (deleteCurrentContent)
            resolver.delete(uri, null, null);

        Cursor cur = MediaStoreUtils.querySafe(resolver, uri, cols, null, null, null);
        int numInserted = 0;
        if (cur != null) {
            cur.moveToFirst();
            int base = cur.getInt(0);
            cur.close();

            ContentValues[] sContentValuesCache = null;

            for (int i = 0; i < size; i += 1000) {
                sContentValuesCache = makeInsertItems(sContentValuesCache, songsIds, i, 1000, base);
                numInserted += resolver.bulkInsert(uri, sContentValuesCache);
            }
        }

        return numInserted;
    }
}
