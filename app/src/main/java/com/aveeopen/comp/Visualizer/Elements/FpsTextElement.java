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

public class FpsTextElement extends TextElement {

    private static int color2 = 0x80A00000;
    private static int textBlendMode = 4;

    public FpsTextElement() {

        this.setColor(color2);
        this.setBlendMode(textBlendMode);
        this.setFontSize(28);
        this.setText("$artist");
        this.setPosition(0.05f, 1.0f);
        this.setLocalPosition(0.0f, 1.7f);
        this.setPositionUniform(true, false);

        this.setText("$fps");
    }
}
