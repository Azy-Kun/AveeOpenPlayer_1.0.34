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

import com.aveeopen.Common.Utils;
import com.aveeopen.comp.playback.Song.PlaylistSong;

import java.io.File;
import java.io.IOException;
import java.util.List;

import christophedelory.playlist.SpecificPlaylist;
import christophedelory.playlist.m3u.Resource;

public class PlaylistFilesRUtils {

    static int readFromSpecificPlaylist(SpecificPlaylist specificPlaylist, ReadParameters parameters, List<PlaylistSong> resultList) {
        if (specificPlaylist == null)
            return 0;

        if (specificPlaylist instanceof christophedelory.playlist.pla.PLA) {
            return readFromSpecificPlaylist((christophedelory.playlist.pla.PLA) specificPlaylist, parameters, resultList);
        }

        if (specificPlaylist instanceof christophedelory.playlist.kpl.Xml) {
            return readFromSpecificPlaylist((christophedelory.playlist.kpl.Xml) specificPlaylist, parameters, resultList);
        }

        if (specificPlaylist instanceof christophedelory.playlist.pls.PLS) {
            return readFromSpecificPlaylist((christophedelory.playlist.pls.PLS) specificPlaylist, parameters, resultList);
        }

        if (specificPlaylist instanceof christophedelory.playlist.mpcpl.MPCPL) {
            return readFromSpecificPlaylist((christophedelory.playlist.mpcpl.MPCPL) specificPlaylist, parameters, resultList);
        }

        if (specificPlaylist instanceof christophedelory.playlist.plp.PLP) {
            return readFromSpecificPlaylist((christophedelory.playlist.plp.PLP) specificPlaylist, parameters, resultList);
        }

        if (specificPlaylist instanceof christophedelory.playlist.m3u.M3U) {
            return readFromSpecificPlaylist((christophedelory.playlist.m3u.M3U) specificPlaylist, parameters, resultList);
        }

        return 0;
    }

    static int readFromSpecificPlaylist(christophedelory.playlist.pla.PLA specificPlaylist, ReadParameters parameters, List<PlaylistSong> resultList) {

        List<String> list = specificPlaylist.getFilenames();

        for (String item : list) {
            //its Lizzy bug, it includes null terminators
            item = Utils.fixIncludedNullTerminatorString(item);
            resultList.add(makePlaylistSong(-1, item, parameters));
        }

        return list.size();
    }

    static int readFromSpecificPlaylist(christophedelory.playlist.kpl.Xml specificPlaylist, ReadParameters parameters, List<PlaylistSong> resultList) {

        List<christophedelory.playlist.kpl.Entry> list = specificPlaylist.getEntries();

        for (christophedelory.playlist.kpl.Entry item : list) {
            resultList.add(makePlaylistSong(-1, item.getFilename(), parameters));
        }

        return list.size();
    }

    static int readFromSpecificPlaylist(christophedelory.playlist.pls.PLS specificPlaylist, ReadParameters parameters, List<PlaylistSong> resultList) {
        List<Resource> resourcesList = specificPlaylist.getResources();

        for (Resource r : resourcesList) {
            resultList.add(makePlaylistSong(-1, r.getLocation(), r.getName(), null, parameters));
        }

        return resourcesList.size();
    }

    static int readFromSpecificPlaylist(christophedelory.playlist.mpcpl.MPCPL specificPlaylist, ReadParameters parameters, List<PlaylistSong> resultList) {
        List<christophedelory.playlist.mpcpl.Resource> resourcesList = specificPlaylist.getResources();

        for (christophedelory.playlist.mpcpl.Resource r : resourcesList) {
            resultList.add(makePlaylistSong(-1, r.getFilename(), null, r.getSubtitle(), parameters));
        }

        return resourcesList.size();
    }

    static int readFromSpecificPlaylist(christophedelory.playlist.plp.PLP specificPlaylist, ReadParameters parameters, List<PlaylistSong> resultList) {
        List<String> list = specificPlaylist.getFilenames();

        for (String r : list) {
            resultList.add(makePlaylistSong(-1, r, parameters));
        }

        return list.size();
    }

    static int readFromSpecificPlaylist(christophedelory.playlist.m3u.M3U specificPlaylist, ReadParameters parameters, List<PlaylistSong> resultList) {
        specificPlaylist.setExtensionM3U(true);

        List<Resource> resourcesList = specificPlaylist.getResources();

        for (Resource r : resourcesList) {
            resultList.add(makePlaylistSong(-1, r.getLocation(), r.getName(), null, parameters));
        }

        return resourcesList.size();
    }

    static PlaylistSong makePlaylistSong(long audioId, String path, ReadParameters parameters) {
        return new PlaylistSong(audioId, makeSongPath(path, parameters.playlistPath));
    }

    static PlaylistSong makePlaylistSong(long audioId, String path, String providedTitle, String subtitlePath, ReadParameters parameters) {
        return new PlaylistSong(audioId, makeSongPath(path, parameters.playlistPath), providedTitle, subtitlePath);
    }

    static String makeSongPath(String songPath, String playlistPath) {
        if (songPath.startsWith("/"))
            return makeSongPathAbsolute(songPath, playlistPath);

        if (songPath.startsWith("\\"))
            return makeSongPathAbsolute(songPath, playlistPath);

        if (songPath.startsWith("..")) {
            String path = makeSongPathRelative(songPath, playlistPath);
            if (path == null) return makeSongPathAbsolute(songPath, playlistPath);
            else return path;
        }

        if (songPath.contains(":"))
            return makeSongPathAbsolute(songPath, playlistPath);

        //not sure, lets test relative path
        String path = makeSongPathRelative(songPath, playlistPath);
        if (path == null) return makeSongPathAbsolute(songPath, playlistPath);
        else return path;//nope, return as is
    }

    static String makeSongPathRelative(String path, String playlistPath) {
        File plFile = new File(playlistPath);
        if (!plFile.isDirectory()) {
            File pldir = plFile.getParentFile();
            if (pldir != null)
                plFile = pldir;
        }

        File file = new File(plFile, path);

        if (file.exists()) {
            try {
                return file.getCanonicalPath();
            } catch (IOException e) {
                return file.getAbsolutePath();
            }
        }

        return null;
    }

    static String makeSongPathAbsolute(String path, String playlistPath) {
        return path;
    }

    public static class ReadParameters {
        public String playlistPath;
    }
}
