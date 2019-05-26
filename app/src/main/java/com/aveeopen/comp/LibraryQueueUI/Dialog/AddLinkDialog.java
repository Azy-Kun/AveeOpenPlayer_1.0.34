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
import android.widget.EditText;
import android.widget.TextView;

import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.ContextData;
import com.aveeopen.R;

public class AddLinkDialog extends DialogFragment {

    public static WeakEvent2<ContextData, String> onSubmitAddByLink = new WeakEvent2<>();

    private int currentSample = 0;

    public static AddLinkDialog createAndShowDialog(FragmentManager fragmentManager) {
        AddLinkDialog dialog = new AddLinkDialog();
        dialog.show(fragmentManager, "AddLinkDialog");
        return dialog;
    }

    public AddLinkDialog() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View rootView = View.inflate(getActivity(), R.layout.dialog_add_link, null);
        builder.setView(rootView);

        final EditText et1 = (EditText) rootView.findViewById(R.id.editTxtFolderName);
        et1.setText(R.string.dialog_add_link_default_value);

        TextView txtUnder = (TextView) rootView.findViewById(R.id.txtUnder);
        txtUnder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentSample == 0)
                    et1.setText(R.string.dialog_add_link_sample_0);
                else if (currentSample == 1)
                    et1.setText(R.string.dialog_add_link_sample_1);
                else if (currentSample == 2)
                    et1.setText(R.string.dialog_add_link_sample_2);
                else if (currentSample == 3)
                    et1.setText(R.string.dialog_add_link_sample_3);
                else if (currentSample == 4)
                    et1.setText("http://yt-dash-mse-test.commondatastorage.googleapis.com/media/oops-20120802-manifest.mpd");
                else if (currentSample == 5)
                    et1.setText("http://wams.edgesuite.net/media/MPTExpressionData02/BigBuckBunny_1080p24_IYUV_2ch.ism/manifest(format=mpd-time-csf)");
                else if (currentSample == 6)
                    et1.setText("http://playready.directtaps.net/smoothstreaming/TTLSS720VC1/To_The_Limit_720.ism/Manifest");
                else if (currentSample == 7)
                    et1.setText("http://playready.directtaps.net/smoothstreaming/TTLSS720VC1/To_The_Limit_720_688.ismv");
                else if (currentSample == 8)
                    et1.setText("http://techslides.com/demos/sample-videos/small.flv");

                currentSample = ((currentSample + 1) % 9);
            }
        });

        builder.setTitle(R.string.dialog_add_link_title);

        builder.setPositiveButton(R.string.dialog_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                onSubmitAddByLink.invoke(new ContextData(et1), et1.getText().toString());
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddLinkDialog.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}