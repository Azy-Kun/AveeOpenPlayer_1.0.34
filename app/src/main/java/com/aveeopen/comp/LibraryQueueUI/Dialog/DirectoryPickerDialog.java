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
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.ContextData;
import com.aveeopen.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class DirectoryPickerDialog extends DialogFragment {

    public static WeakEvent3<ContextData /*contextData*/, String /*what*/, String /*value*/> onSubmitValue = new WeakEvent3<>();

    private ArrayList<File> currentFiles = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private EditText textView;
    private File dir;
    private static final boolean showHidden = false;
    private static final boolean onlyDirs = true;

    public static DirectoryPickerDialog createAndShowDialog(FragmentManager fragmentManager) {
        DirectoryPickerDialog dialog = new DirectoryPickerDialog();
        dialog.show(fragmentManager, "DirectoryPickerDialog");
        return dialog;
    }

    public DirectoryPickerDialog() {

    }

    private static ArrayList<File> filter(File[] fileList, boolean onlyDirs, boolean showHidden) {
        ArrayList<File> files = new ArrayList<>();
        for (File file : fileList) {
            if (onlyDirs && !file.isDirectory())
                continue;
            if (!showHidden && file.isHidden())
                continue;
            files.add(file);
        }
        Collections.sort(files);
        return files;
    }

    private static String[] names(ArrayList<File> files) {
        String[] names = new String[files.size()];
        int i = 0;
        for (File file : files) {
            names[i] = file.getName();
            i++;
        }
        return names;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String preferredStartDir = "";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_add_folder_title);

        View rootView = View.inflate(getActivity(), R.layout.bgreco_chooser_list, null);
        builder.setView(rootView);

        textView = (EditText) rootView.findViewById(R.id.txtName);

        Button btnChoose = (Button) rootView.findViewById(R.id.btnChoose);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String parentPath = dir.getParent();
                if (parentPath != null)
                    setCurrentPath(parentPath);
            }
        });

        ListView lv = (ListView) rootView.findViewById(R.id.list);
        lv.setTextFilterEnabled(true);

        adapter = new ArrayAdapter<>(this.getActivity(), R.layout.bgreco_list_item);
        lv.setAdapter(adapter);

        setCurrentPath(preferredStartDir);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= currentFiles.size() || !currentFiles.get(position).isDirectory())
                    return;

                String path = currentFiles.get(position).getAbsolutePath();
                setCurrentPath(path);
            }
        });

        builder.setPositiveButton(R.string.dialog_choose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                returnDir(textView.getText().toString());
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DirectoryPickerDialog.this.getDialog().cancel();
            }
        });

        return builder.create();
    }

    private void setCurrentPath(String path) {
        if (path == null || path.isEmpty()) {
            dir = Environment.getExternalStorageDirectory();
        } else {
            File startDir = new File(path);
            if (startDir.isDirectory() && startDir.canRead()) {
                dir = startDir;
            } else {
                return;
            }
        }

        currentFiles = filter(dir.listFiles(), onlyDirs, showHidden);
        String[] names = names(currentFiles);

        if (names.length < 1)
            names = new String[]{getResources().getString(R.string.dialog_dir_empty_placeholder)};

        adapter.clear();
        adapter.addAll((String[]) names);
        adapter.notifyDataSetChanged();

        String name = "";
        try {
            name = dir.getCanonicalPath();
        } catch (IOException ignored) {
        }
        if (name.length() == 0)
            name = "/";

        textView.setText(name);
    }

    private void returnDir(final String folderPath) {
        onSubmitValue.invoke(new ContextData(getActivity()), folderPath, "");
    }
}