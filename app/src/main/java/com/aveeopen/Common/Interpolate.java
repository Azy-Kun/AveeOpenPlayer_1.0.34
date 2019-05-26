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

import android.graphics.Color;

public class Interpolate {

    public static void Lerp(Vec2f result, Vec2f a, Vec2f b, double timeNormal) {
        result.x = a.x + (b.x - a.x) * (float) timeNormal;
        result.y = a.y + (b.y - a.y) * (float) timeNormal;
    }

    public static float Lerp(float a, float b, double timeNormal) {
        return a + (b - a) * (float) timeNormal;
    }

    public static double Lerp(double a, double b, double timeNormal) {
        return a + (b - a) * timeNormal;
    }

    public static int LerpColor(int a, int b, float t) {

        float aL = Math.max(Color.red(a), Math.max(Color.green(a), Color.blue(a)));
        float bL = Math.max(Color.red(b), Math.max(Color.green(b), Color.blue(b)));
        float oL = aL + (bL - aL) * t;

        float cr = Color.red(a) + (Color.red(b) - Color.red(a)) * t;
        float cg = Color.green(a) + (Color.green(b) - Color.green(a)) * t;
        float cb = Color.blue(a) + (Color.blue(b) - Color.blue(a)) * t;
        float ca = Color.alpha(a) + (Color.alpha(b) - Color.alpha(a)) * t;

        //normalize
        float len = (float) Math.sqrt(cr * cr + cg * cg + cb * cb);
        cr /= len;
        cg /= len;
        cb /= len;
        return Color.argb((int) ca, (int) (cr * oL), (int) (cg * oL), (int) (cb * oL));
    }
}
