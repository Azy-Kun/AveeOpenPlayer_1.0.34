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

import com.aveeopen.comp.Visualizer.Graphic.RenderState;

import mdesl.graphics.glutils.FrameBuffer;

public class RootElement extends ElementGroup {

    private int compareIdentifier;
    private IFrameDataProvider frameDataProvider;

    public RootElement(int compareIdentifier) {
        this.compareIdentifier = compareIdentifier;
    }

    public RootElement(int compareIdentifier, Element childToAdd) {
        this.compareIdentifier = compareIdentifier;
        this.addChildAtEnd(childToAdd);
    }

    public int getIdentifier() {
        return compareIdentifier;
    }

    public IFrameDataProvider getFrameDataProvider() {
        return frameDataProvider;
    }

    public RootElement setFrameDataProvider(IFrameDataProvider frameDataProvider) {
        this.frameDataProvider = frameDataProvider;
        return this;
    }

    @Override
    public int hashCode() {
        return (compareIdentifier * 45) + 47;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RootElement)) return false;
        RootElement ob = (RootElement) o;
        return this.compareIdentifier == ob.compareIdentifier;
    }

    @Override
    protected void onCreateGLResources(RenderState renderData) {
        super.onCreateGLResources(renderData);
    }

    @Override
    public void onRender(RenderState renderData, FrameBuffer resultFB) {
        super.onRender(renderData, resultFB);
        renderChilds(renderData, resultFB);
    }

    public int readThemeCustomizationData(Element.CustomizationList customization) {
        if (getCustomization(customization, 0))
            return getIdentifier();

        return -1;
    }
}
