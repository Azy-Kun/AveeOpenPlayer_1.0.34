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

package com.aveeopen.comp.AlbumArt;

public class AlbumArtRequest {

    public final String videoThumbDataSource;
    public final String path0;
    public final String path1;
    public final String genStr;

    public AlbumArtRequest(String videoThumbDataSource, String path0, String path1, String genStr) {
        this.videoThumbDataSource = videoThumbDataSource;
        this.path0 = path0;
        this.path1 = path1;
        this.genStr = genStr;
    }

    public AlbumArtRequest makeCopy() {
        return new AlbumArtRequest(
                videoThumbDataSource != null ? new String(videoThumbDataSource) : null,
                path0 != null ? new String(path0) : null,
                path1 != null ? new String(path1) : null,
                genStr != null ? new String(genStr) : null);
    }

}
