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

import com.aveeopen.Common.Utils;

public class Equalization {

    static double loFreq = 20.0;
    static double hiFreq = 22050.0;

    public static void getEqBandsPresetsConvert(EQPreset presetIn, EQPreset bandsValuesOut) {

        float[] eqBandsNormalOut = new float[bandsValuesOut.points.length];
        float[] eqBandsFreq = new float[bandsValuesOut.points.length];
        for (int i = 0; i < eqBandsFreq.length; i++)
            eqBandsFreq[i] = bandsValuesOut.points[i].freq;

        PointCurve pntCrv = new PointCurve();
        eqCurveToPointCurve(presetIn, pntCrv);
        pointCurveToEqBands(pntCrv, eqBandsNormalOut, eqBandsFreq);

        for (int i = 0; i < eqBandsNormalOut.length; i++)
            bandsValuesOut.points[i].value = eqBandsNormalOut[i];
    }


    public static void getEqBandsBassTrebleControl(EQPreset preset, EQPreset bassPreset, EQPreset treblePreset, float bassValue, float trebleValue, float[] eqBandsNormalOut, float[] eqBandsFreq)//hz
    {
        float[] eqBandsOutCurrent = new float[eqBandsNormalOut.length];
        float[] eqBandsOutBass = new float[eqBandsNormalOut.length];
        float[] eqBandsOutTreble = new float[eqBandsNormalOut.length];

        PointCurve pntCrv = new PointCurve();

        eqCurveToPointCurve(preset, pntCrv);
        pointCurveToEqBands(pntCrv, eqBandsOutCurrent, eqBandsFreq);

        eqCurveToPointCurve(bassPreset, pntCrv);
        pointCurveToEqBands(pntCrv, eqBandsOutBass, eqBandsFreq);

        eqCurveToPointCurve(treblePreset, pntCrv);
        pointCurveToEqBands(pntCrv, eqBandsOutTreble, eqBandsFreq);

        for (int i = 0; i < eqBandsNormalOut.length; i++) {
            eqBandsNormalOut[i] = eqBandsOutCurrent[i] + (eqBandsOutBass[i] * bassValue) + (eqBandsOutTreble[i] * trebleValue);
            eqBandsNormalOut[i] = Utils.ensureRange(eqBandsNormalOut[i], -1.0f, 1.0f);
        }
    }

    static void eqCurveToPointCurve(EQPreset curve, PointCurve pointCurve) {
        pointCurve.clear();

        if (curve.points.length > 0) {
            float loLog = (float) Math.log10(loFreq);
            float hiLog = (float) Math.log10(hiFreq);
            float divider = hiLog - loLog;

            for (int i = 0; i < curve.points.length; i++) {
                float flog = (float) Math.log10(curve.points[i].freq);
                if (flog >= loLog) {
                    float time = (flog - loLog) / divider;
                    if (time <= 1.0f) {
                        pointCurve.insert(time, curve.points[i].value);
                    } else {
                        pointCurve.insert(time, curve.points[i].value);
                        break;
                    }
                } else {
                    pointCurve.insert(0.0f, curve.points[i].value);
                }
            }
        }

    }


    static void pointCurveToEqBands(PointCurve pointCurve, float[] eqBandsNormalOut, float[] eqBandsFreq)//hz
    {
        float loLog = (float) Math.log10(loFreq);
        float hiLog = (float) Math.log10(hiFreq);
        float divider = hiLog - loLog;

        float time;
        for (int i = 0; i < eqBandsNormalOut.length; i++) {
            if (eqBandsFreq[i] == loFreq) {
                time = 0.0f;
            } else {
                float flog = (float) Math.log10(eqBandsFreq[i]);
                time = (flog - loLog) / divider;
            }
            eqBandsNormalOut[i] = pointCurve.getValue(time);
        }
    }

}
