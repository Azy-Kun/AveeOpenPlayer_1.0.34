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

package com.aveeopen.Design;

import android.view.ViewGroup;

import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.comp.LibraryQueueUI.LibraryQueueFragmentBase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.LinkedList;
import java.util.List;

public class AdsDesign {

    private List<Object> listenerRefHolder = new LinkedList<>();

    public AdsDesign() {

        LibraryQueueFragmentBase.onShowAdView.subscribeWeak(new WeakEvent3.Handler<AdView, Integer, Integer>() {
            @Override
            public void invoke(AdView adView, Integer id, Integer containerItemCount) {

                //if (id == 0) {}//header banner
                if (id == 1) //footer banner
                {
                    if (containerItemCount < 7) {
                        adView.setVisibility(ViewGroup.GONE);
                        return;
                    }
                }

                // Create an ad request.
                AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

                //Remove calls to addTestDevice in PUBLISH VERSION
                //get test ads on this device.
                //adRequestBuilder.addTestDevice("017A8B0B9B26571BC381AD87C6F682F2");//AdRequest.DEVICE_ID_EMULATOR);

                // Start loading the ad.
                AdRequest adRequest = adRequestBuilder.build();
                adView.loadAd(adRequest);
            }
        }, listenerRefHolder);
    }
}
