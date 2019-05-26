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


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.aveeopen.Common.tlog;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import mdesl.graphics.Texture;

public class VTexture extends Texture {

    public static final int DEFAULT_FILTER = LINEAR;
    public static final int DEFAULT_WRAP = REPEAT;

    public VTexture(int width, int height, int filter, int wrap, boolean genMipmap) {
        int[] textures_container = new int[1];
        GLES20.glGenTextures(1, textures_container, 0);
        id = textures_container[0];

        this.width = width;
        this.height = height;

        bind();

        setFilter(filter);
        setWrap(wrap);

        ByteBuffer buf;
        try {
            buf = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
        } catch (Exception ex) {
            dispose();
            return;
        }
        upload(GLES20.GL_RGBA, buf);

        if (genMipmap) {
            GLES20.glGenerateMipmap(getTarget());
        }
    }

    public VTexture(String path, int minFilter, int magFilter, int wrap,
                    boolean genMipmap) {
        File imgFile = new File(path);
        Bitmap bitmap = null;
        if (imgFile.exists()) {
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            width = bitmap.getWidth();
            height = bitmap.getHeight();

            if (width < 1 || height < 1)
                tlog.w("texture invalid dimensions");

        } else {
            //TODO: what call really causes this?
            //don't pass zero values as width or height, it will mess up texture warping, at least on android
            width = 1;
            height = 1;
        }

        GLES20.glEnable(getTarget());
        int[] textures_container = new int[1];
        GLES20.glGenTextures(1, textures_container, 0);
        id = textures_container[0];

        bind();

        setFilter(minFilter, magFilter);
        setWrap(wrap);
        if (bitmap != null)
            upload(GLES20.GL_RGBA, bitmap);

        if (genMipmap) {
            GLES20.glGenerateMipmap(getTarget());
        }
    }

    public VTexture(int colorARGB, int width, int height, int minFilter, int magFilter, int wrap,
                    boolean genMipmap) {
        this(Bitmap.createBitmap(createSolidArray(colorARGB, width * height), 0, width, width, height, Bitmap.Config.ARGB_8888),
                minFilter,
                magFilter,
                wrap,
                genMipmap);

    }

    public VTexture(Bitmap bitmap, int minFilter, int magFilter, int wrap,
                    boolean genMipmap) {
        this(bitmap, minFilter, magFilter, wrap,
                genMipmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    public VTexture(Bitmap bitmap, int minFilter, int magFilter, int wrap,
                    boolean genMipmap, int xoffset, int yoffset, int w, int h) {
        width = w;
        height = h;

        GLES20.glEnable(getTarget());
        int[] textures_container = new int[1];
        GLES20.glGenTextures(1, textures_container, 0);
        id = textures_container[0];

        bind();

        setFilter(minFilter, magFilter);
        setWrap(wrap);
        if (bitmap != null) {
            upload(GLES20.GL_RGBA, bitmap);
        }

        if (genMipmap) {
            GLES20.glGenerateMipmap(getTarget());
        }
    }

    private static int[] createSolidArray(int val, int size) {
        int[] ret = new int[size];
        Arrays.fill(ret, val);
        return ret;
    }

    protected void upload(int dataFormat, Bitmap bitmap) {
        bind();
        setUnpackAlignment();
        GLUtils.texImage2D(getTarget(), 0, bitmap, 0);
    }

    public VTexture checkIfValid() {
        if (!this.valid()) {
            this.dispose();
            tlog.w("Texture is not valid");
            return null;
        }
        return this;
    }

}
