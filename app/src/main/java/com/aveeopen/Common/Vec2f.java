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

public class Vec2f {
    private static final float edF = 0.0001f;//equal diff
    private static final float epsilonF = 1.192092896e-07f;

    public static Vec2f invalid = new Vec2f(-Float.MAX_VALUE, -Float.MAX_VALUE);
    public static Vec2f zero = new Vec2f(0, 0);
    public static Vec2f one = new Vec2f(1.0f, 1.0f);

    public float x, y;

    public static Vec2f FromString(String s, Vec2f defaultVal)
    {
        if (s == null) return defaultVal;
        try {
            int index = s.indexOf(" ");
            if (index < 0) return defaultVal;
            String s1 = s.substring(0, index);
            String s2 = s.substring(index + 1);
            return new Vec2f(Float.parseFloat(s1), Float.parseFloat(s2));
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US, "%f %f", x, y);
    }

    public static float cw90X(float x, float y) {
        return y;
    }

    public static float cw90Y(float x, float y) {
        return -x;
    }

    public static float ccw90X(float x, float y) {
        return -y;
    }

    public static float ccw90Y(float x, float y) {
        return x;
    }

    public static Vec2f fromAngle(float angle) {
        return new Vec2f((float) Math.cos(angle), (float) Math.sin(angle));
    }

    public static Vec2f rotate(Vec2f v, float angle) {
        return new Vec2f((float) Math.cos(angle) * v.x - (float) Math.sin(angle) * v.y,
                (float) Math.sin(angle) * v.x + (float) Math.cos(angle) * v.y);
    }

    static public float cross(Vec2f v1, Vec2f v2) {
        return v1.x * v2.y - v1.y * v2.x;
    }

    static public float dot(Vec2f a, Vec2f b) {
        return a.x * b.x + a.y * b.y;
    }

    public static void interpolate(float t, Vec2f a, Vec2f b, Vec2f out) {
        out.x = a.x + t * (b.x - a.x);
        out.y = a.y + t * (b.y - a.y);
    }

    public boolean equals(Vec2f other) {
        return (x == other.x && y == other.y);
    }

    public void abs() {
        x = Math.abs(x);
        y = Math.abs(y);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float distance(Vec2f b) {
        Vec2f delta = new Vec2f(this.x - b.x, this.y - b.y);
        return (float) Math.sqrt((delta.x * delta.x) + (delta.y * delta.y));
    }

    public Vec2f normalize() {
        float len = length();
        x /= len;
        y /= len;
        return this;
    }

    public Vec2f normalizedResult() {
        float len = length();
        float xn = x / len;
        float yn = y / len;
        return new Vec2f(xn, yn);
    }

    public float getAngle() {
        float len = length();
        if (len == 0.0f) len = epsilonF;
        float xx = x / len;
        float yy = y / len;

        float angle = (float) -Math.atan2(yy == 0 ? epsilonF : -yy, xx == 0 ? epsilonF : xx);

        if (angle < 0) angle += (float) Math.PI * 2.0f;

        return angle;
    }

    public boolean compareValues(Vec2f b) {
        return Math.abs(x - b.x) < edF && Math.abs(y - b.y) < edF;
    }

}