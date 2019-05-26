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

package com.aveeopen.comp.AppPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.MultiList;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.Common.Utils;
import com.aveeopen.Common.UtilsFileSys;
import com.aveeopen.Common.UtilsSerialize;
import com.aveeopen.Design.SortDesign;
import com.aveeopen.Common.tlog;
import com.aveeopen.comp.Visualizer.Elements.Element;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class AppPreferences implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static WeakEvent3<Integer /*preference*/, Integer /*value*/, Boolean /*userForce*/> onIntPreferenceChanged = new WeakEvent3<>();
    public static WeakEvent2<Integer /*preference*/, Boolean /*value*/> onBoolPreferenceChanged = new WeakEvent2<>();
    public static WeakEvent2<Integer /*preference*/, String /*value*/> onStringPreferenceChanged = new WeakEvent2<>();

    private static final Object createInstanceLock = new Object();
    private static volatile WeakReference<AppPreferences> instanceWeak = new WeakReference<>(null);

    private static int offset_Bool = 1000;
    public static int PREF_Bool_pref_visControlsTimeout = offset_Bool;
    public static int PREF_Bool_visualPreferShowVideoContent = offset_Bool + 1;
    public static int PREF_Bool_fixAssumeMonoOutputFromMonoInput = offset_Bool + 2;
    public static int PREF_Bool_followCurrentState = offset_Bool + 3;
    public static int PREF_Bool_audioMuteState = offset_Bool + 4;
    public static int PREF_Bool_showAlbumArtInstead = offset_Bool + 5;
    public static int PREF_Bool_tipShow_reorder = offset_Bool + 6;
    public static int PREF_Bool_firstLaunch = offset_Bool + 7;
    public static int PREF_Bool_uiSectionOpened0 = offset_Bool + 8;
    public static int PREF_Bool_uiSectionOpened1 = offset_Bool + 9;
    public static int PREF_Bool_uiSectionOpened00 = offset_Bool + 10;
    public static int PREF_Bool_uiSectionOpened01 = offset_Bool + 11;
    public static int PREF_Bool_uiSectionOpened2 = offset_Bool + 12;
    public static int PREF_Bool_visualizerUseGlobalSession = offset_Bool + 13;
    public static int PREF_Bool_equalizerEnabled = offset_Bool + 14;
    private static int PREF_Bool_COUNT = offset_Bool + 15;

    private static int offset_Int = 2000;
    public static int PREF_Int_mainPageIndex = offset_Int;
    public static int PREF_Int_recentlyAddedWeeks = offset_Int + 1;
    public static int PREF_Int_visualizerThemeId = offset_Int + 2;
    public static int PREF_Int_lockOrient = offset_Int + 3;
    public static int PREF_Int_playbackEngine = offset_Int + 4;
    public static int PREF_Int_videoScalingMode = offset_Int + 5;
    public static int PREF_Int_SortSelectedRadioOption = offset_Int + 6;
    public static int PREF_Int_SortMaskCheckOptions = offset_Int + 7;
    public static int PREF_Int_volumeStereoBalance = offset_Int + 8;
    public static int PREF_Int_crossfadeValue = offset_Int + 9;
    public static int PREF_Int_equalizerPreset = offset_Int + 10;
    public static int PREF_Int_equalizerBassValue = offset_Int + 11;
    public static int PREF_Int_equalizerTrebleValue = offset_Int + 12;
    public static int PREF_Int_virtualizerStrength = offset_Int + 13;
    public static int PREF_Int_reverbPreset = offset_Int + 14;
    private static int PREF_Int_COUNT = offset_Int + 15;

    private static int offset_String = 3000;
    public static int PREF_String_currentAbsoluteLibraryAddress = offset_String;
    public static int PREF_String_vThemeCustomization0 = offset_String + 1;
    public static int PREF_String_vThemeCustomization1 = offset_String + 2;
    public static int PREF_String_vThemeCustomization2 = offset_String + 3;
    public static int PREF_String_vThemeCustomization3 = offset_String + 4;
    public static int PREF_String_vThemeCustomization4 = offset_String + 5;
    public static int PREF_String_vThemeCustomization5 = offset_String + 6;
    public static int PREF_String_vThemeCustomization6 = offset_String + 7;
    public static int PREF_String_vThemeCustomization7 = offset_String + 8;
    public static int PREF_String_vThemeCustomization8 = offset_String + 9;
    public static int PREF_String_vThemeCustomization9 = offset_String + 10;
    public static int PREF_String_vThemeCustomization10 = offset_String + 11;
    public static int PREF_String_equalizerBarsValues = offset_String + 12;
    private static int PREF_String_COUNT = offset_String + 13;

    private AtomicIntegerArray prefBool = new AtomicIntegerArray(PREF_Bool_COUNT - offset_Bool);
    private AtomicIntegerArray prefsInt = new AtomicIntegerArray(PREF_Int_COUNT - offset_Int);
    private AtomicReferenceArray<String> prefsString = new AtomicReferenceArray<>(PREF_String_COUNT - offset_String);
    private String defaultFolderString = null;

    private AppPreferences() {
        setBoolDefault(PREF_Bool_pref_visControlsTimeout, false);//false
        setBoolDefault(PREF_Bool_visualPreferShowVideoContent, false);//false
        setBoolDefault(PREF_Bool_fixAssumeMonoOutputFromMonoInput, true);//true
        setBoolDefault(PREF_Bool_followCurrentState, true);//true
        setBoolDefault(PREF_Bool_audioMuteState, false);//false
        setBoolDefault(PREF_Bool_showAlbumArtInstead, true);//true
        setBoolDefault(PREF_Bool_tipShow_reorder, true);//true
        setBoolDefault(PREF_Bool_firstLaunch, true);//true
        setBoolDefault(PREF_Bool_uiSectionOpened0, true);//true
        setBoolDefault(PREF_Bool_uiSectionOpened1, true);//true
        setBoolDefault(PREF_Bool_uiSectionOpened00, true);//true
        setBoolDefault(PREF_Bool_uiSectionOpened01, true);//true
        setBoolDefault(PREF_Bool_visualizerUseGlobalSession, true);//true
        setBoolDefault(PREF_Bool_equalizerEnabled, false);

        setIntDefault(PREF_Int_mainPageIndex, 1);
        setIntDefault(PREF_Int_recentlyAddedWeeks, 2);
        setIntDefault(PREF_Int_visualizerThemeId, 8);//themeid 7
        setIntDefault(PREF_Int_lockOrient, 0);//0 - disabled
        setIntDefault(PREF_Int_playbackEngine, 1);//0 - native //1 - exo
        setIntDefault(PREF_Int_videoScalingMode, 1);//1 - fit
        setIntDefault(PREF_Int_SortSelectedRadioOption, SortDesign.Sort_Mode_Title);//title
        setIntDefault(PREF_Int_SortMaskCheckOptions, 0);
        setIntDefault(PREF_Int_volumeStereoBalance, 0); //-100 .. 100
        setIntDefault(PREF_Int_crossfadeValue, -1000);//-1000: 0ff, 0:gapless, 0 .. X000 //ms
        setIntDefault(PREF_Int_equalizerPreset, -1);
        setIntDefault(PREF_Int_equalizerBassValue, 0);
        setIntDefault(PREF_Int_equalizerTrebleValue, 0);
        setIntDefault(PREF_Int_virtualizerStrength, 0);//0..1000
        setIntDefault(PREF_Int_reverbPreset, 0);

        setStringDefault(PREF_String_currentAbsoluteLibraryAddress, "");
        setStringDefault(PREF_String_equalizerBarsValues, "");
    }

    public static AppPreferences createOrGetInstance() {
        AppPreferences inst0 = instanceWeak.get();
        if (inst0 != null) return inst0;

        synchronized (createInstanceLock) {
            AppPreferences inst = instanceWeak.get();
            if (inst == null) {
                inst = new AppPreferences();
                instanceWeak = new WeakReference<>(inst);
            }

            return inst;
        }
    }

    public static boolean preferencesGetBoolSafe(SharedPreferences settings, String key, boolean defValue) {
        try {
            return settings.getBoolean(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static int preferencesGetIntSafe(SharedPreferences settings, String key, int defValue) {
        try {
            return settings.getInt(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static String preferencesGetStringSafe(SharedPreferences settings, String key, String defValue) {
        try {
            return settings.getString(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public boolean preferencesGetBoolSafe(Context context, String key, boolean defValue) {
        return preferencesGetBoolSafe(getPreferences(context), key, defValue);
    }

    public int preferencesGetIntSafe(Context context, String key, int defValue) {
        return preferencesGetIntSafe(getPreferences(context), key, defValue);
    }

    public String preferencesGetStringSafe(Context context, String key, String defValue) {
        return preferencesGetStringSafe(getPreferences(context), key, defValue);
    }

    public boolean getBool(int pref) {
        return prefBool.get(pref - offset_Bool) != 0;
    }

    public int getInt(int pref) {
        return prefsInt.get(pref - offset_Int);
    }

    public String getString(int pref) {
        return prefsString.get(pref - offset_String);
    }

    public void toggleBool(final int preference) {
        setBool(preference, prefBool.get(preference - offset_Bool) == 0);
    }

    public void setBool(final int preference, final boolean value) {
        int oldValue = prefBool.getAndSet(preference - offset_Bool, value ? 1 : 0);

        if (value == (oldValue == 0))
            onBoolPreferenceChanged.invoke(preference, value);
    }

    public void setInt(final int preference, final int value) {
        setInt(preference, value, false);
    }

    public void setInt(final int preference, final int value, boolean userForce) {
        int oldValue = prefsInt.getAndSet(preference - offset_Int, value);

        if (userForce || value != oldValue)
            onIntPreferenceChanged.invoke(preference, value, userForce);
    }

    public void setString(final int preference, final String value) {
        String oldValue = prefsString.getAndSet(preference - offset_String, value);

        if (Utils.compareNullEqual(oldValue, value))
            onStringPreferenceChanged.invoke(preference, value);
    }

    public void setBoolDefault(final int preference, final boolean value) {
        prefBool.set(preference - offset_Bool, value ? 1 : 0);
    }

    public void setIntDefault(final int preference, final int value) {
        prefsInt.set(preference - offset_Int, value);
    }

    public void setStringDefault(final int preference, final String value) {
        prefsString.set(preference - offset_String, value);
    }

    private void onContext(Context appContext) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(appContext);
        settings.registerOnSharedPreferenceChangeListener(this);

        load_pref_playbackEngine(settings);
        load_pref_visControlsTimeout(settings);
        load_pref_visualizerGlobalSession(settings);
    }

    public void load(Context context) {
        onContext(context);

        //load
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        for (int i = 0; i < prefBool.length(); i++) {
            try {
                final boolean value = preferencesGetBoolSafe(preferences, "bool" + i, prefBool.get(i) != 0);
                setBool(i + offset_Bool, value);
            } catch (Exception ignored) {
            }
        }

        for (int i = 0; i < prefsInt.length(); i++) {
            try {
                final int value = preferencesGetIntSafe(preferences, "int" + i, prefsInt.get(i));
                setInt(i + offset_Int, value);
            } catch (Exception ignored) {
            }
        }


        for (int i = 0; i < prefsString.length(); i++) {
            try {
                final String value = preferencesGetStringSafe(preferences, "string" + i, prefsString.get(i));
                setString(i + offset_String, value);
            } catch (Exception ignored) {
            }
        }
    }

    public void save(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        //save
        SharedPreferences.Editor ed = preferences.edit();

        for (int i = 0; i < prefBool.length(); i++) {
            boolean value = prefBool.get(i) != 0;
            ed.putBoolean("bool" + i, value);
        }

        for (int i = 0; i < prefsInt.length(); i++) {
            int value = prefsInt.get(i);
            ed.putInt("int" + i, value);
        }

        for (int i = 0; i < prefsString.length(); i++) {
            String value = prefsString.get(i);
            ed.putString("string" + i, value);
        }

        save_pref_playbackEngine(ed);
        save_pref_visControlsTimeout(ed);
        save_pref_visualizerGlobalSession(ed);


        ed.apply();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences settings, String key) {
        switch (key) {
            case "pref_playbackEngine":
                load_pref_playbackEngine(settings);
                break;
            case "pref_visControlsTimeout":
                load_pref_visControlsTimeout(settings);
                break;
            case "pref_visualizerGlobalSession":
                load_pref_visualizerGlobalSession(settings);
                break;
        }
    }

    void load_pref_playbackEngine(SharedPreferences settings) {
        String valueStr = preferencesGetStringSafe(settings, "pref_playbackEngine", "0");
        int valueInt = Utils.strToIntSafe(valueStr);
        this.setInt(PREF_Int_playbackEngine, valueInt);
    }

    void save_pref_playbackEngine(SharedPreferences.Editor ed) {
        ed.putString("pref_playbackEngine", "" + this.getInt(PREF_Int_playbackEngine));
    }

    void load_pref_visControlsTimeout(SharedPreferences settings) {
        boolean val = preferencesGetBoolSafe(settings, "pref_visControlsTimeout", false);
        this.setBool(PREF_Bool_pref_visControlsTimeout, val);
    }

    void save_pref_visControlsTimeout(SharedPreferences.Editor ed) {
        ed.putBoolean("pref_visControlsTimeout", this.getBool(PREF_Bool_pref_visControlsTimeout));
    }

    void load_pref_visualizerGlobalSession(SharedPreferences settings) {
        boolean val = preferencesGetBoolSafe(settings, "pref_visualizerGlobalSession", true);
        this.setBool(PREF_Bool_visualizerUseGlobalSession, val);
    }

    void save_pref_visualizerGlobalSession(SharedPreferences.Editor ed) {
        ed.putBoolean("pref_visualizerGlobalSession", this.getBool(PREF_Bool_visualizerUseGlobalSession));
    }

    public SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveAddTokenList(List<String> entrys, Context context, String key) {
        //load
        SharedPreferences preferences = getPreferences(context);
        String strdata = preferencesGetStringSafe(preferences, key, "");
        List<String> listedFolders = UtilsSerialize.deserializeIterableAsList(";", strdata);

        //save
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString(key, UtilsSerialize.serializeIterableSkipInvalidWithAdd(";", listedFolders, entrys, true));
        ed.apply();
    }

    public void prefAddLibraryFolderGenerateHash(String folderPath, Context context) {

        MultiList<String, String> entrys = prefGetLibraryFolders(context);

        Random rnd = new Random();

        String newIdHash;
        int maxCount = 1000000;
        int counter = 0;
        do {
            counter++;
            newIdHash = "" + rnd.nextInt(maxCount);
        } while (entrys.contains1(newIdHash) && counter < maxCount);

        prefAddLibraryFolder(newIdHash, folderPath, context);
    }

    public void prefAddLibraryFolder(String idhash, String folderPath, Context context) {
        if (idhash.contains(";")) return;
        if (idhash.contains(":")) return;
        if (folderPath.contains(";")) return;
        if (folderPath.contains(":")) return;

        //load
        SharedPreferences preferences = getPreferences(context);
        String strdata = getLibFoldersString(preferences);
        List<String> listedFolders = UtilsSerialize.deserializeIterableAsList(";", strdata);

        String entryStr = idhash + ":" + folderPath;
        //save
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString("libFolders", UtilsSerialize.serializeIterableSkipInvalidWithAdd(";", listedFolders, entryStr, true));
        ed.apply();
    }

    public void prefRemoveLibraryFolder(String idHash, String folderPath, Context context) {
        //load
        SharedPreferences preferences = getPreferences(context);
        String strdata = getLibFoldersString(preferences);
        List<String> listedFolders = UtilsSerialize.deserializeIterableAsList(";", strdata);//readonly list

        String entryStr = idHash + ":" + folderPath;

        //save
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString("libFolders", UtilsSerialize.serializeIterableSkipInvalidWithExclude(";", listedFolders, entryStr, true));
        ed.apply();
    }

    public MultiList<String, String> prefGetLibraryFolders(Context context) {
        SharedPreferences preferences = getPreferences(context);

        String strdata = getLibFoldersString(preferences);
        List<String> entryStr = UtilsSerialize.deserializeIterableAsList(";", strdata);

        MultiList<String, String> result = new MultiList<>(entryStr.size());
        for (String s : entryStr) {
            int index = s.indexOf(":");
            if (index < 0) continue;
            String s1 = s.substring(0, index);
            String s2 = s.substring(index + 1);
            result.add(new Tuple2<>(s1, s2));
        }

        return result;
    }

    String getLibFoldersString(SharedPreferences preferences) {
        if (defaultFolderString == null) {
            StringBuilder strbld = new StringBuilder();

            strbld.append("001:");
            strbld.append("/storage");
            strbld.append(";");

            try {
                strbld.append("002:");
                strbld.append(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM).getCanonicalPath());
                strbld.append(";");
            } catch (IOException ignored) {
            }


            try {
                strbld.append("003:");
                strbld.append(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MOVIES).getCanonicalPath());
                strbld.append(";");
            } catch (IOException ignored) {
            }

            try {
                strbld.append("004:");
                strbld.append(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC).getCanonicalPath());

                //don't add separator (;) for last
            } catch (IOException ignored) {
            }

            defaultFolderString = strbld.toString();
        }

        return preferencesGetStringSafe(preferences, "libFolders", defaultFolderString);
    }

    public void prefAddStandalonePlaylistGenerateHash(Context context, String path, boolean preventDuplicates) {

        List<String> pathList = new ArrayList<>(1);
        pathList.add(path);
        prefAddStandalonePlaylistGenerateHash(context, pathList, preventDuplicates);
    }

    public void prefAddStandalonePlaylistGenerateHash(Context context, List<String> path, boolean preventDuplicates) {

        MultiList<String, String> entrys = prefGetStandalonePlaylists(context);

        List<String> finalEntrys = new ArrayList<>();

        Random rnd = new Random();

        for (String pth : path) {

            if (preventDuplicates)
                if (entrys.contains2(pth)) continue;

            String newidhash;
            do {
                newidhash = "" + rnd.nextInt(1000000);
            } while (entrys.contains1(newidhash));

            if (newidhash.contains(";")) continue;
            if (newidhash.contains(":")) continue;
            if (pth.contains(";")) continue;
            if (pth.contains(":")) continue;

            finalEntrys.add(newidhash + ":" + pth);
        }

        //will do for now
        saveAddTokenList(finalEntrys, context, "libStandalonePlaylists");

    }

    public void prefRemoveStandalonePlaylist(String idHash, String folderPath, Context context) {
        //load
        SharedPreferences preferences = getPreferences(context);
        String strdata = preferencesGetStringSafe(preferences, "libStandalonePlaylists", "");
        List<String> listedFolders = UtilsSerialize.deserializeIterableAsList(";", strdata);//readonly list

        String entryStr = idHash + ":" + folderPath;

        //save
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString("libStandalonePlaylists", UtilsSerialize.serializeIterableSkipInvalidWithExclude(";", listedFolders, entryStr, true));
        ed.apply();
    }

    //idHash, path
    public MultiList<String, String> prefGetStandalonePlaylists(Context context) {
        SharedPreferences preferences = getPreferences(context);

        String strData = preferencesGetStringSafe(preferences, "libStandalonePlaylists", "");
        List<String> entryStr = UtilsSerialize.deserializeIterableAsList(";", strData);

        MultiList<String, String> result = new MultiList<>(entryStr.size());
        for (String s : entryStr) {

            int index = s.indexOf(":");
            if (index < 0) continue;
            String s1 = s.substring(0, index);
            String filepath = s.substring(index + 1);

            if (UtilsFileSys.fileExists(filepath)) //TODO: Move this check to design
                result.add(new Tuple2<>(s1, filepath));
        }

        return result;
    }

    public Element.CustomizationList getPrefThemeCustomizationData(int identifier) {

        int pref = themeCustomizationIdentifierToPref(identifier);
        if (pref < 0) {
            tlog.w("invalid _identifier");
            return null;
        }

        return Element.CustomizationList.deserialize(getString(pref));
    }

    public void savePrefThemeCustomizationData(int identifier, Element.CustomizationList customizationList) {

        int pref = themeCustomizationIdentifierToPref(identifier);
        if (pref < 0) {
            tlog.w("invalid _identifier");
            return;
        }

        String str = customizationList.serialize();

        if (str != null && str.length() > 1)
            setString(pref, str);

    }

    int themeCustomizationIdentifierToPref(int identifier) {
        int pref = PREF_String_vThemeCustomization0 + identifier;

        if (pref >= PREF_String_vThemeCustomization0 && pref <= PREF_String_vThemeCustomization10)
            return pref;

        return -1;
    }

    public void resetTips() {
        this.setBool(PREF_Bool_tipShow_reorder, true);
    }

}
