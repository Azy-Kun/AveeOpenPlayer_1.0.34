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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;

import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEventR1;
import com.aveeopen.R;

import java.util.ArrayList;
import java.util.List;


public class VisualizerStyleDialog extends DialogFragment {

    public static WeakEventR1<List<VisualizerThemeInfo> /*listOut*/, Integer> onRequestSkinThemePresetList = new WeakEventR1<>();
    public static WeakEvent1<VisualizerThemeInfo /*presetInfo*/> onSkinThemePresetSelected = new WeakEvent1<>();
    public static WeakEvent onShowVideoContentAction = new WeakEvent();

    public VisualizerStyleDialog() {
        this.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
    }

    public static VisualizerStyleDialog createAndShowDialog(FragmentManager fragmentManager) {
        VisualizerStyleDialog dialog = newInstance();
        dialog.show(fragmentManager, "VisualizerStyleDialog");
        return dialog;
    }

    private static VisualizerStyleDialog newInstance() {
        VisualizerStyleDialog dialog = new VisualizerStyleDialog();

        Bundle args = new Bundle();

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
        View rootView = inflater.inflate(R.layout.dialog_choose_vizstyle, container, false);
        GridLayout gridLayout = (GridLayout) rootView.findViewById(R.id.gridLayoutElements);

        final List<VisualizerThemeInfo> themeslist = new ArrayList<>();
        int selectedSkinThemeIndex = onRequestSkinThemePresetList.invoke(themeslist, -1);

        if (themeslist.size() > 0) {

            View.OnClickListener btnOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = (Integer)v.getTag();
                    id = id - 1;
                    if (id < 0) {
                        onShowVideoContentAction.invoke();
                    } else if (id < themeslist.size()) {
                        VisualizerThemeInfo selectedSkinTheme = themeslist.get(id);
                        onSkinThemePresetSelected.invoke(selectedSkinTheme);
                    }
                }
            };

            for (int i = 0; i < themeslist.size(); i++) {
                View element = View.inflate(this.getActivity(), R.layout.dialog_choose_vizstyle_element, null);
                ImageButton btnElement = (ImageButton) element.findViewById(R.id.btnElement);
                btnElement.setOnClickListener(btnOnClickListener);
                btnElement.setImageResource(themeslist.get(i).iconResId);
                btnElement.setTag(i + 1);

                gridLayout.addView(element);
            }
        }

        this.getDialog().setCanceledOnTouchOutside(true);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

}