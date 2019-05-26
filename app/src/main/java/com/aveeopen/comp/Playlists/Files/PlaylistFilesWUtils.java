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

package com.aveeopen.comp.Playlists.Files;

import android.content.Context;

import com.aveeopen.Common.UtilsFileSys;
import com.aveeopen.PlayerCore;
import com.aveeopen.comp.playback.Song.PlaylistSong;

import java.io.File;
import java.util.List;

import christophedelory.playlist.SpecificPlaylist;
import christophedelory.playlist.kpl.Entry;
import christophedelory.playlist.m3u.M3U;
import christophedelory.playlist.m3u.Resource;
import christophedelory.playlist.pla.PLA;

public class PlaylistFilesWUtils {

    static int appendToSpecificPlaylist(SpecificPlaylist specificPlaylist, AppendParameters parameters, List<PlaylistSong> listToAdd) {

        if (listToAdd == null)
            return 0;

        if (specificPlaylist == null)
            return 0;

        if (specificPlaylist instanceof PLA) {
            return appendToSpecificPlaylist((PLA) specificPlaylist, parameters, listToAdd);
        }

        if (specificPlaylist instanceof christophedelory.playlist.kpl.Xml) {
            return appendToSpecificPlaylist((christophedelory.playlist.kpl.Xml) specificPlaylist, parameters, listToAdd);
        }

        if (specificPlaylist instanceof christophedelory.playlist.pls.PLS) {
            return appendToSpecificPlaylist((christophedelory.playlist.pls.PLS) specificPlaylist, parameters, listToAdd);
        }

        if (specificPlaylist instanceof christophedelory.playlist.mpcpl.MPCPL) {
            return appendToSpecificPlaylist((christophedelory.playlist.mpcpl.MPCPL) specificPlaylist, parameters, listToAdd);
        }

        if (specificPlaylist instanceof christophedelory.playlist.plp.PLP) {
            return appendToSpecificPlaylist((christophedelory.playlist.plp.PLP) specificPlaylist, parameters, listToAdd);
        }

        if (specificPlaylist instanceof M3U) {
            return appendToSpecificPlaylist((M3U) specificPlaylist, parameters, listToAdd);
        }

        return 0;
    }

    static int appendToSpecificPlaylist(PLA specificPlaylist, AppendParameters parameters, List<PlaylistSong> listToAdd) {

        for (PlaylistSong s : listToAdd) {
            specificPlaylist.getFilenames().add(getDataPathForPlaylist(s, parameters.playlistPath, parameters.writeRelativePaths));
        }
        return listToAdd.size();
    }

    static int appendToSpecificPlaylist(christophedelory.playlist.kpl.Xml specificPlaylist, AppendParameters parameters, List<PlaylistSong> listToAdd) {

        for (PlaylistSong s : listToAdd) {
            christophedelory.playlist.kpl.Entry item = new Entry();
            item.setFilename(getDataPathForPlaylist(s, parameters.playlistPath, parameters.writeRelativePaths));
            specificPlaylist.getEntries().add(item);
        }

        return listToAdd.size();
    }

    static int appendToSpecificPlaylist(christophedelory.playlist.pls.PLS specificPlaylist, AppendParameters parameters, List<PlaylistSong> listToAdd) {

        Context context = PlayerCore.s().getAppContext();

        for (PlaylistSong s : listToAdd) {

            Resource item = new Resource();
            item.setLocation(getDataPathForPlaylist(s, parameters.playlistPath, parameters.writeRelativePaths));

            if (context != null) {
                PlaylistSong.Data data = s.getDataBlocking(context);
                item.setName(data.trackName);
                item.setLength(data.getDurationSeconds());
            }

            specificPlaylist.getResources().add(item);
        }

        return listToAdd.size();
    }

    static int appendToSpecificPlaylist(christophedelory.playlist.mpcpl.MPCPL specificPlaylist, AppendParameters parameters, List<PlaylistSong> listToAdd) {

        for (PlaylistSong s : listToAdd) {
            christophedelory.playlist.mpcpl.Resource item = new christophedelory.playlist.mpcpl.Resource();
            item.setFilename(getDataPathForPlaylist(s, parameters.playlistPath, parameters.writeRelativePaths));
            specificPlaylist.getResources().add(item);
        }

        return listToAdd.size();
    }

    static int appendToSpecificPlaylist(christophedelory.playlist.plp.PLP specificPlaylist, AppendParameters parameters, List<PlaylistSong> listToAdd) {
        for (PlaylistSong s : listToAdd) {
            specificPlaylist.getFilenames().add(getDataPathForPlaylist(s, parameters.playlistPath, parameters.writeRelativePaths));
        }

        return listToAdd.size();
    }

    static int appendToSpecificPlaylist(M3U specificPlaylist, AppendParameters parameters, List<PlaylistSong> listToAdd) {

        Context context = PlayerCore.s().getAppContext();
        specificPlaylist.setExtensionM3U(true);

        for (PlaylistSong s : listToAdd) {

            Resource item = new Resource();
            item.setLocation(getDataPathForPlaylist(s, parameters.playlistPath, parameters.writeRelativePaths));

            if (context != null) {
                PlaylistSong.Data data = s.getDataBlocking(context);
                item.setName(data.trackName);
                item.setLength(data.getDurationSeconds());
            }

            specificPlaylist.getResources().add(item);
        }

        return listToAdd.size();
    }

    static String getDataPathForPlaylist(PlaylistSong s, String playlistPath, boolean relativePath) {

        String dataSource = s.getDataSourceForPlaylist();
        if (dataSource.startsWith("file://")) {
            if (dataSource.length() > 7)
                dataSource = dataSource.substring(7, dataSource.length());//remove "file://"
        }

        if (relativePath) {

            File file = new File(dataSource);//is it even a file?
            if (file.exists()) {
                dataSource = UtilsFileSys.getRelativePath(dataSource, playlistPath, "/");
            }

        }

        return dataSource;
    }

    public static class AppendParameters {
        public String playlistPath;
        public boolean writeRelativePaths;
    }

}
