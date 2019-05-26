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

package com.aveeopen.comp.playback.Song;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.aveeopen.Common.Utils;

public class PlaylistSongMetadataRetriever {

    public static PlaylistSong.DataDetails AcquireDataMediaMetadataRetrieverLocal2(Context context, Uri uri, PlaylistSong.Data simpleData) {
        PlaylistSong.DataDetails _data = new PlaylistSong.DataDetails(simpleData);

        if (uri == null || uri == Uri.EMPTY) {
            return _data;
        }

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        boolean metadataRetrieverSet = false;

        String trackSecondName = null;
        if ("content".equals(uri.getScheme())) {
            try {
                metadataRetriever.setDataSource(context, uri);
                metadataRetrieverSet = true;
            } catch (Exception ignored) {
            }

        } else if ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme())) {
            String uriPath = uri.getPath();//part after www.site.com
            int len = uriPath.length();

            int doti = uriPath.lastIndexOf(".");
            if (doti < 0) doti = len - 1;
            int d1i = Math.max(uriPath.lastIndexOf('/', doti), 0) + 1;
            int d2i = uriPath.indexOf('/', doti);
            if (d2i < 0) d2i = (len - 1) + 1;

            try {
                trackSecondName = uriPath.substring(d1i, d2i);
                if (trackSecondName.length() < 2)
                    trackSecondName = null;
            } catch (Exception e) {
                trackSecondName = null;
            }

        } else {
            //path must be canonical path !(?)
            try {
                metadataRetriever.setDataSource(context, uri);
                metadataRetrieverSet = true;
            } catch (Exception ignored) {
            }
        }

        if (metadataRetrieverSet) {
            _data.isStream = false;
            //_data.audioId = 0;
            _data.trackName = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            _data.artistName = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            //_data.artistId = 0;
            _data.albumName = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            //_data.albumId = 0;
            _data.duration = Utils.strToIntSafe(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            _data.trackNum = Utils.strToIntSafe(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));
            _data.cdNum = Utils.strToIntSafe(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER));
            _data.year = Utils.strToIntSafe(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR));
            _data.albumArtist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
            //_data.author = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
            _data.composer = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
            //_data.writer = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER);
            _data.bitRate = Utils.strToIntSafe(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
            _data.width = Utils.strToIntSafe(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            _data.height = Utils.strToIntSafe(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

        } else {
            _data.isStream = true;

            if (trackSecondName == null) {
                _data.secondName = "";
            } else {
                _data.secondName = trackSecondName;
            }
        }

        if (_data.trackName == null) _data.trackName = "";
        if (_data.albumName == null) _data.albumName = "";
        if (_data.artistName == null) _data.artistName = "";
        if (_data.albumArtist == null) _data.albumArtist = "";
        if (_data.composer == null) _data.composer = "";

        metadataRetriever.release();

        return _data;
    }
}
