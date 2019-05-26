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

package com.aveeopen.Design;

import android.graphics.Color;
import android.graphics.PointF;

import com.aveeopen.Common.Tuple2;
import com.aveeopen.Common.Utils;
import com.aveeopen.Common.Vec2f;
import com.aveeopen.Common.Vec3f;
import com.aveeopen.comp.VisualUI.VisualizerThemeInfo;
import com.aveeopen.comp.Visualizer.Elements.AlbumArtBlurredPictureElement;
import com.aveeopen.comp.Visualizer.Elements.AlbumArtPictureElement;
import com.aveeopen.comp.Visualizer.Elements.AudioDataProviderElement;
import com.aveeopen.comp.Visualizer.Elements.BackgroundElement;
import com.aveeopen.comp.Visualizer.Elements.BlurGroupElement;
import com.aveeopen.comp.Visualizer.Elements.ElementGroup;
import com.aveeopen.comp.Visualizer.Elements.Particles.HorizontalLineArea;
import com.aveeopen.comp.Visualizer.Elements.Particles.IParticle;
import com.aveeopen.comp.Visualizer.Elements.Particles.IParticleFactory;
import com.aveeopen.comp.Visualizer.Elements.Particles.Particle;
import com.aveeopen.comp.Visualizer.Elements.Particles.ParticleParameterStopp;
import com.aveeopen.comp.Visualizer.Elements.Particles.RectArea;
import com.aveeopen.comp.Visualizer.Elements.ParticlesElement;
import com.aveeopen.comp.Visualizer.Elements.RootElement;
import com.aveeopen.comp.Visualizer.Elements.Segment.SegmentAudioSpectrumData;
import com.aveeopen.comp.Visualizer.Elements.Segment.SegmentAudioWaveformData;
import com.aveeopen.comp.Visualizer.Elements.Segment.SegmentPathSided;
import com.aveeopen.comp.Visualizer.Elements.Segment.SegmentRendererBar;
import com.aveeopen.comp.Visualizer.Elements.Segment.SegmentPathCircle;
import com.aveeopen.comp.Visualizer.Elements.Segment.SegmentRendererLine;
import com.aveeopen.comp.Visualizer.Elements.Segment.SegmentPathHorizontalLine;
import com.aveeopen.comp.Visualizer.Elements.Segment.SegmentRendererSharpBar;
import com.aveeopen.comp.Visualizer.Elements.SegmentElement;
import com.aveeopen.comp.Visualizer.Elements.TextElement;
import com.aveeopen.comp.Visualizer.Graphic.AtlasTexture;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;
import com.aveeopen.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VisualizerThemes {

    public interface IVisualizerFactory
    {
        RootElement create(int themeId);
    }

    private List<Tuple2<VisualizerThemeInfo, IVisualizerFactory>> themesList = new ArrayList<>();

    static int color1 = 0xffffffff;
    static int color2 = 0x60606060;
    static int textBlendMode = 4;
    static VisualizerThemes instance = null;

    public static VisualizerThemes s() {
        if (instance == null)
            new VisualizerThemes();

        return instance;
    }

    VisualizerThemes() {
        instance = this;

        themesList.add(new Tuple2<VisualizerThemeInfo, IVisualizerFactory>(new VisualizerThemeInfo(themesList.size(), R.drawable.vtheme1), new IVisualizerFactory() {
            @Override
            public RootElement create(int themeId) {
                return createVisPreset2(themeId);
            }
        }));
        themesList.add(new Tuple2<VisualizerThemeInfo, IVisualizerFactory>(new VisualizerThemeInfo(themesList.size(), R.drawable.vtheme2), new IVisualizerFactory() {
            @Override
            public RootElement create(int themeId) {
                return createVisPreset3(themeId);
            }
        }));
        themesList.add(new Tuple2<VisualizerThemeInfo, IVisualizerFactory>(new VisualizerThemeInfo(themesList.size(), R.drawable.vtheme3), new IVisualizerFactory() {
            @Override
            public RootElement create(int themeId) {
                return createVisPreset4(themeId);
            }
        }));
        themesList.add(new Tuple2<VisualizerThemeInfo, IVisualizerFactory>(new VisualizerThemeInfo(themesList.size(), R.drawable.vtheme4), new IVisualizerFactory() {
            @Override
            public RootElement create(int themeId) {
                return createVisPreset5(themeId);
            }
        }));
        themesList.add(new Tuple2<VisualizerThemeInfo, IVisualizerFactory>(new VisualizerThemeInfo(themesList.size(), R.drawable.vtheme5), new IVisualizerFactory() {
            @Override
            public RootElement create(int themeId) {
                return createVisPreset6(themeId);
            }
        }));
        themesList.add(new Tuple2<VisualizerThemeInfo, IVisualizerFactory>(new VisualizerThemeInfo(themesList.size(), R.drawable.vtheme6), new IVisualizerFactory() {
            @Override
            public RootElement create(int themeId) {
                return createVisPreset7(themeId);
            }
        }));
        themesList.add(new Tuple2<VisualizerThemeInfo, IVisualizerFactory>(new VisualizerThemeInfo(themesList.size(), R.drawable.vtheme7), new IVisualizerFactory() {
            @Override
            public RootElement create(int themeId) {
                return createVisPreset8(themeId);
            }
        }));
        themesList.add(new Tuple2<VisualizerThemeInfo, IVisualizerFactory>(new VisualizerThemeInfo(themesList.size(), R.drawable.vtheme8), new IVisualizerFactory() {
            @Override
            public RootElement create(int themeId) {
                return createVisPreset9(themeId);
            }
        }));
        themesList.add(new Tuple2<VisualizerThemeInfo, IVisualizerFactory>(new VisualizerThemeInfo(themesList.size(), R.drawable.vtheme9), new IVisualizerFactory() {
            @Override
            public RootElement create(int themeId) {
                return createVisPreset10(themeId);
            }
        }));

    }

    public List<Tuple2<VisualizerThemeInfo, VisualizerThemes.IVisualizerFactory>> getThemesList() {
        return themesList;
    }

    public RootElement getThemeObject(int themeId) {

        if(themeId < 0 || themeId >= themesList.size()) return null;

        return themesList.get(themeId).obj2.create(themeId);

    }

    static ElementGroup createDefaultRoot(BackgroundElement rootElement) {

        rootElement.setBackgroundColor(0.05f, 0.05f, 0.05f, 1.0f);
        return rootElement;

//        FxaaGroupElement ms = new FxaaGroupElement();
//        ms.setBlendMode(2);
//        rootElement.addChildAtEnd(ms);
//        return ms;

    }

    static ElementGroup onFinishedDefaultRoot(ElementGroup rootElement) {
//        {
//            FpsTextElement fps = new FpsTextElement();
//            fps.setPosition(0.0f, 0.1f);
//            fps.setLocalPosition(0.0f, 1.0f);
//            rootElement.addChildAtEnd(fps);
//        }
        return rootElement;
    }


    static RootElement createVisPreset2(int themeId) {
        BackgroundElement root0Element = new BackgroundElement();
        ElementGroup rootElement = createDefaultRoot(root0Element);

        AudioDataProviderElement ad = new AudioDataProviderElement();
        ad.setSegmentDataProvider(new SegmentAudioWaveformData());

        AlbumArtPictureElement e = new AlbumArtPictureElement();
        e.setPosition(0.0f, 0.0f);
        e.setLocalPosition(0.0f, 0.0f);
        e.setScale(1.0f, 1.0f);

        SegmentElement wve = new SegmentElement();
        wve.setSegmentPath(new SegmentPathHorizontalLine());
        wve.setSegmentRenderer(new SegmentRendererSharpBar());
        wve.setColor(0xffffffff);
        wve.setPosition(0.0f, 0.5f);
        wve.setLocalPosition(0.0f, 0.5f);
        wve.setScale(1.0f, 0.8f);

        rootElement.addChildAtEnd(ad);
        rootElement.addChildAtEnd(e);
        rootElement.addChildAtEnd(wve);

        return new RootElement(themeId, onFinishedDefaultRoot(root0Element));
    }

    static RootElement createVisPreset3(int themeId) {
        BackgroundElement root0Element = new BackgroundElement();
        ElementGroup rootElement = createDefaultRoot(root0Element);

        AudioDataProviderElement ad = new AudioDataProviderElement();
        SegmentAudioSpectrumData segmentAudioSpectrumData = new SegmentAudioSpectrumData();
        segmentAudioSpectrumData.setDataMode(0);
        segmentAudioSpectrumData.setSampleOutCount(63);
        ad.setSegmentDataProvider(segmentAudioSpectrumData);

        AlbumArtPictureElement e = new AlbumArtPictureElement();
        e.setScale(1.0f, 1.0f);

        SegmentElement wve = new SegmentElement();
        wve.setSegmentPath(new SegmentPathHorizontalLine());
        wve.setSegmentRenderer(new SegmentRendererBar());
        wve.setPosition(0.5f, 0.975f);
        wve.setPositionUniform(false, false);
        wve.setLocalPosition(0.5f, 0.5f);
        wve.setScale(1.0f, 1.0f);
        wve.setMinBarHeightScale(0.003f);

        rootElement.addChildAtEnd(ad);
        rootElement.addChildAtEnd(e);
        rootElement.addChildAtEnd(wve);

        return new RootElement(themeId, onFinishedDefaultRoot(root0Element));
    }

    static RootElement createVisPreset4(int themeId) {
        BackgroundElement root0Element = new BackgroundElement();
        ElementGroup rootElement = createDefaultRoot(root0Element);

        AudioDataProviderElement ad = new AudioDataProviderElement();
        SegmentAudioSpectrumData segmentAudioSpectrumData = new SegmentAudioSpectrumData();
        segmentAudioSpectrumData.setDataMode(1);
        segmentAudioSpectrumData.setSampleOutCount(100);
        ad.setSegmentDataProvider(segmentAudioSpectrumData);

        AlbumArtBlurredPictureElement e = new AlbumArtBlurredPictureElement();
        e.setScale(1.0f, 1.0f);

        AlbumArtPictureElement art = new AlbumArtPictureElement();
        art.drawBorderArt(false);
        art.setPosition(0.5f, 0.5f);
        art.setLocalPosition(0.5f, -0.3f);
        art.setScale(0.35f, 0.35f);
        art.setLocalPositionUniform(true, true);
        art.setScaleUniform(true, true);

        TextElement texte = new TextElement();
        texte.setColor(color1);
        texte.setBlendMode(textBlendMode);
        texte.setFontSize(56);
        texte.setText("$artist");
        texte.setPosition(0.5f, 0.5f);
        texte.setLocalPosition(0.5f, 1.4f);

        TextElement texte2 = new TextElement();
        texte2.setColor(color1);
        texte2.setBlendMode(textBlendMode);
        texte2.setFontSize(28);
        texte2.setText("$title");
        texte2.setPosition(0.5f, 0.5f);
        texte2.setLocalPosition(0.5f, 1.4f);

        final Random randomGenerator = new Random();
        ParticlesElement particles = new ParticlesElement();
        particles.setPosition(0.0f, 0.5f);
        particles.setScale(1.0f, 1.0f);
        particles.setParticlesCountLimit(1000);
        particles.setParticlesSpawnTime(0.05f);
        particles.setArea(new HorizontalLineArea());
        particles.setBlendMode(2);
        particles.setParticlesFactory(new IParticleFactory() {

            @Override
            public IParticle allocateParticle() {
                return new Particle();
            }

            @Override
            public boolean emitParticleMaybe(RenderState renderData, IParticle reusedParticle, PointF point, PointF vec) {

                Particle particle = (Particle)reusedParticle;

                float val10 = randomGenerator.nextFloat();

                float _lifetime = 3.0f;
                AtlasTexture _animsprite = renderData.res.getAtlasTexParticle0();

                ParticleParameterStopp[] stopps = new ParticleParameterStopp[3];
                for (int i = 0; i < stopps.length; i++)
                    stopps[i] = new ParticleParameterStopp();

                stopps[0].atTime = 0.0f;
                stopps[1].atTime = 0.9f;
                stopps[2].atTime = 1.0f;

                stopps[0].sizeX = stopps[0].sizeY = 5.5f + (val10 * 2.0f);
                stopps[1].sizeX = stopps[1].sizeY = 5.5f + (val10 * 2.0f);
                stopps[2].sizeX = stopps[2].sizeY = 5.5f + (val10 * 2.0f);

                stopps[0].colorArgb = Color.argb((int) (255.0f * val10), (int) (255.0f * val10), (int) (255.0f * val10), (int) (255.0f * val10));
                stopps[1].colorArgb = Color.argb((int) (255.0f * val10), (int) (255.0f * val10), (int) (255.0f * val10), (int) (255.0f * val10));
                stopps[2].colorArgb = 0x00000000;

                particle.vel = (new Vec2f(randomGenerator.nextFloat() * 2.0f - 1.0f, -1.0f)).normalizedResult();

                float speed = (100.0f * val10) + 0.0f;

                particle.pos = new Vec2f(point.x, point.y);
                particle.vel.x *= speed;
                particle.vel.y *= speed;
                particle.gravity = new Vec2f(0.0f, 100.0f);
                particle.createdTime = Utils.tickCount();
                particle.frameFloat = 0.0f;
                particle.currLifetime = 0.0f;
                particle.currLifetime10 = 0.0f;
                particle.lifeTime = _lifetime;
                particle.loop = false;
                particle.animPaused = false;
                particle.sprite = _animsprite;
                particle.stopps = stopps;
                particle.setAlive(true);


                return true;
            }
        });

        SegmentElement wve = new SegmentElement();
        wve.setSegmentPath(new SegmentPathHorizontalLine());
        wve.setSegmentRenderer(new SegmentRendererBar());
        wve.setPosition(0.5f, 0.5f);
        wve.setScale(1.0f, -1.0f);
        wve.setMinBarHeightScale(0.003f);

        BlurGroupElement blurE = new BlurGroupElement();
        blurE.setRenderContentOnTop(true);
        blurE.setBlendMode(2);

        blurE.addChildAtEnd(wve);
        blurE.addChildAtEnd(particles);

        rootElement.addChildAtEnd(ad);
        rootElement.addChildAtEnd(e);
        rootElement.addChildAtEnd(texte);
        rootElement.addChildAtEnd(texte2);
        rootElement.addChildAtEnd(art);
        rootElement.addChildAtEnd(blurE);


        return new RootElement(themeId, onFinishedDefaultRoot(root0Element));
    }

    static RootElement createVisPreset5(int themeId) {
        BackgroundElement root0Element = new BackgroundElement();
        ElementGroup rootElement = createDefaultRoot(root0Element);

        AudioDataProviderElement ad = new AudioDataProviderElement();
        ad.setSegmentDataProvider(new SegmentAudioWaveformData());

        AlbumArtBlurredPictureElement e = new AlbumArtBlurredPictureElement();
        e.setScale(1.0f, 1.0f);

        AlbumArtPictureElement art = new AlbumArtPictureElement();
        art.drawBorderArt(false);
        art.setPosition(0.5f, 0.5f);
        art.setLocalPosition(0.5f, -0.3f);
        art.setScale(0.35f, 0.35f);
        art.setLocalPositionUniform(true, true);
        art.setScaleUniform(true, true);

        TextElement texte = new TextElement();
        texte.setColor(color1);
        texte.setBlendMode(textBlendMode);
        texte.setFontSize(56);
        texte.setText("$artist");
        texte.setPosition(0.5f, 0.5f);
        texte.setLocalPosition(0.5f, 1.4f);

        TextElement texte2 = new TextElement();
        texte2.setColor(color1);
        texte2.setBlendMode(textBlendMode);
        texte2.setFontSize(28);
        texte2.setText("$title");
        texte2.setPosition(0.5f, 0.5f);
        texte2.setLocalPosition(0.5f, 1.4f);

        SegmentElement wve = new SegmentElement();
        wve.setSegmentPath(new SegmentPathHorizontalLine());
        wve.setSegmentRenderer(new SegmentRendererSharpBar());
        wve.setPosition(0.5f, 0.5f);
        wve.setScale(1.0f, 0.40f);

        BlurGroupElement blurE = new BlurGroupElement();
        blurE.setRenderContentOnTop(true);
        blurE.setBlendMode(2);

        blurE.addChildAtEnd(wve);

        rootElement.addChildAtEnd(ad);
        rootElement.addChildAtEnd(e);
        rootElement.addChildAtEnd(art);
        rootElement.addChildAtEnd(texte);
        rootElement.addChildAtEnd(texte2);
        rootElement.addChildAtEnd(blurE);


        return new RootElement(themeId, onFinishedDefaultRoot(root0Element));
    }

    static RootElement createVisPreset6(int themeId) {

        BackgroundElement root0Element = new BackgroundElement();
        ElementGroup rootElement = createDefaultRoot(root0Element);

        AudioDataProviderElement ad = new AudioDataProviderElement();
        SegmentAudioSpectrumData segmentAudioSpectrumData = new SegmentAudioSpectrumData();
        segmentAudioSpectrumData.setDataMode(2);
        ad.setSegmentDataProvider(segmentAudioSpectrumData);

        AlbumArtBlurredPictureElement e = new AlbumArtBlurredPictureElement();
        e.setScale(1.0f, 1.0f);

        TextElement texte = new TextElement();
        texte.setColor(color2);
        texte.setBlendMode(textBlendMode);
        texte.setFontSize(28);
        texte.setText("$artist");
        texte.setPosition(0.05f, 1.0f);
        texte.setLocalPosition(0.0f, 1.7f);
        texte.setPositionUniform(true, false);

        TextElement texte2 = new TextElement();
        texte2.setColor(color2);
        texte2.setBlendMode(textBlendMode);
        texte2.setFontSize(28);
        texte2.setText("$title");
        texte2.setPosition(0.05f, 1.0f);
        texte2.setLocalPosition(0.0f, 1.0f);
        texte2.setPositionUniform(true, false);

        SegmentElement wve = new SegmentElement();
        wve.setSegmentPath(new SegmentPathCircle());
        wve.setSegmentRenderer(new SegmentRendererBar().setBarWidth(0.5f));
        wve.setPosition(0.5f, 0.5f);
        wve.setScale(0.4f, 0.4f);
        wve.setMinBarHeightScale(0.0f);

        BlurGroupElement blurE = new BlurGroupElement();
        blurE.setRenderContentOnTop(true);
        blurE.setColor2(0xffa0a0a0);
        blurE.setBlendMode(2);
        blurE.setBlurLayerScale(0, 0.0f, 0.0f);
        blurE.setBlurLayerScale(1, 1.9f, 1.9f);

        AlbumArtPictureElement art = new AlbumArtPictureElement();
        art.drawBorderArt(false);
        art.setPosition(0.5f, 0.5f);
        art.setScale(0.4f, 0.4f);
        art.setLocalPositionUniform(true, true);
        art.setScaleUniform(true, true);
        art.setCircleShape(true);

        blurE.addChildAtEnd(wve);

        rootElement.addChildAtEnd(ad);
        rootElement.addChildAtEnd(e);
        rootElement.addChildAtEnd(texte);
        rootElement.addChildAtEnd(texte2);
        rootElement.addChildAtEnd(blurE);
        rootElement.addChildAtEnd(art);

        return new RootElement(themeId, onFinishedDefaultRoot(root0Element));
    }

    static RootElement createVisPreset7(int themeId) {

        BackgroundElement root0Element = new BackgroundElement();
        ElementGroup rootElement = createDefaultRoot(root0Element);

        AudioDataProviderElement ad = new AudioDataProviderElement();
        SegmentAudioSpectrumData segmentAudioSpectrumData = new SegmentAudioSpectrumData();
        segmentAudioSpectrumData.setDataMode(2);
        ad.setSegmentDataProvider(segmentAudioSpectrumData);

        AlbumArtBlurredPictureElement e = new AlbumArtBlurredPictureElement();
        e.setScale(1.0f, 1.0f);
        e.setColor(0xffb0b0b0);

        TextElement texte = new TextElement();
        texte.setColor(color2);
        texte.setBlendMode(textBlendMode);
        texte.setFontSize(28);
        texte.setText("$artist");
        texte.setPosition(0.05f, 1.0f);
        texte.setLocalPosition(0.0f, 1.7f);
        texte.setPositionUniform(true, false);

        TextElement texte2 = new TextElement();
        texte2.setColor(color2);
        texte2.setBlendMode(textBlendMode);
        texte2.setFontSize(28);
        texte2.setText("$title");
        texte2.setPosition(0.05f, 1.0f);
        texte2.setLocalPosition(0.0f, 1.0f);
        texte2.setPositionUniform(true, false);

        SegmentElement wve = new SegmentElement();
        wve.setColor(0xffff934a);
        wve.setSegmentPath(new SegmentPathCircle());
        wve.setSegmentRenderer(new SegmentRendererSharpBar());
        wve.setPosition(0.5f, 0.5f);
        wve.setScale(0.4f, 0.4f);
        wve.setMinBarHeightScale(0.01f);
        wve.setBarHeightScale(1.0f);

        BlurGroupElement blurE = new BlurGroupElement();
        blurE.setRenderContentOnTop(true);
        blurE.setColor2(0xffa31010);
        blurE.setBlendMode(2);
        blurE.setBlurLayerScale(0, 1.0f, 1.0f);
        blurE.setBlurLayerScale(1, 1.9f, 1.9f);

        AlbumArtPictureElement art = new AlbumArtPictureElement();
        art.drawBorderArt(false);
        art.setPosition(0.5f, 0.5f);
        art.setScale(0.4f, 0.4f);
        art.setLocalPositionUniform(true, true);
        art.setScaleUniform(true, true);
        art.setCircleShape(true);

        blurE.addChildAtEnd(wve);

        rootElement.addChildAtEnd(ad);
        rootElement.addChildAtEnd(e);
        rootElement.addChildAtEnd(texte);
        rootElement.addChildAtEnd(texte2);
        rootElement.addChildAtEnd(blurE);
        rootElement.addChildAtEnd(art);

        return new RootElement(themeId, onFinishedDefaultRoot(root0Element));
    }

    static RootElement createVisPreset8(int themeId) {
        BackgroundElement root0Element = new BackgroundElement();
        ElementGroup rootElement = createDefaultRoot(root0Element);

        AudioDataProviderElement ad = new AudioDataProviderElement();
        SegmentAudioSpectrumData segmentAudioSpectrumData = new SegmentAudioSpectrumData();
        segmentAudioSpectrumData.setDataMode(2);
        ad.setSegmentDataProvider(segmentAudioSpectrumData);

        AlbumArtBlurredPictureElement e = new AlbumArtBlurredPictureElement();
        e.setScale(1.0f, 1.0f);
        e.setColor(0xffb0b0b0);

        TextElement texte = new TextElement();
        texte.setColor(color2);
        texte.setBlendMode(textBlendMode);
        texte.setFontSize(28);
        texte.setText("$artist");
        texte.setPosition(0.05f, 1.0f);
        texte.setLocalPosition(0.0f, 1.7f);
        texte.setPositionUniform(true, false);

        TextElement texte2 = new TextElement();
        texte2.setColor(color2);
        texte2.setBlendMode(textBlendMode);
        texte2.setFontSize(28);
        texte2.setText("$title");
        texte2.setPosition(0.05f, 1.0f);
        texte2.setLocalPosition(0.0f, 1.0f);
        texte2.setPositionUniform(true, false);

        final Random randomGenerator = new Random();
        ParticlesElement particles = new ParticlesElement();
        particles.setPosition(0.0f, 0.5f);
        particles.setScale(1.0f, 1.0f);
        particles.setParticlesCountLimit(1000);
        particles.setParticlesSpawnTime(0.05f);
        particles.setArea(new RectArea());
        particles.setBlendMode(2);
        particles.setParticlesFactory(new IParticleFactory() {

            @Override
            public IParticle allocateParticle() {
                return new Particle();
            }

            @Override
            public boolean emitParticleMaybe(RenderState renderData, IParticle reusedParticle, PointF point, PointF vec) {

                Particle particle = (Particle)reusedParticle;

                Vec3f vel3 = new Vec3f(vec.x * 0.2f, vec.y * 0.2f, 1.0f);
                vel3.normalize();

                float val10 = randomGenerator.nextFloat();

                float _lifetime = 8.0f;
                AtlasTexture _animsprite = renderData.res.getAtlasTexParticle0();

                ParticleParameterStopp[] stopps = new ParticleParameterStopp[4];
                for (int i = 0; i < stopps.length; i++)
                    stopps[i] = new ParticleParameterStopp();

                stopps[0].atTime = 0.0f;
                stopps[1].atTime = 0.1f;
                stopps[2].atTime = 0.95f;
                stopps[3].atTime = 1.0f;

                stopps[0].sizeX = stopps[0].sizeY = 5.5f;//+ (val10 * 2.0f);
                stopps[1].sizeX = stopps[1].sizeY = 5.5f + (vel3.z * 10.0f * stopps[1].atTime);
                stopps[2].sizeX = stopps[2].sizeY = 5.5f + (vel3.z * 10.0f);
                stopps[3].sizeX = stopps[3].sizeY = 5.5f + (vel3.z * 10.0f);

                stopps[0].colorArgb = 0x00000000;
                stopps[1].colorArgb = Color.argb((int) (255.0f * val10), (int) (255.0f * val10), (int) (255.0f * val10), (int) (255.0f * val10));
                stopps[2].colorArgb = Color.argb((int) (255.0f * val10), (int) (255.0f * val10), (int) (255.0f * val10), (int) (255.0f * val10));
                stopps[3].colorArgb = 0x00000000;


                float speed = (100.0f * val10) + 30.0f;
                speed *= 10.0f;

                particle.pos = new Vec2f(point.x, point.y);
                particle.vel = new Vec2f(vel3.x * speed, vel3.y * speed);
                particle.createdTime = Utils.tickCount();
                particle.frameFloat = 0.0f;
                particle.currLifetime = 0.0f;
                particle.currLifetime10 = 0.0f;
                particle.lifeTime = _lifetime;
                particle.loop = false;
                particle.animPaused = false;
                particle.sprite = _animsprite;
                particle.stopps = stopps;
                particle.setAlive(true);


                return true;
            }
        });

        SegmentElement wve = new SegmentElement();
        wve.setColor(0xff00ffff);
        wve.setSegmentPath(new SegmentPathCircle());
        wve.setSegmentRenderer(new SegmentRendererLine());
        wve.setPosition(0.5f, 0.5f);
        wve.setScale(0.4f, 0.4f);
        wve.setScaleMeasure("$rms", 0.0005f);
        wve.setMinBarHeightScale(0.01f);
        wve.setBarHeightScale(1.1f);

        BlurGroupElement blurE = new BlurGroupElement();
        blurE.setRenderContentOnTop(true);
        blurE.setColor2(0xff1d37a5);
        blurE.setBlendMode(2);
        blurE.setBlurLayerScale(0, 1.0f, 1.0f);

        AlbumArtPictureElement art = new AlbumArtPictureElement();
        art.drawBorderArt(false);
        art.setPosition(0.5f, 0.5f);
        art.setScale(0.4f, 0.4f);
        art.setLocalPositionUniform(true, true);
        art.setScaleUniform(true, true);
        art.setCircleShape(true);
        art.setScaleMeasure("$rms", 0.0005f);

        blurE.addChildAtEnd(wve);
        blurE.addChildAtEnd(particles);

        rootElement.addChildAtEnd(ad);
        rootElement.addChildAtEnd(e);
        rootElement.addChildAtEnd(texte);
        rootElement.addChildAtEnd(texte2);
        rootElement.addChildAtEnd(blurE);
        rootElement.addChildAtEnd(art);

        return new RootElement(themeId, onFinishedDefaultRoot(root0Element)).setFrameDataProvider(segmentAudioSpectrumData);
    }

    static RootElement createVisPreset9(int themeId) {

        BackgroundElement root0Element = new BackgroundElement();
        ElementGroup rootElement = createDefaultRoot(root0Element);

        AudioDataProviderElement ad = new AudioDataProviderElement();
        SegmentAudioSpectrumData segmentAudioSpectrumData = new SegmentAudioSpectrumData();
        segmentAudioSpectrumData.setDataMode(2);
        segmentAudioSpectrumData.setSampleOutCount(128);
        ad.setSegmentDataProvider(segmentAudioSpectrumData);

        AlbumArtBlurredPictureElement e = new AlbumArtBlurredPictureElement();
        e.setScale(1.0f, 1.0f);
        e.setColor(0xff000000);

        final Random randomGenerator = new Random();
        ParticlesElement particles = new ParticlesElement();
        particles.setColor(0xff00a000);
        particles.setPosition(0.0f, 0.5f);
        particles.setScale(1.0f, 1.0f);
        particles.setParticlesCountLimit(1000);
        particles.setParticlesSpawnTime(0.05f);
        particles.setParticlesScale(5.0f);
        particles.setArea(new RectArea());
        particles.setBlendMode(2);
        particles.setParticlesFactory(new IParticleFactory() {

            @Override
            public IParticle allocateParticle() {
                return new Particle();
            }

            @Override
            public boolean emitParticleMaybe(RenderState renderData, IParticle reusedParticle, PointF point, PointF vec) {

                Particle particle = (Particle)reusedParticle;

                Vec3f vel3 = new Vec3f(randomGenerator.nextFloat() * 2.0f - 1.0f, randomGenerator.nextFloat() * 2.0f - 1.0f, 1.0f);
                vel3.normalize();

                float val10 = randomGenerator.nextFloat();

                float _lifetime = 8.0f;
                AtlasTexture _animsprite = renderData.res.getAtlasTexParticle0();

                ParticleParameterStopp[] stopps = new ParticleParameterStopp[4];
                for (int i = 0; i < stopps.length; i++)
                    stopps[i] = new ParticleParameterStopp();

                stopps[0].atTime = 0.0f;
                stopps[1].atTime = 0.1f;
                stopps[2].atTime = 0.95f;
                stopps[3].atTime = 1.0f;

                stopps[0].sizeX = stopps[0].sizeY = 5.5f;//+ (val10 * 2.0f);
                stopps[1].sizeX = stopps[1].sizeY = 5.5f + (vel3.z * 10.0f * stopps[1].atTime);
                stopps[2].sizeX = stopps[2].sizeY = 5.5f + (vel3.z * 10.0f);
                stopps[3].sizeX = stopps[3].sizeY = 5.5f + (vel3.z * 10.0f);

                stopps[0].colorArgb = 0x00000000;
                stopps[1].colorArgb = Color.argb((int) (255.0f * val10), (int) (255.0f * val10), (int) (255.0f * val10), (int) (255.0f * val10));
                stopps[2].colorArgb = Color.argb((int) (255.0f * val10), (int) (255.0f * val10), (int) (255.0f * val10), (int) (255.0f * val10));
                stopps[3].colorArgb = 0x00000000;


                float speed = (100.0f * val10) + 30.0f;
                speed *= 1.0f;

                particle.pos = new Vec2f(point.x, point.y);
                particle.vel = new Vec2f(vel3.x * speed, vel3.y * speed);

                particle.createdTime = Utils.tickCount();
                particle.frameFloat = 0.0f;

                particle.currLifetime = 0.0f;
                particle.currLifetime10 = 0.0f;
                particle.lifeTime = _lifetime;
                particle.loop = false;

                particle.animPaused = false;
                particle.sprite = _animsprite;
                particle.stopps = stopps;
                particle.setAlive(true);

                return true;
            }
        });

        TextElement texte = new TextElement();
        texte.setColor(color2);
        texte.setBlendMode(textBlendMode);
        texte.setFontSize(28);
        texte.setText("$artist");
        texte.setPosition(0.20f, 1.0f);
        texte.setLocalPosition(0.0f, 1.7f);
        texte.setPositionUniform(true, false);

        TextElement texte2 = new TextElement();
        texte2.setColor(color2);
        texte2.setBlendMode(textBlendMode);
        texte2.setFontSize(28);
        texte2.setText("$title");
        texte2.setPosition(0.20f, 1.0f);
        texte2.setLocalPosition(0.0f, 1.0f);
        texte2.setPositionUniform(true, false);

        AlbumArtPictureElement art = new AlbumArtPictureElement();
        art.drawBorderArt(false);
        art.setPosition(0.0f, 1.0f);
        art.setLocalPosition(0.0f, 1.0f);
        art.setScale(0.15f, 0.15f);
        art.setLocalPositionUniform(true, true);
        art.setScaleUniform(true, true);

        SegmentElement wve = new SegmentElement();
        wve.setColor(0xff00ff00);
        wve.setSegmentPath(new SegmentPathSided().setSides(3).setRadius(1.5f));
        wve.setSegmentRenderer(new SegmentRendererSharpBar().setBarWidth(0.3f).setFixedBarHeight(true, 10.0f));
        wve.setSegmentRenderer2(new SegmentRendererBar().setBarWidth(0.3f).setMirror(true));
        wve.setPosition(0.5f, 0.5f);
        wve.setScale(0.5f, 0.5f);
        wve.setMinBarHeightScale(0.01f);
        wve.setBarHeightScale(1.0f);

//        SolidCircleElement solid = new SolidCircleElement();
//        solid.setColor(0x00000000);
//        solid.setSideCount(25);
//        solid.setScaleUniform(true, true);
//        solid.setPosition(0.5f, 0.5f);
//        solid.setScale(0.5f, 0.5f);
//        solid.setColorBlendMeasure("$rms", 0.002f);

        BlurGroupElement blurE = new BlurGroupElement();
        blurE.setRenderContentOnTop(true);
        blurE.setColor2(0xff00ffa0);
        blurE.setBlendMode(2);
        blurE.setBlurLayerScale(0, 1.0f, 1.0f);

        blurE.addChildAtEnd(wve);
        blurE.addChildAtEnd(particles);
        //blurE.addChildAtEnd(solid);

        rootElement.addChildAtEnd(ad);
        rootElement.addChildAtEnd(e);
        rootElement.addChildAtEnd(texte);
        rootElement.addChildAtEnd(texte2);
        rootElement.addChildAtEnd(art);
        rootElement.addChildAtEnd(blurE);

        return (new RootElement(themeId, onFinishedDefaultRoot(root0Element))).setFrameDataProvider(segmentAudioSpectrumData);
    }

    static RootElement createVisPreset10(int themeId) {

        BackgroundElement root0Element = new BackgroundElement();
        ElementGroup rootElement = createDefaultRoot(root0Element);

        AudioDataProviderElement ad = new AudioDataProviderElement();
        SegmentAudioSpectrumData segmentAudioSpectrumData = new SegmentAudioSpectrumData();
        segmentAudioSpectrumData.setDataMode(1);
        segmentAudioSpectrumData.setSampleOutCount(100);
        ad.setSegmentDataProvider(segmentAudioSpectrumData);

        AlbumArtBlurredPictureElement e = new AlbumArtBlurredPictureElement();
        e.setColor(0xffff008c);
        e.setScale(1.0f, 1.0f);

        TextElement texte = new TextElement();
        texte.setColor(0x8064E090);
        texte.setBlendMode(textBlendMode);
        texte.setFontSize(70);
        texte.setText("$artist");
        texte.setPosition(0.5f, 0.5f);
        texte.setLocalPosition(0.5f, 1.4f);

        TextElement texte2 = new TextElement();
        texte2.setColor(0x8064E090);
        texte2.setBlendMode(textBlendMode);
        texte2.setFontSize(50);
        texte2.setText("$title");
        texte2.setPosition(0.5f, 0.5f);
        texte2.setLocalPosition(0.5f, -2.1f);

        SegmentElement wve = new SegmentElement();
        wve.setSegmentPath(new SegmentPathHorizontalLine());
        wve.setSegmentRenderer(new SegmentRendererBar().setBarWidth(0.7f).setMirror(true));
        wve.setPosition(0.5f, 0.5f);
        wve.setScale(1.0f, -1.0f);
        wve.setMinBarHeightScale(0.003f);
        wve.setColor(0x80ffffff);

        BlurGroupElement blurE = new BlurGroupElement();
        blurE.setColor2(0xff359ff7);
        blurE.setRenderContentOnTop(true);
        blurE.setBlurLayerScale(0, 1.0f, 1.0f);
        blurE.setBlurLayerScale(1, 5.0f, 10.0f);
        blurE.setBlurLayerScale(2, 10.0f, 20.0f);
        blurE.setBlendMode(2);

        blurE.addChildAtEnd(wve);
        blurE.addChildAtEnd(texte);
        blurE.addChildAtEnd(texte2);

        rootElement.addChildAtEnd(ad);
        rootElement.addChildAtEnd(e);
        rootElement.addChildAtEnd(blurE);

        return new RootElement(themeId, onFinishedDefaultRoot(root0Element));
    }
}
