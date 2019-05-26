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

package com.aveeopen.comp.playback;

import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;

import com.aveeopen.Common.Utils;
import com.aveeopen.Common.tlog;


public abstract class BaseEqualizerEffect {

    private int lastKnownAudioSession = 0;
    private EqualizerDesc desc;
    private IEqualizerEffectListener equalizerEffectListener;
    private String name;
    private boolean shouldBeEnabled = false;

    private Equalizer equalizer;
    private int audioSessionEqualizer = 0;

    private Virtualizer virtualizer;
    private int audioSessionVirtualizer = 0;

    public BaseEqualizerEffect(IEqualizerEffectListener equalizerEffectListener, String name) {
        this.equalizerEffectListener = equalizerEffectListener;
        this.name = name;
        desc = new EqualizerDesc(name);
        equalizer = null;
        virtualizer = null;
    }

    public void release() {
        releaseEqualizer();
        releaseVirtualizer();
    }
    private void releaseEqualizer() {
        try {
            if (equalizer != null)
                equalizer.release();
        } catch (Exception ignored) {
        }

        equalizer = null;
        audioSessionEqualizer = 0;
    }

    private void releaseVirtualizer() {
        try {
            if(virtualizer != null)
                virtualizer.release();
        } catch (Exception ignored) {
        }

        virtualizer = null;
        audioSessionVirtualizer = 0;
    }

    public void onAudioSessionChanged(int audioSession) {
        lastKnownAudioSession = audioSession;

        if(equalizerEffectListener.isEqualizerEnabled(getEqualizerName())) {
            EqualizerSettings settings = equalizerEffectListener.getEqualizerSettings(getEqualizerName());
            shouldBeEnabled = settings.enabled;

            initializeEqualizer(settings.enabled, audioSession);
            applySettings(settings, desc);

            initializeVirtualizer(settings.enabled && settings.virtualizerStrength > 0.0f, audioSession);
            applyVirtualizerSettings(settings);


        } else {
            initializeEqualizer(false, audioSession);
            initializeVirtualizer(false, audioSession);
        }

        equalizerEffectListener.onEqualizerDescChanged(desc);
    }

    public void onCheckEqualizerLife()
    {
        if(!shouldBeEnabled)
            releaseEqualizer();
    }

    public String getEqualizerName()
    {
        return name;
    }

    public EqualizerDesc getEqualizerDesc() {
        if(initializeEqualizer(true, lastKnownAudioSession));
            equalizerEffectListener.onEqualizerDescChanged(desc);
        return desc;
    }


    private boolean initializeEqualizer(boolean initialize, int audioSession)
    {
        boolean descChanged = false;

        if(audioSession != 0 && initialize) {
            if (equalizer == null || audioSessionEqualizer != audioSession) {
                descChanged = true;
                releaseEqualizer();
                audioSessionEqualizer = audioSession;
                try {
                    equalizer = new Equalizer(0, audioSession);
                } catch (Exception ignored) {
                }
            }
        } else
        {
            releaseEqualizer();
        }

        if (equalizer == null) {
            if (desc == null) {
                descChanged = true;
                desc = new EqualizerDesc(getEqualizerName());
            }
            return descChanged;
        }

        if (desc == null) {
            descChanged = true;
            desc = new EqualizerDesc();
        }

        desc.name = getEqualizerName();
        desc.numBands = equalizer.getNumberOfBands();
        desc.lowerBandLevel = equalizer.getBandLevelRange()[0];
        desc.higherBandLevel = equalizer.getBandLevelRange()[1];
        desc.currentBandLevels = new float[desc.numBands];
        desc.bandsFreq = new int[desc.numBands];

        float bandLevelSpanHalf = (desc.higherBandLevel - desc.lowerBandLevel) / 2;

        for (int i = 0; i < desc.currentBandLevels.length; i++) {
            desc.currentBandLevels[i] = ((equalizer.getBandLevel((short) i)-desc.lowerBandLevel) - bandLevelSpanHalf) / bandLevelSpanHalf;
            desc.bandsFreq[i] = equalizer.getCenterFreq((short) i);
        }

        return descChanged;
    }

