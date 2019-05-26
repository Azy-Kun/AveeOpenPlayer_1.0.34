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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aveeopen.Common.Tuple2;
import com.aveeopen.Common.UtilsUI;
import com.aveeopen.ContextData;
import com.aveeopen.R;

class VolumePopupWindow extends PopupWindow {

    private Handler handler;
    private ImageButton muteButton;

    private ImageButton toggleExpandButton;
    private View expandableView;

    private SeekBar seekBarBalance;
    private TextView balanceText;

    private SeekBar seekBarCrossFade;
    private TextView crossFadeText;

    private float crossFadeTextSizeSmall;
    private float crossFadeTextSizeDefault;

    private SeekBar seekBarAudioVolume;
    private TextView audioVolumeText;

    private ImageButton eqButton;

    public VolumePopupWindow(LayoutInflater inflater, View anchor) {
        super(anchor.getContext(), null, 0, R.style.MyListPopupWindowDarkStyle);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 0) {
                    UtilsUI.dismissSafe(VolumePopupWindow.this);
                }

                return false;
            }
        });

        View rootView = View.inflate(anchor.getContext(), R.layout.popup_media_controls_volume, null);

        muteButton = (ImageButton) rootView.findViewById(R.id.btnVolumeMute);
        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControlsUI.onToggleMuteAction.invoke();

                handler.sendEmptyMessageDelayed(0, 900);
            }
        });

        {
            seekBarAudioVolume = (SeekBar) rootView.findViewById(R.id.seekBarVolume);
            audioVolumeText = (TextView) rootView.findViewById(R.id.txtVolume);

            final Tuple2<Integer, Integer> volume_volumeMax = MediaControlsUI.onRequestAudioVolumeState.invoke(new Tuple2<>(0, 0));

            onAudioVolumeChanged(volume_volumeMax.obj1, volume_volumeMax.obj2, false);

            seekBarAudioVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                    onAudioVolumeChanged(progress, volume_volumeMax.obj2, true);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    handler.removeMessages(0);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    handler.sendEmptyMessageDelayed(0, 900);

                }
            });
        }

        boolean audioViewExpanded = MediaControlsUI.onRequestAudioViewExpandedState.invoke(false);

        expandableView = rootView.findViewById(R.id.layoutExpandable);
        toggleExpandButton = (ImageButton) rootView.findViewById(R.id.btnExpand);

        if (audioViewExpanded) {
            toggleExpandButton.setImageResource(R.drawable.ic_minus);
            expandableView.setVisibility(ViewGroup.VISIBLE);
        } else {
            toggleExpandButton.setImageResource(R.drawable.ic_expand2up);
            expandableView.setVisibility(ViewGroup.GONE);
        }

        toggleExpandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (expandableView.getVisibility() == ViewGroup.VISIBLE) {
                    toggleExpandButton.setImageResource(R.drawable.ic_expand2up);
                    expandableView.setVisibility(ViewGroup.GONE);

                    MediaControlsUI.onSetAudioViewExpandedState.invoke(false);
                } else {
                    toggleExpandButton.setImageResource(R.drawable.ic_minus);
                    expandableView.setVisibility(ViewGroup.VISIBLE);

                    MediaControlsUI.onSetAudioViewExpandedState.invoke(true);
                }
            }
        });

        {
            seekBarBalance = (SeekBar) rootView.findViewById(R.id.seekBarVolumeBalance);
            balanceText = (TextView) rootView.findViewById(R.id.txtVolumeBalanceStatus);

            final Tuple2<Integer, Integer> balance_balanceMax = MediaControlsUI.onRequestAudioBalanceState.invoke(new Tuple2<>(0, 0));

            onAudioStereoBalanceChanged(balance_balanceMax.obj1, balance_balanceMax.obj2, false);

            seekBarBalance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                    onAudioStereoBalanceChanged(progress, balance_balanceMax.obj2, true);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        {
            seekBarCrossFade = (SeekBar) rootView.findViewById(R.id.seekBarCrossfade);
            crossFadeText = (TextView) rootView.findViewById(R.id.txtCrossfadeStatus);

            crossFadeTextSizeSmall = inflater.getContext().getResources().getDimensionPixelSize(R.dimen.textSizeM3);
            crossFadeTextSizeDefault = crossFadeText.getTextSize();

            final Tuple2<Integer, Integer> crossfade_crossfadeMax = MediaControlsUI.onRequestCrossFadeState.invoke(new Tuple2<>(0, 0));

            onCrossFadeChanged(crossfade_crossfadeMax.obj1, crossfade_crossfadeMax.obj2, false);

            seekBarCrossFade.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {

                    onCrossFadeChanged(progress, crossfade_crossfadeMax.obj2, true);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

        }

        eqButton = (ImageButton) rootView.findViewById(R.id.btnEq);
        eqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessageDelayed(0, 900);
                MediaControlsUI.onActionEq.invoke(new ContextData(v));
            }
        });

        this.setContentView(rootView);

        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        // Closes the popup window when touch outside of it - when looses focus
        setOutsideTouchable(true);
        setFocusable(true);

        boolean muteState = MediaControlsUI.onRequestVolumeMuteState.invoke(false);
        boolean audioEffectsActiveState = MediaControlsUI.onRequestAudioEffectsActiveState.invoke(false);

        onVolumeMuteChanged(muteState, audioEffectsActiveState);

        boolean eqState = MediaControlsUI.onRequestEqState.invoke(false);
        onEqStateChanged(eqState);

        {
            int yoffset = (int) anchor.getResources().getDimension(R.dimen.player_controls_volume_popup_offset);

            Rect displayFrame = new Rect();
            anchor.getWindowVisibleDisplayFrame(displayFrame);
            int displayHeight = displayFrame.height();

            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            this.showAtLocation(anchor, Gravity.BOTTOM | Gravity.START, location[0], (displayHeight - location[1]) + yoffset);
        }
    }

    private void onAudioVolumeChanged(int val, int valMax, boolean fromSeekBarEvent) {
        float vol = ((float) val / (float) valMax);

        int percent = Math.round(vol * 100.0f);
        audioVolumeText.setText(audioVolumeText.getResources().getQuantityString(
                R.plurals.audio_volume_x, percent, percent));

        if (!fromSeekBarEvent) {
            seekBarAudioVolume.setMax(valMax);
            seekBarAudioVolume.setProgress(val);
        } else {
            MediaControlsUI.onSetAudioVolume.invoke(val, valMax);
        }

    }

    private void onAudioStereoBalanceChanged(final int val, final int valMax, boolean fromSeekBarEvent) {
        int valHalfMax = valMax / 2;

        float vol = ((float) (val - valHalfMax) / (float) valHalfMax);

        int percent = Math.round(vol * 100.0f);
        balanceText.setText(balanceText.getResources().getQuantityString(
                R.plurals.audio_stereo_balance_x, percent, percent));

        if (!fromSeekBarEvent) {
            seekBarBalance.setMax(valMax);
            seekBarBalance.setProgress(val);
        } else {
            MediaControlsUI.onSetAudioStereoBalance.invoke(val, valMax);
        }

    }

    private void onCrossFadeChanged(final int val, final int valMax, boolean fromSeekbarEvent) {
        if (val == 0) {
            crossFadeText.setText(R.string.playback_crossfade_off);
            crossFadeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, crossFadeTextSizeDefault);
        } else if (val == 1) {
            crossFadeText.setText(R.string.playback_gapless);
            crossFadeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, crossFadeTextSizeSmall);
        } else {
            crossFadeText.setText(crossFadeText.getResources().getQuantityString(
                    R.plurals.playback_crossFade_x_sec, (val - 1), (val - 1)));
            crossFadeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, crossFadeTextSizeDefault);
        }

        if (!fromSeekbarEvent) {
            seekBarCrossFade.setMax(valMax);
            seekBarCrossFade.setProgress(val);
        } else {
            MediaControlsUI.onSetCrossFade.invoke(val, valMax);
        }
    }

    public void onVolumeMuteChanged(boolean state, boolean audioEffectsActiveState) {

        if (state)
            muteButton.setColorFilter(UtilsUI.getAttrColor(muteButton, R.attr.highlight_color_1));
        else
            muteButton.setColorFilter(UtilsUI.getAttrColor(muteButton, R.attr.buttonColorLight));
    }

    public void onEqStateChanged(boolean state) {
        if (state)
            eqButton.setColorFilter(UtilsUI.getAttrColor(muteButton, R.attr.highlight_color_2));
        else
            eqButton.setColorFilter(UtilsUI.getAttrColor(muteButton, R.attr.buttonColorLight));
    }
}
