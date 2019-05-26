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

import android.graphics.RectF;
import android.graphics.Typeface;

import com.aveeopen.Common.Vec2i;
import com.aveeopen.Common.Vec3f;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;
import com.aveeopen.comp.Visualizer.Graphic.SpriteFont;

import mdesl.graphics.glutils.FrameBuffer;

public class TextElement extends Element {

    private SpriteFont font1;
    private String text = "";
    private int fontSize = 24;
    private int color1 = 0xffffffff;

    public TextElement() {
        super();

        posX = 0.5f;
        posY = 0.5f;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFontSize(int fontSize) {
        if(this.fontSize == fontSize) return;
        this.fontSize = fontSize;
        this.markNeedReCreateGLResources();
    }

    public void setColor(int colorARGB) {
        color1 = colorARGB;
    }

    @Override
    protected void onApplyCustomization(CustomizationData customizationData) {
        super.onApplyCustomization(customizationData);
        setColor(customizationData.getPropertyInt("color", color1));
        setFontSize(customizationData.getPropertyInt("fontSize", fontSize));
    }

    @Override
    protected void onReadCustomization(CustomizationData outCustomizationData) {
        super.onReadCustomization(outCustomizationData);
        outCustomizationData.setCustomizationName("Text");
        outCustomizationData.putPropertyInt("color", color1, "crgba");
        outCustomizationData.putPropertyInt("fontSize", fontSize, "i 8 70");
    }

    @Override
    public void onCreateGLResources(RenderState renderData) {
        font1 = new SpriteFont(Typeface.DEFAULT, fontSize, SpriteFont.CharSet.createAscii32to126());
        super.onCreateGLResources(renderData);
    }

    @Override
    public void onRender(RenderState renderData, FrameBuffer resultFB) {
        super.onRender(renderData, resultFB);

        if(!font1.isValid()) return;

        String textMeasured = renderData.res.meter.measureText(text);

        Vec2i dim = new Vec2i(0, 0);
        if (localPosX != 0.0f) {//if you change measureDrawRect logic, this wont work probably
            dim = renderData.res.getFontRenderer().measureText(font1, textMeasured);
        } else {
            //we can save time and skip  measureText here..
            dim.y = renderData.res.getFontRenderer().measureTextY(font1);
        }

        RectF drawRect = measureDrawRect(renderData.res.meter, dim);

        renderData.res.getFontRenderer().drawText(renderData, font1, font1.getEntryTexture(), new Vec3f(drawRect.left, drawRect.top, 0.0f), textMeasured, color1, 0, false, 0, 0, 0, 0);
    }
}
