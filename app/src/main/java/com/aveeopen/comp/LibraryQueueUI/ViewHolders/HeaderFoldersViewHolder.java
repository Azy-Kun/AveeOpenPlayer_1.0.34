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
import com.aveeopen.comp.LibraryQueueUI.LibraryQueueFragmentBase;
import com.aveeopen.Common.UtilsUI;
import com.aveeopen.ContextData;
import com.aveeopen.R;

public class HeaderFoldersViewHolder
        extends BaseHeaderViewHolder {

    private TextView[] buttonTexts = new TextView[1];
    private View imgBtnPlay;

    public HeaderFoldersViewHolder(Context context, ViewGroup parent) {
        super(UtilsUI.getInflaterFromContext(context).inflate(R.layout.header_folder_item, parent, false));

        View view = this.itemView;

        imgBtnPlay = view.findViewById(R.id.group1);
        buttonTexts[0] = (TextView) view.findViewById(R.id.txt1);

        updateShowButtonTexts(buttonTexts, context);
    }

    public void onBind(final IContainerData containerData, int position) {
        super.onBind(containerData, position);

        HeaderFoldersViewHolder holder = this;

        holder.imgBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAction.invoke(new ContextData(v), LibraryQueueFragmentBase.ACTION_AddFolder);
            }
        });
    }

}