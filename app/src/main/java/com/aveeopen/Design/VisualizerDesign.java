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

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.aveeopen.AppPermissions;
import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.Events.WeakEventR1;
import com.aveeopen.Common.Events.WeakEventR2;
import com.aveeopen.Common.Events.WeakEventR3;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.Common.Utils;
import com.aveeopen.comp.AlbumArt.AlbumArtCore;
import com.aveeopen.comp.AlbumArt.AlbumArtRequest;
import com.aveeopen.comp.AlbumArt.ImageLoadedListener;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.Common.IItemIdentifier;
import com.aveeopen.comp.VisualUI.CustomizeVisDialog;
import com.aveeopen.comp.playback.AudioFrameData;
import com.aveeopen.comp.playback.EventsPlaybackService;
import com.aveeopen.comp.playback.IMediaPlayerCore;
import com.aveeopen.comp.playback.MediaPlaybackService;
import com.aveeopen.comp.playback.PlayingMediaInfo;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.comp.VisualUI.Fragment2;
import com.aveeopen.comp.VisualUI.VisualizerStyleDialog;
import com.aveeopen.comp.VisualUI.VisualizerThemeInfo;
import com.aveeopen.comp.Visualizer.Elements.Element;
import com.aveeopen.comp.Visualizer.Elements.RootElement;
import com.aveeopen.comp.Visualizer.VisualizerViewCore;
import com.aveeopen.ContextData;
import com.aveeopen.EventsGlobal.EventsGlobalTextNotifier;
import com.aveeopen.MainActivity;
import com.aveeopen.PlayerCore;
import com.aveeopen.R;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class VisualizerDesign {

    private volatile WeakReference<SurfaceHolder> surfaceHolder = new WeakReference<>(null);
    private WeakReference<VisualizerViewCore> visualizerSurfaceView = new WeakReference<>(null);
    private volatile float videoWidthHeightRatio = 1.0f;
    private boolean uiNeedShowVisual = true;
    private List<Object> listenerRefHolder = new LinkedList<>();
    private Handler threadHandler = new Handler();

    public VisualizerDesign() {

        MediaPlaybackService.onDisplayMetaDataStateChanged.subscribeWeak(new WeakEvent4.Handler<PlaylistSong, IItemIdentifier, PlaylistSong.Data, PlayingMediaInfo>() {
            @Override
            public void invoke(PlaylistSong currentTrack, IItemIdentifier _currentItemIdent, PlaylistSong.Data currentTrackData, PlayingMediaInfo playingMediaInfo) {
                //TODO: Currently we can't detect correctly if video or song is playing
//                if (playingMediaInfo.containsVideoTrack) {
//                    AppPreferences.createOrGetInstance().setBool(AppPreferences.PREF_Bool_visualPreferShowVideoContent, true);
//                } else {
//                    AppPreferences.createOrGetInstance().setBool(AppPreferences.PREF_Bool_visualPreferShowVideoContent, false);
//                }
            }
        }, listenerRefHolder);
        MediaPlaybackService.onRequestVideoScalingMode.subscribeWeak(new WeakEventR.Handler<Integer>() {
            @Override
            public Integer invoke() {
                return getPlayerbackVideoScalingMode();
            }
        }, listenerRefHolder);
        MediaPlaybackService.onRequestVideoSurfaceHolder.subscribeWeak(new WeakEventR.Handler<SurfaceHolder>() {
            @Override
            public SurfaceHolder invoke() {
                return surfaceHolder.get();
            }
        }, listenerRefHolder);
        MediaPlaybackService.onVideoSizeChanged.subscribeWeak(new WeakEvent3.Handler<Integer, Integer, Float>() {
            @Override
            public void invoke(Integer width, Integer height, Float widthHeightRatio) {
                videoWidthHeightRatio = widthHeightRatio;

                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Fragment2 fragment2 = MainActivity.getFragment2Instance();
                        if (fragment2 != null) {
                            fragment2.setVideoSizeTh(getSurfaceVideoSize(videoWidthHeightRatio));
                        }
                    }
                });
            }
        }, listenerRefHolder);

        Fragment2.onSurfaceCreated.subscribeWeak(new WeakEvent1.Handler<VisualizerViewCore>() {
            @Override
            public void invoke(VisualizerViewCore visualizerView) {
                visualizerSurfaceView = new WeakReference<>(visualizerView);

                int themeId = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_visualizerThemeId);

                RootElement visualizerThemeElementRoot = VisualizerThemes.s().getThemeObject(themeId);

                if(visualizerThemeElementRoot != null) {
                    Element.CustomizationList customizationList = AppPreferences.createOrGetInstance().getPrefThemeCustomizationData(visualizerThemeElementRoot.getIdentifier());
                    applyThemeCustomizationData(visualizerThemeElementRoot, customizationList);
                    visualizerView.setThemeElements(visualizerThemeElementRoot);
                }
            }
        }, listenerRefHolder);

        VisualizerViewCore.onRequestsSoundVisualizationData.subscribeWeak(new WeakEventR1.Handler<AudioFrameData, AudioFrameData>() {
            @Override
            public AudioFrameData invoke(final AudioFrameData outResult) {
                boolean useGlobalSession = AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_visualizerUseGlobalSession);
                return EventsPlaybackService.Receive.getVisualizationData.invoke(outResult, useGlobalSession, null);
            }
        }, listenerRefHolder);

        VisualizerViewCore.onRequestMeasureText.subscribeWeak(new WeakEventR2.Handler<String, VisualizerViewCore, String>() {
            @Override
            public String invoke(String val, VisualizerViewCore visualizerViewCore) {
                if (val == null) return "";
                if (val.length() > 0 && val.charAt(0) != '$') return val;

                if ("$timeCurrent".equals(val)) {
                    long trackPosition = PlaybackDesign.trackPosition;
                    return Utils.getDurationStringHMSS((int) (trackPosition / 1000));
                } else if ("$timeLength".equals(val)) {
                    long duration = PlaybackDesign.playingMediaInfo.duration;
                    return Utils.getDurationStringHMSS((int) (duration / 1000));
                } else if ("$artist".equals(val)) {
                    PlaylistSong.Data songData = PlaybackControlsDesign.fieldsongData;
                    return songData.isArtistKnown() ? songData.artistName : "";
                } else if ("$title".equals(val)) {
                    return PlaybackControlsDesign.fieldsongData.trackName;
                } else if ("$album".equals(val)) {
                    return PlaybackControlsDesign.fieldsongData.albumName;
                } else if ("$fps".equals(val)) {
                    return "" + visualizerViewCore.getFps();
                } else if ("$frametime".equals(val)) {
                    return "" + visualizerViewCore.getFrameTimeMs();
                }

                return val;
            }
        }, listenerRefHolder);


        VisualizerViewCore.onRequestMeasureVec2f.subscribeWeak(new WeakEventR3.Handler<String, PointF, Float, PointF>() {
            @Override
            public PointF invoke(String val, PointF defaultValue, Float frameDataRmsValue) {
                if (val == null) return defaultValue;
                if (val.length() > 0 && val.charAt(0) != '$') return defaultValue;

                if ("$isPlaying".equals(val)) {
                    return PlaybackDesign.isPlaying ? new PointF(1.0f, 1.0f) : new PointF(0.0f, 0.0f);
                } else if (val.equals("$rms")) {
                    return new PointF(frameDataRmsValue, frameDataRmsValue);
                }

                return defaultValue;
            }
        }, listenerRefHolder);

        VisualizerViewCore.onRequestsAlbumArtPath.subscribeWeak(new WeakEventR.Handler<AlbumArtRequest>() {
            @Override
            public AlbumArtRequest invoke() {
                PlaylistSong.Data songData = PlaybackControlsDesign.fieldsongData;
                if (songData == PlaylistSong.emptyData)
                    return null;

                return new AlbumArtRequest(songData.getVideoThumbDataSourceAsStr(), songData.getAlbumArtPath0Str(), songData.getAlbumArtPath1Str(), songData.getAlbumArtGenerateStr());
            }
        }, listenerRefHolder);

        VisualizerViewCore.onRequestAlbumArtPathAndBitmap.subscribeWeak(new WeakEvent4.Handler<ImageLoadedListener, Integer, Integer, AlbumArtRequest>() {
            @Override
            public void invoke(
                    final ImageLoadedListener loadedListener,
                    final Integer targetBoundsWidth,
                    final Integer targetBoundsHeight,
                    final AlbumArtRequest albumArtRequest) {

                ImageLoadedListener loadedListener2 = new ImageLoadedListener() {

                    Object object1;

                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, final String url00, final String url0, final String url1) {

                        VisualizerViewCore surfaceView = visualizerSurfaceView.get();
                        if (surfaceView != null)
                            surfaceView.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    loadedListener.onBitmapLoaded(bitmap, url00, url0, url1);
                                }
                            });
                    }

                    @Override
                    public void setUserObject1(Object obj1) {
                        object1 = obj1;
                    }
                };

                loadedListener.setUserObject1(loadedListener2);//keep reference

                AlbumArtCore albumArtCore = AlbumArtCore.getInstance();
                if (albumArtCore != null) {
                    albumArtCore.loadAlbumArtLarge(
                            albumArtRequest.videoThumbDataSource,
                            albumArtRequest.path0,
                            albumArtRequest.path1,
                            albumArtRequest.genStr,
                            loadedListener2,
                            targetBoundsWidth,
                            targetBoundsHeight
                    );
                }


            }
        }, listenerRefHolder);


        VisualizerStyleDialog.onRequestSkinThemePresetList.subscribeWeak(new WeakEventR1.Handler<List<VisualizerThemeInfo>, Integer>() {
            @Override
            public Integer invoke(List<VisualizerThemeInfo> listOut) {
                List<Tuple2<VisualizerThemeInfo, VisualizerThemes.IVisualizerFactory>> themes = VisualizerThemes.s().getThemesList();

                for (Tuple2<VisualizerThemeInfo, VisualizerThemes.IVisualizerFactory> t : themes)
                    listOut.add(t.obj1);

                return AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_visualizerThemeId);
            }
        }, listenerRefHolder);

        VisualizerStyleDialog.onSkinThemePresetSelected.subscribeWeak(new WeakEvent1.Handler<VisualizerThemeInfo>() {
            @Override
            public void invoke(VisualizerThemeInfo presetInfo) {
                AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_visualizerThemeId, presetInfo.id, true);
                AppPreferences.createOrGetInstance().setBool(AppPreferences.PREF_Bool_visualPreferShowVideoContent, false);
            }
        }, listenerRefHolder);


        VisualizerViewCore.onRequestSelectedSkinThemePreset.subscribeWeak(new WeakEventR.Handler<RootElement>() {
            @Override
            public RootElement invoke() {
                int themeId = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_visualizerThemeId);

                RootElement visualizerThemeElementRoot = VisualizerThemes.s().getThemeObject(themeId);

                if(visualizerThemeElementRoot != null) {
                    Element.CustomizationList customizationList = AppPreferences.createOrGetInstance().getPrefThemeCustomizationData(visualizerThemeElementRoot.getIdentifier());
                    applyThemeCustomizationData(visualizerThemeElementRoot, customizationList);
                }

                return visualizerThemeElementRoot;
            }
        }, listenerRefHolder);

        VisualizerStyleDialog.onShowVideoContentAction.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_visualizerThemeId,
                        AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_visualizerThemeId), true);

                AppPreferences.createOrGetInstance().setBool(AppPreferences.PREF_Bool_visualPreferShowVideoContent, true);
            }
        }, listenerRefHolder);

        Fragment2.onRequestShowVideoContentState.subscribeWeak(new WeakEventR.Handler<Boolean>() {
            @Override
            public Boolean invoke() {
                return AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_visualPreferShowVideoContent);
            }
        }, listenerRefHolder);

        Fragment2.onToggleVideoScalingMode.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                int mode = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_videoScalingMode);

                if (mode == 1) mode = 2;
                else if (mode == 2) mode = 3;
                else if (mode == 3) mode = 1;
                else mode = 1;

                AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_videoScalingMode, mode);

                Context context = PlayerCore.s().getAppContext();
                if (context != null) {

                    String msg;

                    if (mode == 1) {
                        msg = context.getResources().getString(R.string.video_scaling_fit);
                    } else if (mode == 2) {
                        msg = context.getResources().getString(R.string.video_scaling_crop);
                    } else {
                        msg = context.getResources().getString(R.string.video_scaling_stretch);
                    }

                    EventsGlobalTextNotifier.onTextMsg.invoke(msg);
                }

            }
        }, listenerRefHolder);

        Fragment2.onRequestVideoScalingMode.subscribeWeak(new WeakEventR.Handler<Integer>() {
            @Override
            public Integer invoke() {
                return AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_videoScalingMode);
            }
        }, listenerRefHolder);

        Fragment2.onRequestVideoWidthHeightRatio.subscribeWeak(new WeakEventR.Handler<Float>() {
            @Override
            public Float invoke() {
                return getSurfaceVideoSize(videoWidthHeightRatio);
            }
        }, listenerRefHolder);


        Fragment2.onToggleVisualPreferShowContent.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                AppPreferences.createOrGetInstance().setInt(AppPreferences.PREF_Int_visualizerThemeId,
                        AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_visualizerThemeId), true);
                AppPreferences.createOrGetInstance().toggleBool(AppPreferences.PREF_Bool_visualPreferShowVideoContent);


                Context context = PlayerCore.s().getAppContext();
                if (context != null) {
                    if (AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_visualPreferShowVideoContent)) {
                        final String message = context.getResources().getString(R.string.switched_to_video);
                        EventsGlobalTextNotifier.onTextMsg.invoke(message);

                    } else {
                        final String message = context.getResources().getString(R.string.switched_to_visualizer);
                        EventsGlobalTextNotifier.onTextMsg.invoke(message);

                    }
                }
            }
        }, listenerRefHolder);

        Fragment2.onVideoSurfaceHolderCreated.subscribeWeak(new WeakEvent1.Handler<SurfaceHolder>() {
            @Override
            public void invoke(final SurfaceHolder holder) {
                surfaceHolder = new WeakReference<>(holder);

                EventsPlaybackService.Receive.setVideoSurfaceHolder.invoke(holder);
            }
        }, listenerRefHolder);

        Fragment2.onVideoSurfaceHolderDestroyed.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                surfaceHolder = new WeakReference<>(null);
            }
        }, listenerRefHolder);


        MainActivity.onViewPagerPageSelected.subscribeWeak(new WeakEvent2.Handler<Integer, Activity>() {
            @Override
            public void invoke(Integer page, Activity activity) {
                if (page == MainActivity.VISUAL_PAGE_INDEX) {


                    AppPermissions.is_RecordAudio_PermissionGranted(activity, activity);


                    uiNeedShowVisual = true;

                    boolean showVideoContent = AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_visualPreferShowVideoContent);
                    Fragment2 frag2 = MainActivity.getFragment2Instance();
                    if (frag2 != null) frag2.updateSurfaceVisibility(true, showVideoContent);

                } else {
                    uiNeedShowVisual = false;

                    boolean showVideoContent = AppPreferences.createOrGetInstance().getBool(AppPreferences.PREF_Bool_visualPreferShowVideoContent);
                    Fragment2 frag2 = MainActivity.getFragment2Instance();
                    if (frag2 != null) frag2.updateSurfaceVisibility(false, showVideoContent);
                }

            }
        }, listenerRefHolder);


        Fragment2.onUIComponentNeedChanged.subscribeWeak(new WeakEvent1.Handler<Boolean>() {
            @Override
            public void invoke(Boolean need) {
                uiNeedShowVisual = need;
            }
        }, listenerRefHolder);

        Fragment2.onRequestUIComponentNeedChangedValue.subscribeWeak(new WeakEventR.Handler<Boolean>() {
            @Override
            public Boolean invoke() {
                return uiNeedShowVisual;
            }
        }, listenerRefHolder);

        Fragment2.onVideoElementInteracted.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {

                MainActivity mainActivity = MainActivity.getInstance();
                if (mainActivity != null) {
                    if (mainActivity.currentFragmentPage == MainActivity.VISUAL_PAGE_INDEX) {
                        mainActivity.toggleShowControls(mainActivity.currentFragmentPage);
                        //mainActivity.showControls(false, mainActivity.currentFragmentPage);//un-hiding is handle by systemUIHider
                    }
                }
            }
        }, listenerRefHolder);

        //Themes
        Fragment2.onCustomizeAction.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                final VisualizerViewCore surfaceView = visualizerSurfaceView.get();
                if (surfaceView != null)
                    surfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            //GL thread
                            final Element.CustomizationList customization = new Element.CustomizationList();
                            final int rootIdentifier = surfaceView.readThemeCustomizationData(customization);

                            surfaceView.post(new Runnable() {
                                @Override
                                public void run() {
                                    //UI thread
                                    if (rootIdentifier >= 0) {
                                        Fragment2 fragment2 = MainActivity.getFragment2Instance();
                                        if (fragment2 != null)
                                            fragment2.showCustomizationMenu(new Tuple2<>(rootIdentifier, customization));
                                    }
                                }
                            });
                        }
                    });
            }
        }, listenerRefHolder);

        Fragment2.onPickElementAction.subscribeWeak(new WeakEvent4.Handler<ContextData, Integer, Element.CustomizationList, Integer>() {
            @Override
            public void invoke(ContextData contextData, Integer rootIdentifier, Element.CustomizationList customizationList, Integer colorIndex) {
                FragmentManager fragmentManager = contextData.getFragmentManager();
                if (fragmentManager != null) {
                    CustomizeVisDialog.createAndShowCustomizeVisDialog(fragmentManager, rootIdentifier, customizationList, colorIndex);
                }
            }
        }, listenerRefHolder);

        Fragment2.onResetAction.subscribeWeak(new WeakEvent3.Handler<ContextData, Integer, Element.CustomizationList>() {
            @Override
            public void invoke(ContextData contextData, Integer rootIdentifier, Element.CustomizationList customizationList) {

                int themeId = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_visualizerThemeId);
                RootElement visualizerThemeElementRoot = VisualizerThemes.s().getThemeObject(themeId);

                final Element.CustomizationList readCustomization = new Element.CustomizationList();
                final int readRootIdentifier = visualizerThemeElementRoot.readThemeCustomizationData(readCustomization);

                if(visualizerThemeElementRoot != null) {
                    //don't applyThemeCustomizationData
                    VisualizerViewCore surfaceView = visualizerSurfaceView.get();
                    if (surfaceView != null)
                        surfaceView.setThemeElements(visualizerThemeElementRoot);
                }

                AppPreferences.createOrGetInstance().savePrefThemeCustomizationData(readRootIdentifier, readCustomization);
                //
            }
        }, listenerRefHolder);
