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

package com.aveeopen.comp.EqualizerUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.Utils;
import com.aveeopen.Common.VerticalSeekBar;
import com.aveeopen.Common.tlog;
import com.aveeopen.R;
import com.triggertrap.seekarc.SeekArc;

import java.util.LinkedList;
import java.util.List;

public class EqualizerDialog extends DialogFragment {

    public static WeakEvent1<EqualizerUIDesc> onReceiveEqualizerDescChanged = new WeakEvent1<>();

    public static WeakEvent2<EqualizerUISettings /*equalizerSettings*/, EqualizerUIDesc /*desc*/> onSubmitEqualizerSettings = new WeakEvent2<>();
    public static WeakEventR<EqualizerUIDesc> onRequestEqualizerDesc = new WeakEventR<>();

    private List<Object> listenerRefHolder = new LinkedList<>();
    private Switch switchEnable;
    private Spinner spinnerPresets;
    private ScrollView scrollView1;
    private HorizontalScrollView scrollView2;

    private ViewGroup linearLayoutBars;
    private VerticalSeekBar[] bandSeekBars;
    private TextView[] bandTexts;
    private SeekArc seekArcBass, seekArcTreble;
    private TextView textBass, textTreble;
    //private TextView textVirtualizer, textVirtualizerValue;
    private SeekBar seekBarVirtualizer;

    private boolean spinnerPresetsEventFromUser = false;
    private static int seekArcMax = 30;
    private EqualizerUIDesc equalizerUIDesc = null;
    private int bandBarMax = 30;

    private EQPreset currentBands = EQPreset.clone(EQPreset.empty);
    private float bassValue;
    private float trebleValue;

    private boolean preventSettingsUpdate = false;

