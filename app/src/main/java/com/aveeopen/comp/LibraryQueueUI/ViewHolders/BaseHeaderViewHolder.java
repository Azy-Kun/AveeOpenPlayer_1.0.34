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
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.ContextData;
import com.aveeopen.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import java.util.List;

public abstract class BaseHeaderViewHolder extends BaseViewHolder {

    public static WeakEventR<Boolean> onUIRequestFollowCurrentValue = new WeakEventR<>();
    public static WeakEvent1<ContextData> onActionChooseSortFiles = new WeakEvent1<>();
    public static WeakEvent1<ContextData> onActionChooseSort = new WeakEvent1<>();
    public static WeakEvent2<ContextData /*contextData*/, Integer /*action*/> onAction = new WeakEvent2<>();
    public static WeakEvent1<ContextData /*contextData*/> onLibraryQueue2UI_ActionScanStandalonePlaylist = new WeakEvent1<>();
    public static WeakEvent3<long[] /*addSongsNativePL*/, List<String> /*addSongDataSources*/, ContextData /*contextData*/> onActionCreatePlaylist = new WeakEvent3<>();

    private static boolean mAdViewLastTimeLoadSuccess = false;//if we are offline/online, show ad visible/gone before its loaded, so no blinking space
    //ADS
    private AdView adView;

    public BaseHeaderViewHolder(View itemView) {
        super(itemView);

        adView = (AdView) itemView.findViewById(R.id.adView);

        if (adView != null) {

            if (mAdViewLastTimeLoadSuccess)
                adView.setVisibility(ViewGroup.VISIBLE);
            else
                adView.setVisibility(ViewGroup.GONE);

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    adView.setVisibility(ViewGroup.VISIBLE);
                    mAdViewLastTimeLoadSuccess = true;
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    adView.setVisibility(ViewGroup.GONE);
                    mAdViewLastTimeLoadSuccess = false;
                }
            });
        }
    }

    protected static void updateShowButtonTexts(TextView[] buttonTexts, Context context) {
        boolean showButtonTexts = AppPreferences.preferencesGetBoolSafe(AppPreferences.createOrGetInstance().getPreferences(context), "pref_toolButtonsShowTexts", true);

        if (showButtonTexts) {
            for (TextView txt : buttonTexts) {
                txt.setVisibility(View.VISIBLE);
            }
        } else {
            for (TextView txt : buttonTexts) {
                txt.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBind(IContainerData containerData, int position) {
        if (adView != null)
            onShowAdView.invoke(adView, 0, containerData.getItemCount());
    }

}
