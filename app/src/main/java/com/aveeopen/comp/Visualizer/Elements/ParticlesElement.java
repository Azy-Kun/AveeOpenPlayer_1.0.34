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

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;

import com.aveeopen.Common.Vec2f;
import com.aveeopen.comp.Visualizer.Graphic.AtlasTexture;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;
import com.aveeopen.comp.Visualizer.Elements.Particles.IParticle;
import com.aveeopen.comp.Visualizer.Elements.Particles.IParticleFactory;

import mdesl.graphics.glutils.FrameBuffer;

public class ParticlesElement extends Element {

    private int particlesLowCount = 0;
    private IParticle[] particles = new IParticle[0];
    private float emittingTimeAcc = 0.0f;
    private IArea areaField = null;
    private float everySec = 0.1f;
    private IParticleFactory particleFactory = null;
    private int color1 = 0xffffffff;
    private float particleScale = 1.0f;

    public void setArea(IArea areaField) {
        this.areaField = areaField;
    }

    public void setParticlesCountLimit(int particlesLowCount) {
        this.particlesLowCount = particlesLowCount;

        if (particleFactory != null) {

            particles = new IParticle[particlesLowCount];
            for (int i = 0; i < particles.length; i++)
                particles[i] = particleFactory.allocateParticle();

        } else {
            particles = new IParticle[0];
        }

    }

    public void setParticlesSpawnTime(float everySec) {
        this.everySec = everySec;
    }

    public void setParticlesScale(float value) {
        this.particleScale = value;
    }

    public void setParticlesFactory(IParticleFactory particleFactory) {
        this.particleFactory = particleFactory;

        if (particleFactory != null) {

            particles = new IParticle[particlesLowCount];
            for (int i = 0; i < particles.length; i++)
                particles[i] = particleFactory.allocateParticle();

        } else {
            particles = new IParticle[0];
        }
    }

    void setParticleDead(int index) {
        particles[index].setAlive(false);
    }

    void setParticleAlive(int index) {
        particles[index].setAlive(true);
    }

    boolean isParticleAlive(int index) {
        return particles[index].getAlive();
    }

    int getFreeParticleIndex() {

        int lowestTime = Integer.MAX_VALUE;
        int oldestParticelIndex = 0;

        int i;
        for (i = 0; i < particles.length; i++) {
            if (!particles[i].getAlive()) {
                break;
            }

            //if cant, don't create new one
//            if (particles[i].createdTime < lowestTime)
//            {
//                lowestTime = particles[i].createdTime;
//                oldestParticelIndex = i;
//            }

        }

        if (i < particles.length) {
        } else {
            //if can't, don't create new one //DESIGN
            return -1;
            //i = oldestParticelIndex;
            //setParticleDead(i);
        }


        return i;
    }

    public void setColor(int colorARGB) {
        color1 = colorARGB;
    }

    @Override
    protected void onApplyCustomization(CustomizationData customizationData) {
        super.onApplyCustomization(customizationData);
        color1 = customizationData.getPropertyInt("color", color1);
        particleScale = customizationData.getPropertyFloat("scale", particleScale);
        everySec =  customizationData.getPropertyFloat("spawnTime", everySec);
    }

    @Override
    protected void onReadCustomization(CustomizationData outCustomizationData) {
        super.onReadCustomization(outCustomizationData);

        outCustomizationData.setCustomizationName("Particles");
        outCustomizationData.putPropertyInt("color", color1, "crgba");
        outCustomizationData.putPropertyFloat("scale", particleScale, "f 0.5 10.0");
        outCustomizationData.putPropertyFloat("spawnTime", everySec, "f 0.05 1.0");
    }


