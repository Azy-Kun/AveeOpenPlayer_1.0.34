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

package com.aveeopen.comp.VisualUI;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
//import android.test.UiThreadTest;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.Common.UtilsUI;
import com.aveeopen.ContextData;
import com.aveeopen.comp.LibraryQueueUI.MyView;
import com.aveeopen.comp.Visualizer.Elements.Element;
import com.aveeopen.comp.Visualizer.VisualizerViewCore;
import com.aveeopen.R;
import com.google.android.exoplayer.AspectRatioFrameLayout;

public class Fragment2 extends Fragment {

    public static WeakEvent1<VisualizerViewCore /*surface*/> onSurfaceCreated = new WeakEvent1<>();
    public static WeakEventR<Boolean> onRequestShowVideoContentState = new WeakEventR<>();
    public static WeakEvent onToggleVideoScalingMode = new WeakEvent();
    public static WeakEventR<Integer> onRequestVideoScalingMode = new WeakEventR<>();
    public static WeakEventR<Float> onRequestVideoWidthHeightRatio = new WeakEventR<>();
    public static WeakEvent onToggleVisualPreferShowContent = new WeakEvent();
    public static WeakEvent1<SurfaceHolder /*holder*/> onVideoSurfaceHolderCreated = new WeakEvent1<>();
    public static WeakEvent onVideoSurfaceHolderDestroyed = new WeakEvent();
    public static WeakEventR<Boolean> onRequestUIComponentNeedChangedValue = new WeakEventR<>();
    public static WeakEvent onVideoElementInteracted = new WeakEvent();
    public static WeakEvent1<Boolean /*need*/> onUIComponentNeedChanged = new WeakEvent1<>();//unused?
    public static WeakEvent onCustomizeAction = new WeakEvent();
    public static WeakEvent4<ContextData /*contextData*/, Integer /*rootIdentifier*/, Element.CustomizationList /*customizationList*/, Integer /*elementIndex*/> onPickElementAction = new WeakEvent4<>();
    public static WeakEvent3<ContextData /*contextData*/, Integer /*rootIdentifier*/, Element.CustomizationList /*customizationList*/> onResetAction = new WeakEvent3<>();

    private View rootView;
    private AspectRatioFrameLayout videoFrame;
    private VisualizerViewCore surfaceViewVisualizer;
    private SurfaceView surfaceViewVideo;
    private int surfaceViewTag = 0;
    private int surfaceViewVideoTag = 0;
    private View layoutButtons;
    private ImageButton btn1;
    private ImageButton btn3;
    private float widthHeightRatio = 0.0f;

    public Fragment2() {
    }

    public static Fragment2 newInstance() {
        Fragment2 fragment = new Fragment2();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_2, container, false);

        setStatusBarDimensions(rootView.findViewById(R.id.viewStatusBarBg));

        rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                final int w = right - left;
                final int h = bottom - top;

