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

import android.content.Context;
import android.opengl.GLES20;

import com.aveeopen.Common.Vec2f;
import com.aveeopen.Common.Vec2i;
import com.aveeopen.Common.tlog;
import com.aveeopen.PlayerCore;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.Visualizer.Graphic.GraphicsUtils;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;
import com.aveeopen.comp.Visualizer.Graphic.VFrameBuffer;
import com.aveeopen.comp.Visualizer.Graphic.VTexture;

import mdesl.graphics.Texture;
import mdesl.graphics.glutils.FrameBuffer;

public class BlurGroupElement extends ElementGroup {

    private boolean useMipmaps = false;
    private float radius = 1.0f;//0.0 .. 1.0
    private VFrameBuffer blurTargetA, blurTargetB, blurTargetContent;
    private int color2 = 0xffffffff;//for this parameter alpha is ignored(?)
    private Vec2f[] blurLayerScales = new Vec2f[3];//0.0f - no render
    private boolean renderContentFxaa = false;
    private boolean renderContent = false;

    public BlurGroupElement() {
        blurLayerScales[0] = new Vec2f(1.0f, 1.0f);
        for (int i = 1; i < blurLayerScales.length; i++)
            blurLayerScales[i] = new Vec2f(0.0f, 0.0f);
    }

    public void setBlurRadius(float radius) {
        this.radius = radius;
    }

    public void setColor2(int colorARGB) {
        color2 = colorARGB;
    }

    public void setBlurLayerScale(int index, float blurLayerScaleX, float blurLayerScaleY) {
        blurLayerScales[index] = new Vec2f(blurLayerScaleX, blurLayerScaleY);
    }

    public void setBlurLayerScale(int index, Vec2f blurLayerScale) {
        blurLayerScales[index] = blurLayerScale;
    }

    public void setRenderContentOnTop(boolean renderContent) {
        //renderContent = _renderContent;
        renderContentFxaa = renderContent;
    }

    @Override
    protected void onApplyCustomization(CustomizationData customizationData) {
        super.onApplyCustomization(customizationData);
        setColor2(customizationData.getPropertyInt("color", color2));
        setBlurRadius(customizationData.getPropertyFloat("blurRadius", radius));
        setRenderContentOnTop(customizationData.getPropertyBool("showUnblurred", renderContentFxaa));

        blurLayerScales[0] = customizationData.getPropertyVec2f("1layerScale", blurLayerScales[0]);
        blurLayerScales[1] = customizationData.getPropertyVec2f("2layerScale", blurLayerScales[1]);
        blurLayerScales[2] = customizationData.getPropertyVec2f("3layerScale", blurLayerScales[2]);
    }

    @Override
    protected void onReadCustomization(CustomizationData outCustomizationData) {
        super.onReadCustomization(outCustomizationData);
        outCustomizationData.setCustomizationName("Blur");
        outCustomizationData.putPropertyInt("color", color2, "crgb");
        outCustomizationData.putPropertyFloat("blurRadius", radius, "f 0.0 2.0");
        outCustomizationData.putPropertyBool("showUnblurred", renderContentFxaa, "b");

        outCustomizationData.putPropertyVec2f("1layerScale", blurLayerScales[0], "f2 0.0 20.0");
        outCustomizationData.putPropertyVec2f("2layerScale", blurLayerScales[1], "f2 0.0 20.0");
        outCustomizationData.putPropertyVec2f("3layerScale", blurLayerScales[2], "f2 0.0 20.0");
    }

    @Override
    protected void onCreateGLResources(RenderState renderData) {

        Context context = PlayerCore.s().getAppContext();
        useMipmaps = context != null && AppPreferences.createOrGetInstance().preferencesGetBoolSafe(context, "pref_highQualityBlur", false);

        try {
            Vec2i frameBufferSize = renderData.getSafeRenderBufferSizeTextureDim();

            blurTargetContent = VFrameBuffer.createSafe(frameBufferSize.x, frameBufferSize.y, VTexture.LINEAR, VTexture.DEFAULT_WRAP, useMipmaps);//should be as large as display
            if(blurTargetContent != null) blurTargetContent = blurTargetContent.checkIfValid();

            float downScaledSizeX = frameBufferSize.x / 4.1f;
            float downScaledSizeY = frameBufferSize.y / 4.1f;

            blurTargetA = VFrameBuffer.createSafe((int) downScaledSizeX, (int) downScaledSizeY, VTexture.LINEAR, VTexture.DEFAULT_WRAP, false);
            if(blurTargetA != null) blurTargetA = blurTargetA.checkIfValid();

            blurTargetB = VFrameBuffer.createSafe((int) downScaledSizeX, (int) downScaledSizeY, VTexture.LINEAR, VTexture.DEFAULT_WRAP, false);
            if(blurTargetB != null) blurTargetB = blurTargetB.checkIfValid();

        } catch (Exception e) {
            tlog.w(e.getMessage());
        }

        super.onCreateGLResources(renderData);
    }

