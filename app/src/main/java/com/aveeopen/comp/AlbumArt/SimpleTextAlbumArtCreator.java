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

package com.aveeopen.comp.AlbumArt;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

class SimpleTextAlbumArtCreator {

    /**
     * Sets the text size for a Paint object so a given string of text will be a
     * given width.
     *
     * @param paint        the Paint to set the text size for
     * @param desiredWidth the desired width
     * @param text         the text that should be that width
     */
    private static void setTextSizeForWidth(Paint paint, float desiredWidth,
                                            String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
    }

    public static Bitmap textAsBitmap(int width, int height, String text, int textColor, int bgColor, int bgColor2, Drawable backgroundOver) {

        List<String> linesCombined = new ArrayList<>();
        int maxLineWidth = 0;//in chars
        String maxLine = "";
        {
            String splitChar = " ";
            String[] lines = text.split(splitChar);

            for (int i = 0; i < lines.length; i++) {
                int len = lines[i].length();
                String str = lines[i];

                if (i + 1 < lines.length) {
                    int lenNext = lines[i + 1].length();
                    if (lenNext == 1) {
                        len += 1 + 1;//+space
                        str += splitChar + lines[i + 1];
                    }
                }

                if (len > maxLineWidth) {
                    maxLineWidth = len;
                    maxLine = str;
                }
            }

            int currentLineLen = 0;
            linesCombined.add("");
            for (String line : lines) {
                int len = line.length();
                if (len <= 0) continue;

                if (currentLineLen + len <= maxLineWidth) {
                    //add to current line
                    if (currentLineLen > 0) {
                        String s = linesCombined.get(linesCombined.size() - 1);
                        linesCombined.set(linesCombined.size() - 1, s + splitChar + line);
                        currentLineLen += splitChar.length() + len;
                    } else {
                        linesCombined.set(linesCombined.size() - 1, line);
                        currentLineLen += len;
                    }

                } else {
                    linesCombined.add(line);
                    currentLineLen = len;
                }
            }
        }

        Paint paint = new Paint();
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        int narrowest = Math.min(width, height);
        setTextSizeForWidth(paint, narrowest - (narrowest / 10), maxLine);//-10%

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        canvas.drawColor(bgColor);

        if (backgroundOver != null) {
            backgroundOver.setBounds(0, 0, width, height);
            backgroundOver.draw(canvas);

            canvas.drawColor(bgColor2, PorterDuff.Mode.ADD);
        }

        float leading = paint.getFontMetrics().leading;

        float fontheight2 = -(paint.descent() + paint.ascent());

        float neededH = (linesCombined.size() * fontheight2) + (linesCombined.size() - 1 * leading);

        float x = (width * 0.5f);
        float y = (height * 0.5f) + (-neededH * 0.5f) + (fontheight2 * 1.0f);
        for (String line : linesCombined)
        {
            canvas.drawText(line, x, y, paint);
            y += fontheight2 + leading;
        }

        return image;
    }

    static Bitmap textAsBitmap(String text, float textSize, int textColor, int bgColor) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(bgColor);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    static float valueInAlphabet(char ch) {

        int temp = (int) Character.toUpperCase(ch);

        int temp_integer = 65; //for upper case
        if (temp <= 90 & temp >= 65) {
            float total = (90 - 65) + 1;
            float index = temp - temp_integer;
            return index / total;
        } else {
            //not in alphabet
            return 0.5f;
        }

    }
}