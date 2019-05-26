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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aveeopen.comp.LibraryQueueUI.Containers.Base.IContainerData;
import com.aveeopen.Common.UtilsUI;
import com.aveeopen.ContextData;
import com.aveeopen.R;

public class HeaderPlaylistViewHolder
        extends BaseHeaderViewHolder {

    private View[] imgBtns = new View[2];
    private TextView[] buttonTexts = new TextView[2];

    public HeaderPlaylistViewHolder(Context context, ViewGroup parent) {
        super(UtilsUI.getInflaterFromContext(context).inflate(R.layout.header_playlists_item, parent, false));

        View view = this.itemView;

        imgBtns[0] = view.findViewById(R.id.group1);
        imgBtns[1] = view.findViewById(R.id.group2);
        buttonTexts[0] = (TextView) view.findViewById(R.id.txt1);
        buttonTexts[1] = (TextView) view.findViewById(R.id.txt2);

        updateShowButtonTexts(buttonTexts, context);
    }

    public void onBind(final IContainerData containerData, int position) {
        super.onBind(containerData, position);

        HeaderPlaylistViewHolder holder = this;

        holder.imgBtns[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionCreatePlaylist.invoke(null, null, new ContextData(v));
            }
        });

        holder.imgBtns[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLibraryQueue2UI_ActionScanStandalonePlaylist.invoke(new ContextData(v));
            }
        });
    }

}