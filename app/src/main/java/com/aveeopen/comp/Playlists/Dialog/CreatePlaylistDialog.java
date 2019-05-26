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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEvent5;
import com.aveeopen.comp.Playlists.Files.PlaylistFilesType;
import com.aveeopen.R;

import java.util.ArrayList;
import java.util.List;

public class CreatePlaylistDialog extends DialogFragment {

    public static WeakEvent3<String /*name*/, long[] /*addSongsNativePL*/, List<String> /*addSongDataSources*/> onSubmitCreatePlaylist = new WeakEvent3<>();
    public static WeakEvent5<String /*destPath*/, String /*name*/, PlaylistFilesType /*playlistType*/, List<String> /*addSongDataSources*/, Boolean /*useRelativePaths*/> onSubmitCreateStandalonePlaylist = new WeakEvent5<>();

    private static final String arg1 = "arg1";
    private static final String arg2 = "arg2";
    private static final String arg3 = "arg3";
    private static final String arg4 = "arg4";

    public static CreatePlaylistDialog createAndShowCreatePlaylistDialog(FragmentManager fragmentManager, long[] addSongsNativePL, List<String> addSongDataSources) {
        CreatePlaylistDialog dialog = CreatePlaylistDialog.newInstance(addSongsNativePL, addSongDataSources);
        dialog.show(fragmentManager, "CreatePlaylistDialog");
        return dialog;
    }

    private static CreatePlaylistDialog newInstance(long[] addSongsNativePL, List<String> addSongDataSources) {
        CreatePlaylistDialog dialog = new CreatePlaylistDialog();
        Bundle args = new Bundle();
        args.putInt(arg1, 0);
        args.putString(arg2, null);
        args.putStringArrayList(arg3, (ArrayList<String>) addSongDataSources);
        args.putLongArray(arg4, addSongsNativePL);
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

        int type = args.getInt(arg1);
        final String defaultValue = args.getString(arg2);//could be null
        final ArrayList<String> songDataSourcePL = args.getStringArrayList(arg3);//could be null
        final long[] addSongsNativePL = args.getLongArray(arg4);//could be null

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View rootView = View.inflate(getActivity(), R.layout.dialog_create_playlist, null);
        builder.setView(rootView);

        final EditText editTxtPlaylistName = (EditText) rootView.findViewById(R.id.editTxtPlaylistName);
        if (defaultValue == null)
            editTxtPlaylistName.setText(R.string.dialog_add_playlist_default_value);
        else
            editTxtPlaylistName.setText(defaultValue);

        final Spinner spinnerType = (Spinner) rootView.findViewById(R.id.spinnerType);
        {
            String[] arraySpinner = new String[PlaylistFilesType.playlistFilesTypes.length + 1];
            arraySpinner[0] = getResources().getString(R.string.playlist_system_name);
            for (int i = 0; i < PlaylistFilesType.playlistFilesTypes.length; i++)
                arraySpinner[i + 1] = PlaylistFilesType.playlistFilesTypes[i].name;

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(),
                    android.R.layout.simple_spinner_item, arraySpinner);
            spinnerType.setAdapter(adapter);
        }

        TextView txtInfo = (TextView) rootView.findViewById(R.id.txtInfo);
        if (addSongsNativePL == null && songDataSourcePL == null) {
            txtInfo.setVisibility(View.GONE);
        } else {
            txtInfo.setVisibility(View.VISIBLE);
            int num = addSongsNativePL != null ? addSongsNativePL.length : songDataSourcePL.size();
            String message = this.getResources().getQuantityString(
                    R.plurals.x_items_about_to_added, num, num);
            txtInfo.setText(message);
        }

        if (type == 0) {
            builder.setTitle(R.string.dialog_add_playlist_title);

            builder.setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    String folderPath = editTxtPlaylistName.getText().toString();
                    boolean useRelativePaths = true;
                    int spinnerPos = spinnerType.getSelectedItemPosition();

                    if (spinnerPos >= 0 && spinnerPos < PlaylistFilesType.playlistFilesTypes.length + 1) {
                        if (spinnerPos == 0) {
                            onSubmitCreatePlaylist.invoke(folderPath, addSongsNativePL, songDataSourcePL);
                        } else {
                            PlaylistFilesType playlistType = PlaylistFilesType.playlistFilesTypes[spinnerPos - 1];
                            onSubmitCreateStandalonePlaylist.invoke(null, folderPath, playlistType, songDataSourcePL, useRelativePaths);
                        }
                    }

                }
            });
        }

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                CreatePlaylistDialog.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}