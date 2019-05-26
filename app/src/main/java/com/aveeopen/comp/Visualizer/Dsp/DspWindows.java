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

package com.aveeopen.comp.Visualizer.Dsp;


public class DspWindows {

    public static float hannWindow(int n, int N) {
        return 0.5f * (1.0f - (float) Math.cos((2.0 * Math.PI * n) / (N - 1)));
    }

    public static float hammingWindow(int n, int N) {
        return 0.54f - 0.46f * (float) Math.cos((2 * Math.PI * n) / (N - 1));
    }

    // Modified Bessel function of order 0 for complex inputs.
    static float I0(float x) {
        float y = x / 3.75f;
        y *= y;
        return 1.0f + y * (
                3.5156229f + y * (
                        3.0899424f + y * (
                                1.2067492f + y * (
                                        0.2659732f + y * (
                                                0.360768e-1f + y * 0.45813e-2f)))));
    }

    public static float kaiserWindow(int n, int length, float beta) {
        float r = ((2.0f * n) / length - 1.0f);//-1.0f;
        float k = (float) (Math.PI) * beta * (float) Math.sqrt(1.0f - r * r);

        return I0(k) / I0((float) Math.PI * beta);
    }

}
