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

public class Particle implements IParticle {

    public long createdTime;
    public float frameFloat;
    public int frame;
    public float textCoord;
    public float currLifetime;
    public float currLifetime10;
    public float lifeTime;
    public boolean animPaused;
    public boolean loop;
    public AtlasTexture sprite;
    public ParticleParameterStopp[] stopps;
    public Vec2f vel = new Vec2f(0.0f, 0.0f);
    public Vec2f gravity = new Vec2f(0.0f, 0.0f);
    public Vec2f pos = new Vec2f(0.0f, 0.0f);
    boolean alive = false; //for external use, don't try to check inside this class
    ParticleParameterStopp currStopp = new ParticleParameterStopp();

    @Override
    public Vec2f getPosition() {
        return pos;
    }

    @Override
    public float getSizeX() {
        return currStopp.sizeX;
    }

    @Override
    public float getSizeY() {
        return currStopp.sizeY;
    }

    @Override
    public float getRot() {
        if (!currStopp.velocityAngle)
            return currStopp.rot;
        else
            return vel.getAngle();
    }

    @Override
    public int getColorArgb() {
        return currStopp.colorArgb;
    }

    @Override
    public AtlasTexture getTextureFrame() {
        return sprite;
    }

    @Override
    public boolean getAlive() {
        return alive;
    }

    @Override
    public void setAlive(boolean alive) {
        this.alive = alive;
    }


    @Override
    public boolean updateTransform(float dt, Vec2f outParticeVisiblePos, Vec2f outParticleVisibleBounds) {

        currLifetime += dt;
        currLifetime10 = currLifetime / lifeTime;

        if (currLifetime10 > 1.0f) {
            alive = false;
            return false;
        }

        pos.x = pos.x + vel.x * dt;
        pos.y = pos.y + vel.y * dt;
        vel.x += gravity.x * dt;
        vel.y += gravity.y * dt;

        return true;
    }

    @Override
    public void updateRest(float dt) {

        if (!animPaused)
            frame = (int) ((sprite.getFramesCount()) * currLifetime10);

        int s;
        for (s = 0; s < stopps.length; s++) {
            if (stopps[s].atTime >= currLifetime10) break;
        }

        s -= 1;
        if (s < 0) s = 0;
        if (s >= stopps.length - 1) s = stopps.length - 2;
        int sA = s;
        int sB = s + 1;
        if (sA < 0) sA = 0;

        //float difftime = stopps[sB].atTime - stopps[sA].atTime;
        float interpolTime = (currLifetime10 - stopps[sA].atTime) / (stopps[sB].atTime - stopps[sA].atTime);
        if (interpolTime > 1.0f) interpolTime = 1.0f;

        ParticleParameterStopp.Interpolate(currStopp, stopps[sA], stopps[sB], interpolTime);

        if (!loop && frame >= sprite.getFramesCount()) {
            frame = (sprite.getFramesCount()) - 1;
        }
        frame %= (sprite.getFramesCount());

        //float timepassed = (Utils.tickCount() - createdTime) * 0.001f;
        if (!animPaused)
            textCoord = 1.0f * currLifetime10;
    }

}

