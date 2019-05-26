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
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

public class MediaButtonEventReceiver extends BroadcastReceiver {

    private static final int MSG_LONGPRESS_TIMEOUT = 1;
    private static final int LONG_PRESS_DELAY = 1000;
    private static Handler mHandler;
    private static long mLastClickTime = 0;
    private static boolean mDown = false;
    private static boolean mLaunched = false;

    public MediaButtonEventReceiver() {

        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                switch (msg.what) {
                    case MSG_LONGPRESS_TIMEOUT:
                        if (!mLaunched) {
                            Context context = (Context) msg.obj;

                            notifyEvent(context, MediaPlaybackServiceDefs.HEADSET_ASSIST_ACTION);

                            mLaunched = true;
                        }
                        break;
                }

                return false;
            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String intentAction = intent.getAction();

        if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (event == null) {
                return;
            }

            int keycode = event.getKeyCode();
            int action = event.getAction();
            long eventtime = event.getEventTime();

            // single quick press: pause/resume.
            // double press: next track
            // long press: --start auto-shuffle mode.
            String command = null;
            switch (keycode) {
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    command = MediaPlaybackServiceDefs.STOP_ACTION;
                    break;
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    command = MediaPlaybackServiceDefs.TOGGLE_PAUSE_ACTION;
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    command = MediaPlaybackServiceDefs.NEXT_ACTION;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    command = MediaPlaybackServiceDefs.PREVIOUS_ACTION;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    command = MediaPlaybackServiceDefs.PAUSE_ACTION;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    command = MediaPlaybackServiceDefs.PLAY_ACTION;
                    break;
            }

            if (command != null) {
                if (action == KeyEvent.ACTION_DOWN) {
                    if (mDown) {
                        if ((MediaPlaybackServiceDefs.TOGGLE_PAUSE_ACTION.equals(command) ||
                                MediaPlaybackServiceDefs.PLAY_ACTION.equals(command))
                                && mLastClickTime != 0
                                && eventtime - mLastClickTime > LONG_PRESS_DELAY) {
                            mHandler.sendMessage(
                                    mHandler.obtainMessage(MSG_LONGPRESS_TIMEOUT, context));
                        }
                    } else if (event.getRepeatCount() == 0) {
                        // only consider the first event in a sequence, not the repeat events,
                        // so that we don't trigger in cases where the first event went to
                        // a different app (e.g. when the user ends a phone call by
                        // long pressing the headset button)

//                        // The service may or may not be running, but we need to send it
//                        // a command.
//                        Intent i = new Intent(context, MediaPlaybackService.class);
//                        i.setAction(MediaPlaybackService.SERVICECMD);

                        if (keycode == KeyEvent.KEYCODE_HEADSETHOOK && eventtime - mLastClickTime < 300) {

//                            i.putExtra(MediaPlaybackService.CMDNAME, MediaPlaybackServiceDefs.NEXT_ACTION);
//                            context.startService(i);

                            notifyEvent(context, MediaPlaybackServiceDefs.NEXT_ACTION);
                            mLastClickTime = 0;

                        } else {

//                            i.putExtra(MediaPlaybackService.CMDNAME, command);
//                            context.startService(i);

                            notifyEvent(context, command);
                            mLastClickTime = eventtime;

                        }

                        mLaunched = false;
                        mDown = true;
                    }
                } else {
                    mHandler.removeMessages(MSG_LONGPRESS_TIMEOUT);
                    mDown = false;
                }
                if (isOrderedBroadcast()) {//currently processing an ordered broadcast?
                    abortBroadcast();
                }
            }
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
