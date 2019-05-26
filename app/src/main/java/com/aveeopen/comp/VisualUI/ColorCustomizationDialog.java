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

package com.aveeopen.comp.VisualUI;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.comp.Visualizer.Elements.Element;
import com.aveeopen.R;

import it.moondroid.colormixer.HSLColor;
import it.moondroid.colormixer.HueSeekBar;
import it.moondroid.colormixer.LightnessSeekBar;
import it.moondroid.colormixer.MyOpacitySeekBar;
import it.moondroid.colormixer.SaturationSeekBar;

public class ColorCustomizationDialog extends DialogFragment {

    private static final String arg1 = "arg1";
    private static final String arg3 = "arg3";
    private static final String arg4 = "arg4";
    private static final String arg5 = "arg5";
    public static WeakEvent4<Integer /*rootIdentifier*/, Element.CustomizationList /*customization*/, Integer /*colorIndex*/, Integer /*color*/> onPickedColor = new WeakEvent4<>();
    public static WeakEvent4<Integer /*rootIdentifier*/, Element.CustomizationList /*customizationList*/, Integer /*colorIndex*/, Integer /*color*/> onFinishedPickingColor = new WeakEvent4<>();

    private HSLColor hslColor;
    private HueSeekBar hueSeekBar;
    private SaturationSeekBar saturationSeekBar;
    private LightnessSeekBar lightnessSeekBar;
    private int color = 0xffffffff;
    private int colorIndex = 0;
    private Element.CustomizationList customization = null;

    public ColorCustomizationDialog() {
        this.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
    }

    public static ColorCustomizationDialog createAndShowColorCustomizationDialog(
            FragmentManager fragmentManager,
            Integer initialColor,
            Integer rootIdentifier,
            Element.CustomizationList customization,
            Integer colorIndex) {

        ColorCustomizationDialog dialog = ColorCustomizationDialog.newInstance(initialColor, rootIdentifier, customization, colorIndex);
        dialog.show(fragmentManager, "ColorCustomizationDialog");

        return dialog;
    }

    private static int argb(int alpha, int rgb) {
        return (alpha << 24) | (rgb & 0x00ffffff);
    }

    private static ColorCustomizationDialog newInstance(int initialColor, int rootIdentifier, Element.CustomizationList customization, int colorIndex) {
        ColorCustomizationDialog dialog = new ColorCustomizationDialog();

        Bundle args = new Bundle();
        args.putInt(arg1, initialColor);
        args.putInt(arg3, rootIdentifier);
        args.putInt(arg4, colorIndex);
        args.putString(arg5, customization.serialize());

        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = this.getArguments();

        int initialColor = args.getInt(arg1);
        final int rootIdentifier = args.getInt(arg3);
        colorIndex = args.getInt(arg4);
        customization = Element.CustomizationList.deserialize(args.getString(arg5));
        color = initialColor;

        View rootView = inflater.inflate(R.layout.dialog_choose_vizcolor, container, false);

        hueSeekBar = (HueSeekBar) rootView.findViewById(R.id.hueBar);
        saturationSeekBar = (SaturationSeekBar) rootView.findViewById(R.id.saturationBar);
        lightnessSeekBar = (LightnessSeekBar) rootView.findViewById(R.id.lightnessBar);
        final MyOpacitySeekBar opacitySeekBar = (MyOpacitySeekBar) rootView.findViewById(R.id.opacityBar);

        int alpha = Color.alpha(initialColor);
        int r = Color.red(initialColor);
        int g = Color.green(initialColor);
        int b = Color.blue(initialColor);

        int initialColorNoAlpha = Color.argb(0xff, r, g, b);

        hslColor = new HSLColor(initialColorNoAlpha);

        hueSeekBar.initWithColor(initialColorNoAlpha);
        saturationSeekBar.initWithColor(initialColorNoAlpha);
        lightnessSeekBar.initWithColor(initialColorNoAlpha);
        opacitySeekBar.initWithColor(initialColor);

        final int[] colorAlpha = new int[1];
        colorAlpha[0] = alpha;

        SeekBar.OnSeekBarChangeListener colorListener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    hslColor.setHue(hueSeekBar.getHue());
                    hslColor.setLuminance(lightnessSeekBar.getLightness());
                    hslColor.setSaturation(saturationSeekBar.getSaturation());
                    colorAlpha[0] = opacitySeekBar.getOpacityColor();
                    updateSeekBars();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateSeekBars();
            }

            private void updateSeekBars() {
                lightnessSeekBar.setColor(hslColor);
                saturationSeekBar.setColor(hslColor);
                opacitySeekBar.setColor(hslColor);
                color = argb(colorAlpha[0], hslColor.getRGB());
                onPickedColor.invoke(rootIdentifier, customization, colorIndex, color);
            }
        };


        hueSeekBar.setOnSeekBarChangeListener(colorListener);
        saturationSeekBar.setOnSeekBarChangeListener(colorListener);
        lightnessSeekBar.setOnSeekBarChangeListener(colorListener);
        opacitySeekBar.setOnSeekBarChangeListener(colorListener);

        this.getDialog().setCanceledOnTouchOutside(true);

        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Bundle args = this.getArguments();

        args.putInt(arg1, color);

        final int rootIdentifier = args.getInt(arg3);
        onFinishedPickingColor.invoke(rootIdentifier, customization, colorIndex, color);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        Bundle args = this.getArguments();
        final int rootIdentifier = args.getInt(arg3);
        onFinishedPickingColor.invoke(rootIdentifier, customization, colorIndex, color);
    }

}