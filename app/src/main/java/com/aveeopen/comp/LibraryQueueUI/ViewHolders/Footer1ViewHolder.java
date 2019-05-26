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
import com.aveeopen.comp.LibraryQueueUI.Containers.Base.ContainerBase;
import com.aveeopen.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import java.lang.ref.WeakReference;

public class Footer1ViewHolder
        extends BaseViewHolder
        implements ContainerBase.IContainerStatusListener {

    //ADS
    private static boolean adViewLastTimeLoadSuccess = false;//if we are offline/online, show ad visible/gone before its loaded, so no blinking space
    private AdView adView;
    private TextView textInfo, textInfoItems;

    public Footer1ViewHolder(Context context, ViewGroup parent) {
        super(UtilsUI.getInflaterFromContext(context).inflate(R.layout.footer_item, parent, false));

        View view = this.itemView;

        textInfo = (TextView) view.findViewById(R.id.textInfo);
        textInfoItems = (TextView) view.findViewById(R.id.textInfoItems);

        View navBarPlaceHolder = view.findViewById(R.id.navbar_placeholder);
        ViewGroup.LayoutParams params = navBarPlaceHolder.getLayoutParams();
        params.height = UtilsUI.getNavBarHeightIgnoreOrienCached(context);
        navBarPlaceHolder.setLayoutParams(params);

        adView = (AdView) view.findViewById(R.id.adView);

        if (adView != null) {

            if (adViewLastTimeLoadSuccess)
                adView.setVisibility(ViewGroup.VISIBLE);
            else
                adView.setVisibility(ViewGroup.GONE);

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    adView.setVisibility(ViewGroup.VISIBLE);
                    adViewLastTimeLoadSuccess = true;
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    adView.setVisibility(ViewGroup.GONE);
                    adViewLastTimeLoadSuccess = false;
                }
            });
        }
    }

    public void onBind(final IContainerData containerData, int position) {
        if (adView != null)
            onShowAdView.invoke(adView, 1, containerData.getItemCount());

        textInfo.setText("");
        textInfoItems.setText("");
        containerData.setContainerStatusListener(new WeakReference<ContainerBase.IContainerStatusListener>(this));

        onDataItemCountChanged();
    }

    private void onDataItemCountChanged() {

    }

    @Override
    public void onItemCountChanged(int itemCount, int totalTime, boolean searchingActive) {

        int itemCountVisible = itemCount;

        if (searchingActive) {
            textInfo.setText(R.string.searching);
            String txt = textInfoItems.getResources().getQuantityString(
                    R.plurals.showing_x_of_y_items3_item_count, itemCountVisible, itemCountVisible);
            textInfoItems.setText(txt);
        } else {

            if (itemCountVisible == 0) {
                textInfo.setText(R.string.nothing_to_show);
                textInfoItems.setText("");
            } else {
                String txt = textInfoItems.getResources().getQuantityString(
                        R.plurals.showing_x_of_y_items3_item_count, itemCountVisible, itemCountVisible);
                textInfoItems.setText(txt);
            }
        }
    }

}