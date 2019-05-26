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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.R;

public class RenamePlaylistDialog extends DialogFragment {

    public static WeakEvent3<Context /*context*/, Long /*playlistId*/, String /*newName*/> onSubmitRenamePlaylist = new WeakEvent3<>();

    private static final String arg1 = "arg1";
    private static final String arg2 = "arg2";

    public static RenamePlaylistDialog createAndShowCreateRenamePlaylistDialog(FragmentManager fragmentManager, Long playlistId, String defaultValue) {
        RenamePlaylistDialog dialog = RenamePlaylistDialog.newInstanceRename(playlistId, defaultValue);
        dialog.show(fragmentManager, "RenamePlaylistDialog");
        return dialog;
    }

    private static RenamePlaylistDialog newInstanceRename(long playlistId, String defaultValue) {
        RenamePlaylistDialog dialog = new RenamePlaylistDialog();
        Bundle args = new Bundle();
        args.putLong(arg1, playlistId);
        args.putString(arg2, defaultValue);
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

        final long playlistId = args.getLong(arg1);//could be 0
        final String defaultValue = args.getString(arg2);//could be null

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View rootView = View.inflate(getActivity(), R.layout.dialog_rename_playlist, null);
        builder.setView(rootView);

        final EditText editTxtPlaylistName = (EditText) rootView.findViewById(R.id.editTxtPlaylistName);
        if (defaultValue == null)
            editTxtPlaylistName.setText(R.string.dialog_add_playlist_default_value);
        else
            editTxtPlaylistName.setText(defaultValue);

        builder.setTitle(R.string.dialog_rename_playlist_title);

        builder.setPositiveButton(R.string.dialog_rename, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                onSubmitRenamePlaylist.invoke(editTxtPlaylistName.getContext(), playlistId, editTxtPlaylistName.getText().toString());
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                RenamePlaylistDialog.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}