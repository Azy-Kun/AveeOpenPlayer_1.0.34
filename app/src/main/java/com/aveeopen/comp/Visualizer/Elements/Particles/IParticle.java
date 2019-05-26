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

import com.aveeopen.Common.Vec2f;
import com.aveeopen.comp.Visualizer.Graphic.AtlasTexture;

public interface IParticle {
    Vec2f getPosition();

    float getSizeX();

    float getSizeY();

    float getRot();

    int getColorArgb();

    AtlasTexture getTextureFrame();

    boolean getAlive();

    boolean updateTransform(float dt, Vec2f outParticeVisiblePos, Vec2f outParticleVisibleBounds);

    void updateRest(float dt);

    void setAlive(boolean alive);
}
