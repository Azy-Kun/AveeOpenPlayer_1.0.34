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
import android.graphics.RectF;

import com.aveeopen.comp.Visualizer.Elements.Element;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;

public class SegmentPathCircle implements ISegmentPath {

    public static final String typeName = "Circle";//SegmentPathCircle

    private float radius = 1.0f;

    public SegmentPathCircle setRadius(float val)
    {
        radius = val;
        return this;
    }

    @Override
    public void process(RenderState renderData) {

    }

    @Override
    public void getPointOnPath(int pointIndex, int pointsCount, RectF bounds, PointF pathPointOut, PointF pathPointVecOut) {
        float progress = (float) pointIndex / (float) pointsCount;
        double length = 1.0;
        double angle = 2.0 * Math.PI * progress;

        pathPointVecOut.x = (float) (length * -Math.sin(angle));
        pathPointVecOut.y = (float) (length * Math.cos(angle));

        float drawRadius;
        if (bounds.width() < bounds.height())
            drawRadius = bounds.width();
        else
            drawRadius = bounds.height();

        drawRadius = drawRadius * 0.5f * radius;

        pathPointOut.x = bounds.centerX() + (-pathPointVecOut.x * drawRadius);
        pathPointOut.y = bounds.centerY() + (-pathPointVecOut.y * drawRadius);
    }

    @Override
    public float getPathLength(RectF bounds, int neededPointsCountHint) {

        float drawRadius;
        if (bounds.width() < bounds.height())
            drawRadius = bounds.width();
        else
            drawRadius = bounds.height();

        double circumference = 2.0 * Math.PI * radius;

        return (float) circumference * drawRadius * 0.5f;
    }

    @Override
    public int getPreferredPointCount(RectF bounds) {
        float segmentW = 18.0f;

        float drawRadius;
        if (bounds.width() < bounds.height())
            drawRadius = bounds.width();
        else
            drawRadius = bounds.height();

        float circumference = (float) (2.0 * Math.PI * drawRadius);
        int num = (int) ((circumference / segmentW) + 0.5f);//roundup

        return Math.max(num, 18);
    }

    @Override
    public void onApplyCustomization(Element.CustomizationData customizationData) {
        radius = customizationData.getPropertyFloat("radius", radius);
    }

    @Override
    public void onReadCustomization(Element.CustomizationData outCustomizationData) {
        outCustomizationData.putPropertyFloat("radius", radius, "f 0.1 3.0");
    }
}