                rootView.post(new Runnable() {
                    @Override
                    public void run() {
                        updateVideoSize(w, h);
                    }
                });

            }
        });

        layoutButtons = rootView.findViewById(R.id.layoutButtons);

        ImageButton btn0 = (ImageButton) layoutButtons.findViewById(R.id.btn0);
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createThemeChooserMenu(v);
            }
        });

        btn1 = (ImageButton) layoutButtons.findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCustomizeAction.invoke();
            }
        });

        ImageButton btn4 = (ImageButton) layoutButtons.findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleVisualPreferShowContent.invoke();
            }
        });

        btn3 = (ImageButton) layoutButtons.findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleVideoScalingMode.invoke();
            }
        });
        //

        MyView surfaceViewBackground = (MyView) rootView.findViewById(R.id.surfaceViewBackground);
        surfaceViewBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVideoElementInteracted.invoke();
            }
        });

        videoFrame = (AspectRatioFrameLayout) rootView.findViewById(R.id.videoFrame);
        surfaceViewVisualizer = (VisualizerViewCore) rootView.findViewById(R.id.surfaceViewVisualizer);
        surfaceViewVisualizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVideoElementInteracted.invoke();
            }
        });

        surfaceViewVideo = (SurfaceView) rootView.findViewById(R.id.surfaceViewVideo);
        surfaceViewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVideoElementInteracted.invoke();
            }
        });

        onSurfaceCreated.invoke(surfaceViewVisualizer);

        if (surfaceViewVideo != null) {
            final SurfaceHolder surfaceHolder = surfaceViewVideo.getHolder();
            onVideoSurfaceHolderCreated.invoke(surfaceHolder);
        }

        int _videoScalingMode = onRequestVideoScalingMode.invoke(0);
        updateVideoScaleMode(_videoScalingMode);

        float _widthHeightRatio = onRequestVideoWidthHeightRatio.invoke(1.0f);
        setVideoSize(_widthHeightRatio);

        {
            boolean need = onRequestUIComponentNeedChangedValue.invoke(true);
            boolean showVideoContent = onRequestShowVideoContentState.invoke(false);
            updateSurfaceVisibility(need, showVideoContent);
        }


        return rootView;
    }


    @Override
    public void onDestroyView() {
        onVideoSurfaceHolderDestroyed.invoke();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    boolean isViewCreated()
    {
        return  rootView!=null;
    }

    boolean isSurfaceVisible() {
        return surfaceViewVideo != null && (surfaceViewVideo.getVisibility() == View.VISIBLE || surfaceViewVideoTag == 1) ||
                surfaceViewVisualizer != null && (surfaceViewVisualizer.getVisibility() == View.VISIBLE || surfaceViewTag == 1);

    }

    public void updateSurfaceVisibility(boolean visible, boolean showVideoContent) {
        //we do little delay show, so when we swipe to other fragment we don't get immediate "hang"
        if (visible) {
            if (showVideoContent) {

                if (surfaceViewVisualizer != null) {
                    surfaceViewTag = 0;
                    surfaceViewVisualizer.setVisibility(View.GONE);
                }

                if (surfaceViewVideo != null) {
                    surfaceViewVideoTag = 1;
                    surfaceViewVideo.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (surfaceViewVideoTag == 1) {
                                surfaceViewVideo.setVisibility(View.VISIBLE);
                                final SurfaceHolder surfaceHolder = surfaceViewVideo.getHolder();
                                onVideoSurfaceHolderCreated.invoke(surfaceHolder);

                            }
                        }
                    }, 250);
                }

            } else {
                if (surfaceViewVideo != null) {
                    surfaceViewVideoTag = 0;
                    surfaceViewVideo.setVisibility(View.GONE);
                }
                if (surfaceViewVisualizer != null) {
                    surfaceViewTag = 1;
                    surfaceViewVisualizer.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (surfaceViewTag == 1) {
                                surfaceViewVisualizer.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 250);
                }
            }
        } else {
            if (surfaceViewVideo != null) {
                surfaceViewVideoTag = 0;
                surfaceViewVideo.setVisibility(View.GONE);
            }
            if (surfaceViewVisualizer != null) {
                surfaceViewTag = 0;
                surfaceViewVisualizer.setVisibility(View.GONE);
            }
        }
    }

    //@UiThreadTest
    public void setShowVideoContentState(boolean state) {
        updateSurfaceVisibility(isSurfaceVisible(), state);
    }

    public void updateVideoScaleMode(int mode) {
        if (mode == 1) {
            btn3.setImageResource(R.drawable.ic_vis_fit3);
        } else if (mode == 2) {
            btn3.setImageResource(R.drawable.ic_vis_fit_crop3);
        } else if (mode == 3) {
            btn3.setImageResource(R.drawable.ic_vis_stretch3);
        }
    }

    public void setVideoSize(float widthHeightRatio) {
        if(!isViewCreated()) return;
        this.widthHeightRatio = widthHeightRatio;
        updateVideoSize(rootView.getWidth(), rootView.getHeight());
    }

    public void setVideoSizeTh(final float widthHeightRatio) {
        if(!isViewCreated()) return;
        rootView.post(new Runnable() {
            @Override
            public void run() {
                Fragment2.this.widthHeightRatio = widthHeightRatio;
                updateVideoSize(rootView.getWidth(), rootView.getHeight());
            }
        });
    }

    void updateVideoSize(float w, float h) {
        if (widthHeightRatio == 0.0f) {
            float fullScreenRatio;
            if (w > 0.0f && h > 0.0f) {
                fullScreenRatio = w / h;
                if (videoFrame != null) videoFrame.setAspectRatio(fullScreenRatio);
            }
        } else {
            if (videoFrame != null) videoFrame.setAspectRatio(widthHeightRatio);
        }
    }

    public void animateShow(boolean show) {

        if (layoutButtons == null) return;

        int mShortAnimTime;

        mShortAnimTime = layoutButtons.getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        if (show) {
            layoutButtons.animate()
                    .translationX(0).alpha(1.0f)
                    .setDuration(mShortAnimTime);
        } else {
            layoutButtons.animate()
                    .translationX(layoutButtons.getWidth()).alpha(0.0f)
                    .setDuration(mShortAnimTime);
        }
    }

    private void createThemeChooserMenu(View v) {
        ContextData contextData = new ContextData(getActivity());
        FragmentManager fragmentManager = contextData.getFragmentManager();

        if (fragmentManager != null) {
            VisualizerStyleDialog.createAndShowDialog(fragmentManager);
        }
    }

    public void showCustomizationMenu(Tuple2<Integer, Element.CustomizationList> currentCustomization) {
        View v = btn1;
        if (v == null) return;

        if (currentCustomization == null) return;
        if (currentCustomization.obj2 == null) return;

        final int rootIdentifier = currentCustomization.obj1;
        final Element.CustomizationList customizationDataList = currentCustomization.obj2;

        PopupMenu popup = new PopupMenu(v.getContext(), v);


        MenuItem menuItem = popup.getMenu().add(Menu.NONE,
                0,
                0,
                this.getString(R.string.reset_visualize));

        for (int i = 0; i < customizationDataList.dataCount(); i++) {
            Element.CustomizationData customizationData = customizationDataList.getData(i);

            String name = customizationData.getCustomizationName();
            if (name == null || name.isEmpty())
                continue;//skip empty, eg element have no customization

            MenuItem menuItem2 = popup.getMenu().add(Menu.NONE,
                    i + 1,
                    i + 1,
                    name
            );
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();

                if(id == 0) {
                    onResetAction.invoke(new ContextData(getActivity()), rootIdentifier, customizationDataList);
                } else {
                    id = id - 1;
                    if (id >= 0 && id < customizationDataList.dataCount()) {
                        onPickElementAction.invoke(new ContextData(getActivity()), rootIdentifier, customizationDataList, id);
                    }
                }

                return true;
            }
        });

        popup.show();
    }

    private static void setStatusBarDimensions(View view) {
        if (view == null) return;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = UtilsUI.getStatusBarHeight(view.getContext());
    }
}