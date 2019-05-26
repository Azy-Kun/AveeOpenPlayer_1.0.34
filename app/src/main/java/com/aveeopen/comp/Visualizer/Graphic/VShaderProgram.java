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

import android.opengl.GLES20;

import org.lwjgl.LWJGLException;

import mdesl.graphics.glutils.ShaderProgram;

public class VShaderProgram extends ShaderProgram {

    public VShaderProgram(String vertexShaderSource, String fragShaderSource) throws LWJGLException {
        super(vertexShaderSource, fragShaderSource);
    }

    public void setUniformMatrix(String name, boolean transpose, float[] m) {
        setUniformMatrix(getUniformLocation(name), transpose, m);
    }

    public void setUniformMatrix(int loc, boolean transpose, float[] m) {
        GLES20.glUniformMatrix4fv(loc, 1, transpose, m, 0);
    }
}
