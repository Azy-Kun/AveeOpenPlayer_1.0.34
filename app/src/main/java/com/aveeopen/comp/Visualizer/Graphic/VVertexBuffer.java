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

package com.aveeopen.comp.Visualizer.Graphic;

import mdesl.graphics.glutils.VertexArray;
import mdesl.graphics.glutils.VertexAttrib;


public class VVertexBuffer extends VertexArray {

    public VVertexBuffer(int vertCount, VertexAttrib... attributes) {
        super(vertCount, attributes);
    }

    public void dispose() {

    }

    public int remaining() {
        return buffer.remaining();
    }
}
