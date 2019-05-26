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

public class Vec3f {
    public float x, y, z;

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vec3f cross(Vec3f v, Vec3f v2) {
        return new Vec3f(
                v.y * v2.z - v.z * v2.y,
                v.z * v2.x - v.x * v2.z,
                v.x * v2.y - v.y * v2.x);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public void normalize() {
        float len = length();
        x /= len;
        y /= len;
        z /= len;
    }

    public Vec3f normalizedResult() {
        float len = length();
        return new Vec3f(x / len, y / len, z / len);
    }

    public Vec3f cross(Vec3f v) {
        return new Vec3f(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x);
    }

    public float dot(Vec3f v1) {
        return (x * v1.x + y * v1.y + z * v1.z);
    }

}

