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

public class SegmentRendererFactory {

    public static final String typeNameNone = "None";

    public static final String[] typeNames = new String[]{
            typeNameNone,
            SegmentRendererBar.typeName,
            SegmentRendererLine.typeName,
            SegmentRendererSharpBar.typeName
    };

    public static ISegmentRenderer create(String typeName, ISegmentRenderer reuseOld) {

        if(getTypeName(reuseOld).equals(typeName)) return reuseOld;

        switch (typeName) {
            case typeNameNone:
                return null;
            case SegmentRendererBar.typeName:
                return new SegmentRendererBar();
            case SegmentRendererLine.typeName:
                return new SegmentRendererLine();
            case SegmentRendererSharpBar.typeName:
                return new SegmentRendererSharpBar();
        }

        tlog.w("unknown typeName: "+typeName);

        return reuseOld;//don't do anything to original
    }

    public static String getTypeName(ISegmentRenderer instance)
    {
        if(instance == null) return typeNameNone;

        if(instance instanceof SegmentRendererBar)
            return SegmentRendererBar.typeName;
        else if(instance instanceof SegmentRendererLine)
            return SegmentRendererLine.typeName;
        else if(instance instanceof SegmentRendererSharpBar)
            return SegmentRendererSharpBar.typeName;

        tlog.w("unknown instance type");

        return "unk";
    }
}
