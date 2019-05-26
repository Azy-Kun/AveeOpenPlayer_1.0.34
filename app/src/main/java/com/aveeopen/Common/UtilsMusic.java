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
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.aveeopen.comp.playback.Song.PlaylistSong;

import java.util.ArrayList;
import java.util.List;

public class UtilsMusic {

    public static int findSongInList(List<PlaylistSong> list, PlaylistSong song) {
        return findSongInList(list, song, 0);
    }

    public static int findSongInList(List<PlaylistSong> list, PlaylistSong song, int start) {
        for (int i = start; i < list.size(); i++) {
            if (list.get(i).compare(song)) return i;
        }
        return -1;
    }

    public static List<PlaylistSong> songListFromCursor(Cursor cursor) {
        return songListFromCursor(cursor, null);
    }

    public static List<PlaylistSong> songListFromCursor(Cursor cursor, List<PlaylistSong> dest) {
        int size = cursor.getCount();
        if (dest == null)
            dest = new ArrayList<>(size);
        cursor.moveToFirst();

        int colidx;
        int colData;
        try {
            //check for AUDIO_ID first
            colidx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID);
        } catch (IllegalArgumentException ex) {
            colidx = -1;
        }

        if (colidx != -1) {
            try {
                colidx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            } catch (IllegalArgumentException ex) {
                colidx = -1;
            }
        }

        try {
            colData = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        } catch (IllegalArgumentException ex) {
            return dest;
        }

        if (colidx >= 0) {
            for (int i = 0; i < size; i++) {
                long audioId = cursor.getLong(colidx);
                String dataSource = cursor.getString(colData);
                dest.add(new PlaylistSong(audioId, dataSource));
                cursor.moveToNext();
            }
        } else {
            for (int i = 0; i < size; i++) {
                String dataSource = cursor.getString(colData);
                dest.add(new PlaylistSong(-1, dataSource));
                cursor.moveToNext();
            }
        }

        return dest;
    }

    public static int playlistIdForPlaylist(Context context, String name) {
        ContentResolver res = context.getContentResolver();
        Cursor c = MediaStoreUtils.querySafe(res, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Playlists._ID},
                MediaStore.Audio.Playlists.NAME + "=?",
                new String[]{name},
                MediaStore.Audio.Playlists.NAME);
        int id = -1;
        if (c != null) {
            c.moveToFirst();
            if (!c.isAfterLast()) {
                id = c.getInt(0);
            }
            c.close();
        }
        return id;
    }


    public static void getPlayLists(Context context, List<Long> ids, List<String> names) {
        String[] cols = new String[]{
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME
        };
        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {
            System.out.println("resolver = null");
        } else {
            String whereClause = MediaStore.Audio.Playlists.NAME + " != ''";
            Cursor cur = MediaStoreUtils.querySafe(resolver, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    cols, whereClause, null,
                    MediaStore.Audio.Playlists.NAME);

            if (cur != null && cur.getCount() > 0) {
                cur.moveToFirst();
                while (!cur.isAfterLast()) {
                    ids.add(cur.getLong(0));
                    String name = cur.getString(1);
                    names.add(name == null ? "unnamed" : name);
                    cur.moveToNext();
                }
            }
            if (cur != null) {
                cur.close();
            }
        }
    }

    public static List<PlaylistSong> getMostRecentTrackListByCount(Context context, int maxCount) {

        ContentResolver cr = context.getContentResolver();

        //seems not to work
        String where = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        final String[] columns = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA};

        String orderBy = MediaStore.Audio.Media.DATE_ADDED + " DESC limit " + maxCount;
        Cursor cursor = MediaStoreUtils.querySafe(cr, uri, columns, where, null, orderBy);

        List<PlaylistSong> songs;
        if (cursor != null) {
            songs = UtilsMusic.songListFromCursor(cursor);
            cursor.close();
        } else {
            songs = new ArrayList<>();
        }

        return songs;
    }

}
