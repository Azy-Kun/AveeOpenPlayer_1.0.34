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

import com.aveeopen.Common.tlog;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;

import junit.framework.Assert;
import java.util.ArrayList;
import java.util.List;

import mdesl.graphics.glutils.FrameBuffer;

public abstract class ElementGroup extends Element {

    private List<Element> childList = new ArrayList<>();

    public void addChild(Element child, int location) {
        Assert.assertEquals(null, child.parent);
        childList.add(location, child);
        child.parent = this;
    }

    public void addChildAtBeginning(Element child) {
        Assert.assertEquals(null, child.parent);
        childList.add(0, child);
        child.parent = this;
    }

    public void addChildAtEnd(Element child) {
        Assert.assertEquals(null, child.parent);
        childList.add(child);
        child.parent = this;
    }

    public void removeChild(Element child) {
        Assert.assertEquals(this, child.parent);

        childList.remove(child);
        child.parent = null;
    }

    @Override
    protected void onCreateGLResources(RenderState renderData) {
        for (Element e : childList) {
            e.reCreateGLResources(renderData);
        }
    }

    @Override
    public void onEarlyUpdate(RenderState renderData, FrameBuffer resultFB) {
        super.onEarlyUpdate(renderData, resultFB);
        for (Element e : childList) {
            e.onEarlyUpdate(renderData, resultFB);
        }
    }

    protected void renderChilds(RenderState renderData, FrameBuffer resultFB) {
        for (Element e : childList) {
            e.onRender(renderData, resultFB);
        }
    }

    @Override
    public boolean getCustomization(Element.CustomizationList customization, int customizationIndex) {
        super.getCustomization(customization, 0);

        if (customization == null)
            return false;

        for (Element e : childList) {
            if (!e.getCustomization(customization, 0))
                return false;
        }

        return true;
    }

    //return: false - failed
    @Override
    public boolean setCustomization(Element.CustomizationList customization, Integer[] dataCounter) {
        if (customization == null)
            return false;

        super.setCustomization(customization, dataCounter);

        for (Element e : childList) {
            if (!e.setCustomization(customization, dataCounter))
                return false;//failed
        }

        return true;
    }

    public boolean setCustomization(Element.CustomizationList customization) {
        if (customization == null)
            return false;

        Integer[] dataCounter = new Integer[1];
        dataCounter[0] = 0;
        boolean result = setCustomization(customization, dataCounter);

        if (result) {
            if (customization.dataCount() != (int)dataCounter[0])
                tlog.w("elements changed");
        } else {
            tlog.w("setCustomization failed");
        }

        return result;
    }

}
