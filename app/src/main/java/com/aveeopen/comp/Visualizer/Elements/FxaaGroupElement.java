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

package com.aveeopen.comp.Visualizer.Elements;

import android.opengl.GLES20;

import com.aveeopen.Common.Vec2i;
import com.aveeopen.Common.tlog;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;
import com.aveeopen.comp.Visualizer.Graphic.VFrameBuffer;
import com.aveeopen.comp.Visualizer.Graphic.VTexture;

import mdesl.graphics.glutils.FrameBuffer;

public class FxaaGroupElement extends ElementGroup {

    private VFrameBuffer blurTargetContent;

    @Override
    protected void onCreateGLResources(RenderState renderData) {
        try {
            Vec2i frameBufferSize = renderData.getSafeRenderBufferSizeTextureDim();
            blurTargetContent = VFrameBuffer.createSafe(frameBufferSize.x, frameBufferSize.y, VTexture.LINEAR, VTexture.DEFAULT_WRAP, false);//should be as large as display
            if(blurTargetContent != null) blurTargetContent = blurTargetContent.checkIfValid();
        } catch (Exception e) {
            tlog.w(e.getMessage());
        }

        super.onCreateGLResources(renderData);
    }

    @Override
    public void onRender(RenderState renderData, FrameBuffer resultFB) {
        if (blurTargetContent == null) {
            //ordinary way to render, no blur effect at all
            super.onRender(renderData, resultFB);
            renderChilds(renderData, resultFB);
            return;
        }

        this.onRenderCheckResources(renderData);

        renderData.bindFrameBuffer(blurTargetContent);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        setupFrameBuffer();

        renderChilds(renderData, blurTargetContent);

        super.onRender(renderData, resultFB);

        renderData.bindShader(renderData.res.getFxaaShader());
        renderData.res.getFxaaShader().setUniformf("resolutionW", blurTargetContent.getTexture().getWidth());
        renderData.res.getFxaaShader().setUniformf("resolutionH", blurTargetContent.getTexture().getHeight());
        blurTargetContent.getTexture().bind();
        renderData.res.getFullQuad().drawShader(renderData.res.getFxaaShader(), "Position");
    }

    private void setupFrameBuffer() {
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
    }

}
