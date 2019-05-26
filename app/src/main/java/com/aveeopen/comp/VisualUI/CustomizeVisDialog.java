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
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.Common.Utils;
import com.aveeopen.Common.Vec2f;
import com.aveeopen.Common.tlog;
import com.aveeopen.R;
import com.aveeopen.comp.Visualizer.Elements.Element;

import java.util.Iterator;

import it.moondroid.colormixer.HSLColor;
import it.moondroid.colormixer.HueSeekBar;
import it.moondroid.colormixer.LightnessSeekBar;
import it.moondroid.colormixer.MyOpacitySeekBar;
import it.moondroid.colormixer.SaturationSeekBar;

public class CustomizeVisDialog extends DialogFragment {

    private static final String arg1 = "arg1";
    private static final String arg2 = "arg2";
    private static final String arg3 = "arg3";
    public static WeakEvent4<Integer /*rootIdentifier*/, Element.CustomizationList /*customizationList*/, Integer /*index*/, WeakEvent2<Integer /*rootIdentifier*/, Element.CustomizationList /*customizationList*/> /*onCustomStructureChanged*/> onPickedColor = new WeakEvent4<>();
    public static WeakEvent3<Integer /*rootIdentifier*/, Element.CustomizationList /*customizationList*/, Integer /*index*/> onFinishedPickingColor = new WeakEvent3<>();

    private WeakEvent2<Integer /*rootIdentifier*/, Element.CustomizationList /*customList*/> onCustomStructureChanged = new WeakEvent2<>();
    private Object handlerRefHolder;

    int rootIdentifier;
    int customizationIndex;
    Element.CustomizationList customizationDataList;
    //
    private TextView txtElementTitle;
    private ViewGroup linearLayoutRootContent;
    private boolean eventsFromUser = false;

    private static int argb(int alpha, int rgb) {
        return (alpha << 24) | (rgb & 0x00ffffff);
    }

    private static String formatPropertyDisplayName(String name) {
        StringBuilder sb = new StringBuilder();
        boolean lastLower = false;

        if (name.length() > 0) {
            char c = Character.toUpperCase(name.charAt(0));
            sb.append(c);
            lastLower = Character.isDigit(c);
        }

        for (int i = 1; i < name.length(); i++){
            char c = name.charAt(i);
            boolean upper = Character.isUpperCase(c) || Character.isDigit(c);

            if(lastLower && upper)
                sb.append(' ');

            sb.append(c);
            lastLower = !upper;
        }

        return sb.toString();
    }

    public CustomizeVisDialog() {
        this.setStyle(DialogFragment.STYLE_NO_FRAME, 0);

        handlerRefHolder = onCustomStructureChanged.subscribeHoldWeak(new WeakEvent2.Handler<Integer, Element.CustomizationList>() {
            @Override
            public void invoke(Integer rootIdent, Element.CustomizationList customizationList) {
                if(rootIdentifier != rootIdent) {
                    tlog.w("rootIdentifiers doesn't match");
                    return;
                }
                eventsFromUser = false;
                parseCustomizationData(customizationList);
                eventsFromUser = true;
            }
        });
    }

    public static CustomizeVisDialog createAndShowCustomizeVisDialog(
            FragmentManager fragmentManager,
            Integer rootIdentifier,
            Element.CustomizationList customization,
            Integer customizationIndex) {

        CustomizeVisDialog dialog = new CustomizeVisDialog();
        Bundle args = new Bundle();
        args.putInt(arg1, rootIdentifier);
        args.putInt(arg2, customizationIndex);
        args.putString(arg3, customization.serialize());
        dialog.setArguments(args);

        dialog.show(fragmentManager, "CustomizeVisDialog");
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
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
            //d.setTitle(customizationData.getCustomizationName());
        }
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//
//
//        Bundle args = this.getArguments();
//
//        View rootView = View.inflate(getActivity(), R.layout.dialog_customize_vis, null);
//        builder.setView(rootView);
//
//        linearLayoutRootContent = (ViewGroup)rootView.findViewById(R.id.linearLayoutRootContent);
//
//        rootIdentifier = args.getInt(arg1);
//        customizationIndex = args.getInt(arg2);
//        customizationDataList = Element.CustomizationList.deserialize(args.getString(arg3));
//
//        customizationData = customizationDataList.getData(customizationIndex);
//        if(customizationData == null) {
//            tlog.w("customizationData is null");
//            return null;
//        }
//
//
//        linearLayoutRootContent.removeAllViews();
//
//        Iterator<String> keys = customizationData.GetAllProperties();
//        while( keys.hasNext() ) {
//            String key = keys.next();
//
//            String type = customizationData.getPropertyType(key);
//
//            if(type.equals("i")) {
//                createPropertyViewInt(formatPropertyDisplayName(key), key, customizationData.getPropertyInt(key), 0, 1000);
//            } else if(type.equals("f")) {
//                createPropertyViewFloat(formatPropertyDisplayName(key), key, customizationData.getPropertyInt(key), 0.0f, 10.0f, 0.5f);
//            } else if(type.equals("crgba")) {
//                createPropertyViewRGBA(formatPropertyDisplayName(key), key, customizationData.getPropertyInt(key));
//            }
//        }
//        //builder.setView(rootView);
//        Dialog dialog = builder.create();
//        //dialog.setTitle(customizationData.getCustomizationName());
//        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(true);
//
//
//        return dialog;
//    }
//

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = this.getArguments();

