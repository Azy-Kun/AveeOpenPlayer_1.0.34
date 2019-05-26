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

package com.aveeopen.comp.Visualizer.Elements.Particles;

import android.graphics.PointF;
import android.graphics.RectF;

import com.aveeopen.comp.Visualizer.Elements.IArea;

import java.util.Random;

public class HorizontalLineArea implements IArea {

    private Random randomGenerator = new Random();

    @Override
    public void getRandomPointInArea(RectF bounds, PointF pathPointOut, PointF pathPointVecOut) {

        float val10 = randomGenerator.nextFloat();

        pathPointOut.x = bounds.width() * val10;
        pathPointOut.y = bounds.centerY();
        pathPointVecOut.x = 0.0f;
        pathPointVecOut.y = 1.0f;
    }

}
