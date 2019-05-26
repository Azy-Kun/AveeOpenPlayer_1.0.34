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

package com.aveeopen.comp.Visualizer;

import android.graphics.PointF;

import com.aveeopen.comp.AlbumArt.AlbumArtRequest;
import com.aveeopen.comp.AlbumArt.ImageLoadedListener;
import com.aveeopen.comp.playback.AudioFrameData;

public interface InternalVisualizationDataProvider {

    AudioFrameData onRequestSoundVisualizationData(AudioFrameData outResult);

    String onRequestsMeasureText(String val);

    PointF onRequestMeasureVec2f(String val, PointF defaultValue, Float frameDataRmsValue);

    AlbumArtRequest onRequestsAlbumArtPath();

    void onRequestAlbumArtPathAndBitmap(
            ImageLoadedListener loadedListener,
            Integer targetBoundsWidth,
            Integer targetBoundsHeight,
            AlbumArtRequest albumartRequest);

}
