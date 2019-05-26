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

public class AlbumArtBlurredPictureElement extends AlbumArtPictureBaseElement {

    private Texture tex1 = null;
    private AtlasTexture atlasTex1 = null;
    private int color1 = 0xffffffff;
    //private int downscaledDim = 32;
    private int blurDivider = 5;
    private int pixelRadius = 7;

    public AlbumArtBlurredPictureElement() {
    }

    public void setColor(int colorARGB) {
        color1 = colorARGB;
    }

    public void setBlurAmount(int blurDivider, int pixelRadius) {
        if(this.blurDivider == blurDivider && this.pixelRadius == pixelRadius) return;
        this.blurDivider = blurDivider;
        this.pixelRadius = pixelRadius;
        this.markNeedReCreateGLResources();
    }

    @Override
    protected void onApplyCustomization(CustomizationData customizationData) {
        super.onApplyCustomization(customizationData);
        setColor(customizationData.getPropertyInt("color", color1));

        setBlurAmount(customizationData.getPropertyInt("blurDivider", blurDivider),
                customizationData.getPropertyInt("blurRadius", pixelRadius));
    }

    @Override
    protected void onReadCustomization(CustomizationData outCustomizationData) {
        super.onReadCustomization(outCustomizationData);
        outCustomizationData.setCustomizationName("AlbumArt Blurred");
        outCustomizationData.putPropertyInt("color", color1, "crgba");
        outCustomizationData.putPropertyInt("blurDivider", blurDivider, "i 0 10");
        outCustomizationData.putPropertyInt("blurRadius", pixelRadius, "i 1 25");
    }

    @Override
    protected void onAlbumArtCreateGLResources(Bitmap bitmap) {
        if (bitmap == null) {
            tex1 = null;
            atlasTex1 = null;
            return;
        }

        //int downScaledDim = Utils.pow2roundup( Math.max(bitmap.getWidth(), bitmap.getHeight()), 2048);

        int downscaledDim = Math.min(1 << blurDivider, 1024);

        Bitmap bitmapSmall = Bitmap.createScaledBitmap(bitmap, downscaledDim, downscaledDim, true);
        try {
            bitmapSmall = FastBlur.fastBlur(bitmapSmall, pixelRadius);
        } catch(Exception e)
        {
            tlog.w("Art bluring failed: "+e.getMessage());
        }

        tex1 = new VTexture(bitmapSmall,
                VTexture.DEFAULT_FILTER,
                VTexture.DEFAULT_FILTER,
                VTexture.DEFAULT_WRAP,
                false);
        atlasTex1 = new AtlasTexture(tex1);
    }

    @Override
    protected void onAlbumArtRender(RenderState renderData) {
        RectF drawRect = measureDrawRect(renderData.res.meter);

        if (atlasTex1 != null) {
            renderData.res.getBufferRenderer().drawRectangleRightBottomWH(
                    renderData,
                    drawRect.left, drawRect.top, 0.0f,
                    drawRect.width(), drawRect.height(),
                    color1,
                    Vec2f.zero, Vec2f.one,
                    atlasTex1);
        }
    }
}
