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

package com.aveeopen.comp.MediaControlsUI;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.aveeopen.Common.UtilsUI;
import com.aveeopen.comp.playback.MediaPlaybackServiceDefs;
import com.aveeopen.R;


class ThreeDotPopupWindow extends PopupWindow {

    private Handler handler;
    private ImageButton shuffleButton, repeatOnceButton, repeatAllButton;
    private ImageButton musicSys0Button, musicSys1Button;

    public ThreeDotPopupWindow(View anchor) {
        super(anchor.getContext(), null, 0, R.style.MyListPopupWindowDarkStyle);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 0) {
                    UtilsUI.dismissSafe(ThreeDotPopupWindow.this);
                }
                return false;
            }
        });

        View rootView = View.inflate(anchor.getContext(), R.layout.popup_media_controls_overflow, null);

        shuffleButton = (ImageButton) rootView.findViewById(R.id.btnShuffle);
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int mode = MediaControlsUI.onRequestShuffleMode.invoke(MediaPlaybackServiceDefs.SHUFFLE_NONE);
                int shuffleMode = mode;

                if (mode == MediaPlaybackServiceDefs.SHUFFLE_NONE)
                    shuffleMode = MediaPlaybackServiceDefs.SHUFFLE_NORMAL;
                else if (mode == MediaPlaybackServiceDefs.SHUFFLE_NORMAL)
                    shuffleMode = MediaPlaybackServiceDefs.SHUFFLE_NONE;

                MediaControlsUI.onSetShuffleMode.invoke(shuffleMode);

            }
        });

        repeatOnceButton = (ImageButton) rootView.findViewById(R.id.btnRepeatOnce);
        repeatOnceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int repeatMode = MediaControlsUI.onRequestRepeatMode.invoke(0);

                if (repeatMode != MediaPlaybackServiceDefs.REPEAT_CURRENT)
                    repeatMode = MediaPlaybackServiceDefs.REPEAT_CURRENT;
                else
                    repeatMode = MediaPlaybackServiceDefs.REPEAT_NONE;

                MediaControlsUI.onSetRepeatMode.invoke(repeatMode);
            }
        });

        repeatAllButton = (ImageButton) rootView.findViewById(R.id.btnRepeatAll);
        repeatAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int repeatMode = MediaControlsUI.onRequestRepeatMode.invoke(0);

                if (repeatMode != MediaPlaybackServiceDefs.REPEAT_ALL)
                    repeatMode = MediaPlaybackServiceDefs.REPEAT_ALL;
                else
                    repeatMode = MediaPlaybackServiceDefs.REPEAT_NONE;

                MediaControlsUI.onSetRepeatMode.invoke(repeatMode);
            }
        });

        musicSys0Button = (ImageButton) rootView.findViewById(R.id.btnPlaybackEngine0);
        musicSys0Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControlsUI.onSelectMusicSysAction.invoke(0);
            }
        });

        musicSys1Button = (ImageButton) rootView.findViewById(R.id.btnPlaybackEngine1);
        musicSys1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControlsUI.onSelectMusicSysAction.invoke(1);
            }
        });

        this.setContentView(rootView);

        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        // Closes the popup window when touch outside of it - when looses focus
        setOutsideTouchable(true);
        setFocusable(true);

        int musicSysIndex = MediaControlsUI.onRequestMusicSystemIndex.invoke(-1);
        onMusicSysChanged(musicSysIndex);
        onRepeatModeChanged(MediaControlsUI.onRequestRepeatMode.invoke(0));
        onShuffleModeChanged(MediaControlsUI.onRequestShuffleMode.invoke(MediaPlaybackServiceDefs.SHUFFLE_NONE));

        int yoffset = (int) anchor.getResources().getDimension(R.dimen.player_controls_volume_popup_offset);
        int xoffset = (int) anchor.getResources().getDimension(R.dimen.player_controls_volume_popup_offset_x);
        Rect displayFrame = new Rect();
        anchor.getWindowVisibleDisplayFrame(displayFrame);
        int displayHeight = displayFrame.height();
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);//

        this.showAtLocation(anchor, Gravity.BOTTOM | Gravity.START, location[0] - xoffset, (displayHeight - location[1]) + yoffset);

    }

    public void onRepeatModeChanged(int repeatMode) {
        int buttonColor = UtilsUI.getAttrColor(repeatAllButton, R.attr.buttonColorLight);

        if (repeatMode == MediaPlaybackServiceDefs.REPEAT_CURRENT) {
            repeatOnceButton.setColorFilter(UtilsUI.getAttrColor(repeatOnceButton, R.attr.highlight_color_2));
            repeatAllButton.setColorFilter(buttonColor);
        } else if (repeatMode == MediaPlaybackServiceDefs.REPEAT_ALL) {
            repeatOnceButton.setColorFilter(buttonColor);
            repeatAllButton.setColorFilter(UtilsUI.getAttrColor(repeatAllButton, R.attr.highlight_color_2));
        } else {
            repeatOnceButton.setColorFilter(buttonColor);
            repeatAllButton.setColorFilter(buttonColor);
        }
    }

    public void onShuffleModeChanged(int shuffleMode) {
        if (shuffleMode != MediaPlaybackServiceDefs.SHUFFLE_NONE)
            shuffleButton.setColorFilter( UtilsUI.getAttrColor(shuffleButton, R.attr.highlight_color_2));
        else
            shuffleButton.setColorFilter(UtilsUI.getAttrColor(shuffleButton, R.attr.buttonColorLight));
    }

    public void onMusicSysChanged(int musicSysIndex) {
        if (musicSysIndex == 0) {
            musicSys0Button.setColorFilter(UtilsUI.getAttrColor(musicSys0Button, R.attr.highlight_color_2));
            musicSys1Button.setColorFilter(UtilsUI.getAttrColor(musicSys1Button, R.attr.buttonColorLight));
        } else {
            musicSys0Button.setColorFilter(UtilsUI.getAttrColor(musicSys0Button, R.attr.buttonColorLight));
            musicSys1Button.setColorFilter(UtilsUI.getAttrColor(musicSys1Button, R.attr.highlight_color_2));
        }
    }
}
