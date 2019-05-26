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

import android.graphics.PointF;

import com.aveeopen.Common.tlog;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;
import com.aveeopen.comp.Visualizer.Elements.Segment.ISegmentDataProvider;

import java.lang.ref.WeakReference;

public class Meter {

    private RenderState renderState;
    private float frameDataRmsValue;
    private WeakReference<ISegmentDataProvider> audioDataProviderWeak = new WeakReference<ISegmentDataProvider>(null);

    public Meter(RenderState renderState) {
        this.renderState = renderState;
    }

    float getCenterAligmentX() {
        return 0.0f;
    }

    float getCenterAligmentY() {
        return 0.0f;
    }

    //output range: 0.5 + (0 to screenW/H)
    public float measureScreenSpaceX(float val, boolean uniform) {
        if (uniform && renderState.getScreenHeight() < renderState.getScreenWidth())//multiply by smallest
            return (val * renderState.getScreenHeight()) + getCenterAligmentX();

        return (int) (val * renderState.getScreenWidth()) + getCenterAligmentX();
    }

    //output range: 0.5 + (0 to screenW/H)
    public float measureScreenSpaceY(float val, boolean uniform) {
        if (uniform && renderState.getScreenWidth() < renderState.getScreenHeight())//multiply by smallest
            return (val * renderState.getScreenWidth()) + getCenterAligmentY();

        return (int) (val * renderState.getScreenHeight()) + getCenterAligmentY();
    }

    public float measureLocalSpaceX(float val, boolean uniform, float localW, float localH) {
        if (uniform && localH < localW)//multiply by smallest
            return (val * localH);

        return (int) (val * localW);
    }

    public float measureLocalSpaceY(float val, boolean uniform, float localW, float localH) {
        if (uniform && localW < localH)//multiply by smallest
            return (val * localW);

        return (int) (val * localH);
    }
    //

    //output range: 0 - screenW/H
    public float measureScreenScaleX(float val, boolean uniform) {
        if (uniform && renderState.getScreenHeight() < renderState.getScreenWidth())//multiply by smallest
            return (val * renderState.getScreenHeight());

        return (int) (val * renderState.getScreenWidth());
    }

    //output range: 0 - screenW/H
    public float measureScreenScaleY(float val, boolean uniform) {
        if (uniform && renderState.getScreenWidth() < renderState.getScreenHeight())//multiply by smallest
            return (val * renderState.getScreenWidth());

        return (int) (val * renderState.getScreenHeight());
    }

    //
    //output range: 0 - 1
    public float measureScaleX(float val, boolean uniform) {
        if (uniform && renderState.getScreenHeight() < renderState.getScreenWidth())//multiply by smallest
            return val * (renderState.getScreenHeight() / renderState.getScreenWidth());

        return val;
    }

    //output range: 0 - 1
    public float measureScaleY(float val, boolean uniform) {
        if (uniform && renderState.getScreenWidth() < renderState.getScreenHeight())//multiply by smallest
            return val * (renderState.getScreenWidth() / renderState.getScreenHeight());

        return val;
    }

    public float measureScaleZ(float val, boolean uniform) {
        if (uniform && renderState.getScreenWidth() < renderState.getScreenHeight())//multiply by smallest
            return val * (renderState.getScreenWidth() / renderState.getScreenHeight());

        return val;
    }

    public String measureText(final String val) {
        String result = renderState.res.visualizationData.onRequestsMeasureText(val);

        if (result == null) {
            tlog.w("result null, " + val);
            return val;
        }

        return result;
    }

    public PointF measureVec2f(final String val) {
        if (val == null)
            return new PointF(0.0f, 0.0f);

        PointF result = renderState.res.visualizationData.onRequestMeasureVec2f(val, null, frameDataRmsValue);

        if (result == null) {
            tlog.w("result null, " + val);
            return new PointF(0.0f, 0.0f);
        }

        return result;
    }

    public void setFrameDataRmsValue(float rmsValue) {
        this.frameDataRmsValue = (this.frameDataRmsValue * 0.5f) + (rmsValue * 0.5f);
    }

    public void setAudioDataProvider(ISegmentDataProvider audioDataProvider) {
        this.audioDataProviderWeak = new WeakReference<>(audioDataProvider);
    }

    public ISegmentDataProvider getAudioDataProvider()
    {
        return audioDataProviderWeak.get();
    }
}
