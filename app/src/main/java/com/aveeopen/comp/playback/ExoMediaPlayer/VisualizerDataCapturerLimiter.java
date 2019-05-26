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

package com.aveeopen.comp.playback.ExoMediaPlayer;


public class VisualizerDataCapturerLimiter {

    private static final VisualizerDataCapturerLimiter instance = new VisualizerDataCapturerLimiter();
    private IVisualizerDataCapturer listener;
    private Object currentObj = null;

    public static VisualizerDataCapturerLimiter getInstance(Object obj) {
        if (instance.currentObj != obj)
            return null;

        return instance;
    }

    public static void assignInstance(Object obj, IVisualizerDataCapturer listener) {
        instance.currentObj = obj;
        instance.listener = listener;
    }

    public static IVisualizerDataCapturer getListener(Object obj) {
        VisualizerDataCapturerLimiter inst = getInstance(obj);
        if (inst == null) return null;

        return inst.listener;
    }
}
