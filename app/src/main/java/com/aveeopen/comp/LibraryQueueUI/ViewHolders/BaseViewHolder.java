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

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.aveeopen.comp.LibraryQueueUI.Containers.Base.IContainerData;
import com.aveeopen.Common.Events.WeakEvent3;
import com.google.android.gms.ads.AdView;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    public static WeakEvent3<AdView /*adView*/, Integer /*id*/, Integer /*containerItemCount*/> onShowAdView = new WeakEvent3<>();

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBind(IContainerData containerData, int position);
}
