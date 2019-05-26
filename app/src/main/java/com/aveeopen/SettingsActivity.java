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

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.R;
import com.aveeopen.comp.AppPreferences.AppPreferences;

import nz.net.speakman.androidlicensespage.LicensesFragment;

public class SettingsActivity extends PreferenceActivity {

    public static WeakEvent1<ContextData /*contextData*/> onSendGeneralFeedbackAction = new WeakEvent1<>();
    public static WeakEvent1<Object /*newValue*/> onPrefAppThemeChanged = new WeakEvent1<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_settings_name));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle paramBundle) {
            super.onCreate(paramBundle);

            //important to save AppPreferences first,
            // so SharedPreferences gets updated and UI reflects changes
            AppPreferences.createOrGetInstance().save(this.getActivity());

            addPreferencesFromResource(R.xml.preferences);

            EditTextPreference buttonPlaylistDefaultPath = (EditTextPreference) findPreference("pref_playlistDefaultPath");
            buttonPlaylistDefaultPath.setSummary(buttonPlaylistDefaultPath.getText());

            addMyResetListener();
        }

        private void addMyResetListener() {

            Preference button = findPreference("pref_resetToDefault");
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.pref_reset_dialog_title);
                    builder.setMessage(R.string.pref_reset_dialog_message);
                    builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            resetSettings(SettingsFragment.this.getActivity().getApplicationContext());
                        }
                    });
                    builder.setNegativeButton(R.string.dialog_cancel, null);
                    builder.create().show();

                    return true;
                }
            });

            {
                Preference listAppTheme = findPreference("pref_appTheme");

                listAppTheme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        //SettingsFragment.this.getActivity().recreate();
                        onPrefAppThemeChanged.invoke(newValue);

                        return true;
                    }
                });
            }

            {
                Preference buttonTips = findPreference("pref_resetTips");
                buttonTips.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {

                        AppPreferences.createOrGetInstance().resetTips();

                        return true;
                    }
                });
            }

            {
                Preference buttonLicenses = findPreference("pref_openSourceLicenses");
                buttonLicenses.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {

                        LicensesFragment.displayLicensesFragment(getFragmentManager(), true);

                        return true;
                    }
                });
            }

            {
                Preference buttonPlaylistDefaultPath = findPreference("pref_playlistDefaultPath");
                buttonPlaylistDefaultPath.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        preference.setSummary(newValue.toString());

                        return true;
                    }
                });
            }

//            {
//                Preference buttonTips = findPreference("pref_emailDeveloper");
//                buttonTips.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                    @Override
//                    public boolean onPreferenceClick(Preference arg0) {
//
//                        final ContextData contextData = new ContextData(getActivity());
//                        onSendGeneralFeedbackAction.invoke(contextData);
//
//                        return true;
//                    }
//                });
//            }

        }

        void resetSettings(Context ctx) {
            PreferenceManager
                    .getDefaultSharedPreferences(ctx)
                    .edit()
                    .clear()
                    .commit();
            PreferenceManager.setDefaultValues(ctx, R.xml.preferences, true);

            setPreferenceScreen(null); //this clears our setOnPreferenceClickListener, so we call addMyResetListener
            addPreferencesFromResource(R.xml.preferences);

            addMyResetListener();
        }
    }

}