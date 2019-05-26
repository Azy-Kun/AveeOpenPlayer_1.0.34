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

public class SegmentPathHorizontalLine implements ISegmentPath {

    public static final String typeName = "HorizontalLine";//SegmentPathHorizontalLine

    boolean vertical = false;

    public SegmentPathHorizontalLine setVertical(boolean val)
    {
        vertical = val;
        return this;
    }

    @Override
    public void process(RenderState renderData) {

    }

    @Override
    public void getPointOnPath(int pointIndex, int pointsCount, RectF bounds, PointF pathPointOut, PointF pathPointVecOut) {
        float parts = bounds.width() / pointsCount;
        float paddedBoundsLeft = bounds.left + parts;
        float paddedBoundsWidth = bounds.width() - (parts * 2.0f);
        float step = Math.round(paddedBoundsWidth / (float) pointsCount);
        float minx = paddedBoundsLeft + (step * 0);
        float maxx = paddedBoundsLeft + (step * pointsCount);

        float centerXPadding = (bounds.width() - (maxx - minx)) * 0.5f;
        pathPointOut.x = centerXPadding + paddedBoundsLeft + (step * pointIndex);
        pathPointOut.y = bounds.centerY();

        pathPointVecOut.x = 0.0f;
        pathPointVecOut.y = 1.0f;
    }

    @Override
    public float getPathLength(RectF bounds, int neededPointsCountHint) {

        float parts = bounds.width() / neededPointsCountHint;
        return bounds.width() - (parts * 2.0f);//padded bounds
    }

    @Override
    public int getPreferredPointCount(RectF bounds) {
        return 2;
    }

    @Override
    public void onApplyCustomization(Element.CustomizationData customizationData) {
        //vertical = customizationData.getPropertyBool("vertical", vertical);
    }

    @Override
    public void onReadCustomization(Element.CustomizationData outCustomizationData) {
        //outCustomizationData.putPropertyBool("vertical", vertical, "b");
    }
}
