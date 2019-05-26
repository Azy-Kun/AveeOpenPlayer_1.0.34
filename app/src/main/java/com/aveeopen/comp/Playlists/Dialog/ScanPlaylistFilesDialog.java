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

package com.aveeopen.comp.Playlists.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.UtilsUI;
import com.aveeopen.Design.PlaylistsDesign;
import com.aveeopen.R;

import java.util.LinkedList;
import java.util.List;

public class ScanPlaylistFilesDialog extends DialogFragment {

    public static WeakEventR<PlaylistsDesign.PlaylistScanningStatus /*return*/> onRequestPlaylistScanStatus = new WeakEventR<>();
    public static WeakEvent onStopPlaylistScan = new WeakEvent();

    private static final String arg1 = "arg1";
    private TextView txtInfo;
    private List<Object> listenerRefHolder = new LinkedList<>();
    private static WeakEvent1<PlaylistsDesign.PlaylistScanningStatus /*status*/> internalOnPlaylistScanStatusUpdated = new WeakEvent1<>();

    public static void updatePlaylistScanStatus(PlaylistsDesign.PlaylistScanningStatus status)
    {
        internalOnPlaylistScanStatusUpdated.invoke(status);
    }

    public static ScanPlaylistFilesDialog createAndShowScanPlaylistFilesDialog(FragmentManager fragmentManager) {
        ScanPlaylistFilesDialog dialog = new ScanPlaylistFilesDialog();
        dialog.show(fragmentManager, "ScanPlaylistFilesDialog");
        return dialog;
    }

    private static ScanPlaylistFilesDialog newInstance(int mode) {
        ScanPlaylistFilesDialog dialog = new ScanPlaylistFilesDialog();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View rootView = View.inflate(getActivity(), R.layout.dialog_scan_playlists, null);
        builder.setView(rootView);

        txtInfo = (TextView) rootView.findViewById(R.id.txtInfo);

        builder.setTitle(R.string.dialog_scan_playlists);

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                onStopPlaylistScan.invoke();
            }
        });


        {
            internalOnPlaylistScanStatusUpdated.subscribeWeak(new WeakEvent1.Handler<PlaylistsDesign.PlaylistScanningStatus>() {
                @Override
                public void invoke(PlaylistsDesign.PlaylistScanningStatus playlistScanningStatus) {
                    updateScanStatus(playlistScanningStatus);
                }
            }, listenerRefHolder);

            PlaylistsDesign.PlaylistScanningStatus scanStatus = onRequestPlaylistScanStatus.invoke(null);
            if (scanStatus != null)
                updateScanStatus(scanStatus);
        }

        return builder.create();

    }

    private void updateScanStatus(PlaylistsDesign.PlaylistScanningStatus scanStatus) {
        if (scanStatus.active)
            txtInfo.setText(scanStatus.text);
        else {
            txtInfo.setText("..");
            UtilsUI.dismissSafe(this);
        }
    }
}