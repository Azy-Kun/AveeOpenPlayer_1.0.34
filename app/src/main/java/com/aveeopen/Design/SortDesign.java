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

package com.aveeopen.Design;

import android.app.FragmentManager;

import com.aveeopen.comp.LibraryQueueUI.Dialog.SortDialog;
import com.aveeopen.comp.LibraryQueueUI.LibraryQueueFragmentBase;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.Common.Events.WeakEventR2;
import com.aveeopen.Common.Events.WeakEventR3;
import com.aveeopen.Common.MultiList;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.Common.UtilsFileSys;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.comp.LibraryQueueUI.Fragment0;
import com.aveeopen.ContextData;
import com.aveeopen.MainActivity;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class SortDesign {

    public static final int Sort_Mode_Title = 0;
    public static final int Sort_Mode_Artist = 1;
    public static final int Sort_Mode_Album = 2;
    public static final int Sort_Mode_Path = 3;
    public static final int Sort_Mode_DateAdded = 4;
    public static final int Sort_Mode_DateModified = 5;
    public static final int Sort_Mode_Duration = 6;
    public static final int Sort_Mode_Size = 7;

    public static final int Sort_Descending = 0x1;

    private MultiList<Integer, String> radioOptions = new MultiList<>();
    private MultiList<Integer, String> checkOptions = new MultiList<>();
    private List<Object> listenerRefHolder = new LinkedList<>();

    public SortDesign() {

        radioOptions.add(Sort_Mode_Title, "Title/Name");
        radioOptions.add(Sort_Mode_Artist, "Artist");
        radioOptions.add(Sort_Mode_Album, "Album");
        radioOptions.add(Sort_Mode_Path, "File Path");
        radioOptions.add(Sort_Mode_DateAdded, "Date Added");
        radioOptions.add(Sort_Mode_DateModified, "Date Modified");
        radioOptions.add(Sort_Mode_Duration, "Duration");
        radioOptions.add(Sort_Mode_Size, "Size");
        //
        checkOptions.add(Sort_Descending, "Descending");

        LibraryQueueFragmentBase.onActionChooseSort.subscribeWeak(new WeakEvent1.Handler<ContextData>() {
            @Override
            public void invoke(ContextData contextData) {
                FragmentManager fragmentManager = contextData.getFragmentManager();

                if (fragmentManager != null) {
                    SortDialog.createAndShowDialog(fragmentManager, 0);
                }
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onActionChooseSortFiles.subscribeWeak(new WeakEvent1.Handler<ContextData>() {
            @Override
            public void invoke(ContextData contextData) {
                FragmentManager fragmentManager = contextData.getFragmentManager();
                if (fragmentManager != null) {
                    SortDialog.createAndShowDialog(fragmentManager, 1);
                }
            }
        }, listenerRefHolder);

        SortDialog.onRequestSortOptions.subscribeWeak(new WeakEventR2.Handler<Integer, IGeneralItemContainerIdentifier, SortOptions>() {
            @Override
            public SortOptions invoke(Integer pageIndex, IGeneralItemContainerIdentifier containerIdentifier) {
                return getSortOptions();
            }
        }, listenerRefHolder);

        SortDialog.onSubmitSortOptions.subscribeWeak(new WeakEvent4.Handler<Integer, IGeneralItemContainerIdentifier, Integer, Integer>() {
            @Override
            public void invoke(Integer pageIndex, IGeneralItemContainerIdentifier containerIdentifier, Integer sortRadioOption, Integer sortMaskOptions) {
                AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_SortSelectedRadioOption, sortRadioOption);
                AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_SortMaskCheckOptions, sortMaskOptions);
                updateLibraryItems();
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onRequestCurrentSortDesc.subscribeWeak(new WeakEventR2.Handler<Integer, IGeneralItemContainerIdentifier, SortDesc>() {
            @Override
            public SortDesc invoke(Integer pageIndex, IGeneralItemContainerIdentifier containerIdentifier) {
                int selectedRadioOption = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_SortSelectedRadioOption);
                int maskCheckOptions = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_SortMaskCheckOptions);
                SortDesc sortDesc = new SortDesc();
                sortDesc.sortModeIndex = selectedRadioOption;
                sortDesc.sortDescending = (maskCheckOptions & Sort_Descending) != 0;
                return sortDesc;
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onRequestFilterFileResult.subscribeWeak(new WeakEventR3.Handler<Integer, IGeneralItemContainerIdentifier, File, Boolean>() {
            @Override
            public Boolean invoke(Integer pageIndex, IGeneralItemContainerIdentifier containerIdentifier, File file) {

                if (file.isDirectory())
                    return true;

                String ext = UtilsFileSys.extractFilenameExt(file);

                if (ext.equals("mp3")) return true;
                if (ext.equals("wav")) return true;

                if (ext.equals("mp4")) return true;
                if (ext.equals("m4a")) return true;
                if (ext.equals("m4p")) return true;
                if (ext.equals("m4b")) return true;
                if (ext.equals("m4r")) return true;
                if (ext.equals("m4v")) return true;

                if (ext.equals("mp4v")) return true;

                if (ext.equals("3gp")) return true;
                if (ext.equals("3g2")) return true;

                if (ext.equals("3gp2")) return true;
                if (ext.equals("3gpp")) return true;

                if (ext.equals("3ga")) return true;

                if (ext.equals("webm")) return true;
                //exo cant...native can
                if (ext.equals("flv")) return true;

                if (ext.equals("aac")) return true;

                if (ext.equals("mkv")) return true;
                if (ext.equals("fmp4")) return true;

                if (ext.equals("ts")) return true;
                if (ext.equals("tsv")) return true;
                if (ext.equals("tsa")) return true;

                if (ext.equals("flac")) return true;

                if (ext.equals("mid")) return true;
                if (ext.equals("midi")) return true;
                if (ext.equals("rmi")) return true;

                if (ext.equals("xmf")) return true;
                if (ext.equals("mxmf")) return true;
                if (ext.equals("rtttl")) return true;
                if (ext.equals("rtx")) return true;
                if (ext.equals("ota")) return true;
                if (ext.equals("imy")) return true;

                if (ext.equals("ogg")) return true;
                //
                if (ext.equals("asf")) return true;
                if (ext.equals("wma")) return true;
                if (ext.equals("wmv")) return true;
                if (ext.equals("wm")) return true;

                if (ext.equals("asx")) return true;
                if (ext.equals("wax")) return true;
                if (ext.equals("wvx")) return true;
                if (ext.equals("wmx")) return true;

                if (ext.equals("wpl")) return true;
                if (ext.equals("dvr-ms")) return true;
                if (ext.equals("wmd")) return true;
                if (ext.equals("avi")) return true;

                if (ext.equals("mpg")) return true;
                if (ext.equals("mpeg")) return true;
                if (ext.equals("m1v")) return true;
                if (ext.equals("mp2")) return true;
                if (ext.equals("mpa")) return true;
                if (ext.equals("mpe")) return true;

                if (ext.equals("mpga")) return true;

                if (ext.equals("aif")) return true;
                if (ext.equals("aifc")) return true;
                if (ext.equals("aiff")) return true;

                if (ext.equals("au")) return true;
                if (ext.equals("snd")) return true;
                if (ext.equals("cda")) return true;
                if (ext.equals("ivf")) return true;

                if (ext.equals("mov")) return true;

                if (ext.equals("adt")) return true;
                if (ext.equals("adts")) return true;

                if (ext.equals("m2ts")) return true;

                if (ext.equals("amr")) return true;
                if (ext.equals("aup")) return true;
                if (ext.equals("caf")) return true;
                if (ext.equals("kar")) return true;

                if (ext.equals("mmf")) return true;
                if (ext.equals("oma")) return true;
                if (ext.equals("opus")) return true;
                if (ext.equals("qcp")) return true;

                if (ext.equals("ra")) return true;
                if (ext.equals("ram")) return true;

                if (ext.equals("xspf")) return true;
                //
                if (ext.equals("m3u")) return true;
                if (ext.equals("m3u8")) return true;

                return false;
            }
        }, listenerRefHolder);

    }

    public static <T> boolean contains(final T[] array, final T v) {
        for (final T e : array)
            if (e == v || v != null && v.equals(e))
                return true;

        return false;
    }

    public static boolean contains(final int[] array, final int v) {
        for (final int e : array)
            if (e == v)
                return true;

        return false;
    }

    public SortOptions getSortOptions() {
        int selectedRadioOption = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_SortSelectedRadioOption);
        int maskCheckOptions = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_SortMaskCheckOptions);

        SortOptions options = new SortOptions();

        options.radioOptions = radioOptions;

        options.checkOptions = checkOptions;
        options.selectedRadioOption = selectedRadioOption;
        options.maskCheckOptions = maskCheckOptions;

        return options;
    }
    private void updateLibraryItems() {
        Fragment0 fragment0 = MainActivity.getFragment0Instance();
        if (fragment0 != null) fragment0.updateLibraryItems();
    }

    public static class SortDesc {
        public int sortModeIndex;
        public boolean sortDescending;
    }

    public static class SortOptions {
        public MultiList<Integer, String> radioOptions;
        public MultiList<Integer, String> checkOptions;

        public int selectedRadioOption;
        public int maskCheckOptions;

        public SortOptions getRefined(int... supportedOptions) {
            SortOptions refined = new SortOptions();

            refined.radioOptions = new MultiList<>();

            for (Tuple2<Integer, String> opt : this.radioOptions) {
                if (contains(supportedOptions, opt.obj1))
                    refined.radioOptions.add(opt);
            }

            refined.checkOptions = this.checkOptions;
            refined.selectedRadioOption = this.selectedRadioOption;
            refined.maskCheckOptions = maskCheckOptions;

            return refined;
        }
    }
}
