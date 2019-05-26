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

package com.aveeopen.comp.Visualizer.Graphic;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.view.Display;
import android.view.WindowManager;

import com.aveeopen.Common.Vec2f;
import com.aveeopen.Common.Vec2i;
import com.aveeopen.Common.tlog;
import com.aveeopen.comp.Visualizer.InternalVisualizationDataProvider;
import com.aveeopen.comp.Visualizer.Elements.Meter;
import com.aveeopen.R;

import javax.microedition.khronos.opengles.GL10;

import mdesl.graphics.Texture;
import mdesl.graphics.glutils.FrameBuffer;
import mdesl.graphics.glutils.ShaderProgram;

public class RenderState {

    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private float frameTimeF = 0.0f;
    private float frameTimeSmooth = 0.0f;
    private int fps = 0;
    private int frameTime = 0;
    private int fpsAcc = 0;
    private long fpsTimeAcc = 0;
    private long lastTimeMs = 0;

    private int fullscreenWidth, fullscreenHeight;
    private int screenWidth, screenHeight;
    private int currentBlendMode = -1;

    public final RenderResources res;

    public RenderState(InternalVisualizationDataProvider internalDataProvider) {
        res = new RenderResources(this, internalDataProvider);
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.translateM(viewMatrix, 0, 0.0f, 0.0f, -10.0f);
    }

    public float[] getVPMatrix() {
        return vPMatrix;
    }

    public float getFrameTimeF() {
        return frameTimeF;
    }

    public float getFrameTimeSmooth() {
        return frameTimeSmooth;
    }

    public int getFps() {
        return fps;
    }

    public int getFrameTime() {
        return frameTime;
    }

    public int getFullscreenWidth() {
        return fullscreenWidth;
    }