    View.OnTouchListener seekBarTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    scrollView1.requestDisallowInterceptTouchEvent(true);
                    scrollView2.requestDisallowInterceptTouchEvent(true);
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    break;
                }
            }
            return v.onTouchEvent(event);
        }
    };

    public static EqualizerDialog createAndShowEqualizerDialog(FragmentManager fragmentManager) {

        EqualizerDialog dialog = new EqualizerDialog();
        dialog.show(fragmentManager, "EqualizerDialog");

        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
        EqualizerUIDesc desc = onRequestEqualizerDesc.invoke(null);
        //if(desc == null) dismiss();
        updateEqualizerDesc(desc);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View rootView = View.inflate(getActivity(), R.layout.dialog_equalizer, null);
        builder.setView(rootView);

        switchEnable = (Switch)rootView.findViewById(R.id.switchEnable);
        switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onSettingsChanged();
            }
        });

        spinnerPresets = (Spinner)rootView.findViewById(R.id.spinnerPresets);
        scrollView1 = (ScrollView)rootView.findViewById(R.id.scrollView1);
        scrollView2 = (HorizontalScrollView)rootView.findViewById(R.id.scrollView2);
        linearLayoutBars = (ViewGroup)rootView.findViewById(R.id.linearLayoutBars);

        textBass = (TextView)rootView.findViewById(R.id.textBass);
        seekArcBass = (SeekArc)rootView.findViewById(R.id.seekArcBass);
        seekArcBass.setOnTouchListener(seekBarTouch);
        seekArcBass.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                if (fromUser) {
                    spinnerPresets.setSelection(0, false);//Custom
                    bassValue = (progress - (seekArcMax / 2)) / ((float) seekArcMax / 2);
                    onBassTrebleChanged(bassValue, trebleValue, true, true);
                    onSettingsChanged();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

            }
        });

        textTreble= (TextView)rootView.findViewById(R.id.textTreble);
        seekArcTreble = (SeekArc)rootView.findViewById(R.id.seekArcTreble);
        seekArcTreble.setOnTouchListener(seekBarTouch);
        seekArcTreble.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                if (fromUser) {
                    spinnerPresets.setSelection(0, false);//Custom
                    trebleValue = (progress - (seekArcMax / 2)) / ((float) seekArcMax / 2);
                    onBassTrebleChanged(bassValue, trebleValue, true, true);
                    onSettingsChanged();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

            }
        });

        //textVirtualizerValue = (TextView)rootView.findViewById(R.id.textVirtualizerValue);
        seekBarVirtualizer = (SeekBar)rootView.findViewById(R.id.seekBarVirtualizer);
        seekBarVirtualizer.setMax(100);
        seekBarVirtualizer.setOnTouchListener(seekBarTouch);
        seekBarVirtualizer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onVirtualizerValueChanged(progress / 100.0f, true);
                onSettingsChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Dialog dialog = builder.create();

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        onReceiveEqualizerDescChanged.subscribeWeak(new WeakEvent1.Handler<EqualizerUIDesc>() {
            @Override
            public void invoke(EqualizerUIDesc equalizerUIDesc) {
                updateEqualizerDesc(equalizerUIDesc);
            }
        }, listenerRefHolder);

        return dialog;
    }


    private void updateEqualizerDesc(EqualizerUIDesc newEqualizerUIDesc)
    {
        if(this.getActivity() == null) return;
        if(switchEnable == null) return;

        preventSettingsUpdate = true;

        if(newEqualizerUIDesc ==null)
            newEqualizerUIDesc = EqualizerUIDesc.empty;
        this.equalizerUIDesc = newEqualizerUIDesc;

        switchEnable.setChecked(equalizerUIDesc.enabled);

        currentBands = EQPreset.clone(equalizerUIDesc.currentBands);

        int bandCount = equalizerUIDesc.currentBands.points.length;

        if((bandBarMax % 2) != 0)//make it Even number, so seekBar has a middle point
            bandBarMax += 1;

        {

            spinnerPresetsEventFromUser = false;

            String[] arraySpinner = new String[equalizerUIDesc.presets.length+1];
            arraySpinner[0] = this.getResources().getString(R.string.audio_eqcustom);
            for (int i=0;i< equalizerUIDesc.presets.length;i++)
                arraySpinner[i+1] = equalizerUIDesc.presets[i].name;

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(),
                    android.R.layout.simple_spinner_item, arraySpinner);
            spinnerPresets.setAdapter(adapter);

            int selection = equalizerUIDesc.currentPreset >= 0 ? equalizerUIDesc.currentPreset+1 : 0;
            if(selection < 0 || selection >= arraySpinner.length)
                selection = 0;
            spinnerPresets.setSelection(selection, false);
            spinnerPresets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    position = position - 1;

                    if (position >= 0 && position < equalizerUIDesc.presets.length) {
                        Equalization.getEqBandsPresetsConvert(equalizerUIDesc.presets[position], currentBands);
                        onBassTrebleChanged(0.0f, 0.0f, false, true);
                    }

                    onSettingsChanged();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spinnerPresetsEventFromUser = true;
        }

        //TextView maxTextView = (TextView)rootView.findViewById(R.id.maxtxt);
        //maxTextView.setText(""+equalizerUIDesc.higherBandLevel);
        //TextView minTextView = (TextView)rootView.findViewById(R.id.mintxt);
        //minTextView.setText(""+equalizerUIDesc.lowerBandLevel);

        bandSeekBars = new VerticalSeekBar[bandCount];
        bandTexts = new TextView[bandCount];

        linearLayoutBars.removeAllViews();
        for (int i = 0; i < bandCount; i++) {
            final int barIndex = i;
            View barItem = View.inflate(getActivity(), R.layout.dialog_equalizer_bar, null);
            bandSeekBars[i] = (VerticalSeekBar) barItem.findViewById(R.id.seekBarVolume);
            bandTexts[i] = (TextView) barItem.findViewById(R.id.txtBarTop);
            TextView bottomTextView = (TextView) barItem.findViewById(R.id.txtBarBottom);
            bottomTextView.setText(formatFreqHz(equalizerUIDesc.currentBands.points[i].freq));

            bandSeekBars[i].setMax(bandBarMax);

            bandSeekBars[i].setOnTouchListener(seekBarTouch);
            bandSeekBars[i].setOnSeekBarChangeFromUserListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    if (fromUser) {
                        spinnerPresets.setSelection(0, false);//Custom
                        setBandProgress(barIndex, progress - (bandBarMax / 2), true);
                        setEqPresetFromBandBars(currentBands);
                        onBassTrebleChanged(0.0f, 0.0f, false, false);
                        onSettingsChanged();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            linearLayoutBars.addView(barItem);
        }

        onBassTrebleChanged(equalizerUIDesc.bassBoostValue, equalizerUIDesc.trebleBoostValue, false, true);
        onVirtualizerValueChanged(equalizerUIDesc.virtualizerStrength, false);


        preventSettingsUpdate = false;
    }

    private void setBandProgress(final float[] normalVals) {
        if(normalVals.length != bandSeekBars.length) return;

        for(int i=0;i<normalVals.length;i++) {

            int val = Math.round((bandBarMax / 2) * normalVals[i]);
            setBandProgress(i, val, false);
        }
    }

    private void setBandProgress(EQPreset preset) {
        if(preset.points.length != bandSeekBars.length) return;

        for(int i=0;i<preset.points.length;i++) {
            int val = Math.round((bandBarMax / 2) * preset.points[i].value);
            setBandProgress(i, val, false);
        }
    }

    private void setBandProgress(int i, int val, boolean progressBarChangedEvent) {

        val = Utils.ensureRange(val, -(bandBarMax / 2), bandBarMax / 2);

        if(!progressBarChangedEvent) {
            int progress = val + (bandBarMax / 2);
            bandSeekBars[i].setProgress(progress);
            bandSeekBars[i].updateThumb();//seekBar bug
        }

        bandTexts[i].setText("" + val);
    }

    private void onBassTrebleChanged(float bassValue, float trebleValue, boolean progressBarChangedEvent, boolean updateBars) {

        this.bassValue = bassValue;
        this.trebleValue = trebleValue;

        if(!progressBarChangedEvent)
        {
            seekArcBass.setProgress(Math.round((seekArcMax/2) * bassValue) + (seekArcMax/2));
            seekArcTreble.setProgress(Math.round((seekArcMax/2) * trebleValue) + (seekArcMax/2));
        }

        {
            int percent = Math.round(bassValue * (seekArcMax/2));
            textBass.setText(textBass.getResources().getQuantityString(
                    R.plurals.audio_bass_x, percent, percent));
        }

        {
            int percent = Math.round(trebleValue * (seekArcMax/2));
            textTreble.setText(textTreble.getResources().getQuantityString(
                    R.plurals.audio_treble_x, percent, percent));
        }

        float[] eqBandsNormalOut = new float[currentBands.points.length];
        float[] eqBandsFreq = new float[currentBands.points.length];
        for(int i=0; i<eqBandsFreq.length; i++)
            eqBandsFreq[i] = currentBands.points[i].freq;

        if(updateBars) {
                Equalization.getEqBandsBassTrebleControl(currentBands,
                        equalizerUIDesc.bassBoost,
                        equalizerUIDesc.trebleBoost,
                        bassValue,
                        trebleValue,
                        eqBandsNormalOut,
                        eqBandsFreq);

                setBandProgress(eqBandsNormalOut);
        }
    }

    private void onSettingsChanged() {

        if(preventSettingsUpdate) return;
        if(bandSeekBars == null) return;
        if(equalizerUIDesc == null) return;

        if(bandSeekBars.length != equalizerUIDesc.currentBands.points.length) {
            tlog.w("equalizerUIDesc bands count doesnt match ");
            return;
        }
        EqualizerUISettings settings = new EqualizerUISettings();

        settings.enabled = switchEnable.isChecked();
        settings.presetIndex = spinnerPresets.getSelectedItemPosition()-1;
        settings.bandsFinal = new EQPreset("Default", bandSeekBars.length);
        settings.bassValue = bassValue;
        settings.trebleValue = trebleValue;
        settings.currentBands = currentBands;
        settings.virtualizerStrength = seekBarVirtualizer.getProgress() / (float)seekBarVirtualizer.getMax();

        setEqPresetFromBandBars(settings.bandsFinal);

        onSubmitEqualizerSettings.invoke(settings, equalizerUIDesc);
    }

    void setEqPresetFromBandBars(EQPreset bandsOut)
    {
        int seekBarMaxHalf = (bandSeekBars.length>0 ? bandSeekBars[0].getMax() : 2)/2;

        if(bandsOut.points.length != bandSeekBars.length)
            bandsOut.resize(bandSeekBars.length);

        for(int i=0;i<bandSeekBars.length;i++)
        {
            bandsOut.points[i] = new EQPreset.Point(equalizerUIDesc.currentBands.points[i].freq, (bandSeekBars[i].getProgress()-(seekBarMaxHalf)) / (float)seekBarMaxHalf);
        }
    }


    private void onVirtualizerValueChanged(float value, boolean progressBarChangedEvent) {

        if(!progressBarChangedEvent) {
            seekBarVirtualizer.setProgress((int)(value*100.0f));
        }

//        int percent = (int)(value*100.0f);
//        if(percent>99)
//            textVirtualizerValue.setText(""+percent);
//        else
//            textVirtualizerValue.setText(""+percent+"%");
    }

        static String formatFreqHz(int milliHertz)
    {
        if(milliHertz<1000) return String.format(java.util.Locale.US,"%.1fHz", milliHertz*0.001f);
        if(milliHertz<1000000) return ""+milliHertz/1000+"Hz";
        return String.format(java.util.Locale.US,"%.1fkHz", milliHertz*0.000001f);
    }

    static String formatFreqHz(float hertz)
    {
        return formatFreqHz((int)(hertz*1000.0f));
    }

}