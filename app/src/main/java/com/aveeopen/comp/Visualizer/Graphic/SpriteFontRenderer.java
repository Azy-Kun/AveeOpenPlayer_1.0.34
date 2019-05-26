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

import com.aveeopen.Common.Vec2f;
import com.aveeopen.Common.Vec2i;
import com.aveeopen.Common.Vec3f;

public class SpriteFontRenderer {

    private BufferRenderer bufferRenderer;

    public SpriteFontRenderer(BufferRenderer bufferRenderer) {
        this.bufferRenderer = bufferRenderer;
    }

    //measures graphical bounds
    public Vec2i measureTextBounds(SpriteFont font, String text) {
        SpriteFont.Glyph glyph;

        float posX = 0.0f;
        int i = 0;

        glyph = font.getSpriteDescByChar(text.charAt(i));
        posX += glyph.visualXOffset;
        if (posX < 0.0f) posX = 0.0f;
        i++;
        for (; i < text.length() - 1; i++) {
            glyph = font.getSpriteDescByChar(text.charAt(i));
            posX += glyph.spaceWidth;
        }

        glyph = font.getSpriteDescByChar(text.charAt(i));
        posX += glyph.visualXOffset + glyph.spaceWidth;


        return new Vec2i((int) posX, (int) font.fontMaxHeight());
    }

    public Vec2i measureText(SpriteFont font, String text) {
        SpriteFont.Glyph glyph;
        float posX = 0.0f;

        for (int i = 0; i < text.length(); i++) {
            glyph = font.getSpriteDescByChar(text.charAt(i));
            posX += glyph.spaceWidth;
        }

        return new Vec2i((int) posX, (int) font.fontHeight());
    }

    public int measureTextY(SpriteFont font) {
        return (int) font.fontHeight();
    }


    public void drawText(RenderState renderData, SpriteFont fonts, Vec3f pos, String text, int color) {
        drawText(renderData, fonts, pos, text, color, 0, false, 0, 0, 0, 0);
    }

    public void drawText(RenderState renderData, SpriteFont fonts, Vec3f pos, String text, int color, int textsourceIndex) {
        drawText(renderData, fonts, pos, text, color, textsourceIndex, false, 0, 0, 0, 0);
    }

    public void drawText(RenderState renderData, SpriteFont fonts, Vec3f pos, String text, int color, int textsourceIndex, boolean clipEnabled, int clipX, int clipY, int clipW, int clipH) {
        drawText(renderData, fonts, fonts.getEntryTexture(), pos, text, color, textsourceIndex, clipEnabled, clipX, clipY, clipW, clipH);
    }

    public void drawText(RenderState renderData, SpriteFont fonts, IAtlasTexture tex, Vec3f pos, String text, int color, int textsourceIndex, boolean clipEnabled, int clipX, int clipY, int clipW, int clipH) {
        int len = text.length();
        float x = pos.x;
        float y = pos.y;
        float posZ = 1.0f;

        float clipX2 = clipX + clipW, clipY2 = clipY + clipH;
        float posX;
        float posY;
        float clippedX, clippedY;
        float clippedW, clippedH;

        posX = x;
        posY = y;
        SpriteFont.Glyph glyph;

        if (!clipEnabled) {
            for (int i = textsourceIndex; i < len; i++) {
                char ch = text.charAt(i);
                glyph = fonts.getSpriteDescByChar(ch);

                bufferRenderer.drawRectangleRightBottomWH(
                        renderData,
                        posX + glyph.visualXOffset, (posY + glyph.visualYOffset) - glyph.height, posZ,
                        glyph.width, glyph.height,
                        color,
                        new Vec2f(glyph.x / fonts.textureDim().x, glyph.y / fonts.textureDim().y),
                        new Vec2f((glyph.x + glyph.width) / fonts.textureDim().x, (glyph.y + glyph.height) / fonts.textureDim().y),
                        tex
                );

                posX += glyph.spaceWidth;
            }
        } else {
            for (int i = textsourceIndex; i < len; i++) {
                float posy2 = posY;
                char ch = text.charAt(i);
                glyph = fonts.getSpriteDescByChar(ch);

                clippedX = clipX - posX;
                clippedY = clipY - posY;
                clippedW = (glyph.width + posX) - clipX2;
                clippedH = (glyph.height + posY) - clipY2;

                if (clippedX < 0) clippedX = 0;
                if (clippedY < 0) clippedY = 0;
                if (clippedW < 0) clippedW = 0;
                if (clippedH < 0) clippedH = 0;

                if (clippedX > glyph.width || clippedW > glyph.width || clippedY > glyph.height || clippedH > glyph.height) {
                    posX += glyph.spaceWidth;
                    continue;
                }

                posX += clippedX;
                posy2 += clippedY;
                float glyphX = glyph.x + clippedX;
                float glyphY = glyph.y + clippedY;
                float glyphWidth = glyph.width - clippedW;
                float glyphHeight = glyph.height - clippedH;

                bufferRenderer.drawRectangleRightBottomWH(
                        renderData,
                        posX, posy2, posZ,
                        glyphWidth, glyphHeight,
                        color,
                        new Vec2f(glyphX / fonts.textureDim().x, glyphY / fonts.textureDim().y),
                        new Vec2f((glyphX + glyphWidth) / fonts.textureDim().x, (glyphY + glyphHeight) / fonts.textureDim().y),
                        tex
                );

                posX += glyph.spaceWidth;
            }
        }
    }
}
