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

import com.aveeopen.comp.Visualizer.Elements.Segment.ISegmentDataProvider;
import com.aveeopen.comp.Visualizer.Elements.Segment.SegmentDataProviderFactory;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;

import mdesl.graphics.glutils.FrameBuffer;

public class AudioDataProviderElement extends Element {

    private ISegmentDataProvider segmentDataProvider = null;

    public void setSegmentDataProvider(ISegmentDataProvider segmentDataProvider) {
        this.segmentDataProvider = segmentDataProvider;
    }


    @Override
    protected void onApplyCustomization(CustomizationData customizationData) {
        super.onApplyCustomization(customizationData);

        {
            CustomizationData segmentRendererCutom1 = customizationData.getChild("sampleProvider");
            segmentDataProvider = SegmentDataProviderFactory.create(segmentRendererCutom1.getChildTypeValue(), segmentDataProvider);
            if (segmentDataProvider != null)
                segmentDataProvider.onApplyCustomization(segmentRendererCutom1);
        }
    }

    @Override
    protected void onReadCustomization(CustomizationData outCustomizationData) {
        super.onReadCustomization(outCustomizationData);
        outCustomizationData.setCustomizationName("Sample Provider");

        {
            CustomizationData segmentRendererCutom1 = outCustomizationData.putChild("sampleProvider", SegmentDataProviderFactory.getTypeName(segmentDataProvider), SegmentDataProviderFactory.typeNames);
            if (segmentDataProvider != null) segmentDataProvider.onReadCustomization(segmentRendererCutom1);
        }
    }

    @Override
    public void onCreateGLResources(RenderState renderData) {
        super.onCreateGLResources(renderData);
    }

    @Override
    public void onRender(RenderState renderData, FrameBuffer resultFB) {
        super.onRender(renderData, resultFB);
    }

    @Override
    public void onEarlyUpdate(RenderState renderData, FrameBuffer resultFB) {
        super.onEarlyUpdate(renderData, resultFB);

        if(segmentDataProvider != null)
            segmentDataProvider.process(renderData.res.visualizationData);

        renderData.res.meter.setAudioDataProvider(segmentDataProvider);
    }
}
