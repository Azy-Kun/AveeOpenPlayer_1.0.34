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

import com.NAudio.FastFourierTransform;
import com.aveeopen.comp.playback.AudioFrameData;
import com.aveeopen.comp.Visualizer.Elements.Element;
import com.aveeopen.comp.Visualizer.Dsp.DspCurves;
import com.aveeopen.comp.Visualizer.Dsp.DspWindows;
import com.aveeopen.comp.Visualizer.Elements.IFrameDataProvider;
import com.aveeopen.comp.Visualizer.InternalVisualizationDataProvider;

public class SegmentAudioSpectrumData implements ISegmentDataProvider, IFrameDataProvider {

    public static final String typeName = "Spectrum";//SegmentAudioSpectrumData

    private AudioFrameData visData = null;
    private int sampleInCountPower = 9;
    private int dataCount = 512;
    private double[] fftMag = new double[1];

    private int datamode = 0;

    private float loFreq;
    private float hiFreq;
    private int barsCount = 64;
    private float[] barSmoothValues = new float[1];
    private float[] barValues = new float[1];
    private float[] barFreq = new float[1];

    private final float smoothFactorInc = 0.6f;
    private final float smoothFactor = 0.6f;//1.0f - no smooth
    private final float rangeTarget = 50.0f;
    private final float rangeSmoothFactor = 0.1f;//1.0f - no smooth

    private int rmsTargetBar = (64 / 16);
    private float windowedRms = 0.0f;
    private float rangeHiSmooth = 0.0f;
    private float rangeLoSmooth = 0.0f;

    public SegmentAudioSpectrumData() {
        setSampleOutCount(64);
    }

    public void setSampleInCountPower(int sampleInCountPower) {
        this.sampleInCountPower = sampleInCountPower;
        this.dataCount = Math.min(Math.max(1 << sampleInCountPower, 32), 1024);//512
    }

    public void setSampleOutCount(int sampleOutCount) {
        setSampleOutCount(sampleOutCount, 20.0f, 18000.0f);//22050.0f;
    }

    public void setSampleOutCount(int sampleOutCount, float loFreq, float hiFreq) {
        this.loFreq = loFreq;
        this.hiFreq = hiFreq;
        this.barsCount = sampleOutCount;
        barFreq = new float[this.barsCount];
        updateBandsFreq(this.barsCount, loFreq, hiFreq);

        setBeatBart(6);
    }

    public void setBeatBart(int barsIndex) {
        rmsTargetBar = barsIndex;
    }

    public void setDataMode(int datamode) {
        this.datamode = datamode;
    }

    public void process(InternalVisualizationDataProvider visualisationData) {
        visData = AudioFrameData.createReuse(visData, dataCount);
        AudioFrameData visDataResult = visualisationData.onRequestSoundVisualizationData(visData);

        short[] buf;
        int sampleRate;

        if (visDataResult != null) {
            buf = visDataResult.pcmBuffer;
            sampleRate = visDataResult.sampleRate;
        } else {
            buf = new short[2];
            sampleRate = 44100;
        }

        if (barValues.length != barsCount)
            barValues = new float[barsCount];

        if (barSmoothValues.length != barValues.length)
            barSmoothValues = new float[barValues.length];

        float windowedRmsAvg = 0.0f;

        FastFourierTransform.Complex[] fftResult = new FastFourierTransform.Complex[buf.length];
        for (int i = 0; i < fftResult.length; i++) {
            float signal = buf[i];
            float multiplier = DspWindows.hannWindow(i, fftResult.length);
            float val = (signal / 1.0f) * multiplier;
            windowedRmsAvg += (val * val);
            fftResult[i] = new FastFourierTransform.Complex(val, 0.0f);
        }

        //
        if (fftResult.length > 0)
            windowedRmsAvg = (float) Math.sqrt(windowedRmsAvg / (float) fftResult.length);
        windowedRms = windowedRmsAvg;

        //
        FastFourierTransform.FFT(true, fftResult);

        float[] rangeOut = new float[2];
        fillBarValues(barValues, fftResult, sampleRate, rangeOut);

        float rangeMax = 1000.0f;
        if (rangeOut[0] < -rangeMax) rangeOut[0] = -rangeMax;
        if (rangeOut[0] > rangeMax) rangeOut[0] = rangeMax;
        if (rangeOut[1] < -rangeMax) rangeOut[1] = -rangeMax;
        if (rangeOut[1] > rangeMax) rangeOut[1] = rangeMax;

        rangeLoSmooth = (rangeLoSmooth * (1.0f - rangeSmoothFactor)) + (rangeOut[0] * rangeSmoothFactor);
        rangeHiSmooth = (rangeHiSmooth * (1.0f - rangeSmoothFactor)) + (rangeOut[1] * rangeSmoothFactor);

        float rangeMul = rangeHiSmooth - rangeLoSmooth;
        if (rangeMul < 1.0f) rangeMul = 1.0f;
        rangeMul = rangeTarget / rangeMul;

        for (int j = 0; j < barValues.length; j++) {

            float val = barValues[j];

            if (datamode == 3) {
                val = (val - rangeLoSmooth) * rangeMul;
            } else {
                val = (val - rangeLoSmooth) * 15.0f;
            }

            if (val > barSmoothValues[j])
                barSmoothValues[j] = (barSmoothValues[j] * (1.0f - smoothFactorInc)) + (val * smoothFactorInc);
            else
                barSmoothValues[j] = (barSmoothValues[j] * (1.0f - smoothFactor)) + (val * smoothFactor);

        }
    }