        View rootView = inflater.inflate(R.layout.dialog_customize_vis, container, false);

        linearLayoutRootContent = (ViewGroup)rootView.findViewById(R.id.linearLayoutContent);
        txtElementTitle = (TextView)rootView.findViewById(R.id.txtElementTitle);

        rootIdentifier = args.getInt(arg1);
        customizationIndex = args.getInt(arg2);

        eventsFromUser = false;
        parseCustomizationData(Element.CustomizationList.deserialize(args.getString(arg3)));
        eventsFromUser = true;

        return rootView;
    }

    void parseCustomizationData(Element.CustomizationList customList)
    {
        if(getActivity() == null) return;

        customizationDataList = customList;
        Element.CustomizationData customizationData = customList.getData(customizationIndex);
        if(customizationData == null) {
            tlog.w("customizationData is null");
            return;
        }

        txtElementTitle.setText(customizationData.getCustomizationName());
        parseDataRecursive(customizationData, linearLayoutRootContent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onFinishedPickingColor.invoke(rootIdentifier, customizationDataList, customizationIndex);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onFinishedPickingColor.invoke(rootIdentifier, customizationDataList, customizationIndex);
    }


    private void parseDataRecursive(Element.CustomizationData customData, ViewGroup contentView)
    {
        contentView.removeAllViews();

        Iterator<String> keys = customData.GetAllPropertiesSorted();
        while( keys.hasNext() ) {
            String key = keys.next();

            String type = customData.getPropertyType(key);

            String[] typeParts = Element.CustomizationData.getPropertyTypeParts(type);//returns at least 1 part
            if(typeParts[0].equals("i") && typeParts.length >= 3) {
                int min = Utils.strToIntSafe(typeParts[1], 0);
                int max = Utils.strToIntSafe(typeParts[2], 100);
                createPropertyViewInt(customData, contentView, formatPropertyDisplayName(key), key, min, max);
            } else if(typeParts[0].equals("b")) {
                createPropertyViewBool(customData, contentView, formatPropertyDisplayName(key), key);
            } else if(typeParts[0].equals("crgb")) {
                createPropertyViewRGBA(customData, contentView, false, formatPropertyDisplayName(key), key);
            } else if(typeParts[0].equals("crgba")) {
                createPropertyViewRGBA(customData, contentView, true, formatPropertyDisplayName(key), key);
            } else if(typeParts[0].equals("f") && typeParts.length >= 3) {
                float min = Utils.strToFloatSafe(typeParts[1], 0.0f);
                float max = Utils.strToFloatSafe(typeParts[2], 100.0f);
                createPropertyViewFloat(customData, contentView, formatPropertyDisplayName(key), key, min, max, (max - min) / 20);
            } else if(typeParts[0].equals("f2") && typeParts.length >= 3) {
                float min = Utils.strToFloatSafe(typeParts[1], 0.0f);
                float max = Utils.strToFloatSafe(typeParts[2], 100.0f);
                createPropertyViewVec2f(customData, contentView, formatPropertyDisplayName(key), key, min, max, (max - min) / 20);
            } else if(typeParts[0].equals("_child")) {

                String[] validValues = new String[typeParts.length-1];
                System.arraycopy(typeParts, 1, validValues, 0, validValues.length);

                final Element.CustomizationData childData = customData.getChild(key);
                createChildView(childData, contentView, formatPropertyDisplayName(key), key, validValues);
            }
        }

    }

    private void createChildView(final Element.CustomizationData childData, ViewGroup parentContentView, String displayName, final String name, final String[] validValues) {
        String value = childData.getChildTypeValue();

        View itemView = View.inflate(getActivity(), R.layout.dialog_customize_vis_element_child, null);

        ViewGroup contentView = (ViewGroup) itemView.findViewById(R.id.linearLayoutContent);

        TextView txtTitle = (TextView) itemView.findViewById(R.id.title);
        txtTitle.setText(displayName);

        Spinner spinnerTypes = (Spinner) itemView.findViewById(R.id.spinnerType);
        {
            int selection = 0;
            for (int i=0;i< validValues.length;i++) {
                if (value.equals(validValues[i])) {
                    selection = i;
                    break;
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(),
                    android.R.layout.simple_spinner_item, validValues);
            spinnerTypes.setAdapter(adapter);

            spinnerTypes.setSelection(selection, false);
            spinnerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position >= 0 && position < validValues.length) {
                        if(!childData.getChildTypeValue().equals(validValues[position]))
                        {
                            childData.putChildTypeValue(validValues[position]);
                            onChildPropertyChanged();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        parentContentView.addView(itemView);

        parseDataRecursive(childData, contentView);
    }

    private void createPropertyViewBool(final Element.CustomizationData customData, ViewGroup contentView, String displayName, final String name) {

        boolean value = customData.getPropertyBool(name, false);

        View itemView = View.inflate(getActivity(), R.layout.dialog_customize_vis_element_toggle, null);

        TextView txtTitle = (TextView) itemView.findViewById(R.id.title);
        txtTitle.setText(displayName);

        CheckBox checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
        checkbox.setChecked(value);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                customData.putPropertyBool(name, isChecked);
                onPropertyChanged();

            }
        });

        contentView.addView(itemView);
    }

    private void createPropertyViewInt(final Element.CustomizationData customData, ViewGroup contentView, String displayName, final String name, final int min, final int max) {

        int value = customData.getPropertyInt(name, min);

        View itemView = View.inflate(getActivity(), R.layout.dialog_customize_vis_element_bar, null);

        TextView txtTitle = (TextView) itemView.findViewById(R.id.title);
        txtTitle.setText(displayName);

        final TextView txtValue = (TextView) itemView.findViewById(R.id.txtValue);
        txtValue.setText(""+value);

        SeekBar bar = (SeekBar) itemView.findViewById(R.id.seekBar);

        bar.setMax(max - min);
        bar.setProgress(value - min);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    txtValue.setText(""+(progress + min));
                    customData.putPropertyInt(name, progress + min);
                    onPropertyChanged();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        contentView.addView(itemView);
    }

    private void createPropertyViewRGBA(final Element.CustomizationData customData, ViewGroup contentView, final boolean showOpacityBar, String displayName, final String name) {

        int value = customData.getPropertyInt(name, 0xffffffff);

        View itemView = View.inflate(getActivity(), R.layout.dialog_customize_vis_element_crgba, null);

        TextView txtTitle = (TextView) itemView.findViewById(R.id.title);
        txtTitle.setText(displayName);

        final HueSeekBar hueSeekBar = (HueSeekBar) itemView.findViewById(R.id.hueBar);
        final SaturationSeekBar saturationSeekBar = (SaturationSeekBar) itemView.findViewById(R.id.saturationBar);
        final LightnessSeekBar lightnessSeekBar = (LightnessSeekBar) itemView.findViewById(R.id.lightnessBar);
        final MyOpacitySeekBar opacitySeekBar = (MyOpacitySeekBar) itemView.findViewById(R.id.opacityBar);


        int alpha = Color.alpha(value);
        int r = Color.red(value);
        int g = Color.green(value);
        int b = Color.blue(value);

        int initialColorNoAlpha = Color.argb(0xff, r, g, b);

        hueSeekBar.initWithColor(initialColorNoAlpha);
        saturationSeekBar.initWithColor(initialColorNoAlpha);
        lightnessSeekBar.initWithColor(initialColorNoAlpha);

        if(showOpacityBar) {
            opacitySeekBar.setVisibility(View.VISIBLE);
            opacitySeekBar.initWithColor(value);
        } else {
            opacitySeekBar.setVisibility(View.GONE);
        }

        //
        SeekBar.OnSeekBarChangeListener colorListener = new SeekBar.OnSeekBarChangeListener() {


            HSLColor hslColor = new HSLColor(0.0f, 0.0f, 0.0f);
            int colorAlpha = 255;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {

                    hslColor.setHue(hueSeekBar.getHue());
                    hslColor.setLuminance(lightnessSeekBar.getLightness());
                    hslColor.setSaturation(saturationSeekBar.getSaturation());
                    colorAlpha = showOpacityBar ? opacitySeekBar.getOpacityColor() : 255;
                    int color = argb(colorAlpha, hslColor.getRGB());

                    customData.putPropertyInt(name, color);

                    updateSeekBars();
                    onPropertyChanged();
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
                //color = argb(colorAlpha, hslColor.getRGB());
                //onPickedColor.invoke(rootIdentifier, customization, colorIndex, color);
            }
        };

        hueSeekBar.setOnSeekBarChangeListener(colorListener);
        saturationSeekBar.setOnSeekBarChangeListener(colorListener);
        lightnessSeekBar.setOnSeekBarChangeListener(colorListener);
        opacitySeekBar.setOnSeekBarChangeListener(colorListener);

        contentView.addView(itemView);
    }

    private void createPropertyViewFloat(final Element.CustomizationData customData, ViewGroup contentView, String displayName, final String name, final float min, final float max, final float step) {
        float value = customData.getPropertyFloat(name, min);

        View itemView = View.inflate(getActivity(), R.layout.dialog_customize_vis_element_bar, null);

        TextView txtTitle = (TextView) itemView.findViewById(R.id.title);
        txtTitle.setText(displayName);

        final TextView txtValue = (TextView) itemView.findViewById(R.id.txtValue);
        float val = value;// * step;
        txtValue.setText(String.format(java.util.Locale.US, "%.3f", val));

        SeekBar bar = (SeekBar) itemView.findViewById(R.id.seekBar);

        bar.setMax((int)((max - min) / step));
        bar.setProgress((int)((value - min) / step));
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    float val = (progress * step) + min;
                    txtValue.setText(String.format(java.util.Locale.US, "%.3f", val));
                    customData.putPropertyFloat(name, val);
                    onPropertyChanged();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        contentView.addView(itemView);
    }

    private void createPropertyViewVec2f(final Element.CustomizationData customData, ViewGroup contentView, String displayName, final String name, final float min, final float max, final float step) {
        Vec2f value = customData.getPropertyVec2f(name, new Vec2f(min, min));

        View itemView = View.inflate(getActivity(), R.layout.dialog_customize_vis_element_bar_xy, null);

        TextView txtTitle = (TextView) itemView.findViewById(R.id.title);
        txtTitle.setText(displayName);

        final TextView txtValue = (TextView) itemView.findViewById(R.id.txtValue);
        Vec2f val = value;
        txtValue.setText(String.format(java.util.Locale.US, "%.3f   %.3f", val.x, val.y));

        final SeekBar barX = (SeekBar) itemView.findViewById(R.id.seekBarX);
        final SeekBar barY = (SeekBar) itemView.findViewById(R.id.seekBarY);

        barX.setMax((int)((max - min) / step));
        barX.setProgress((int)((value.x - min) / step));
        barX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    Vec2f val = new Vec2f((progress * step) + min, (barY.getProgress()* step) + min);
                    txtValue.setText(String.format(java.util.Locale.US, "%.3f   %.3f", val.x, val.y));
                    customData.putPropertyVec2f(name, val);
                    onPropertyChanged();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        barY.setMax((int)((max - min) / step));
        barY.setProgress((int)((value.y - min) / step));
        barY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    Vec2f val = new Vec2f((barX.getProgress()* step) + min, (progress * step) + min);
                    txtValue.setText(String.format(java.util.Locale.US, "%.3f   %.3f", val.x, val.y));
                    customData.putPropertyVec2f(name, val);
                    onPropertyChanged();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        contentView.addView(itemView);
    }

    void onChildPropertyChanged() {
        if(!eventsFromUser) return;
        if(customizationDataList == null) return;
        //we need onCustomStructureChanged callback, when we switch "child element" values, as they make customization structure changes
        onPickedColor.invoke(rootIdentifier, customizationDataList, customizationIndex, onCustomStructureChanged);
    }

    void onPropertyChanged() {
        if(!eventsFromUser) return;
        if(customizationDataList == null) return;
        onPickedColor.invoke(rootIdentifier, customizationDataList, customizationIndex, null);
    }


}