//
//        Runnable onCustomStructureChangedRunnable = new Runnable() {
//            public Element.CustomizationList readCustomization;
//            public int rootIdentifier;
//            public WeakEvent2<Integer /*rootIdentifier*/, Element.CustomizationList /*customizationList*/> onCustomStructureChanged = null;
//
//            @Override
//            public void run() {
//                tlog.w("onPickedColor 4");
//                //UI thread
//                if (onCustomStructureChanged != null)
//                    onCustomStructureChanged.invoke(rootIdentifier, readCustomization);
//            }
//        };

        CustomizeVisDialog.onPickedColor.subscribeWeak(new WeakEvent4.Handler<Integer, Element.CustomizationList, Integer, WeakEvent2<Integer, Element.CustomizationList>>() {
            @Override
            public void invoke(final Integer rootIdentifier, final Element.CustomizationList customizationList, Integer colorIndex, final WeakEvent2<Integer /*rootIdentifier*/, Element.CustomizationList /*customizationList*/> onCustomStructureChanged) {

                final VisualizerViewCore surfaceView = visualizerSurfaceView.get();

                if (surfaceView != null) {

                    final Element.CustomizationList customizationClone = customizationList.createClone();

                    surfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            //GL thread
                            surfaceView.setThemeCustomizationData(rootIdentifier, customizationClone);

                            final Element.CustomizationList readCustomization = new Element.CustomizationList();
                            final int rootIdentifier = surfaceView.readThemeCustomizationData(readCustomization);
                            if (rootIdentifier >= 0) {
                                surfaceView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //UI thread
                                        if (onCustomStructureChanged != null)
                                            onCustomStructureChanged.invoke(rootIdentifier, readCustomization);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }, listenerRefHolder);

        CustomizeVisDialog.onFinishedPickingColor.subscribeWeak(new WeakEvent3.Handler<Integer, Element.CustomizationList, Integer>() {
            @Override
            public void invoke(Integer rootIdentifier, Element.CustomizationList customizationList, Integer colorIndex) {
                AppPreferences.createOrGetInstance().savePrefThemeCustomizationData(rootIdentifier, customizationList);
            }
        }, listenerRefHolder);
        //

        AppPreferences.onIntPreferenceChanged.subscribeWeak(new WeakEvent3.Handler<Integer, Integer, Boolean>() {
            @Override
            public void invoke(Integer preference, Integer value, Boolean userForce) {
                if (preference == AppPreferences.PREF_Int_visualizerThemeId && userForce) {

                    int themeId = value;

                    RootElement visualizerThemeElementRoot = VisualizerThemes.s().getThemeObject(themeId);
                    if(visualizerThemeElementRoot != null) {
                        Element.CustomizationList customizationList = AppPreferences.createOrGetInstance().getPrefThemeCustomizationData(visualizerThemeElementRoot.getIdentifier());
                        applyThemeCustomizationData(visualizerThemeElementRoot, customizationList);

                        VisualizerViewCore surfaceView = visualizerSurfaceView.get();
                        if (surfaceView != null)
                            surfaceView.setThemeElements(visualizerThemeElementRoot);
                    }

                } else if (preference == AppPreferences.PREF_Int_videoScalingMode) {

                    final int videoScaling = getPlayerbackVideoScalingMode();
                    EventsPlaybackService.Receive.setVideoScalingMode.invoke(videoScaling);

                    Fragment2 fragment2 = MainActivity.getFragment2Instance();
                    if (fragment2 != null) {
                        fragment2.setVideoSize(getSurfaceVideoSize(videoWidthHeightRatio));
                        fragment2.updateVideoScaleMode(value);
                    }
                }
            }
        }, listenerRefHolder);

        AppPreferences.onBoolPreferenceChanged.subscribeWeak(new WeakEvent2.Handler<Integer, Boolean>() {
            @Override
            public void invoke(Integer preference, Boolean value) {
                if (preference == AppPreferences.PREF_Bool_visualPreferShowVideoContent) {

                    Fragment2 fragment2 = MainActivity.getFragment2Instance();
                    if (fragment2 != null) {
                        fragment2.setShowVideoContentState(value);
                    }
                }
            }
        }, listenerRefHolder);

        MainActivity.onFullscreenChanged.subscribeWeak(new WeakEvent1.Handler<Boolean>() {
            @Override
            public void invoke(Boolean fullScreen) {

                Fragment2 fragment2 = MainActivity.getFragment2Instance();
                if (fragment2 != null)
                    fragment2.animateShow(!fullScreen);
            }
        }, listenerRefHolder);
    }

    private int getPlayerbackVideoScalingMode() {
        int modePref = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_videoScalingMode);

        if (modePref == 1)
            return IMediaPlayerCore.MP_VIDEO_SCALING_MODE_SCALE_TO_FIT;//1 or 2
        else if (modePref == 2)
            return IMediaPlayerCore.MP_VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;
        else if (modePref == 3) return IMediaPlayerCore.MP_VIDEO_SCALING_MODE_SCALE_TO_FIT;
        else return IMediaPlayerCore.MP_VIDEO_SCALING_MODE_SCALE_TO_FIT;
    }

    //return : >0.0f - w/h ration ; 0.0f - full screen mode
    private float getSurfaceVideoSize(float videoWidthHeightRatio) {

        int modePref = AppPreferences.createOrGetInstance().getInt(AppPreferences.PREF_Int_videoScalingMode);

        if (modePref == 1)
            return videoWidthHeightRatio;
        else if (modePref == 2) return 0.0f;
        else if (modePref == 3) return 0.0f;
        else return videoWidthHeightRatio;

    }

    private void applyThemeCustomizationData(RootElement visualizerThemeElementRoot, Element.CustomizationList customizationList) {
        visualizerThemeElementRoot.setCustomization(customizationList);
    }

}
