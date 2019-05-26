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

package com.aveeopen.comp.LibraryQueueUI.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Space;

import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.Common.Events.WeakEventR2;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.Design.SortDesign;
import com.aveeopen.R;

import java.util.ArrayList;
import java.util.List;

public class SortDialog extends DialogFragment implements RadioGroup.OnCheckedChangeListener {

    public static WeakEventR2<Integer /*pageIndex*/, IGeneralItemContainerIdentifier /*containerIdentifier*/, SortDesign.SortOptions> onRequestSortOptions = new WeakEventR2<>();
    public static WeakEvent4<Integer /*pageIndex*/, IGeneralItemContainerIdentifier /*containerIdentifier*/, Integer /*sortRadioOption*/, Integer /*sortMaskOptions*/> onSubmitSortOptions = new WeakEvent4<>();

    private static final String arg1 = "arg1";
    private List<RadioButton> radioBtnList = null;
    private List<CheckBox> chkBoxList = null;

    public SortDialog() {
    }

    public static SortDialog createAndShowDialog(FragmentManager fragmentManager, int mode) {
        SortDialog dialog = newInstance(mode);
        dialog.show(fragmentManager, "SortDialog");
        return dialog;
    }

    private static SortDialog newInstance(int mode) {
        SortDialog dialog = new SortDialog();
        Bundle args = new Bundle();
        args.putInt(arg1, mode);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        int mode = args.getInt(arg1);

        //TODO: Provide pageIndex and containerIdentifier for onRequestSortOptions event
        final int pageIndex = -1;
        final IGeneralItemContainerIdentifier containerIdentifier = null;

        SortDesign.SortOptions optionsRaw = onRequestSortOptions.invoke(pageIndex, containerIdentifier, null);

        SortDesign.SortOptions options;
        if (mode == 0) {
            options = optionsRaw;
        } else {
            //sort for files
            options = optionsRaw.getRefined(SortDesign.Sort_Mode_Title,
                    SortDesign.Sort_Mode_Path,
                    SortDesign.Sort_Mode_DateModified,
                    SortDesign.Sort_Mode_Size);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View rootView = View.inflate(getActivity(), R.layout.dialog_sort, null);
        builder.setView(rootView);

        LinearLayout layoutCheckOptions = (LinearLayout) rootView.findViewById(R.id.layoutCheckOptions);
        RadioGroup radioGroupOptions = (RadioGroup) rootView.findViewById(R.id.radioGroupOptions);

        Space space = (Space) rootView.findViewById(R.id.space);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

        radioBtnList = new ArrayList<>(options == null ? 0 : options.radioOptions.size());
        chkBoxList = new ArrayList<>(options == null ? 0 : options.checkOptions.size());

        if (options != null) {
            for (Tuple2<Integer, String> item : options.radioOptions) {
                RadioButton radioBtn = new RadioButton(this.getActivity());
                radioBtn.setTag((Integer) item.obj1);
                radioBtn.setText(item.obj2);
                radioGroupOptions.addView(radioBtn, params);

                if (item.obj1 == options.selectedRadioOption) {
                    radioGroupOptions.check(radioBtn.getId());
                }

                radioBtnList.add(radioBtn);
            }

            if (options.checkOptions.size() > 0)
                space.setVisibility(View.VISIBLE);
            else
                space.setVisibility(View.GONE);

            for (Tuple2<Integer, String> item : options.checkOptions) {
                CheckBox chkBox = new CheckBox(this.getActivity());
                chkBox.setTag((Integer) item.obj1);
                chkBox.setText(item.obj2);
                if ((item.obj1 & options.maskCheckOptions) != 0) chkBox.setChecked(true);
                layoutCheckOptions.addView(chkBox, params);

                chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        notifyOptionChange(pageIndex, containerIdentifier);
                    }
                });

                chkBoxList.add(chkBox);
            }
        }

        radioGroupOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                notifyOptionChange(pageIndex, containerIdentifier);
            }
        });

        builder.setTitle(R.string.dialog_sort_title);

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                notifyOptionChange(pageIndex, containerIdentifier);
            }
        });

        return builder.create();
    }

    void notifyOptionChange(final int pageIndex, final IGeneralItemContainerIdentifier containerIdentifier) {
        if (radioBtnList == null || chkBoxList == null) return;

        int sortRadioOption = 0;
        int sortMaskOptions = 0;

        for (RadioButton radioBtn : radioBtnList) {
            if (radioBtn.isChecked())
                sortRadioOption = (int) radioBtn.getTag();
        }

        for (CheckBox chkBox : chkBoxList) {
            if (chkBox.isChecked())
                sortMaskOptions |= (int) chkBox.getTag();
        }

        final int sortRadioOptionFinal = sortRadioOption;
        final int sortMaskOptionsFinal = sortMaskOptions;
        onSubmitSortOptions.invoke(pageIndex, containerIdentifier, sortRadioOptionFinal, sortMaskOptionsFinal);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }
}