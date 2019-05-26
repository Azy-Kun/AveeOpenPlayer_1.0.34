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

package com.aveeopen.comp.Visualizer.Elements.Segment;

import com.aveeopen.comp.Visualizer.Dsp.DspWindows;
import com.aveeopen.comp.Visualizer.Elements.Element;
import com.aveeopen.comp.Visualizer.Elements.IFrameDataProvider;
import com.aveeopen.comp.Visualizer.InternalVisualizationDataProvider;
import com.aveeopen.comp.playback.AudioFrameData;

public class SegmentAudioWaveformData implements ISegmentDataProvider, IFrameDataProvider {

    public static final String typeName = "Waveform";//SegmentAudioWaveformData

    private AudioFrameData visData = null;
    private float[] barVals2 = new float[2];
    private float rms = 0.0f;
    private int sampleInCountPower = 9;
    private int sampleOutCountPower = 8;

    public SegmentAudioWaveformData() {
    }

    public void setSampleInCountPower(int sampleInCountPower) {
        this.sampleInCountPower = sampleInCountPower;
    }

    public void setSampleOutCountPower(int sampleOutCountPower) {
        this.sampleOutCountPower = sampleOutCountPower;
    }

    public void process(InternalVisualizationDataProvider visualisationData) {
        int dataCount = Math.min(Math.max(1 << sampleInCountPower, 32), 1024);//512
        int sampleOutCount = Math.min(Math.max(1 << sampleOutCountPower, 8), 512);//256

        visData = AudioFrameData.createReuse(visData, dataCount);

        AudioFrameData visDataResult = visualisationData.onRequestSoundVisualizationData(visData);

        short[] buf;

        if (visDataResult != null) {
            buf = visDataResult.pcmBuffer;
            rms = visDataResult.rms;
        } else {
            buf = new short[2];
            rms = 0.0f;
        }

        {
            if (barVals2.length != sampleOutCount)
                barVals2 = new float[sampleOutCount];
        }

        float smooth = 0.4f;

        float dataPerBar = (float) buf.length / (float) barVals2.length;

        for (int i = 0; i < barVals2.length; i++) {
            barVals2[i] = barVals2[i] * smooth * 1.0f;
        }

        if (dataPerBar > 1.0f) {

            for (int i = 0; i < buf.length; i++) {

                float multiplier = DspWindows.kaiserWindow(i, buf.length, 0.8f);

                float val = (float) (buf[i]);
                val = val * multiplier;

                int d = (int) Math.floor((float) i / dataPerBar);
                if (d < barVals2.length)
                    barVals2[d] += val;
            }

            for (int i = 0; i < barVals2.length; i++) {
                barVals2[i] /= dataPerBar;
            }

        } else {

            for (int d = 0; d < barVals2.length; d++) {
                int i  = (int) Math.floor((float)d * dataPerBar);

                float multiplier = DspWindows.kaiserWindow(i, buf.length, 0.8f);

                float val = (float) (buf[i]);
                val = val * multiplier;

                barVals2[d] += val;

                barVals2[d] *= 0.5f;
            }
        }


    }

    @Override
    public float[] getFrameValues() {
        return barVals2;
    }

    @Override
    public float getRms() {
        return rms;
    }

    @Override
    public void onApplyCustomization(Element.CustomizationData customizationData) {
        setSampleInCountPower(customizationData.getPropertyInt("sampleInCountPower", sampleInCountPower));
        setSampleOutCountPower(customizationData.getPropertyInt("sampleOutCountPower", sampleOutCountPower));
    }

    @Override
    public void onReadCustomization(Element.CustomizationData outCustomizationData) {
        outCustomizationData.putPropertyInt("sampleInCountPower", sampleInCountPower, "i 5 10");
        outCustomizationData.putPropertyInt("sampleOutCountPower", sampleOutCountPower, "i 3 9" );
    }
}
