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
import com.aveeopen.Common.UtilsSerialize;

public class EQPreset {

    public static final EQPreset empty = new EQPreset("Unnamed", 0);
    public final String name;
    public Point[] points;

    public EQPreset(String name, int pointCount) {
        this.name = name;
        points = new Point[pointCount];
    }

    public void resize(int pointCount) {
        points = new Point[pointCount];
    }

    public static EQPreset clone(EQPreset obj)
    {
        EQPreset result = new EQPreset(obj.name, obj.points.length);
        for(int i=0;i<obj.points.length;i++)
            result.points[i] = new Point(obj.points[i].freq, obj.points[i].value);

        return result;
    }

    public static EQPreset deserialize(String string) {
        String[] strObjs = UtilsSerialize.deserializeIterable(";", string);

        EQPreset result = new EQPreset("Default", strObjs.length);
        for(int i=0;i<strObjs.length;i++) {
            result.points[i] = Point.fromString(strObjs[i]);
        }

        return result;
    }

    public static String serialize(EQPreset preset) {
        return UtilsSerialize.serializeArray(";", preset.points);
    }

    public void normalizeValues(float maxAbs)
    {
        for(Point p : points)
            p.value = p.value / maxAbs;
    }

    public static class Point {
        public float freq;//in hz
        public float value;
        public Point(float freqHz, float val) {
            freq = freqHz;
            value = val;
        }

        @Override
        public String toString() {

            return String.format(java.util.Locale.US, "%.3f:%.3f", freq, value);
        }

        public static Point fromString(String s)
        {
            Point result = new Point(0.0f, 0.0f);
            int index = s.indexOf(":");
            if (index < 0) return result;
            result.freq = Utils.strToFloatSafe( s.substring(0, index) );
            result.value = Utils.strToFloatSafe( s.substring(index + 1) );

            return result;
        }
    }
}
