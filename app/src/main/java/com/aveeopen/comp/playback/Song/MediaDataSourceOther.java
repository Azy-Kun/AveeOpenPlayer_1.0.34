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

import android.net.Uri;

public class MediaDataSourceOther implements IMediaDataSource {

    private final Uri uriString;
    private final int contentType;

    public MediaDataSourceOther(Uri uriString)
    {
        this.uriString = uriString;
        contentType = detectContentType(uriString);
    }

    public static int detectContentType(Uri uri) {

        String ext = uri.getPath();

        if (ext != null) {
            int index = ext.lastIndexOf(".");
            try {
                ext = ext.substring(index + 1);
            } catch (Exception e) {
                ext = "";
            }
            ext = ext.toLowerCase();
        } else {
            return TYPE_OTHER;
        }

        if (ext.startsWith("mpd")) return TYPE_DASH;
        if (ext.startsWith("ism")) return TYPE_SS;//TODO: SS broken?
        //if(ext.startsWith("ism")) return TYPE_DASH;//TODO: --//--
        //if(ext.startsWith("ismv")) return TYPE_MP4;//

        //exo cant...native can
        if (ext.equals("flv")) return TYPE_OTHER;

        if (ext.equals("m3u8")) return TYPE_HLS;

        if (ext.equals("wav")) return TYPE_DEFAULT;

        return TYPE_OTHER;
    }

    @Override
    public int getContentType() {
        return contentType;
    }

    @Override
    public Uri getContentUri() {
        return uriString;
    }

    @Override
    public String getContentId() {
        return null;
    }

    @Override
    public String getProviderDASH() {
        return "widevine_test";
    }
}
