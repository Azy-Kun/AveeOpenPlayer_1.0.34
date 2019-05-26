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

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;

import com.aveeopen.Common.Vec2f;
import com.aveeopen.comp.Visualizer.Graphic.GraphicsUtils;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;

import mdesl.graphics.glutils.FrameBuffer;

public class SolidCircleElement extends Element {

    private String blendMeasure = null;
    private float measureMul = 1.0f;
    private int side = 3;
    private int color1 = Color.argb(0xff, 64, 128, 255);

    public void setSideCount(int side) {
        this.side = side;
    }

    public void setColor(int colorARGB) {
        color1 = colorARGB;
    }

    @Override
    protected void onApplyCustomization(CustomizationData customizationData) {
        super.onApplyCustomization(customizationData);
        setColor(customizationData.getPropertyInt("color", color1));
        setSideCount(customizationData.getPropertyInt("shapeSides", side));
    }

    @Override
    protected void onReadCustomization(CustomizationData outCustomizationData) {
        super.onReadCustomization(outCustomizationData);
        outCustomizationData.setCustomizationName("Solid");
        outCustomizationData.putPropertyInt("color", color1, "crgba");
        outCustomizationData.putPropertyInt("shapeSides", side, "i 3 50");
    }

    @Override
    public void onRender(RenderState renderData, FrameBuffer resultFB) {
        super.onRender(renderData, resultFB);

        RectF drawRect = measureDrawRect(renderData.res.meter);

        PointF blendMeasured = renderData.res.meter.measureVec2f(blendMeasure);
        float blend = blendMeasured.x * measureMul;// * _renderData.frameTime;
        blend *= 2.0f;
        if (blend > 1.0f) blend = 1.0f;

        float blendSmooth = blend;

        int blendInt = (int) (blendSmooth * 255.0f * GraphicsUtils.getAlphaFloatFromIntColor(color1));
        blendInt = Math.min(blendInt, 255);

        int color = Color.argb(blendInt,
                Color.red(color1),
                Color.green(color1),
                Color.blue(color1));

        float x = drawRect.centerX() - drawRect.width() * 0.5f;
        float y = drawRect.centerY() - drawRect.height() * 0.5f;
        float w = drawRect.width();
        float h = drawRect.height();

        renderData.res.getBufferRenderer().drawCircle(
                renderData,
                x, y, 0.0f,
                w, h,
                color,
                new Vec2f(0.0f, 0.0f), new Vec2f(1.0f, 1.0f),
                renderData.res.getAtlasTexWhite(), side);
    }

    public void setColorBlendMeasure(String measure, float mul) {
        blendMeasure = measure;
        measureMul = mul;
    }
}
