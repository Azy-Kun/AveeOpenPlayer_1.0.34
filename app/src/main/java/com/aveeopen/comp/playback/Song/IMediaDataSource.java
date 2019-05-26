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

import com.aveeopen.comp.playback.ExoMediaPlayer.Defines;

public interface IMediaDataSource {

    int TYPE_DASH = Defines.TYPE_DASH;
    int TYPE_SS = Defines.TYPE_SS;
    int TYPE_HLS = Defines.TYPE_HLS;
    int TYPE_OTHER = Defines.TYPE_OTHER;
    int TYPE_DEFAULT = Defines.TYPE_DEFAULT;

    int getContentType();

    Uri getContentUri();

    String getContentId();

    String getProviderDASH();
}
