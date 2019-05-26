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

public class Vec2i {

    public static Vec2i invalid = new Vec2i(-Integer.MAX_VALUE, -Integer.MAX_VALUE);
    public static Vec2i zero = new Vec2i(0, 0);
    public static Vec2i one = new Vec2i(1, 1);

    public int x, y;

    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
