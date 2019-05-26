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

public class SegmentPathSided implements ISegmentPath {

    public static final String typeName = "SidedPolygon";

    float radius = 1.0f;
    int sides = 3;
    float sideInAngle;
    String rotationMeasure = null;
    float measureMul = 1.0f;
    float angleAculumator = 0.0f;

    public SegmentPathSided() {
        setSides(3);
    }

    public SegmentPathSided setRadius(float val)
    {
        radius = val;
        return this;
    }

    public SegmentPathSided setSides(int sides)
    {
        this.sides = Math.max(sides, 3);
        sideInAngle = (float) ((2.0 * Math.PI)) / sides;
        return this;
    }

    public void setRotationMeasure(String measure, float mul) {
        rotationMeasure = measure;
        measureMul = mul;
    }

    @Override
    public void process(RenderState renderData) {
        PointF rotationMeasured = renderData.res.meter.measureVec2f(rotationMeasure);
        angleAculumator += rotationMeasured.x * measureMul * renderData.getFrameTimeF();
    }

    @Override
    public void getPointOnPath(int pointIndex, int pointsCount, RectF bounds, PointF pathPointOut, PointF pathPointVecOut) {

        float progress = (float) pointIndex / (float) pointsCount;
        double length = 1.0;
        PointF point0 = new PointF();
        PointF point1 = new PointF();

        {
            int currentSide = (int) (sides * progress);//Math.round
            float sideProgress = progress - ((float) currentSide / (float) sides);//  1.0f / sides;
            sideProgress = sideProgress / (1.0f / sides);

            double angle0 = (sideInAngle * currentSide) + angleAculumator;
            double angle1 = (angle0 + sideInAngle);

            double angleMid = (angle0 + angle1) * 0.5f;

            point0.x = (float) (length * -Math.sin(angle0));
            point0.y = (float) (length * Math.cos(angle0));

            point1.x = (float) (length * -Math.sin(angle1));
            point1.y = (float) (length * Math.cos(angle1));

            pathPointVecOut.x = (float) (length * -Math.sin(angleMid));
            pathPointVecOut.y = (float) (length * Math.cos(angleMid));

            pathPointOut.x = point0.x + (point1.x - point0.x) * sideProgress;
            pathPointOut.y = point0.y + (point1.y - point0.y) * sideProgress;
        }

        float drawRadius;
        if (bounds.width() < bounds.height())
            drawRadius = bounds.width();
        else
            drawRadius = bounds.height();

        drawRadius = drawRadius * 0.5f * radius;

        pathPointOut.x = bounds.centerX() + (-pathPointOut.x * drawRadius);
        pathPointOut.y = bounds.centerY() + (-pathPointOut.y * drawRadius);
    }

    @Override
    public float getPathLength(RectF bounds, int neededPointsCountHint) {

        float drawRadius;
        if (bounds.width() < bounds.height())
            drawRadius = bounds.width();
        else
            drawRadius = bounds.height();

        double circumference = sideInAngle * sides * radius;
        return (float) circumference * drawRadius * 0.5f;
    }

    @Override
    public int getPreferredPointCount(RectF bounds) {
        return sides;
    }

    @Override
    public void onApplyCustomization(Element.CustomizationData customizationData) {
        radius = customizationData.getPropertyFloat("radius", radius);
        setSides(customizationData.getPropertyInt("sides", sides));
    }

    @Override
    public void onReadCustomization(Element.CustomizationData outCustomizationData) {
        outCustomizationData.putPropertyFloat("radius", radius, "f 0.1 3.0");
        outCustomizationData.putPropertyInt("sides", sides, "i 3 18");
    }
}
