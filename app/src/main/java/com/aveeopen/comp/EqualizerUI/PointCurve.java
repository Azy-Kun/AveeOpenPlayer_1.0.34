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

package com.aveeopen.comp.EqualizerUI;

import java.util.ArrayList;
import java.util.List;

public class PointCurve {

    public class Point
    {
        public float x, y;

        public Point(float x, float y)
        {
            this.x = x;
            this.y = y;
        }
    }

    private List<Point> points = new ArrayList<>();

    public PointCurve()
    {
    }

    public void clear() {
        points.clear();
    }

    public void insert(float time, float value) {
        for(int i = 0; i< points.size(); i++)
        {
            if(points.get(i).x == time)
                return;

            if(points.get(i).x > time) {
                points.add(i, new Point(time, value));
                return;
            }
        }

        points.add(points.size(), new Point(time, value));
    }

    public float getValue(float time)
    {
        int lastI = -1;
        for(int i = 0; i< points.size(); i++)
        {
            if(points.get(i).x == time)
                return points.get(i).y;

            if(points.get(i).x > time)
                return getValue(lastI, i, time);

            lastI = i;
        }

        return getValue(lastI, points.size(), time);
    }

    public float getValue(int indexA, int indexB, float time)
    {
        if(points.size()==0) return 0.0f;

        if(indexA<0) indexA=0;
        if(indexB>= points.size()) indexB= points.size()-1;

        if(indexA == indexB)
            return points.get(indexB).y;

        float time0 = time - points.get(indexA).x;
        float timeSpan = points.get(indexB).x - points.get(indexA).x;

        float interpolate = time0 / timeSpan;
        return (points.get(indexA).y*(1.0f-interpolate)) + (points.get(indexB).y*interpolate);
    }

}
