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

package com.aveeopen.comp.LibraryQueueUI.ViewHolders;

import android.content.Context;
import android.view.ViewGroup;

import junit.framework.Assert;

public class ViewHolderFactory {

    //view types
    public static final int VIEW_HOLDER_libContent = 0;
    public static final int VIEW_HOLDER_footer1 = 1;
    public static final int VIEW_HOLDER_plain = 2;
    public static final int VIEW_HOLDER_plain_gone = 3;
    public static final int VIEW_HOLDER_queue = 4;
    public static final int VIEW_HOLDER_folders = 5;
    public static final int VIEW_HOLDER_playLists = 6;
    public static final int VIEW_HOLDER_albums = 7;
    public static final int VIEW_HOLDER_artists = 8;
    public static final int VIEW_HOLDER_genres = 9;
    public static final int VIEW_HOLDER_songsItem = 10;
    public static final int VIEW_HOLDER_songFilesItem = 11;
    public static final int VIEW_HOLDER_section = 12;

    public static BaseViewHolder newInstance(Context context, ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_HOLDER_libContent:
                return new ContentItemViewHolder(parent);
            case VIEW_HOLDER_footer1:
                return new Footer1ViewHolder(context, parent);
            case VIEW_HOLDER_plain:
                return new HeaderPlainViewHolder(context, parent);
            case VIEW_HOLDER_plain_gone:
                return new HeaderPlainGoneViewHolder(context, parent);
            case VIEW_HOLDER_queue:
                return new HeaderQueueViewHolder(context, parent);
            case VIEW_HOLDER_folders:
                return new HeaderFoldersViewHolder(context, parent);
            case VIEW_HOLDER_playLists:
                return new HeaderPlaylistViewHolder(context, parent);
            case VIEW_HOLDER_albums:
                return new HeaderAlbumsViewHolder(context, parent);
            case VIEW_HOLDER_artists:
                return new HeaderArtistsViewHolder(context, parent);
            case VIEW_HOLDER_genres:
                return new HeaderGenresViewHolder(context, parent);
            case VIEW_HOLDER_songsItem:
                return new HeaderSongsViewHolder(context, parent);
            case VIEW_HOLDER_songFilesItem:
                return new HeaderSongFilesViewHolder(context, parent);
            case VIEW_HOLDER_section:
                return new SectionViewHolder(context, parent);
            default:
                Assert.fail("viewType: " + viewType);
                return null;
        }
    }
}