    public int getFullscreenHeight() {
        return fullscreenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public static boolean checkOGLError(String tag) {
        int err = GLES20.glGetError();
        if (err != GLES20.GL_NO_ERROR) {
            tlog.w("OGL Error (" + tag + ") : " + err);
            return false;
        }

        return true;
    }

    public boolean isVisibleOnScreen(Vec2f pos, float radiusMargin) {
        //TODO: Implement method
        return true;
    }

    public void onResources(Resources resources) {
        res.onResources(resources);
    }

    public void onSurfaceCreated() {
        unsetBlendMode();
        GLES20.glEnable(GL10.GL_BLEND);
        res.onSurfaceCreated();
    }

    public void onSurfaceChanged(Context context, int width, int height) {
        screenWidth = width;
        screenHeight = height;

        fullscreenWidth = screenWidth;
        fullscreenHeight = screenHeight;

        if (context != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();

            Point size = new Point();
            display.getSize(size);
            fullscreenWidth = size.x;
            fullscreenHeight = size.y;

            tlog.notice("fullscreen size: " + fullscreenWidth + "; " + fullscreenHeight);
        }
    }

    public void onFrameStart() {
        {
            long timeMs = SystemClock.uptimeMillis();

            frameTime = (int) (timeMs - lastTimeMs);
            fpsAcc++;

            if (timeMs - fpsTimeAcc >= 1000) {
                fps = fpsAcc;
                fpsAcc = 0;
                fpsTimeAcc = timeMs;
            }

            lastTimeMs = timeMs;
        }

        if (frameTime < 0) frameTime = 0;
        if (frameTime > 1000) frameTime = 1000;
        frameTimeF = frameTime * 0.001f;
        frameTimeSmooth = (frameTimeSmooth * 0.9f) + (frameTimeF * 0.1f);

        res.bufferRenderer.onFrameStart(this);
    }

    public void onFrameEnd() {
        res.bufferRenderer.onFrameEnd(this);
    }

    public void unsetBlendMode() {
        currentBlendMode = -1;
    }

    public void setBlendModeForce(int mode) {
        currentBlendMode = -1;
        setBlendMode(mode);
    }

    public void setBlendMode(int mode) {
        if (currentBlendMode == mode) return;

        res.bufferRenderer.onFrameEnd(this);

        switch (mode) {
            case 0:
                //alpha blend
                GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                break;
            case 1:
                //screen
                GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_COLOR);
                break;
            case 2:
                //linear dodge (add)
                GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE);
                break;
            case 3:
                //draw over
                GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ZERO);
                break;
            case 4:
                //pre-multiplied alpha
                GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
                break;
        }

        currentBlendMode = mode;
    }

    public void bindFrameBuffer(FrameBuffer fb) {
        res.bufferRenderer.onFrameEnd(this);

        if (fb == null) {
            GLES20.glViewport(0, 0, screenWidth, screenHeight);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            Matrix.orthoM(projectionMatrix, 0, 0, screenWidth, screenHeight, 0, 0.01f, 100f);
            Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        } else {
            fb.begin();
        }
    }

    public void bindShader(ShaderProgram shader) {
        if (res.atlasBufferShader != shader)
            res.bufferRenderer.onFrameEnd(this);

        shader.use();
    }

    public Vec2i getSafeRenderBufferSizeTextureDim() {
        int[] out_container = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_RENDERBUFFER_SIZE, out_container, 0);

        int maxTextureSize = out_container[0];

        tlog.notice("OGL Max render buffer size: " + maxTextureSize);
        tlog.notice("fullscreen size: " + fullscreenWidth + "; " + fullscreenHeight);

        int w = fullscreenWidth;
        int h = fullscreenHeight;

        return new Vec2i(Math.min(w, maxTextureSize), Math.min(h, maxTextureSize));
    }

    public Vec2i getSafeFullScreenSizeTextureDim() {
        int[] out_container = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, out_container, 0);

        int maxTextureSize = out_container[0];

        tlog.notice("OGL Max texture size: " + maxTextureSize);
        tlog.notice("fullscreen size: " + fullscreenWidth + "; " + fullscreenHeight);

        int w = fullscreenWidth;
        int h = fullscreenHeight;

        return new Vec2i(Math.min(w, maxTextureSize), Math.min(h, maxTextureSize));
    }

    public Vec2i getSafeScreenSizeTextureDim() {

        int[] out_container = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, out_container, 0);

        int maxTextureSize = out_container[0];

        tlog.notice("OGL Max texture size: " + maxTextureSize);

        int w = screenWidth;
        int h = screenHeight;

        return new Vec2i(Math.min(w, maxTextureSize), Math.min(h, maxTextureSize));
    }

    public void drawFullscreenQuad(int color1, Texture texture) {
        this.res.bufferRenderer.drawRectangleRightBottomWH(
                this,
                0.0f, screenHeight, 0.0f,
                screenWidth, -screenHeight,
                color1,
                Vec2f.zero, Vec2f.one,
                new AtlasTexture(texture));
    }

    public void drawFullscreenQuad(int color1, Texture texture, Vec2f tex0, Vec2f tex1) {
        this.res.bufferRenderer.drawRectangleRightBottomWH(
                this,
                0.0f, screenHeight, 0.0f,
                screenWidth, -screenHeight,
                color1,
                tex0, tex1,
                new AtlasTexture(texture));
    }

    public void drawFullscreenQuad(int color1, AtlasTexture entryTexture) {
        this.res.bufferRenderer.drawRectangleRightBottomWH(
                this,
                0.0f, screenHeight, 0.0f,
                screenWidth, -screenHeight,
                color1,
                Vec2f.zero, Vec2f.one,
                entryTexture);

    }

    public void drawFullscreenQuadNonAtlasBuffer() {
        res.fullQuad.draw();
    }

    public static class RenderResources {

        public final InternalVisualizationDataProvider visualizationData;
        public final Meter meter;

        private String blurVERT,blurFRAG, blurFRAG2;
        private String bufferVERT, bufferFRAG;
        private String fxaaShaderVERT, fxaaShaderFRAG;

        private VShaderProgram blurShader, blurShader2;
        private VShaderProgram fxaaShader;
        private VShaderProgram atlasBufferShader;

        private BufferRenderer bufferRenderer;
        private SpriteFontRenderer fontRenderer;
        private FullscreenQuad fullQuad;

        private Texture texWhite, texBlack;
        private AtlasTexture atlasTexWhite, atlasTexBlack;
        private Texture texParticle0;
        private AtlasTexture atlasTexParticle0;
        private Bitmap bitmapParticle0;

        RenderResources(RenderState renderState, InternalVisualizationDataProvider internalDataProvider)
        {
            visualizationData = internalDataProvider;
            meter = new Meter(renderState);
        }

        public VShaderProgram getAtlasBufferShader() {
            return atlasBufferShader;
        }

        public VShaderProgram getFxaaShader() {
            return fxaaShader;
        }

        public VShaderProgram getBlurShader2() {
            return blurShader2;
        }

        public VShaderProgram getBlurShader() {
            return blurShader;
        }

        public BufferRenderer getBufferRenderer() {
            return bufferRenderer;
        }

        public SpriteFontRenderer getFontRenderer() {
            return fontRenderer;
        }

        public FullscreenQuad getFullQuad() {
            return fullQuad;
        }

        public AtlasTexture getAtlasTexWhite() {
            return atlasTexWhite;
        }

        public AtlasTexture getAtlasTexBlack() {
            return atlasTexBlack;
        }

        public AtlasTexture getAtlasTexParticle0() {
            return atlasTexParticle0;
        }

        public void onSurfaceCreated() {

            fullQuad = new FullscreenQuad();

            texWhite = new VTexture(0xffffffff,
                    2, 2,
                    VTexture.DEFAULT_FILTER,
                    VTexture.DEFAULT_FILTER,
                    VTexture.DEFAULT_WRAP,
                    false);

            texBlack = new VTexture(0xff000000,
                    2, 2,
                    VTexture.DEFAULT_FILTER,
                    VTexture.DEFAULT_FILTER,
                    VTexture.DEFAULT_WRAP,
                    false);

            atlasTexWhite = new AtlasTexture(texWhite);
            atlasTexBlack = new AtlasTexture(texBlack);


            texParticle0 = new VTexture(bitmapParticle0,
                    VTexture.DEFAULT_FILTER,
                    VTexture.DEFAULT_FILTER,
                    VTexture.DEFAULT_WRAP,
                    false);

            atlasTexParticle0 = new AtlasTexture(texParticle0);

            try {

                {
                    blurShader = new VShaderProgram(blurVERT, blurFRAG);

                    if (blurShader.getLog().length() != 0)
                        tlog.w(blurShader.getLog());

                }
                {
                    blurShader2 = new VShaderProgram(blurVERT, blurFRAG2);

                    if (blurShader2.getLog().length() != 0)
                        tlog.w(blurShader2.getLog());
                }

                {
                    fxaaShader = new VShaderProgram(fxaaShaderVERT, fxaaShaderFRAG);

                    if (fxaaShader.getLog().length() != 0)
                        tlog.w(fxaaShader.getLog());
                }

                atlasBufferShader = new VShaderProgram(bufferVERT, bufferFRAG);
                if (atlasBufferShader.getLog().length() != 0)
                    tlog.w(atlasBufferShader.getLog());

                bufferRenderer = new BufferRenderer(atlasBufferShader, 1200);

                fontRenderer = new SpriteFontRenderer(bufferRenderer);
            } catch (Exception e) {
                tlog.w("RenderState error: " + e.getMessage());
            }
        }

        public void onResources(Resources resources) {
            blurVERT = GraphicsUtils.readResource(resources, R.raw.blur_vert);
            blurFRAG = GraphicsUtils.readResource(resources, R.raw.blurh_frag);
            blurFRAG2 = GraphicsUtils.readResource(resources, R.raw.blurv_frag);

            bufferVERT = GraphicsUtils.readResource(resources, R.raw.buffer_vert);
            bufferFRAG = GraphicsUtils.readResource(resources, R.raw.buffer_frag);

            fxaaShaderVERT = GraphicsUtils.readResource(resources, R.raw.fxaa_vert);
            fxaaShaderFRAG = GraphicsUtils.readResource(resources, R.raw.fxaa_frag);

            bitmapParticle0 = BitmapFactory.decodeResource(resources,
                    R.drawable.particle_blur0);
        }
    }
}
