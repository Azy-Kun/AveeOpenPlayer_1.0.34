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

package com.aveeopen.Common;

import android.os.SystemClock;

public class Utils {

    public static long tickCount() {
        return SystemClock.uptimeMillis(); //handler.postDelayed use same uptimeMillis (not elapsedRealtimes)
    }

    public static int ensureRange(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static float ensureRange(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    // Round up to next higher power of 2 (return x if it's already a power of 2).
    public static int pow2roundup(int a, int max) {
        if (a < 0)
            return 0;
        --a;
        a |= a >> 1;
        a |= a >> 2;
        a |= a >> 4;
        a |= a >> 8;
        a |= a >> 16;
        return Math.min(a + 1, max);
    }

    public void stringSplitInTwo(String s, int c,  String[] twoPartsOut)
    {
        int index = s.indexOf(c);
        if (index < 0) {
            twoPartsOut[0] = "";
            twoPartsOut[1] = "";
        }
        twoPartsOut[0] = s.substring(0, index);
        twoPartsOut[1] = s.substring(index + 1);
    }

    public static long strToLongSafe(String s) {
        return strToLongSafe(s, 0);
    }

    public static long strToLongSafe(String s, long defaultVal) {
        if (s == null) return 0;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static int strToIntSafe(String s) {
        return strToIntSafe(s, 0);
    }

    public static int strToIntSafe(String s, int defaultVal) {
        if (s == null) return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static float strToFloatSafe(String s) {
        return strToFloatSafe(s, 0.0f);
    }

    public static float strToFloatSafe(String s, float defaultVal) {
        if (s == null) return 0;
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static String fixIncludedNullTerminatorString(String includedNullTerminator) {
        int pos = includedNullTerminator.indexOf(0);
        if (pos < 0) return includedNullTerminator;
        return includedNullTerminator.substring(0, pos);
    }

    public static boolean compareNullStrings(String s1, String s2) {
        return ((s1 == null && s2 == null) || (s1 != null && s1.equals(s2)));
    }

    public static <T> boolean compareNullEqual(T s1, T s2) {
        //java.util.Objects.equals(s1, s2);//api19
        return ((s1 == null && s2 == null) || (s1 != null && s1.equals(s2)));
    }

    public static String getDurationStringHHMMSS(int seconds) {
        return getDurationStringHHMMSS(seconds, true);
    }

    public static String getDurationStringHMSS(int seconds) {
        return getDurationStringHMSS(seconds, true);
    }

    public static String getDurationStringHHMMSS(int seconds, boolean hideHoursIfZero) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if (hideHoursIfZero && hours == 0)
            return twoDigitString(minutes) + ":" + twoDigitString(seconds);
        else
            return twoDigitString(hours) + ":" + twoDigitString(minutes) + ":" + twoDigitString(seconds);
    }

    public static String getDurationStringHMSS(int seconds, boolean hideHoursIfZero) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if (hideHoursIfZero && hours == 0)
            return minutes + ":" + twoDigitString(seconds);
        else
            return hours + ":" + minutes + ":" + twoDigitString(seconds);
    }

    private static String twoDigitString(int number) {

        if (number == 0)
            return "00";

        if (number / 10 == 0)
            return "0" + number;

        return String.valueOf(number);
    }

}
