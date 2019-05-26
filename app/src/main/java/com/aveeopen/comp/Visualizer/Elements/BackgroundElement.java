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

import com.aveeopen.comp.Visualizer.Graphic.GraphicsUtils;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;

import mdesl.graphics.glutils.FrameBuffer;

public class BackgroundElement extends ElementGroup {

    private float bgR, bgG, bgB, bgA = 0.0f;

    public BackgroundElement() {
    }

    public void setBackgroundColor(float red,
                                   float green,
                                   float blue,
                                   float alpha) {
        bgR = red;
        bgG = green;
        bgB = blue;
        bgA = alpha;
    }


    @Override
    protected void onApplyCustomization(CustomizationData customizationData) {
        super.onApplyCustomization(customizationData);

        float[] rgbaF4Color = new float[4];
        GraphicsUtils.intColorToF4Color(rgbaF4Color, customizationData.getPropertyInt("color", GraphicsUtils.f4ColorToIntColor(new float[]{bgR, bgG, bgB, bgA})));

        bgR = rgbaF4Color[0];
        bgG = rgbaF4Color[1];
        bgB = rgbaF4Color[2];
        bgA = rgbaF4Color[3];
    }

    @Override
    protected void onReadCustomization(CustomizationData outCustomizationData) {
        super.onReadCustomization(outCustomizationData);
        outCustomizationData.setCustomizationName("Background Color");
        outCustomizationData.putPropertyInt("color", GraphicsUtils.f4ColorToIntColor(new float[]{bgR, bgG, bgB, bgA}), "crgb");
    }

    @Override
    protected void onCreateGLResources(RenderState renderData) {
        super.onCreateGLResources(renderData);
    }

    @Override
    public void onRender(RenderState renderData, FrameBuffer resultFB) {
        super.onRender(renderData, resultFB);

        GLES20.glClearColor(bgR, bgG, bgB, bgA);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(0, 0, renderData.getScreenWidth(), renderData.getScreenHeight());

        renderChilds(renderData, resultFB);
    }
}
