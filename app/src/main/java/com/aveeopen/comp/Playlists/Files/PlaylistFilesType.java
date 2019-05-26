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

public class PlaylistFilesType {

    public static PlaylistFilesType[] playlistFilesTypes = new PlaylistFilesType[]{
            new PlaylistFilesType("PLA Playlist", 1, "pla", true),
            //new PlaylistFilesType("kpl", 2, "kpl", false),
            new PlaylistFilesType("PLS Playlist", 3, "pls", true),
            new PlaylistFilesType("MPCPL Playlist", 4, "mpcpl", true),
            new PlaylistFilesType("PLP Playlist", 5, "plp", true),

            new PlaylistFilesType("M3U Playlist", 6, "m3u", true),
            new PlaylistFilesType("M3U8 Playlist", 7, "m3u8", true),
            //            new PlaylistFilesType("", 1, "m4u"),
            //            new PlaylistFilesType("", 1, "ram"),
    };

    public final String name;
    public final String fileExtension;
    public final boolean supportSaving;

    public PlaylistFilesType(String name, int typeId, String ext, boolean supportSaving) {
        this.name = name;
        this.fileExtension = ext;
        this.supportSaving = supportSaving;
    }

    public static boolean isPlaylistFileExtension(String ext) {
        //ext: extension without dot

        //keep these sync with our playlist library

        //PLAProvider
        if (ext.equals("pla")) return true;

        //KplProvider
        //if(ext.equals("kpl")) return true;

        //PLSProvider
        if (ext.equals("pls")) return true;

        //MPCPLProvider
        if (ext.equals("mpcpl")) return true;

        //PLAProvider
        if (ext.equals("plp")) return true;

        //M3UProvider
        if (ext.equals("m3u")) return true;
        if (ext.equals("m3u8")) return true;
        if (ext.equals("m4u")) return true;
        if (ext.equals("ram")) return true;

        return false;
    }
}
