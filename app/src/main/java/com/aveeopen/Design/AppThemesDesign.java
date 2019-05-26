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

import android.app.Activity;

import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Utils;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.MainActivity;
import com.aveeopen.R;
import com.aveeopen.SettingsActivity;

import java.util.LinkedList;
import java.util.List;

public class AppThemesDesign {

    private List<Object> listenerRefHolder = new LinkedList<>();

    public AppThemesDesign() {

        SettingsActivity.onPrefAppThemeChanged.subscribeWeak(new WeakEvent1.Handler<Object>() {
            @Override
            public void invoke(Object newValue) {
                MainActivity mainActivity = MainActivity.getInstance();
                if (mainActivity != null)
                    mainActivity.recreate();
            }
        }, listenerRefHolder);


        MainActivity.onCreate.subscribeWeak(new WeakEvent1.Handler<Activity>() {
            @Override
            public void invoke(Activity activity) {
                {
                    String themeStr = AppPreferences.createOrGetInstance().preferencesGetStringSafe(activity, "pref_appTheme", "0");
                    int themeIndex = Utils.strToIntSafe(themeStr);
                    switch (themeIndex) {
                        case 0:
                            activity.setTheme(R.style.AppTheme);
                            break;
                        case 1:
                            activity.setTheme(R.style.AppTheme2);
                            break;
                        case 2:
                            activity.setTheme(R.style.AppTheme3);
                            break;
                        case 3:
                            activity.setTheme(R.style.AppTheme4);
                            break;
                        case 4:
                            activity.setTheme(R.style.AppTheme5);
                            break;
                    }
                }
            }
        }, listenerRefHolder);
    }

}
