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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TableRow;
import android.widget.TextView;

import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.comp.AlbumArt.AlbumArtRequest;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.R;

import java.util.ArrayList;

public class SongDetailsDialog extends DialogFragment {

    public static WeakEvent4<AlbumArtRequest /*artRequest*/, ImageView /*imageView*/, Boolean /*fitCenterInside*/, Boolean /*preferLarge*/> onRequestAlbumArt = new WeakEvent4<>();

    private static final String arg1 = "arg1";
    private static final String argSrc = "argSrc";
    private static final String arg2 = "arg2";
    private static final String arg3 = "arg3";
    private static final String arg4 = "arg4";
    private static final String arg5 = "arg5";
    private static final String arg6 = "arg6";

    public static SongDetailsDialog createAndShowDialog(FragmentManager fragmentManager, Context context, PlaylistSong songs) {
        SongDetailsDialog dialog = newInstance(context, songs);
        dialog.show(fragmentManager, "SongDetailsDialog");
        return dialog;
    }

    private static SongDetailsDialog newInstance(Context context, PlaylistSong songs) {
        SongDetailsDialog dialog = new SongDetailsDialog();

        ArrayList<String> infoStrings = new ArrayList<>();

        PlaylistSong.DataDetails dataDetails = songs.getDataDetailsBlocking(context);

        infoStrings.add(emptyStrIfZero(dataDetails.trackNum));//track index
        infoStrings.add(emptyStrIfZero(dataDetails.cdNum));//disc index
        infoStrings.add(dataDetails.trackName);
        infoStrings.add(dataDetails.artistName);
        infoStrings.add(dataDetails.albumName);
        infoStrings.add(dataDetails.albumArtist);//album artist
        infoStrings.add(emptyStrIfZero(dataDetails.year));//year
        infoStrings.add(dataDetails.composer);//year

        if (dataDetails.bitRate > 0)
            infoStrings.add("" + (dataDetails.bitRate / 1000) + "kbps");//year
        else
            infoStrings.add("");

        if (dataDetails.width > 0 && dataDetails.height > 0)
            infoStrings.add("" + dataDetails.width + "x" + dataDetails.height);
        else
            infoStrings.add("");

        //infoStrings.add(android.text.format.Formatter.formatFileSize(_context, dataDetails.data.sizeInBytes));//file size
//        infoStrings.add("");//comment
//        infoStrings.add("");//composer
//        infoStrings.add("");//publisher

        String artPath0 = dataDetails.data.getAlbumArtPath0Str();
        String artPath1 = dataDetails.data.getAlbumArtPath1Str();
        String videoThumbDataSource = dataDetails.data.getVideoThumbDataSourceAsStr();

        Bundle args = new Bundle();
        args.putInt(arg1, dataDetails.isStream ? 1 : 0);
        args.putString(argSrc, songs.getConstrucPath());
        args.putStringArrayList(arg2, infoStrings);
        args.putString(arg3, artPath0);
        args.putString(arg4, artPath1);
        args.putString(arg5, videoThumbDataSource);
        args.putString(arg6, dataDetails.data.getAlbumArtGenerateStr());

        dialog.setArguments(args);
        return dialog;
    }

    public SongDetailsDialog() {
    }

    private static String emptyStrIfZero(int val) {
        return val > 0 ? "" + val : "";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        boolean isStream = args.getInt(arg1) != 0;
        String dataSource = args.getString(argSrc);
        ArrayList<String> infoStrings = args.getStringArrayList(arg2);
        String artPath0 = args.getString(arg3);
        String artPath1 = args.getString(arg4);
        String videoThumbDataSource = args.getString(arg5);
        String artGenerateStr = args.getString(arg6);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View rootView = View.inflate(getActivity(), R.layout.dialog_song_details, null);
        builder.setView(rootView);

        TabHost host = (TabHost) rootView.findViewById(R.id.tabHost);
        host.setup();

        {
            TabHost.TabSpec spec = host.newTabSpec("Tab One");
            spec.setContent(R.id.tab1);
            spec.setIndicator(getResources().getString(R.string.dialog_song_details_tab1));
            host.addTab(spec);

            TextView[] txt0 = new TextView[1];
            txt0[0] = (TextView) rootView.findViewById(R.id.txt0);
            txt0[0].setText(dataSource);

            TableRow[] row = new TableRow[10];
            TextView[] txt = new TextView[10];

            row[0] = (TableRow) rootView.findViewById(R.id.row1);
            row[1] = (TableRow) rootView.findViewById(R.id.row2);
            row[2] = (TableRow) rootView.findViewById(R.id.row3);
            row[3] = (TableRow) rootView.findViewById(R.id.row4);
            row[4] = (TableRow) rootView.findViewById(R.id.row5);
            row[5] = (TableRow) rootView.findViewById(R.id.row6);
            row[6] = (TableRow) rootView.findViewById(R.id.row7);
            row[7] = (TableRow) rootView.findViewById(R.id.row8);
            row[8] = (TableRow) rootView.findViewById(R.id.row9);
            row[9] = (TableRow) rootView.findViewById(R.id.row10);

            txt[0] = (TextView) rootView.findViewById(R.id.txt1);
            txt[1] = (TextView) rootView.findViewById(R.id.txt2);
            txt[2] = (TextView) rootView.findViewById(R.id.txt3);
            txt[3] = (TextView) rootView.findViewById(R.id.txt4);
            txt[4] = (TextView) rootView.findViewById(R.id.txt5);
            txt[5] = (TextView) rootView.findViewById(R.id.txt6);
            txt[6] = (TextView) rootView.findViewById(R.id.txt7);
            txt[7] = (TextView) rootView.findViewById(R.id.txt8);
            txt[8] = (TextView) rootView.findViewById(R.id.txt9);
            txt[9] = (TextView) rootView.findViewById(R.id.txt10);

            if(infoStrings != null) {
                for (int i = 0; i < infoStrings.size(); i++) {
                    if (infoStrings.get(i) == null || infoStrings.get(i).isEmpty()) {
                        row[i].setVisibility(View.GONE);
                    } else {
                        row[i].setVisibility(View.VISIBLE);
                        txt[i].setText(infoStrings.get(i));
                    }
                }
            }
        }

        TabHost.TabSpec spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator(getResources().getString(R.string.dialog_song_details_tab2));
        host.addTab(spec);

        ImageView imgArtwork = (ImageView) rootView.findViewById(R.id.imgArtwork);

        onRequestAlbumArt.invoke(new AlbumArtRequest(videoThumbDataSource, artPath0, artPath1, artGenerateStr), imgArtwork, false, true);

        builder.setNegativeButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        return builder.create();

    }
}