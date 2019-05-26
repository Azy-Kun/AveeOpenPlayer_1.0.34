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

package com.aveeopen.comp.SleepTimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.Utils;
import com.aveeopen.Common.UtilsUI;
import com.aveeopen.R;

public class SleepTimerDialog extends DialogFragment {

    public static WeakEvent3<Boolean /*enabled*/, Integer /*minutes*/, Boolean /*playLastSongToEnd*/> onSleepTimerUISubmit = new WeakEvent3<>();
    public static WeakEventR<SleepTimerConfig> onSleepTimerUIRequestSleepTimerConfig = new WeakEventR<>();
    public static WeakEventR<Integer> onSleepTimerUIRequestRemainingSeconds = new WeakEventR<>();

    private NumberPicker nrM;
    private NumberPicker nrH;
    private ImageButton btnToggle;
    private TextView txtStatus;
    private boolean timerOn = false;
    private int secondsRemaining = 0;
    private int colorOn;
    private int colorOff;
    private Runnable timerTask;

    public static SleepTimerDialog createAndShowSleepTimerDialog(FragmentManager fragmentManager) {
        SleepTimerDialog dialog = new SleepTimerDialog();
        dialog.show(fragmentManager, "SleepTimerDialog");
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View rootView = View.inflate(getActivity(), R.layout.dialog_sleep_timer, null);
        builder.setView(rootView);

        colorOn = UtilsUI.getAttrColor(this.getActivity().getTheme(), R.attr.highlight_color_1);
        colorOff = this.getResources().getColor(R.color.black_alpha_1);

        builder.setTitle(R.string.dialog_sleep_timer_title);

        nrM = (NumberPicker) rootView.findViewById(R.id.numberPickerM);
        nrH = (NumberPicker) rootView.findViewById(R.id.numberPickerH);
        btnToggle = (ImageButton) rootView.findViewById(R.id.btnToggle);
        txtStatus = (TextView) rootView.findViewById(R.id.txtStatus);

        timerTask = new Runnable() {
            @Override
            public void run() {

                secondsRemaining = onSleepTimerUIRequestRemainingSeconds.invoke(0);
                updateTxt();
                txtStatus.postDelayed(timerTask, 1000);
            }
        };

        SleepTimerConfig timerConfig = onSleepTimerUIRequestSleepTimerConfig.invoke(null);

        if (timerConfig == null)
            timerConfig = new SleepTimerConfig();

        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timerOn)
                    updateBtn(false);
                else
                    updateBtn(true);
                configure();
            }
        });

        updateBtn(timerConfig.enabled);

        int hours = timerConfig.minutes / 60;
        int min60 = timerConfig.minutes - (hours * 60);

        nrM.setMaxValue(59);
        nrM.setMinValue(0);
        nrM.setValue(min60);

        nrH.setMaxValue(12);
        nrH.setMinValue(0);
        nrH.setValue(hours);

        nrM.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                configure();
            }
        });
        nrH.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                configure();
            }
        });

        builder.setPositiveButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });


        txtStatus.postDelayed(timerTask, 0);

        return builder.create();
    }

    int getMinutes() {
        return nrM.getValue() + (nrH.getValue() * 60);
    }

    void updateBtn(boolean state) {
        if (state) {
            btnToggle.setColorFilter(colorOn);
        } else {
            btnToggle.setColorFilter(colorOff);
        }

        timerOn = state;

        updateTxt();
    }

    void updateTxt() {
        if (timerOn) {
            txtStatus.setText(txtStatus.getResources().getText(R.string.dialog_timer_remaining) + " " + Utils.getDurationStringHHMMSS(secondsRemaining, false));
        } else {
            txtStatus.setText(R.string.dialog_timer_off);
        }
    }

    void configure() {
        onSleepTimerUISubmit.invoke(timerOn, getMinutes(), false);
    }
}