    private void applySettings(EqualizerSettings settings, EqualizerDesc desc)
    {
        if(equalizer == null) return;

        if (settings != null) {

            if(equalizer.getEnabled()!=settings.enabled)
                equalizer.setEnabled(settings.enabled);

            if(!settings.enabled) return;

            if (settings.usePreset) {
                short newPreset = (short) settings.preset;
                if (newPreset < equalizer.getNumberOfPresets() && newPreset >= 0)
                    equalizer.usePreset(newPreset);
                else
                    tlog.w("invalid preset: " + newPreset);
            } else {

                int bandLevelSpanHalf = (desc.higherBandLevel - desc.lowerBandLevel)/2;

                if (settings.bandLevels.length == equalizer.getNumberOfBands())
                    for (int i = 0; i < desc.currentBandLevels.length; i++) {
                        int level = (Math.round(settings.bandLevels[i]*bandLevelSpanHalf) + bandLevelSpanHalf) + desc.lowerBandLevel;

                        equalizer.setBandLevel((short) i, (short) level);
                    }
                else
                    tlog.w("invalid band count " + settings.bandLevels.length);
            }
        }
    }

    private void initializeVirtualizer(boolean initialize, int audioSession) {
        if(audioSession != 0 && initialize) {
            if (virtualizer == null ||  audioSessionVirtualizer != audioSession) {
                releaseVirtualizer();
                audioSessionVirtualizer = audioSession;
                try {
                    virtualizer = new Virtualizer(0, audioSession);
                } catch (Exception ignored) {
                }
            }
        } else
        {
            releaseVirtualizer();
        }
    }

    private void applyVirtualizerSettings(EqualizerSettings settings) {

        if(virtualizer == null) return;
        virtualizer.setEnabled(settings.enabled && settings.virtualizerStrength > 0.0f);
        virtualizer.setStrength((short) Utils.ensureRange(settings.virtualizerStrength * 1000.0f, 0.0f, 1000.0f));
    }


//    private void initializeReverb(boolean initialize, int audioSession) {
//
//        if(audioSession != 0 && initialize) {
//            if (mReverb == null ||  mAudioSessionReverb != audioSession) {
//                releaseReverb();
//                try {
//                    mReverb = new PresetReverb(0, audioSession);
//                    mAudioSessionReverb = audioSession;
//                } catch (Exception ignored) {
//                }
//            }
//        } else
//        {
//            releaseVirtualizer();
//        }
//    }

    public void setEqualizerSettings(EqualizerSettings settings)
    {
        shouldBeEnabled = settings.enabled;
        initializeEqualizer(settings.enabled, lastKnownAudioSession);
        applySettings(settings, desc);

        initializeVirtualizer(settings.enabled && settings.virtualizerStrength > 0.0f, lastKnownAudioSession);
        applyVirtualizerSettings(settings);
    }

    public interface IEqualizerEffectListener {
        EqualizerSettings getEqualizerSettings(String name);
        boolean isEqualizerEnabled(String name);
        void onEqualizerDescChanged(EqualizerDesc desc);
    }

    public static class EqualizerSettings {
        public boolean enabled;
        public boolean usePreset;
        public int preset;
        public float[] bandLevels;//[-1.0 .. 1.0]
        public float virtualizerStrength; //[0.0 .. 1.0]
    }

    public static class EqualizerDesc {

        public static final EqualizerDesc empty = new EqualizerDesc("Default");

        public String name;
        public int numBands;
        public int lowerBandLevel;
        public int higherBandLevel;
        public int[] bandsFreq;//milliHertz
        public float[] currentBandLevels;//[-1.0 .. 1.0]

        public EqualizerDesc(String name)
        {
            this.name = name;
            numBands = 0;
            lowerBandLevel = -1000;
            higherBandLevel = 1000;
            bandsFreq = new int[0];
            currentBandLevels = new float[0];
        }

        public EqualizerDesc()
        {
        }
    }

}
