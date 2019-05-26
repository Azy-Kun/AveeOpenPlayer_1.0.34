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

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.aveeopen.Common.Vec2f;
import com.aveeopen.Common.tlog;
import com.aveeopen.comp.Visualizer.Graphic.AtlasTexture;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;
import com.aveeopen.comp.Visualizer.Graphic.VTexture;
import com.yahel.FastBlur;

import mdesl.graphics.Texture;

public class AlbumArtPictureElement extends AlbumArtPictureBaseElement {

    private boolean blurredBorder = true;
    private boolean circleShape = false;
    private Texture tex1 = null;
    private AtlasTexture atlasTex1 = null;
    private Texture tex2 = null;
    private AtlasTexture atlasTex2 = null;

    public AlbumArtPictureElement() {
    }

    public void drawBorderArt(boolean border) {
        if(this.blurredBorder == border) return;
        this.blurredBorder = border;
        this.markNeedReCreateGLResources();
    }

    public void setCircleShape(boolean circleShape) {
        this.circleShape = circleShape;
    }


    @Override
    protected void onApplyCustomization(CustomizationData customizationData) {
        super.onApplyCustomization(customizationData);
        drawBorderArt(customizationData.getPropertyBool("blurredBorder", blurredBorder));
        setCircleShape(customizationData.getPropertyBool("circleShape", circleShape));

    }

    @Override
    protected void onReadCustomization(CustomizationData outCustomizationData) {
        super.onReadCustomization(outCustomizationData);
        outCustomizationData.setCustomizationName("AlbumArt");
        outCustomizationData.putPropertyBool("blurredBorder", blurredBorder, "b");
        outCustomizationData.putPropertyBool("circleShape", circleShape, "b");
    }

    @Override
    protected void onAlbumArtCreateGLResources(Bitmap bitmap) {
        if (bitmap == null) {
            tex1 = null;
            atlasTex1 = null;
            tex2 = null;
            atlasTex2 = null;
            return;
        }

        Bitmap bitmapSmall = null;
        {
            if (blurredBorder) {
                bitmapSmall = Bitmap.createScaledBitmap(bitmap, 32, 32, true);
                try {
                    bitmapSmall = FastBlur.fastBlur(bitmapSmall, 7);
                } catch(Exception e)
                {
                    tlog.w("Art bluring failed: "+e.getMessage());
                }
            }
        }

        if (bitmapSmall != null) {
            tex1 = new VTexture(bitmapSmall,
                    VTexture.DEFAULT_FILTER,
                    VTexture.DEFAULT_FILTER,
                    VTexture.DEFAULT_WRAP,
                    false);

            atlasTex1 = new AtlasTexture(tex1);
        } else {
            tex1 = null;
            atlasTex1 = null;
        }

        tex2 = new VTexture(bitmap,
                VTexture.DEFAULT_FILTER,
                VTexture.DEFAULT_FILTER,
                VTexture.DEFAULT_WRAP,
                false);

        atlasTex2 = new AtlasTexture(tex2);
    }

    @Override
    protected void onAlbumArtRender(RenderState renderData) {

        RectF drawRect = measureDrawRect(renderData.res.meter);
        int color1 = 0xffffffff;

        if (atlasTex2 != null) {

            float drawWHRatio = drawRect.width() / drawRect.height();
            float w = atlasTex2.getWidth();
            float h = atlasTex2.getHeight();
            float artWHRatio = w / h;

            int borderNeeded = 0;

            if (w > drawRect.width() || h > drawRect.height()) {
                if (artWHRatio > drawWHRatio) {
                    w = drawRect.width();
                    h = w / artWHRatio;
                    borderNeeded++;
                } else if (artWHRatio < drawWHRatio) {
                    h = drawRect.height();
                    w = artWHRatio * h;
                    borderNeeded++;
                } else {

                    w = drawRect.width();
                    h = drawRect.height();
                }
            } else {
                borderNeeded++;
            }

            if (atlasTex1 != null && borderNeeded > 0) {
                renderData.res.getBufferRenderer().drawRectangleRightBottomWH(
                        renderData,
                        drawRect.left, drawRect.top, 0.0f,
                        drawRect.width(), drawRect.height(),
                        color1,
                        Vec2f.zero, Vec2f.one,
                        atlasTex1);
            }

            if (!circleShape) {
                float x = drawRect.centerX();
                float y = drawRect.centerY();

                x -= w * 0.5;
                y -= h * 0.5;

                renderData.res.getBufferRenderer().drawRectangleRightBottomWH(
                        renderData,
                        x, y, 0.0f,
                        w, h,
                        color1,
                        Vec2f.zero, Vec2f.one,
                        atlasTex2);

            } else {
                renderCircle(renderData, drawRect);
            }

        }
    }

    void renderCircle(RenderState renderData, RectF drawRect) {
        int color1 = 0xffffffff;

        float drawWHRatio = drawRect.width() / drawRect.height();

        float artw = atlasTex2.getWidth();
        float arth = atlasTex2.getHeight();
        float artWHRatio = artw / arth;

        float w2;
        float h2;
        if (artWHRatio > drawWHRatio) {
            w2 = drawRect.width();
            h2 = w2 / artWHRatio;
        } else if (artWHRatio < drawWHRatio) {
            h2 = drawRect.height();
            w2 = artWHRatio * h2;
        } else {
            w2 = drawRect.width();
            h2 = drawRect.height();
        }

        float x = drawRect.centerX();
        float y = drawRect.centerY();

        float texXMul;
        float texYMul;

        if (w2 > h2) {
            texXMul = 1.0f / artWHRatio;
            texYMul = 1.0f;
        } else {
            texXMul = 1.0f;
            texYMul = 1.0f * artWHRatio;
        }

        texXMul *= 0.5f;
        texYMul *= 0.5f;

        float smallest;
        if (w2 < h2) {
            smallest = h2;
        } else {
            smallest = w2;
        }

        x -= smallest * 0.5;
        y -= smallest * 0.5;

        renderData.res.getBufferRenderer().drawCircleSegmentW(
                renderData,
                x, y, 0.0f,
                smallest, smallest,
                color1,
                new Vec2f(0.5f - texXMul, 0.5f - texYMul), new Vec2f(0.5f + texXMul, 0.5f + texYMul),
                atlasTex2, 18.0f);
    }

}
