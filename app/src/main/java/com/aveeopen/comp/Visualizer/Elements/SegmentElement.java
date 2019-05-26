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

import android.graphics.PointF;
import android.graphics.RectF;

import com.aveeopen.comp.Visualizer.Elements.Segment.ISegmentDataProvider;
import com.aveeopen.comp.Visualizer.Elements.Segment.ISegmentPath;
import com.aveeopen.comp.Visualizer.Elements.Segment.ISegmentRenderer;
import com.aveeopen.comp.Visualizer.Elements.Segment.SegmentPathFactory;
import com.aveeopen.comp.Visualizer.Elements.Segment.SegmentRendererFactory;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;

import mdesl.graphics.glutils.FrameBuffer;

public class SegmentElement extends Element {

    //private ISegmentDataProvider segmentDataProvider = null;
    private ISegmentRenderer segmentRenderer = null;
    private ISegmentRenderer segmentRenderer2 = null;
    private ISegmentPath segmentPath = null;
    private int color1 = 0xffffffff;
    private float barHeightScale = 1.0f;
    private float minBarHeightScale = 0.0f;

    public void setSegmentRenderer(ISegmentRenderer segmentRenderer) {
        this.segmentRenderer = segmentRenderer;
    }

    public void setSegmentRenderer2(ISegmentRenderer segmentRenderer) {
        segmentRenderer2 = segmentRenderer;
    }

    public void setSegmentPath(ISegmentPath segmentPath) {
        this.segmentPath = segmentPath;
    }

    public void setColor(int colorARGB) {
        color1 = colorARGB;
    }

    public void setBarHeightScale(float barHeightScale) {
        this.barHeightScale = barHeightScale;
    }

    public void setMinBarHeightScale(float barHeight) {
        minBarHeightScale = barHeight;
    }

    @Override
    protected void onApplyCustomization(CustomizationData customizationData) {
        super.onApplyCustomization(customizationData);
        setColor(customizationData.getPropertyInt("color", color1));
        setBarHeightScale(customizationData.getPropertyFloat("heightScale", barHeightScale));
        setMinBarHeightScale(customizationData.getPropertyFloat("minHeightScale", minBarHeightScale));

        {
            CustomizationData segmentRendererCutom1 = customizationData.getChild("ShapePath");
            segmentPath = SegmentPathFactory.create(segmentRendererCutom1.getChildTypeValue(), segmentPath);
            if (segmentPath != null)
                segmentPath.onApplyCustomization(segmentRendererCutom1);
        }

        CustomizationData segmentRendererCutom1 = customizationData.getChild("Segment1");
        segmentRenderer = SegmentRendererFactory.create(segmentRendererCutom1.getChildTypeValue(), segmentRenderer);
        if (segmentRenderer != null) segmentRenderer.onApplyCustomization(segmentRendererCutom1);

        CustomizationData segmentRendererCutom2 = customizationData.getChild("Segment2");
        segmentRenderer2 = SegmentRendererFactory.create(segmentRendererCutom2.getChildTypeValue(), segmentRenderer2);
        if(segmentRenderer2 != null) segmentRenderer2.onApplyCustomization(segmentRendererCutom2);
    }

    @Override
    protected void onReadCustomization(CustomizationData outCustomizationData) {
        super.onReadCustomization(outCustomizationData);
        outCustomizationData.setCustomizationName("Bars/Line");
        outCustomizationData.putPropertyInt("color", color1, "crgba");
        outCustomizationData.putPropertyFloat("heightScale", barHeightScale, "f -10.0 10.0");
        outCustomizationData.putPropertyFloat("minHeightScale", minBarHeightScale, "f -0.05 0.05");

        {
            CustomizationData segmentRendererCutom1 = outCustomizationData.putChild("ShapePath", SegmentPathFactory.getTypeName(segmentPath), SegmentPathFactory.typeNames);
            if (segmentPath != null) segmentPath.onReadCustomization(segmentRendererCutom1);
        }

        CustomizationData segmentRendererCutom1 = outCustomizationData.putChild("Segment1", SegmentRendererFactory.getTypeName(segmentRenderer), SegmentRendererFactory.typeNames);
        if(segmentRenderer != null) segmentRenderer.onReadCustomization(segmentRendererCutom1);
        CustomizationData segmentRendererCutom2 = outCustomizationData.putChild("Segment2", SegmentRendererFactory.getTypeName(segmentRenderer2), SegmentRendererFactory.typeNames);
        if(segmentRenderer2 != null) segmentRenderer2.onReadCustomization(segmentRendererCutom2);
    }

    @Override
    protected void onCreateGLResources(RenderState renderData) {
        super.onCreateGLResources(renderData);
    }

    @Override
    public void onRender(RenderState renderData, FrameBuffer resultFB) {
        super.onRender(renderData, resultFB);

        ISegmentDataProvider segmentDataProvider = renderData.res.meter.getAudioDataProvider();

        if (segmentDataProvider == null ||
                (segmentRenderer == null && segmentRenderer2 == null) ||
                segmentPath == null) return;

        RectF drawRect = measureDrawRect(renderData.res.meter);
        PointF drawScale = measureDrawScaleRect(renderData.res.meter);

        segmentDataProvider.process(renderData.res.visualizationData);
        segmentPath.process(renderData);

        float[] barVals2 = segmentDataProvider.getFrameValues();//
        int valuesCount = barVals2.length;

        PointF pathPointOut = new PointF();//[0 ; 1]
        PointF pathPointVecOut = new PointF();

        float minBarHeightScaled = renderData.res.meter.measureScreenScaleX(minBarHeightScale, true);
        float pathLength = segmentPath.getPathLength(drawRect, valuesCount);
        float lastBarVal = valuesCount > 0 ? barVals2[valuesCount - 1] : 0.0f;
        PointF lastpathPointVecOut = new PointF();
        PointF lastdrawPoint = new PointF();
        segmentPath.getPointOnPath(valuesCount - 1, valuesCount, drawRect, lastdrawPoint, lastpathPointVecOut);

        for (int i = 0; i < valuesCount; i++) {

            segmentPath.getPointOnPath(i, valuesCount, drawRect, pathPointOut, pathPointVecOut);

            if (segmentRenderer != null)
                segmentRenderer.drawSegment(renderData,
                        i,
                        valuesCount,
                        (lastBarVal * barHeightScale) + minBarHeightScaled,
                        (barVals2[i] * barHeightScale) + minBarHeightScaled,
                        pathLength,
                        lastdrawPoint,
                        lastpathPointVecOut,
                        pathPointOut,
                        pathPointVecOut,
                        drawScale,
                        color1);

            if (segmentRenderer2 != null)
                segmentRenderer2.drawSegment(renderData,
                        i,
                        valuesCount,
                        (lastBarVal * barHeightScale) + minBarHeightScaled,
                        (barVals2[i] * barHeightScale) + minBarHeightScaled,
                        pathLength,
                        lastdrawPoint,
                        lastpathPointVecOut,
                        pathPointOut,
                        pathPointVecOut,
                        drawScale,
                        color1);

            lastBarVal = barVals2[i];
            lastdrawPoint.x = pathPointOut.x;
            lastdrawPoint.y = pathPointOut.y;
            lastpathPointVecOut.x = pathPointVecOut.x;
            lastpathPointVecOut.y = pathPointVecOut.y;
        }

    }
}
