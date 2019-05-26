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
import android.widget.ImageView;
import android.widget.TextView;

import com.aveeopen.comp.LibraryQueueUI.Containers.Base.IContainerData;
import com.aveeopen.comp.LibraryQueueUI.LibraryQueueFragmentBase;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.UtilsUI;
import com.aveeopen.comp.playback.MediaPlaybackServiceDefs;
import com.aveeopen.ContextData;
import com.aveeopen.R;

import java.util.LinkedList;
import java.util.List;

public class HeaderQueueViewHolder
        extends BaseHeaderViewHolder {

    private TextView[] buttonTexts = new TextView[6];
    private ImageView[] buttonIcons = new ImageView[6];
    private int btnColor0;
    private List<Object> listenerRefHolder = new LinkedList<>();

    public HeaderQueueViewHolder(final Context context, ViewGroup parent) {
        super(UtilsUI.getInflaterFromContext(context).inflate(R.layout.header_queue_item, parent, false));
        View view = this.itemView;

        btnColor0 = view.getResources().getColor(R.color.black_alpha_1);

        buttonTexts[0] = (TextView) view.findViewById(R.id.txt1);
        buttonTexts[1] = (TextView) view.findViewById(R.id.txt2);
        buttonTexts[2] = (TextView) view.findViewById(R.id.txt3);
        buttonTexts[3] = (TextView) view.findViewById(R.id.txt4);
        buttonTexts[4] = (TextView) view.findViewById(R.id.txt5);
        buttonTexts[5] = (TextView) view.findViewById(R.id.txt6);

        buttonIcons[3] = (ImageView) view.findViewById(R.id.btn4);
        buttonIcons[4] = (ImageView) view.findViewById(R.id.btn5);
        buttonIcons[5] = (ImageView) view.findViewById(R.id.btn6);

        updateShowButtonTexts(buttonTexts, context);

        View[] buttons = new View[6];
        buttons[0] = view.findViewById(R.id.group1);
        buttons[1] = view.findViewById(R.id.group2);
        buttons[2] = view.findViewById(R.id.group3);
        buttons[3] = view.findViewById(R.id.group4);
        buttons[4] = view.findViewById(R.id.group5);
        buttons[5] = view.findViewById(R.id.group6);

        buttons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAction.invoke(new ContextData(v), LibraryQueueFragmentBase.ACTION_AddByLink);
            }
        });

        buttons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAction.invoke(new ContextData(v), LibraryQueueFragmentBase.ACTION_ClearQueue);
            }
        });

        buttons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAction.invoke(new ContextData(v), LibraryQueueFragmentBase.ACTION_SaveAs);

            }
        });

        buttons[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAction.invoke(new ContextData(v), LibraryQueueFragmentBase.ACTION_Shuffle);
            }
        });

        buttons[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAction.invoke(new ContextData(v), LibraryQueueFragmentBase.ACTION_FollowCurrent);
            }
        });

        buttons[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAction.invoke(new ContextData(v), LibraryQueueFragmentBase.ACTION_ShowAlbumArt);
            }
        });
    }

    public void onBind(final IContainerData containerData, int position) {
        super.onBind(containerData, position);

        containerData.getLibraryContainerDataListener().subscribeWeakShuffleModeChanged(new WeakEvent1.Handler<Integer>() {
            @Override
            public void invoke(Integer shuffleMode) {
                onShuffleModeChanged(shuffleMode);
            }
        }, listenerRefHolder);

        containerData.getLibraryContainerDataListener().subscribeWeakFollowCurrentValueChanged(new WeakEvent1.Handler<Boolean>() {
            @Override
            public void invoke(Boolean followCurrentState) {
                onFollowCurrentChanged(followCurrentState);
            }
        }, listenerRefHolder);

        containerData.getLibraryContainerDataListener().subscribeWeakShowAlbumArtValueChanged(new WeakEvent1.Handler<Boolean>() {
            @Override
            public void invoke(Boolean showAlbumArt) {
                onShowAlbumArtInsteadChanged(showAlbumArt);
            }
        }, listenerRefHolder);

        boolean followCurrentState = onUIRequestFollowCurrentValue.invoke(false);
        boolean showAlbumArtInstead = containerData.getShowAlbumArtValue();

        onFollowCurrentChanged(followCurrentState);
        onShowAlbumArtInsteadChanged(showAlbumArtInstead);

        IContainerData.ILibraryContainerDataListener containerDataListener = containerData.getLibraryContainerDataListener();
        if (containerDataListener != null)
            onShuffleModeChanged(containerDataListener.onRequestShuffleMode());
    }

    public void onShuffleModeChanged(int shuffleMode) {
        if (shuffleMode != MediaPlaybackServiceDefs.SHUFFLE_NONE)
            buttonIcons[3].setColorFilter(UtilsUI.getAttrColor(buttonIcons[3], R.attr.highlight_color_2));
        else
            buttonIcons[3].setColorFilter(btnColor0);
    }


    public void onFollowCurrentChanged(boolean state) {
        if (state)
            buttonIcons[4].setColorFilter(UtilsUI.getAttrColor(buttonIcons[4] , R.attr.highlight_color_2));
        else
            buttonIcons[4].setColorFilter(btnColor0);
    }

    public void onShowAlbumArtInsteadChanged(boolean state) {
        if (state)
            buttonIcons[5].setColorFilter(UtilsUI.getAttrColor(buttonIcons[5], R.attr.highlight_color_2));
        else
            buttonIcons[5].setColorFilter(btnColor0);
    }

}