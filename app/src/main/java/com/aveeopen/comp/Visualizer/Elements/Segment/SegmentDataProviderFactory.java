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

import com.aveeopen.Common.tlog;

public class SegmentDataProviderFactory {
    public static final String typeNameNone = "None";

    public static final String[] typeNames = new String[]{
            SegmentAudioWaveformData.typeName,
            SegmentAudioSpectrumData.typeName,
    };

    public static ISegmentDataProvider create(String typeName, ISegmentDataProvider reuseOld) {

        if(getTypeName(reuseOld).equals(typeName)) return reuseOld;

        switch (typeName) {
            case typeNameNone:
                return null;
            case SegmentAudioWaveformData.typeName:
                return new SegmentAudioWaveformData();
            case SegmentAudioSpectrumData.typeName:
                return new SegmentAudioSpectrumData();
        }

        tlog.w("unknown typeName: "+typeName);

        return reuseOld;//don't do anything to original
    }

    public static String getTypeName(ISegmentDataProvider instance)
    {
        if(instance == null) return typeNameNone;

        if(instance instanceof SegmentAudioWaveformData)
            return SegmentAudioWaveformData.typeName;
        else if(instance instanceof SegmentAudioSpectrumData)
            return SegmentAudioSpectrumData.typeName;

        tlog.w("unknown instance type");

        return "unk";
    }
}
