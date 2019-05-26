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

import com.aveeopen.Common.Interpolate;

public class ParticleParameterStopp {

    public float atTime;
    public float sizeX, sizeY;
    public float rot;
    public boolean velocityAngle;
    public int colorArgb;

    public static void Interpolate(ParticleParameterStopp out, ParticleParameterStopp a, ParticleParameterStopp b, float t) {

        out.atTime = 0.0f;
        out.sizeX = Interpolate.Lerp(a.sizeX, b.sizeX, t);
        out.sizeY = Interpolate.Lerp(a.sizeY, b.sizeY, t);
        out.rot = Interpolate.Lerp(a.rot, b.rot, t);
        out.colorArgb = Interpolate.LerpColor(a.colorArgb, b.colorArgb, t);

        if (t < 0.5f)
            out.velocityAngle = a.velocityAngle;
        else
            out.velocityAngle = b.velocityAngle;
    }
}
