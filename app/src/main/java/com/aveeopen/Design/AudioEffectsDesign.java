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

import android.app.FragmentManager;
import android.os.Handler;

import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.Events.WeakEventR1;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.EqualizerUI.EQPreset;
import com.aveeopen.comp.EqualizerUI.Equalization;
import com.aveeopen.comp.EqualizerUI.EqualizerUIDesc;
import com.aveeopen.comp.EqualizerUI.EqualizerDialog;
import com.aveeopen.comp.EqualizerUI.EqualizerUISettings;
import com.aveeopen.comp.MediaControlsUI.MediaControlsUI;
import com.aveeopen.comp.playback.BaseEqualizerEffect;
import com.aveeopen.comp.playback.EventsPlaybackService;
import com.aveeopen.comp.playback.MediaPlaybackService;
import com.aveeopen.ContextData;
import com.aveeopen.MainActivity;

import java.util.LinkedList;
import java.util.List;

public class AudioEffectsDesign {

    private List<Object> listenerRefHolder = new LinkedList<>();
    private Handler threadHandler = new Handler();

    private EQPreset[] eqPresets;
    private EQPreset bassBoost;
    private EQPreset trebleBoost;

    public AudioEffectsDesign()
    {

        {
            bassBoost = new EQPreset("Bass Boost", 2);
            bassBoost.points[0] = (new EQPreset.Point(100.0f, 1.0f));
            bassBoost.points[1] = (new EQPreset.Point(500.0f, 0.0f));
        }

        {
            trebleBoost = new EQPreset("Treble Boost", 2);
            trebleBoost.points[0] = (new EQPreset.Point(1000.0f, 0.0f));
            trebleBoost.points[1] = (new EQPreset.Point(20000.0f, 1.0f));
            trebleBoost.points[1] = (new EQPreset.Point(20000.0f, 1.0f));
        }

        initEqPresets();


        MainActivity.onDestroy.subscribeWeak(new WeakEvent1.Handler<ContextData>() {
            @Override
            public void invoke(ContextData contextData) {

            }
        }, listenerRefHolder);


        MainActivity.onMainUIAction.subscribeWeak(new WeakEvent2.Handler<Integer, ContextData>() {
            @Override
            public void invoke(Integer id, ContextData contextData) {
                FragmentManager fragmentManager = contextData.getFragmentManager();
                if (fragmentManager == null) return;

                if (id == 3) {
                    EqualizerDialog.createAndShowEqualizerDialog(fragmentManager);
                }

            }
        }, listenerRefHolder);

        MediaControlsUI.onActionEq.subscribeWeak(new WeakEvent1.Handler<ContextData>() {
            @Override
            public void invoke(ContextData contextData) {
                FragmentManager fragmentManager = contextData.getFragmentManager();
                if (fragmentManager == null) return;

                EqualizerDialog.createAndShowEqualizerDialog(fragmentManager);
            }
        }, listenerRefHolder);

        MediaControlsUI.onRequestEqState.subscribeWeak(new WeakEventR.Handler<Boolean>() {
            @Override
            public Boolean invoke() {
                return AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_equalizerEnabled);// ||
                        //AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_virtualizerStrength)>0;
            }
        }, listenerRefHolder);

        EqualizerDialog.onRequestEqualizerDesc.subscribeWeak(new WeakEventR.Handler<EqualizerUIDesc>() {
            @Override
            public EqualizerUIDesc invoke() {
                //wake up equalizer
                BaseEqualizerEffect.EqualizerDesc playbackDesc = EventsPlaybackService.Receive.onRequestEqualizerDesc.invoke(null);
                if(playbackDesc == null) return null;
                return getEqualizerUIDesc(playbackDesc);
            }
        }, listenerRefHolder);

        AppPreferences.onBoolPreferenceChanged.subscribeWeak(new WeakEvent2.Handler<Integer, Boolean>() {
            @Override
            public void invoke(Integer preference, Boolean value) {
                if(preference == AppPreferences.PREF_Bool_equalizerEnabled) {
                    MediaControlsUI mediaControlsUI = MediaControlsUI.getInstance();
                    if (mediaControlsUI != null)
                        mediaControlsUI.onEqStateChanged(value);
                }
            }
        }, listenerRefHolder);