    @Override
    public void onRender(RenderState renderData, FrameBuffer resultFB) {
        super.onRender(renderData, resultFB);

        RectF drawRect = measureDrawRect(renderData.res.meter);

        emittingTimeAcc += renderData.getFrameTimeSmooth();

        PointF pathPointOut = new PointF();
        PointF pathPointVecOut = new PointF();

        if (particleFactory != null) {

            everySec = Math.max(0.01f, everySec);//or it could hang
            while (emittingTimeAcc > everySec) {
                emittingTimeAcc -= everySec;

                int particleIndex = getFreeParticleIndex();

                if (areaField != null)
                    areaField.getRandomPointInArea(drawRect, pathPointOut, pathPointVecOut);

                if (particleIndex >= 0 && particleIndex < particles.length)
                    particleFactory.emitParticleMaybe(renderData, particles[particleIndex], pathPointOut, pathPointVecOut);

            }
        }

        update(renderData, resultFB);
        render(renderData, resultFB);
    }


    void update(RenderState renderData, FrameBuffer resultFB) {

        Vec2f particeVisiblePos = new Vec2f(0.0f, 0.0f);
        Vec2f particeVisibleBounds = new Vec2f(0.0f, 0.0f);

        for (int i = 0; i < particlesLowCount; i++) {

            setParticleDead(i);

            if (!updateParticle(renderData.getFrameTimeSmooth(), i, particeVisiblePos, particeVisibleBounds))
                continue;

            float particleVisibleSize = Math.max(particeVisibleBounds.x, particeVisibleBounds.y);//particles[i].currStopp.sizeX, particles[i].currStopp.sizeY);

            if (renderData.isVisibleOnScreen(particeVisiblePos, particleVisibleSize)) {
                setParticleAlive(i);
                updateParticleVisible(renderData.getFrameTimeSmooth(), i, particles[i]);
            }
        }
    }


    void render(RenderState renderData, FrameBuffer resultFB) {

        for (int i = 0; i < particles.length; i++) {
            if (!isParticleAlive(i)) continue;

            AtlasTexture currentActiveTexture = particles[i].getTextureFrame();
            int dxcolor = particles[i].getColorArgb();
            int colorfinal = Color.argb(
                    Color.alpha(dxcolor) * Color.alpha(dxcolor) / 256,
                    (Color.red(color1) * Color.red(dxcolor)) / 256,
                    (Color.green(color1) * Color.green(dxcolor)) / 256,
                    (Color.blue(color1) * Color.blue(dxcolor)) / 256);

            Vec2f pos = particles[i].getPosition();
            float angle = particles[i].getRot();

            if (currentActiveTexture == null)
                currentActiveTexture = renderData.res.getAtlasTexWhite();

            Vec2f dirx0 = Vec2f.rotate(new Vec2f(-particles[i].getSizeX()*particleScale, particles[i].getSizeY()*particleScale), angle);
            Vec2f dirx1 = Vec2f.rotate(new Vec2f(+particles[i].getSizeX()*particleScale, particles[i].getSizeY()*particleScale), angle);

            float x0 = pos.x + dirx0.x;
            float y0 = pos.y + dirx0.y;
            float x1 = pos.x + dirx1.x;
            float y1 = pos.y + dirx1.y;
            float x2 = pos.x - dirx1.x;
            float y2 = pos.y - dirx1.y;
            float x3 = pos.x - dirx0.x;
            float y3 = pos.y - dirx0.y;

            renderData.res.getBufferRenderer().drawRectangle(
                    renderData,
                    x0, y0,
                    x1, y1,
                    x2, y2,
                    x3, y3,
                    0.0f,
                    colorfinal,
                    Vec2f.zero, Vec2f.one,
                    currentActiveTexture);
        }
    }

    protected boolean updateParticle(float dt, int i, Vec2f particeVisiblePos, Vec2f outParticleVisibleBounds) {
        return particles[i].updateTransform(dt, particeVisiblePos, outParticleVisibleBounds);
    }

    protected void updateParticleVisible(float dt, int i, IParticle vsParticleEntry) {
        vsParticleEntry.updateRest(dt);
    }
}

