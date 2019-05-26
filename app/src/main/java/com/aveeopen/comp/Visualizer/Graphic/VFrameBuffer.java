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

import org.lwjgl.LWJGLException;

import mdesl.graphics.glutils.FrameBuffer;

public class VFrameBuffer extends FrameBuffer {

    public static VFrameBuffer createSafe(int width, int height, int filter, int wrap, boolean genMipmap) {
        VTexture texture = new VTexture(width, height, filter, wrap, genMipmap).checkIfValid();
        if (texture == null) return null;

        try {
            return new VFrameBuffer(texture, true);
        } catch (Exception ex) {
            tlog.w("exception " + ex.getMessage());
            return null;
        }
    }

    private VFrameBuffer(VTexture texture, boolean ownsTexture) throws LWJGLException {
        super(texture, ownsTexture);
    }

    public boolean isValid() {
        return id!=0;
    }

    public VFrameBuffer checkIfValid() {
        if (!this.isValid()) {
            this.dispose();
            tlog.w("FrameBuffer is not valid");
            return null;
        }

        return this;
    }

    public void begin() {
        if (!isValid()) return;

        GLES20.glViewport(0, 0, getWidth(), getHeight());
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, id);
    }

    public void end() {
        if (!isValid()) return;

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }
}
