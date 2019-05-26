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

package com.aveeopen.comp.playback;

public interface MediaPlaybackServiceDefs {

    Class<?> MediaServiceClass = MediaPlaybackService.class;

    public static final String NOTIFICATION_CHANNEL_NAME = "Playback Service";

    //service receive intents
    String AUDIO_BECOMING_NOISY_ACTION = "AUDIO_BECOMING_NOISY_ACTION";
    String NONE_ACTION = "NONE_ACTION";
    String STOP_ACTION = "STOP_ACTION";
    String PLAY_ACTION = "PLAY_ACTION";
    String TOGGLE_PAUSE_ACTION = "TOGGLE_PAUSE_ACTION";
    String PAUSE_ACTION = "PAUSE_ACTION";
    String PREVIOUS_ACTION = "PREVIOUS_ACTION";
    String NEXT_ACTION = "NEXT_ACTION";
    String EXIT_ACTION = "EXIT_ACTION";
    String ACTIVITY_AND_SERVICE_EXIT_ACTION = "ACTIVITY_AND_SERVICE_EXIT_ACTION";
    String TIMEOUT_DISABLE_ACTION = "TIMEOUT_DISABLE_ACTION";
    String HEADSET_ASSIST_ACTION = "HEADSET_ASSIST_ACTION";
    String APP_WIDGET_UPDATE_ACTION = "APP_WIDGET_UPDATE_ACTION";
    String SEEK_ACTION = "SEEK_ACTION";
    String REPEAT_MODE_ACTION = "REPEAT_MODE_ACTION";
    String VIDEO_SCALING_MODE_ACTION = "VIDEO_SCALING_MODE_ACTION";
    String VOLUME_PERCENTAGE_ACTION = "VOLUME_PERCENTAGE_ACTION";
    String VOLUME_ACTION = "VOLUME_ACTION";
    String SET_MUTE_ACTION = "SET_MUTE_ACTION";
    String TOGGLE_MUTE_ACTION = "TOGGLE_MUTE_ACTION";
    String VOLUME_STEREO_BALANCE_ACTION = "VOLUME_STEREO_BALANCE_ACTION";
    String CROSS_FADE_VALUE_ACTION = "CROSS_FADE_VALUE_ACTION";
    String PLAY_DATA_SOURCE_ACTION = "PLAY_DATA_SOURCE_ACTION";

    String EXTRA_ARG_1 = "EXTRA_ARG_1";
    String EXTRA_ARG_2 = "EXTRA_ARG_2";
    String EXTRA_ARG_3 = "EXTRA_ARG_3";
    String EXTRA_ARG_4 = "EXTRA_ARG_4";

    int CLEAR = -1;
    int FIRST = 0;
    int NOW = 1;
    int NEXT = 2;
    int LAST = 3;

    int SHUFFLE_NONE = 0;
    int SHUFFLE_NORMAL = 1;

    int REPEAT_NONE = 0;
    int REPEAT_CURRENT = 1;
    int REPEAT_ALL = 2;
}

