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

package com.aveeopen.comp.Visualizer.Elements.Segment;

import android.graphics.PointF;

import com.aveeopen.Common.Vec2f;
import com.aveeopen.comp.Visualizer.Elements.Element;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;

public class SegmentRendererSharpBar implements ISegmentRenderer {

    public static final String typeName = "SharpBars";

    private float barWidth = 0.5f;
    private boolean mirror = false;
    private boolean useFixedBarHeight = false;
    private float fixedBarHeight = 10.0f;

    public SegmentRendererSharpBar setBarWidth(float val)
    {
        barWidth = val;
        return this;
    }

    public SegmentRendererSharpBar setMirror(boolean val)
    {
        mirror = val;
        return this;
    }

    public SegmentRendererSharpBar setFixedBarHeight(boolean use, float val)
    {
        useFixedBarHeight = use;
        fixedBarHeight = val;
        return this;
    }

    @Override
    public void drawSegment(RenderState renderData,
                            int valueIndex,
                            int valuesCount,
                            float lastSegmentHeightVal,
                            float segmentHeightVal,
                            float drawSegmentWidth,
                            PointF lastDrawPoint,
                            PointF lastDrawVec,
                            PointF _drawPoint,
                            PointF drawVec,
                            PointF drawScale,
                            int color1) {

        PointF drawPoint = new PointF(_drawPoint.x, _drawPoint.y);

        float wstep = (float) Math.round(1.0f * drawSegmentWidth / ((float) (valuesCount + 1)));
        float whalf = wstep * barWidth;

        float h0 = (int) (lastSegmentHeightVal * -2.0f * drawScale.y);
        float h1 = (int) (segmentHeightVal * -2.0f * drawScale.y);

        if (mirror) {
            lastDrawPoint.x -= lastDrawVec.x * h0;
            lastDrawPoint.y -= lastDrawVec.y * h0;
            h0 *= 2.0;

            drawPoint.x -= drawVec.x * h1;
            drawPoint.y -= drawVec.y * h1;
            h1 *= 2.0;
        }

        //0---1
        //|   |
        //2---3
        float x2 = (Vec2f.ccw90X(drawVec.x, drawVec.y) * whalf) + drawPoint.x;
        float y2 = (Vec2f.ccw90Y(drawVec.x, drawVec.y) * whalf) + drawPoint.y;
        float x3 = (Vec2f.cw90X(drawVec.x, drawVec.y) * whalf) + drawPoint.x;
        float y3 = (Vec2f.cw90Y(drawVec.x, drawVec.y) * whalf) + drawPoint.y;
        float x0 = (drawVec.x * h0) + x2;
        float y0 = (drawVec.y * h0) + y2;
        float x1 = (drawVec.x * h1) + x3;
        float y1 = (drawVec.y * h1) + y3;

        if(useFixedBarHeight) {
            float hsign = Math.signum(h1);
            x2 = x0 + (drawVec.x * hsign * fixedBarHeight);
            y2 = y0 + (drawVec.y * hsign * fixedBarHeight);
            x3 = x1 + (drawVec.x * hsign * fixedBarHeight);
            y3 = y1 + (drawVec.y * hsign * fixedBarHeight);
        }

        renderData.res.getBufferRenderer().drawRectangle(
                renderData,
                x0, y0,
                x1, y1,
                x2, y2,
                x3, y3,
                0.0f,
                color1,
                Vec2f.zero, Vec2f.one,
                renderData.res.getAtlasTexWhite());
    }

    @Override
    public void onApplyCustomization(Element.CustomizationData customizationData) {
        barWidth = customizationData.getPropertyFloat("barWidth", barWidth);
        mirror = customizationData.getPropertyBool("mirror", mirror);
        useFixedBarHeight = customizationData.getPropertyBool("useFixedBarHeight", useFixedBarHeight);
        fixedBarHeight = customizationData.getPropertyFloat("fixedBarHeight", fixedBarHeight);
    }

    @Override
    public void onReadCustomization(Element.CustomizationData outCustomizationData) {
        outCustomizationData.putPropertyFloat("barWidth", barWidth, "f 0.0 2.0");
        outCustomizationData.putPropertyBool("mirror", mirror, "b");
        outCustomizationData.putPropertyBool("useFixedBarHeight", useFixedBarHeight, "b");
        outCustomizationData.putPropertyFloat("fixedBarHeight", fixedBarHeight, "f -50.0 50.0");
    }
}
