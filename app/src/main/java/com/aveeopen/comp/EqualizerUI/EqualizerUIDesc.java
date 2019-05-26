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

package com.aveeopen.comp.EqualizerUI;

public class EqualizerUIDesc {

    public static final EqualizerUIDesc empty = new EqualizerUIDesc(0);

    public String name;
    public EQPreset currentBands;
    public boolean enabled;
    public int currentPreset;
    public EQPreset[] presets;
    public float bassBoostValue;
    public EQPreset bassBoost;
    public float trebleBoostValue;
    public EQPreset trebleBoost;
    public float virtualizerStrength; //[0.0 .. 1.0]

    private EqualizerUIDesc(int presetsCount)
    {
        name = "";
        currentBands = EQPreset.empty;
        currentPreset = -1;
        presets = new EQPreset[presetsCount];
        bassBoostValue = 0.0f;
        bassBoost = EQPreset.clone(EQPreset.empty);
        trebleBoostValue = 0.0f;
        trebleBoost = EQPreset.clone(EQPreset.empty);
        virtualizerStrength = 0.0f;
    }

    public EqualizerUIDesc() {
    }
}
