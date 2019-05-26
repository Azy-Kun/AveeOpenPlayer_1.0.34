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

import mdesl.graphics.ITexture;

public interface IAtlasTexture {

    void dispose();

    int getWidth();

    int getHeight();

    float translateU(float u);

    float translateV(float v);

    float translateW(float tw);

    float translateW();

    ITexture getTexture2D();//return null, if not supporting

    IAtlasTexture getSub(int x, int y, int w, int h);

    IAtlasTexture getSub(float u0, float v0, float uw, float vh);

}