    @Override
    public void onRender(RenderState renderData, FrameBuffer resultFB) {

        if (blurTargetContent == null || blurTargetA == null || blurTargetB == null) {
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

        {
            renderChilds(renderData, blurTargetContent);
            //render Content to blurTargetA, using horizontal blur
            horizontalBlur(renderData, blurTargetA, blurTargetContent.getTexture());
            //render blurTargetA to blurTargetB, using vertical blur
            verticalBlur2(renderData, blurTargetB, blurTargetA.getTexture());

            super.onRender(renderData, resultFB);//switch to resultFB

            for(int j = blurLayerScales.length-1; j >=0 ; j--)
            {
                Vec2f textureScale = blurLayerScales[j];
                if (textureScale.x != 0.0f && textureScale.y != 0.0f) {
                    float texw = 1.0f / textureScale.x * 0.5f;
                    float texh = 1.0f / textureScale.y * 0.5f;
                    Vec2f tex0 = new Vec2f(0.5f - texw, 0.5f - texh);
                    Vec2f tex1 = new Vec2f(0.5f + texw, 0.5f + texh);

                    renderData.drawFullscreenQuad(0xffffffff, blurTargetB.getTexture(), tex0, tex1);
                }
            }
        }

        if (renderContentFxaa) {
            //v2
            renderData.bindShader(renderData.res.getFxaaShader());
            renderData.res.getFxaaShader().setUniformf("resolutionW", blurTargetContent.getTexture().getWidth());
            renderData.res.getFxaaShader().setUniformf("resolutionH", blurTargetContent.getTexture().getHeight());
            blurTargetContent.getTexture().bind();
            renderData.res.getFullQuad().drawShader(renderData.res.getFxaaShader(), "Position");


        } else if (renderContent) {
            //v1
            renderData.drawFullscreenQuad(0xffffffff, blurTargetContent.getTexture());
        }
    }

    private void setupFrameBuffer0() {
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
    }

    private void setupFrameBuffer() {
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
    }

    private void horizontalBlur(RenderState renderData, FrameBuffer resultFB, Texture content) {
        renderData.bindShader(renderData.res.getBlurShader());

        renderData.res.getBlurShader().setUniformf("resolutionW", resultFB.getWidth());// renderData.FBO_SIZE2);
        renderData.res.getBlurShader().setUniformf("resolutionH", resultFB.getHeight());// renderData.FBO_SIZE2);
        renderData.res.getBlurShader().setUniformf("radius", radius);

        renderData.bindFrameBuffer(resultFB);
        renderData.setBlendMode(3);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        content.bind();

        if (useMipmaps) {
            setupFrameBuffer0();
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        } else {
            setupFrameBuffer();
        }

        renderData.res.getFullQuad().drawShader(renderData.res.getBlurShader(), "Position");
    }

    private void verticalBlur2(RenderState renderData, FrameBuffer resultFB, Texture contentHBlured) {
        //now we can render to the screen using the vertical blur shader
        renderData.bindShader(renderData.res.getBlurShader2());
        renderData.res.getBlurShader2().setUniformf("resolutionW", resultFB.getWidth());// renderData.FBO_SIZE2);
        renderData.res.getBlurShader2().setUniformf("resolutionH", resultFB.getHeight());// renderData.FBO_SIZE2);

        renderData.res.getBlurShader2().setUniformf("radius", radius);
        float[] color2f = new float[4];
        GraphicsUtils.intColorToF4Color(color2f, color2);
        renderData.res.getBlurShader2().setUniformf("Color2", color2f[0], color2f[1], color2f[2], color2f[3]);

        renderData.bindFrameBuffer(resultFB);
        renderData.setBlendMode(3);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        setupFrameBuffer();

        contentHBlured.bind();
        renderData.res.getFullQuad().drawShader(renderData.res.getBlurShader2(), "Position");
    }
}
