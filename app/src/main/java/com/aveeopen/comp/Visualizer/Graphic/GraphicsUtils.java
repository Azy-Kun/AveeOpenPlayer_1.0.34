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

import android.content.res.Resources;
import android.opengl.GLES20;

import com.aveeopen.Common.tlog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GraphicsUtils {

    public static String readResource(Resources resources, int id) {
        StringBuilder content = new StringBuilder(128);
        BufferedReader br = new BufferedReader(new InputStreamReader(resources.openRawResource(id)));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                content.append(line);
                content.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    public static int loadShader(int type, String shaderCode) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public static void checkGlError(String glOperation) {
        int error;
        if ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            tlog.w(glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public static int f4ColorToIntColor(float[] argb) {
        int argbInt = 0;

        argbInt |= (int) (argb[3] * 255.0f) << 8 * 3;
        argbInt |= (int) (argb[0] * 255.0f) << 8 * 2;
        argbInt |= (int) (argb[1] * 255.0f) << 8;
        argbInt |= (int) (argb[2] * 255.0f);

        return argbInt;
    }

    public static void intColorToF4Color(float[] out, int argb) {
        out[3] = ((argb >> 8 * 3) & 0xFF) / 255.0f;//a
        out[0] = ((argb >> 8 * 2) & 0xFF) / 255.0f;//r
        out[1] = ((argb >> 8) & 0xFF) / 255.0f;//g
        out[2] = ((argb) & 0xFF) / 255.0f; //b
    }

    public static float getAlphaFloatFromIntColor(int argb) {
        return ((argb >> 8 * 3) & 0xFF) / 255.0f;//a
    }

    public static int intColorMultiply(int argb1, int argb2) {
        float[] _resultArgb = new float[4];

        _resultArgb[3] = ((argb1 >> 8 * 3) & 0xFF) / 255.0f;//a
        _resultArgb[0] = ((argb1 >> 8 * 2) & 0xFF) / 255.0f;//r
        _resultArgb[1] = ((argb1 >> 8) & 0xFF) / 255.0f;//g
        _resultArgb[2] = ((argb1) & 0xFF) / 255.0f; //b

        _resultArgb[3] *= ((argb2 >> 8 * 3) & 0xFF) / 255.0f;//a
        _resultArgb[0] *= ((argb2 >> 8 * 2) & 0xFF) / 255.0f;//r
        _resultArgb[1] *= ((argb2 >> 8) & 0xFF) / 255.0f;//g
        _resultArgb[2] *= ((argb2) & 0xFF) / 255.0f; //b

        return f4ColorToIntColor(_resultArgb);
    }
}