        EqualizerDialog.onSubmitEqualizerSettings.subscribeWeak(new WeakEvent2.Handler<EqualizerUISettings, EqualizerUIDesc>() {
            @Override
            public void invoke(EqualizerUISettings equalizerSettings, EqualizerUIDesc desc) {
                
                AppPreferences.createOrGetInstance().setBool(AppPreferences.PREF_Bool_equalizerEnabled, equalizerSettings.enabled);
                AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_equalizerPreset, equalizerSettings.presetIndex);
                AppPreferences.createOrGetInstance().setString(AppPreferences.PREF_String_equalizerBarsValues, EQPreset.serialize(equalizerSettings.currentBands));
                AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_equalizerBassValue, (int) (equalizerSettings.bassValue * 1000.0f));
                AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_equalizerTrebleValue, (int)(equalizerSettings.trebleValue * 1000.0f));
                AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_virtualizerStrength, (int)(equalizerSettings.virtualizerStrength * 1000.0f));

                {
                    BaseEqualizerEffect.EqualizerSettings settings = new BaseEqualizerEffect.EqualizerSettings();

                    float[] eqBandsNormalOut = new float[equalizerSettings.bandsFinal.points.length];
                    float[] eqBandsFreq = new float[equalizerSettings.bandsFinal.points.length];

                    for (int i = 0; i < equalizerSettings.bandsFinal.points.length; i++)
                        eqBandsFreq[i] = equalizerSettings.bandsFinal.points[i].freq;

                    Equalization.getEqBandsBassTrebleControl(equalizerSettings.bandsFinal,
                            bassBoost, trebleBoost,
                            equalizerSettings.bassValue, equalizerSettings.trebleValue,
                            eqBandsNormalOut,
                            eqBandsFreq);

                    settings.enabled = equalizerSettings.enabled;
                    settings.usePreset = false;
                    settings.preset = -1;
                    settings.bandLevels = eqBandsNormalOut;
                    settings.virtualizerStrength = equalizerSettings.virtualizerStrength;

                    EventsPlaybackService.Receive.setEqualizerSettings.invoke(settings);
                }
            }
        }, listenerRefHolder);

        MediaPlaybackService.onRequestEqualizerSettings.subscribeWeak(new WeakEventR1.Handler<String, BaseEqualizerEffect.EqualizerSettings>() {
            @Override
            public BaseEqualizerEffect.EqualizerSettings invoke(String name) {

                BaseEqualizerEffect.EqualizerSettings settings = new BaseEqualizerEffect.EqualizerSettings();

                //int presetIndex = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_equalizerPreset);
                EQPreset preset = EQPreset.deserialize(AppPreferences.createOrGetInstance().getString(AppPreferences.PREF_String_equalizerBarsValues));
                float bassValue = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_equalizerBassValue) * 0.001f;
                float trebleValue = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_equalizerTrebleValue) * 0.001f;

                float[] eqBandsNormalOut = new float[preset.points.length];
                float[] eqBandsFreq = new float[preset.points.length];

                for (int i = 0; i < preset.points.length; i++)
                    eqBandsFreq[i] = preset.points[i].freq;

                Equalization.getEqBandsBassTrebleControl(preset, bassBoost, trebleBoost, bassValue, trebleValue, eqBandsNormalOut, eqBandsFreq);

                settings.enabled = AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_equalizerEnabled);
                settings.usePreset = false;
                settings.preset = -1;
                settings.bandLevels = eqBandsNormalOut;

                settings.virtualizerStrength = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_virtualizerStrength) * 0.001f;

                return settings;
            }
        }, listenerRefHolder);

        MediaPlaybackService.onRequestEqualizerIsEnabled.subscribeWeak(new WeakEventR1.Handler<String, Boolean>() {
            @Override
            public Boolean invoke(String name) {
                return AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_equalizerEnabled);
            }
        }, listenerRefHolder);

        MediaPlaybackService.onEqualizerDescChanged.subscribeWeak(new WeakEvent1.Handler<BaseEqualizerEffect.EqualizerDesc>() {
            @Override
            public void invoke(final BaseEqualizerEffect.EqualizerDesc equalizerDesc) {
                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        EqualizerDialog.onReceiveEqualizerDescChanged.invoke(getEqualizerUIDesc(equalizerDesc));
                    }
                });
            }
        }, listenerRefHolder);

    }


    private EqualizerUIDesc getEqualizerUIDesc(BaseEqualizerEffect.EqualizerDesc playbackDesc)
    {
        if(playbackDesc == null) playbackDesc = BaseEqualizerEffect.EqualizerDesc.empty;

        EqualizerUIDesc desc = new EqualizerUIDesc();
        desc.name = playbackDesc.name;

        EQPreset currentBandsLoaded = EQPreset.deserialize(AppPreferences.createOrGetInstance().getString(AppPreferences.PREF_String_equalizerBarsValues));

        if(currentBandsLoaded.points.length != playbackDesc.currentBandLevels.length) {
            currentBandsLoaded = new EQPreset("", playbackDesc.currentBandLevels.length);

            for (int i = 0; i < playbackDesc.currentBandLevels.length; i++)
                currentBandsLoaded.points[i] = new EQPreset.Point(0.0f, 0.0f);
        }

        for (int i = 0; i < playbackDesc.currentBandLevels.length; i++)
            currentBandsLoaded.points[i].freq = playbackDesc.bandsFreq[i] * 0.001f;

        desc.currentBands = currentBandsLoaded;
        desc.enabled = AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_equalizerEnabled);

        desc.currentPreset = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_equalizerPreset);
        desc.bassBoostValue  = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_equalizerBassValue) * 0.001f;
        desc.trebleBoostValue = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_equalizerTrebleValue) * 0.001f;

        desc.bassBoost = bassBoost;
        desc.trebleBoost = trebleBoost;
        desc.presets = eqPresets;

        desc.virtualizerStrength = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_virtualizerStrength) * 0.001f;

        return desc;
    }

    private void initEqPresets() {

        float normalMaxValue = 10.0f;
        int counter = 0;
        eqPresets = new EQPreset[15];

        {
            EQPreset newCurve = new EQPreset("Default", 2);
            newCurve.points[0] = (new EQPreset.Point(20.0f, 0.0f));
            newCurve.points[1] = (new EQPreset.Point(20000.0f, 0.0f));

            eqPresets[counter++] = newCurve;
        }


        {
            EQPreset newCurve = new EQPreset("Classical", 18);
            newCurve.points[0] = new EQPreset.Point(55.0f, 3.0f);
            newCurve.points[1] = new EQPreset.Point(77.0f, 2.0f);
            newCurve.points[2] = new EQPreset.Point(110.0f, 1.0f);
            newCurve.points[3] = new EQPreset.Point(156.0f, 0.0f);
            newCurve.points[4] = new EQPreset.Point(220.0f, 2.0f);
            newCurve.points[5] = new EQPreset.Point(311.0f, 1.0f);
            newCurve.points[6] = new EQPreset.Point(440.0f, 2.0f);
            newCurve.points[7] = new EQPreset.Point(622.0f, 1.0f);
            newCurve.points[8] = new EQPreset.Point(880.0f, 2.0f);
            newCurve.points[9] = new EQPreset.Point(1200.0f, 3.0f);
            newCurve.points[10] = new EQPreset.Point(1800.0f, 1.0f);
            newCurve.points[11] = new EQPreset.Point(2500.0f, 1.0f);
            newCurve.points[12] = new EQPreset.Point(3500.0f, 1.0f);
            newCurve.points[13] = new EQPreset.Point(5000.0f, 2.0f);
            newCurve.points[14] = new EQPreset.Point(7000.0f, 4.0f);
            newCurve.points[15] = new EQPreset.Point(10000.0f, 3.0f);
            newCurve.points[16] = new EQPreset.Point(14000.0f, 2.0f);
            newCurve.points[17] = new EQPreset.Point(20000.0f, 1.0f);
            newCurve.normalizeValues(normalMaxValue);

            eqPresets[counter++] = newCurve;
        }

        {
            EQPreset newCurve = new EQPreset("Club", 18);
            newCurve.points[0] = new EQPreset.Point(55.0f, 0.0f);
            newCurve.points[1] = new EQPreset.Point(77.0f, 0.0f);
            newCurve.points[2] = new EQPreset.Point(110.0f, 0.0f);
            newCurve.points[3] = new EQPreset.Point(156.0f, 2.0f);
            newCurve.points[4] = new EQPreset.Point(220.0f, 2.0f);
            newCurve.points[5] = new EQPreset.Point(311.0f, 5.0f);
            newCurve.points[6] = new EQPreset.Point(440.0f, 5.0f);
            newCurve.points[7] = new EQPreset.Point(622.0f, 8.0f);
            newCurve.points[8] = new EQPreset.Point(880.0f, 8.0f);
            newCurve.points[9] = new EQPreset.Point(1200.0f, 8.0f);
            newCurve.points[10] = new EQPreset.Point(1800.0f, 8.0f);
            newCurve.points[11] = new EQPreset.Point(2500.0f, 8.0f);
            newCurve.points[12] = new EQPreset.Point(3500.0f, 8.0f);
            newCurve.points[13] = new EQPreset.Point(5000.0f, 5.0f);
            newCurve.points[14] = new EQPreset.Point(7000.0f, 5.0f);
            newCurve.points[15] = new EQPreset.Point(10000.0f, 2.0f);
            newCurve.points[16] = new EQPreset.Point(14000.0f, 0.0f);
            newCurve.points[17] = new EQPreset.Point(20000.0f, 0.0f);
            newCurve.normalizeValues(normalMaxValue);

            eqPresets[counter++] = newCurve;
        }

        {
            EQPreset newCurve = new EQPreset("Dance", 18);
            newCurve.points[0] = new EQPreset.Point(55.0f, 11.0f);
            newCurve.points[1] = new EQPreset.Point(77.0f, 11.0f);
            newCurve.points[2] = new EQPreset.Point(110.0f, 8.0f);
            newCurve.points[3] = new EQPreset.Point(156.0f, 8.0f);
            newCurve.points[4] = new EQPreset.Point(220.0f, 8.0f);
            newCurve.points[5] = new EQPreset.Point(311.0f, 5.0f);
            newCurve.points[6] = new EQPreset.Point(440.0f, 5.0f);
            newCurve.points[7] = new EQPreset.Point(622.0f, 0.0f);
            newCurve.points[8] = new EQPreset.Point(880.0f, 0.0f);
            newCurve.points[9] = new EQPreset.Point(1200.0f, 0.0f);
            newCurve.points[10] = new EQPreset.Point(1800.0f, 0.0f);
            newCurve.points[11] = new EQPreset.Point(2500.0f, -5.0f);
            newCurve.points[12] = new EQPreset.Point(3500.0f, -5.0f);
            newCurve.points[13] = new EQPreset.Point(5000.0f, -5.0f);
            newCurve.points[14] = new EQPreset.Point(7000.0f, -8.0f);
            newCurve.points[15] = new EQPreset.Point(10000.0f, -8.0f);
            newCurve.points[16] = new EQPreset.Point(14000.0f, 0.0f);
            newCurve.points[17] = new EQPreset.Point(20000.0f, 0.0f);
            newCurve.normalizeValues(normalMaxValue*1.3f);

            eqPresets[counter++] = newCurve;
        }

        {
            EQPreset newCurve = new EQPreset("Disco", 18);
            newCurve.points[0] = new EQPreset.Point(55.0f, 3.0f);
            newCurve.points[1] = new EQPreset.Point(77.0f, 3.0f);
            newCurve.points[2] = new EQPreset.Point(110.0f, 1.0f);
            newCurve.points[3] = new EQPreset.Point(156.0f, 1.0f);
            newCurve.points[4] = new EQPreset.Point(220.0f, 3.0f);
            newCurve.points[5] = new EQPreset.Point(311.0f, 1.0f);
            newCurve.points[6] = new EQPreset.Point(440.0f, 1.0f);
            newCurve.points[7] = new EQPreset.Point(622.0f, 1.0f);
            newCurve.points[8] = new EQPreset.Point(880.0f, 2.0f);
            newCurve.points[9] = new EQPreset.Point(1200.0f, 6.0f);
            newCurve.points[10] = new EQPreset.Point(1800.0f, 5.0f);
            newCurve.points[11] = new EQPreset.Point(2500.0f, 4.0f);
            newCurve.points[12] = new EQPreset.Point(3500.0f, 3.0f);
            newCurve.points[13] = new EQPreset.Point(5000.0f, 2.0f);
            newCurve.points[14] = new EQPreset.Point(7000.0f, 2.0f);
            newCurve.points[15] = new EQPreset.Point(10000.0f, 2.0f);
            newCurve.points[16] = new EQPreset.Point(14000.0f, 2.0f);
            newCurve.points[17] = new EQPreset.Point(20000.0f, 1.0f);
            newCurve.normalizeValues(normalMaxValue);

            eqPresets[counter++] = newCurve;
        }

        {
            EQPreset newCurve = new EQPreset("Drum & Bass", 18);
            newCurve.points[0] = new EQPreset.Point(55.0f, 3.0f);
            newCurve.points[1] = new EQPreset.Point(77.0f, 4.0f);
            newCurve.points[2] = new EQPreset.Point(110.0f, 3.0f);
            newCurve.points[3] = new EQPreset.Point(156.0f, 2.0f);
            newCurve.points[4] = new EQPreset.Point(220.0f, 2.0f);
            newCurve.points[5] = new EQPreset.Point(311.0f, 1.0f);
            newCurve.points[6] = new EQPreset.Point(440.0f, 0.0f);
            newCurve.points[7] = new EQPreset.Point(622.0f, 0.0f);
            newCurve.points[8] = new EQPreset.Point(880.0f, 1.0f);
            newCurve.points[9] = new EQPreset.Point(1200.0f, 3.0f);
            newCurve.points[10] = new EQPreset.Point(1800.0f, 5.0f);
            newCurve.points[11] = new EQPreset.Point(2500.0f, 3.0f);
            newCurve.points[12] = new EQPreset.Point(3500.0f, 2.0f);
            newCurve.points[13] = new EQPreset.Point(5000.0f, 1.0f);
            newCurve.points[14] = new EQPreset.Point(7000.0f, 2.0f);
            newCurve.points[15] = new EQPreset.Point(10000.0f, 2.0f);
            newCurve.points[16] = new EQPreset.Point(14000.0f, 1.0f);
            newCurve.points[17] = new EQPreset.Point(20000.0f, 2.0f);
            newCurve.normalizeValues(normalMaxValue);

            eqPresets[counter++] = newCurve;
        }

        {
            EQPreset newCurve = new EQPreset("Heavy Metal", 18);
            newCurve.points[0] = new EQPreset.Point(55.0f, 4.0f);
            newCurve.points[1] = new EQPreset.Point(77.0f, 3.0f);
            newCurve.points[2] = new EQPreset.Point(110.0f, 2.0f);
            newCurve.points[3] = new EQPreset.Point(156.0f, 3.0f);
            newCurve.points[4] = new EQPreset.Point(220.0f, 6.0f);
            newCurve.points[5] = new EQPreset.Point(311.0f, 6.0f);
            newCurve.points[6] = new EQPreset.Point(440.0f, 6.0f);
            newCurve.points[7] = new EQPreset.Point(622.0f, 6.0f);
            newCurve.points[8] = new EQPreset.Point(880.0f, 6.0f);
            newCurve.points[9] = new EQPreset.Point(1200.0f, 5.0f);
            newCurve.points[10] = new EQPreset.Point(1800.0f, 4.0f);
            newCurve.points[11] = new EQPreset.Point(2500.0f, 3.0f);
            newCurve.points[12] = new EQPreset.Point(3500.0f, 3.0f);
            newCurve.points[13] = new EQPreset.Point(5000.0f, 3.0f);
            newCurve.points[14] = new EQPreset.Point(7000.0f, 2.0f);
            newCurve.points[15] = new EQPreset.Point(10000.0f, 2.0f);
            newCurve.points[16] = new EQPreset.Point(14000.0f, 2.0f);
            newCurve.points[17] = new EQPreset.Point(20000.0f, 1.0f);
            newCurve.normalizeValues(normalMaxValue);

            eqPresets[counter++] = newCurve;
        }

        {
            EQPreset newCurve = new EQPreset("Jazz", 18);
            newCurve.points[0] = new EQPreset.Point(55.0f, 0.0f);
            newCurve.points[1] = new EQPreset.Point(77.0f, 1.0f);
            newCurve.points[2] = new EQPreset.Point(110.0f, 2.0f);
            newCurve.points[3] = new EQPreset.Point(156.0f, 2.0f);
            newCurve.points[4] = new EQPreset.Point(220.0f, 3.0f);
            newCurve.points[5] = new EQPreset.Point(311.0f, 1.0f);
            newCurve.points[6] = new EQPreset.Point(440.0f, 2.0f);
            newCurve.points[7] = new EQPreset.Point(622.0f, 0.0f);
            newCurve.points[8] = new EQPreset.Point(880.0f, 0.0f);
            newCurve.points[9] = new EQPreset.Point(1200.0f, 2.0f);
            newCurve.points[10] = new EQPreset.Point(1800.0f, 1.0f);
            newCurve.points[11] = new EQPreset.Point(2500.0f, 2.0f);
            newCurve.points[12] = new EQPreset.Point(3500.0f, 4.0f);
            newCurve.points[13] = new EQPreset.Point(5000.0f, 3.0f);
            newCurve.points[14] = new EQPreset.Point(7000.0f, 3.0f);
            newCurve.points[15] = new EQPreset.Point(10000.0f, 2.0f);
            newCurve.points[16] = new EQPreset.Point(14000.0f, 1.0f);
            newCurve.points[17] = new EQPreset.Point(20000.0f, 0.0f);
            newCurve.normalizeValues(normalMaxValue);

            eqPresets[counter++] = newCurve;
        }

        {
            EQPreset newCurve = new EQPreset("Latin", 18);
            newCurve.points[0] = new EQPreset.Point(55.0f, 0.0f);
            newCurve.points[1] = new EQPreset.Point(77.0f, -2.0f);
            newCurve.points[2] = new EQPreset.Point(110.0f, -1.0f);
            newCurve.points[3] = new EQPreset.Point(156.0f, 0.0f);
            newCurve.points[4] = new EQPreset.Point(220.0f, 1.0f);
            newCurve.points[5] = new EQPreset.Point(311.0f, 1.0f);
            newCurve.points[6] = new EQPreset.Point(440.0f, 2.0f);
            newCurve.points[7] = new EQPreset.Point(622.0f, 2.0f);
            newCurve.points[8] = new EQPreset.Point(880.0f, 3.0f);
            newCurve.points[9] = new EQPreset.Point(1200.0f, 4.0f);
            newCurve.points[10] = new EQPreset.Point(1800.0f, 1.0f);
            newCurve.points[11] = new EQPreset.Point(2500.0f, 2.0f);
            newCurve.points[12] = new EQPreset.Point(3500.0f, 2.0f);
            newCurve.points[13] = new EQPreset.Point(5000.0f, 2.0f);
            newCurve.points[14] = new EQPreset.Point(7000.0f, 3.0f);
            newCurve.points[15] = new EQPreset.Point(10000.0f, 2.0f);
            newCurve.points[16] = new EQPreset.Point(14000.0f, 1.0f);
            newCurve.points[17] = new EQPreset.Point(20000.0f, 1.0f);
            newCurve.normalizeValues(normalMaxValue);

            eqPresets[counter++] = newCurve;
        }

        {
            EQPreset newCurve = new EQPreset("New Age", 18);
            newCurve.points[0] = new EQPreset.Point(55.0f, 3.0f);
            newCurve.points[1] = new EQPreset.Point(77.0f, 1.0f);
            newCurve.points[2] = new EQPreset.Point(110.0f, 3.0f);
            newCurve.points[3] = new EQPreset.Point(156.0f, 2.0f);
            newCurve.points[4] = new EQPreset.Point(220.0f, 2.0f);
            newCurve.points[5] = new EQPreset.Point(311.0f, 2.0f);
            newCurve.points[6] = new EQPreset.Point(440.0f, 3.0f);
            newCurve.points[7] = new EQPreset.Point(622.0f, 2.0f);
            newCurve.points[8] = new EQPreset.Point(880.0f, 0.0f);
            newCurve.points[9] = new EQPreset.Point(1200.0f, 2.0f);
            newCurve.points[10] = new EQPreset.Point(1800.0f, 4.0f);
            newCurve.points[11] = new EQPreset.Point(2500.0f, 1.0f);
            newCurve.points[12] = new EQPreset.Point(3500.0f, 3.0f);
            newCurve.points[13] = new EQPreset.Point(5000.0f, 2.0f);
            newCurve.points[14] = new EQPreset.Point(7000.0f, 4.0f);
            newCurve.points[15] = new EQPreset.Point(10000.0f, 2.0f);
            newCurve.points[16] = new EQPreset.Point(14000.0f, 1.0f);
            newCurve.points[17] = new EQPreset.Point(20000.0f, 1.0f);
            newCurve.normalizeValues(normalMaxValue);

            eqPresets[counter++] = newCurve;
        }

        {
            EQPreset newCurve = new EQPreset("Party", 18);
            newCurve.points[0] = new EQPreset.Point(55.0f, 7.0f);
            newCurve.points[1] = new EQPreset.Point(77.0f, 6.0f);
            newCurve.points[2] = new EQPreset.Point(110.0f, 5.0f);
            newCurve.points[3] = new EQPreset.Point(156.0f, 3.0f);
            newCurve.points[4] = new EQPreset.Point(220.0f, 2.0f);
            newCurve.points[5] = new EQPreset.Point(311.0f, 1.0f);
            newCurve.points[6] = new EQPreset.Point(440.0f, 0.0f);
            newCurve.points[7] = new EQPreset.Point(622.0f, 0.0f);
            newCurve.points[8] = new EQPreset.Point(880.0f, 0.0f);
            newCurve.points[9] = new EQPreset.Point(1200.0f, 0.0f);
            newCurve.points[10] = new EQPreset.Point(1800.0f, 0.0f);
            newCurve.points[11] = new EQPreset.Point(2500.0f, 0.0f);
            newCurve.points[12] = new EQPreset.Point(3500.0f, 0.0f);
            newCurve.points[13] = new EQPreset.Point(5000.0f, 1.0f);
            newCurve.points[14] = new EQPreset.Point(7000.0f, 2.0f);
            newCurve.points[15] = new EQPreset.Point(10000.0f, 4.0f);
            newCurve.points[16] = new EQPreset.Point(14000.0f, 5.0f);
            newCurve.points[17] = new EQPreset.Point(20000.0f, 5.0f);
            newCurve.normalizeValues(normalMaxValue);

            eqPresets[counter++] = newCurve;
        }

        {
            EQPreset newCurve = new EQPreset("Pop", 18);
            newCurve.points[0] = new EQPreset.Point(55.0f, 1.0f);
            newCurve.points[1] = new EQPreset.Point(77.0f, -1.0f);
            newCurve.points[2] = new EQPreset.Point(110.0f, -3.0f);
            newCurve.points[3] = new EQPreset.Point(156.0f, 0.0f);
            newCurve.points[4] = new EQPreset.Point(220.0f, 1.0f);
            newCurve.points[5] = new EQPreset.Point(311.0f, 2.0f);
            newCurve.points[6] = new EQPreset.Point(440.0f, 3.0f);
            newCurve.points[7] = new EQPreset.Point(622.0f, 1.0f);
            newCurve.points[8] = new EQPreset.Point(880.0f, 1.0f);
            newCurve.points[9] = new EQPreset.Point(1200.0f, 2.0f);
            newCurve.points[10] = new EQPreset.Point(1800.0f, 0.0f);
            newCurve.points[11] = new EQPreset.Point(2500.0f, -1.0f);
            newCurve.points[12] = new EQPreset.Point(3500.0f, -2.0f);
            newCurve.points[13] = new EQPreset.Point(5000.0f, 0.0f);
            newCurve.points[14] = new EQPreset.Point(7000.0f, 1.0f);
            newCurve.points[15] = new EQPreset.Point(10000.0f, 2.0f);
            newCurve.points[16] = new EQPreset.Point(14000.0f, 2.0f);
            newCurve.points[17] = new EQPreset.Point(20000.0f, 2.0f);
            newCurve.normalizeValues(normalMaxValue);

            eqPresets[counter++] = newCurve;
        }

        {
            EQPreset newCurve = new EQPreset("Rock", 18);
            newCurve.points[0] = new EQPreset.Point(55.0f, 3.0f);
            newCurve.points[1] = new EQPreset.Point(77.0f, -3.0f);
            newCurve.points[2] = new EQPreset.Point(110.0f, -2.0f);
            newCurve.points[3] = new EQPreset.Point(156.0f, -2.0f);
            newCurve.points[4] = new EQPreset.Point(220.0f, -2.0f);
            newCurve.points[5] = new EQPreset.Point(311.0f, -2.0f);
            newCurve.points[6] = new EQPreset.Point(440.0f, -2.0f);
            newCurve.points[7] = new EQPreset.Point(622.0f, -2.0f);
            newCurve.points[8] = new EQPreset.Point(880.0f, -1.0f);
            newCurve.points[9] = new EQPreset.Point(1200.0f, -1.0f);
            newCurve.points[10] = new EQPreset.Point(1800.0f, -1.0f);
            newCurve.points[11] = new EQPreset.Point(2500.0f, -1.0f);
            newCurve.points[12] = new EQPreset.Point(3500.0f, 0.0f);
            newCurve.points[13] = new EQPreset.Point(5000.0f, 1.0f);
            newCurve.points[14] = new EQPreset.Point(7000.0f, 2.0f);
            newCurve.points[15] = new EQPreset.Point(10000.0f, 3.0f);
            newCurve.points[16] = new EQPreset.Point(14000.0f, 4.0f);
            newCurve.points[17] = new EQPreset.Point(20000.0f, 5.0f);
            newCurve.normalizeValues(normalMaxValue);

            eqPresets[counter++] = newCurve;
        }

        {
            EQPreset newCurve = new EQPreset("Techno", 18);
            newCurve.points[0] = new EQPreset.Point(55.0f, 3.0f);
            newCurve.points[1] = new EQPreset.Point(77.0f, 5.0f);
            newCurve.points[2] = new EQPreset.Point(110.0f, 3.0f);
            newCurve.points[3] = new EQPreset.Point(156.0f, 1.0f);
            newCurve.points[4] = new EQPreset.Point(220.0f, -1.0f);
            newCurve.points[5] = new EQPreset.Point(311.0f, 0.0f);
            newCurve.points[6] = new EQPreset.Point(440.0f, 1.0f);
            newCurve.points[7] = new EQPreset.Point(622.0f, 1.0f);
            newCurve.points[8] = new EQPreset.Point(880.0f, 2.0f);
            newCurve.points[9] = new EQPreset.Point(1200.0f, 5.0f);
            newCurve.points[10] = new EQPreset.Point(1800.0f, 3.0f);
            newCurve.points[11] = new EQPreset.Point(2500.0f, 2.0f);
            newCurve.points[12] = new EQPreset.Point(3500.0f, 5.0f);
            newCurve.points[13] = new EQPreset.Point(5000.0f, 1.0f);
            newCurve.points[14] = new EQPreset.Point(7000.0f, 2.0f);
            newCurve.points[15] = new EQPreset.Point(10000.0f, 3.0f);
            newCurve.points[16] = new EQPreset.Point(14000.0f, 4.0f);
            newCurve.points[17] = new EQPreset.Point(20000.0f, 4.0f);
            newCurve.normalizeValues(normalMaxValue);

            eqPresets[counter++] = newCurve;
        }

        {
            EQPreset newCurve = new EQPreset("Vocal", 18);
            newCurve.points[0] = new EQPreset.Point(55.0f, 2.0f);
            newCurve.points[1] = new EQPreset.Point(77.0f, -1.0f);
            newCurve.points[2] = new EQPreset.Point(110.0f, -1.0f);
            newCurve.points[3] = new EQPreset.Point(156.0f, -1.0f);
            newCurve.points[4] = new EQPreset.Point(220.0f, 2.0f);
            newCurve.points[5] = new EQPreset.Point(311.0f, 2.0f);
            newCurve.points[6] = new EQPreset.Point(440.0f, 4.0f);
            newCurve.points[7] = new EQPreset.Point(622.0f, 3.0f);
            newCurve.points[8] = new EQPreset.Point(880.0f, 4.0f);
            newCurve.points[9] = new EQPreset.Point(1200.0f, 4.0f);
            newCurve.points[10] = new EQPreset.Point(1800.0f, 3.0f);
            newCurve.points[11] = new EQPreset.Point(2500.0f, 2.0f);
            newCurve.points[12] = new EQPreset.Point(3500.0f, 0.0f);
            newCurve.points[13] = new EQPreset.Point(5000.0f, 0.0f);
            newCurve.points[14] = new EQPreset.Point(7000.0f, 0.0f);
            newCurve.points[15] = new EQPreset.Point(10000.0f, 0.0f);
            newCurve.points[16] = new EQPreset.Point(14000.0f, -1.0f);
            newCurve.points[17] = new EQPreset.Point(20000.0f, -1.0f);
            newCurve.normalizeValues(normalMaxValue);

            eqPresets[counter++] = newCurve;
        }
    }
}
