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

public class DspCurves {

    public static double freqdB(double index, double count, double sampleRate) {
        return (index + 0) * (sampleRate / count);
    }

    public static double freqd(double index, double count, double sampleRate) {
        return (index + 1) * (sampleRate / count);
    }

    public static double freq(int index, int count, int sampleRate) {
        return ((index + 1) * (sampleRate)) / count;
    }

    //plot for frequencies -fs/2 to fs/2
    public static int frequency(int index, int countN, int sampleRate) {
        return index * (sampleRate / countN) - (sampleRate / 2);
    }

    //plot for frequencies -fs/2 to fs/2
    public static double frequencied(double index, double countN, double sampleRate) {
        return index * (sampleRate / countN) - (sampleRate / 2.0);
    }

    public static double myAWeight(double freq) {

        double f2 = freq * freq;
        double f4 = freq * freq * freq * freq;

        final double c12200 = 12200.0 * 12200.0;
        final double c206 = 20.6 * 20.6;
        final double c177 = 107.7 * 107.7;
        final double c7379 = 737.9 * 737.9;

        double Ra = c12200 * f4;
        Ra = Ra / ((f2 + c206) * (f2 + c12200) * Math.sqrt(f2 + c177) * Math.sqrt(f2 + c7379));

        return Ra;
    }

    public static double aweight(double freq) {

        double f2 = freq * freq;
        double f4 = freq * freq * freq * freq;

        double fft = 10 * Math.log(1.562339d * f4 / ((f2 + 107.65265d * 107.65265d)
                * (f2 + 737.86223d * 737.86223d))) / Math.log(10d)
                + 10f * Math.log(2.242881E+16d * f4 / ((f2 + 20.598997d * 20.598997d) * (f2 + 20.598997d * 20.598997d)
                * (f2 + 12194.22d * 12194.22d) * (f2 + 12194.22d * 12194.22d))) / Math.log(10d);

        if (Double.isNaN(fft) ||
                Double.isInfinite(fft)) {
            fft = 0d;
        }

        return fft;
    }

}
