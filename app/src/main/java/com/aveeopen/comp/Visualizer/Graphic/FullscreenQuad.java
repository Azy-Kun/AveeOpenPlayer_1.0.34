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

import com.aveeopen.Common.tlog;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import mdesl.graphics.glutils.ShaderProgram;

public class FullscreenQuad {

    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;
    private static float squareCoords[] = {
            -1.0f, 1.0f, 0.0f,   // top left
            -1.0f, -1.0f, 0.0f,   // bottom left
            1.0f, -1.0f, 0.0f,   // bottom right
            1.0f, 1.0f, 0.0f}; // top right

    private static float squareFlippedCoords[] = {
            0.5f, 0.5f, 0.0f,   // top left
            1.0f, -1.0f, 0.0f,   // bottom left
            -1.0f, -1.0f, 0.0f,   // bottom right
            -1.0f, 1.0f, 0.0f}; // top right

    private static final String vertexShaderCode =
            "const vec2 madd=vec2(0.5,0.5);" +
                    "attribute vec2 vertexIn;" +
                    "varying vec2 textureCoord;" +
                    "void main() {" +
                    "textureCoord = vertexIn.xy*madd+madd;" + // scale vertex attribute to [0-1] range
                    "gl_Position = vec4(vertexIn.xy,0.0,1.0);" +
                    "}";

    private static final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec2 textureCoord;" +
                    "uniform sampler2D s_texture;" +
                    "void main() {" +
                    "vec4 color1 = texture2D(s_texture,textureCoord);" +
                    "gl_FragColor = color1;" +
                    "}";

    private final FloatBuffer vertexBuffer, vertexBufferFlipped;
    private final ShortBuffer drawListBuffer;
    private final int program;
    private final short drawOrder[] = {0, 1, 2, 0, 2, 3}; // order to draw vertices
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private int positionHandle;

    public FullscreenQuad() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        ByteBuffer bb2 = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareFlippedCoords.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        vertexBufferFlipped = bb2.asFloatBuffer();
        vertexBufferFlipped.put(squareFlippedCoords);
        vertexBufferFlipped.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = GraphicsUtils.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GraphicsUtils.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        program = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(program, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(program, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(program);                  // create OpenGL program executables

        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            tlog.w("Could not link program: ");
            tlog.w(GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
        }
    }


    public void drawFlipped() {

        // Add program to OpenGL environment
        GLES20.glUseProgram(program);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(program, "vertexIn");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBufferFlipped);

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    public void draw() {
        // Add program to OpenGL environment
        GLES20.glUseProgram(program);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(program, "vertexIn");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    //bind shader first
    public void drawShader(ShaderProgram blurShader, String position) {
        // get handle to vertex shader's vPosition member
        int positionHandle = blurShader.getAttributeLocation(position);

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

}