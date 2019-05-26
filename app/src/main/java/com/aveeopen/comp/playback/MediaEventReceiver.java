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

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class MediaEventReceiver extends BroadcastReceiver {

    public MediaEventReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            notifyEvent(context, MediaPlaybackServiceDefs.AUDIO_BECOMING_NOISY_ACTION);
        }
    }

    void notifyEvent(Context context, String action) {
        Intent playPause = new Intent(action);
        ComponentName service = new ComponentName(context, MediaPlaybackServiceDefs.MediaServiceClass);
        playPause.setComponent(service);
        //peekService(context, playPause);

        context.startService(playPause);
    }
}
