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

public class EqualizerUISettings {

    public boolean enabled;
    public int presetIndex = -1;
    public EQPreset currentBands;
    public float bassValue; //[-1.0 .. 1.0]
    public float trebleValue; //[-1.0 .. 1.0]
    public EQPreset bandsFinal;
    public float virtualizerStrength; //[0.0 .. 1.0]

    public EqualizerUISettings()
    {
        enabled = false;
        presetIndex = -1;
        currentBands = EQPreset.clone(EQPreset.empty);
        bassValue = 0.0f;
        trebleValue = 0.0f;
        bandsFinal = EQPreset.clone(EQPreset.empty);
        virtualizerStrength = 0.0f;
    }
}