    private void fillBarValues(float[] barVals, FastFourierTransform.Complex[] fftResult, int samplingRate, float[] rangeOut) {
        if(barVals.length < 4) return;

        rangeOut[0] = 9999990.0f;//lo
        rangeOut[1] = -9999990.0f;//hi

        for (int i = 0; i < barVals.length; i++) {
            barVals[i] = 0.0f;
        }

        //fftResult to fftMag
        if(fftMag.length != fftResult.length)
            fftMag = new double[fftResult.length];

        for (int j = 0; j < fftMag.length; j++) {

            float mag = (float) Math.sqrt(fftResult[j].re() * fftResult[j].re() + fftResult[j].im() * fftResult[j].im());

            float weight;
            if (datamode == 0) {
                double freq = DspCurves.freqd(j, fftMag.length * 0.5f, samplingRate);//*8
                weight = (float) DspCurves.myAWeight(freq);
            } else {
                double freq = DspCurves.freqd(j, fftMag.length, samplingRate);//*8
                weight = (float) DspCurves.myAWeight(freq);
            }

            fftMag[j] = mag * weight;
        }

        //fftMag to barVals
        int fftSize = fftResult.length;

        final float df = (float)samplingRate / (float)fftSize;
        final float valMul = (2.0f / (float)samplingRate) * 200.0f;//600.0f;

        int fftBinIndx = 0;
        int barIndx = 0;
        float freqLast = 0.0f;
        float freqMultiplier;

        while (fftBinIndx <= (fftSize / 2) && barIndx < barVals.length)
        {
            float freqLin = ((float)fftBinIndx + 0.5f) * df;
            float freqLog = barFreq[barIndx];
            int fftBinI = fftBinIndx;
            int barI = barIndx;

            if(freqLin <= freqLog)
            {
                freqMultiplier = (freqLin - freqLast);
                freqLast = freqLin;
                fftBinIndx += 1;
            }
            else
            {
                freqMultiplier = (freqLog - freqLast);
                freqLast = freqLog;
                barIndx += 1;
            }

            barVals[barI] += freqMultiplier * fftMag[fftBinI] * valMul;
        }

        //quick fix
        barVals[0] = Math.min(barVals[0], barVals[1]);
        barVals[barVals.length-1] = Math.min(barVals[barVals.length-1], barVals[barVals.length-2]);

        //barVals post process
        for (int i = 0; i < barVals.length; i++) {
            barVals[i] =  barVals[i] * 2.0f;//(float) melSpectrumData[i];

            if (barVals[i] < rangeOut[0])
                rangeOut[0] = barVals[i];

            if (barVals[i] > rangeOut[1])
                rangeOut[1] = barVals[i];
        }
    }

    @Override
    public float[] getFrameValues() {
        return barSmoothValues;
    }

    @Override
    public float getRms() {
        //int bar = Math.min(rmsTargetBar, barValues.length-1);
        //return barValues[bar]* 15.0f;
        //return barRmsAvg * 6.0f;
        return windowedRms * 2.0f;
    }

    private void updateBandsFreq(int bandCount, float loFreq, float hiFreq)
    {
        float freqScaleOff = 800.0f;

        float step = (float)((Math.log(hiFreq / (loFreq+(freqScaleOff))) / bandCount) / Math.log(2.0));
        barFreq[0] = loFreq + freqScaleOff;
        float stepMul = (float)Math.pow(2.0, step);
        for (int i = 1; i < bandCount; ++i)
        {
            barFreq[i] = ((barFreq[i - 1] * stepMul * 1.0f));
            barFreq[i - 1] += -freqScaleOff;
        }
    }

    @Override
    public void onApplyCustomization(Element.CustomizationData customizationData) {
        setSampleInCountPower(customizationData.getPropertyInt("sampleInCountPower", sampleInCountPower));
        setSampleOutCount(
                customizationData.getPropertyInt("sampleOutCount", barsCount),
                customizationData.getPropertyFloat("lowerHz", loFreq),
                customizationData.getPropertyFloat("higherHz", hiFreq));


    }

    @Override
    public void onReadCustomization(Element.CustomizationData outCustomizationData) {
        outCustomizationData.putPropertyInt("sampleInCountPower", sampleInCountPower, "i 5 10");
        outCustomizationData.putPropertyInt("sampleOutCount", barsCount, "i 8 512" );

        outCustomizationData.putPropertyFloat("lowerHz", loFreq, "f 20.0 1000.0" );
        outCustomizationData.putPropertyFloat("higherHz", hiFreq, "f 1000.0 20000.0" );
    }
}
