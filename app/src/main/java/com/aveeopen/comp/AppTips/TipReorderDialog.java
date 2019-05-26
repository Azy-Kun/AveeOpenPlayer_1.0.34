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

package com.aveeopen.comp.AppTips;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.R;

public class TipReorderDialog extends DialogFragment {

    public static TipReorderDialog createAndShowTipReorderDialog(FragmentManager fragmentManager) {
        TipReorderDialog dialog = new TipReorderDialog();
        dialog.show(fragmentManager, "TipReorderDialog");
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView = View.inflate(getActivity(), R.layout.dialog_tip_reorder, null);
        builder.setView(rootView);
        builder.setTitle(R.string.dialog_tip);

        builder.setPositiveButton(R.string.dialog_hideTip, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                AppPreferences.createOrGetInstance().setBool(AppPreferences.PREF_Bool_tipShow_reorder, false);
            }
        });

        builder.setNeutralButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        return builder.create();
    }

}