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

import com.aveeopen.comp.Visualizer.Elements.ICustomizable;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;

public interface ISegmentRenderer extends ICustomizable {

    void drawSegment(RenderState renderData,
                     int valueIndex,
                     int valuesCount,
                     float lastSegmentHeightVal,
                     float segmentHeightVal,
                     float drawSegmentWidth,
                     PointF lastDrawPoint,
                     PointF lastDrawVec,
                     PointF drawPoint,
                     PointF drawVec,
                     PointF drawScale,
                     int color1);
}
