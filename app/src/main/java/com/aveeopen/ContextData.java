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

package com.aveeopen;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class ContextData {

    private Activity activity;

    public ContextData(Activity activity) {
        this.activity = activity;
    }

    public ContextData(View view) {
        activity = (Activity)view.getContext();
    }

    public FragmentManager getFragmentManager() {
        return activity.getFragmentManager();
    }

    public LayoutInflater getLayoutInflater() {
        return activity.getLayoutInflater();
    }

    public Context getContext() {
        return activity;
    }
}
