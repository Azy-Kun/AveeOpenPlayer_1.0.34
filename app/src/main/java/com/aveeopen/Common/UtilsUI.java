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

package com.aveeopen.Common;

import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Looper;
import android.support.annotation.AttrRes;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;

import junit.framework.Assert;

public class UtilsUI {

    static int navBarHeight = -1;

    public static void AssertIsUiThread() {
        Assert.assertTrue(Looper.myLooper() == Looper.getMainLooper());
    }

    public static void AssertIsNotUiThread() {
        Assert.assertTrue(Looper.myLooper() != Looper.getMainLooper());
    }

    public static LayoutInflater getInflaterFromContext(Context context) {
        return LayoutInflater.from(context);
    }

    public static void disallowInterceptTouchEventRecursive(View v, ViewParent disallowParent) {

        v.setOnTouchListener(new OnSwipeTouchDisallowParentListener(disallowParent));
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;

            final int count = group.getChildCount();
            // Count backwards - let topmost views consume scroll distance first.
            for (int i = count - 1; i >= 0; i--) {
                // TODO: Add versioned support here for transformed views.
                // This will not work for transformed views in Honeycomb+
                final View child = group.getChildAt(i);
                disallowInterceptTouchEventRecursive(child, disallowParent);
            }
        }
    }

    public static void disallowInterceptTouchEventRecursive(View v) {

        v.setOnTouchListener(new OnSwipeTouchDisallowListener());
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;

            final int count = group.getChildCount();
            // Count backwards - let topmost views consume scroll distance first.
            for (int i = count - 1; i >= 0; i--) {
                // TODO: Add versioned support here for transformed views.
                // This will not work for transformed views in Honeycomb+
                final View child = group.getChildAt(i);
                disallowInterceptTouchEventRecursive(child);
            }
        }

    }

    public static void setViewStyle(View view, int colorPrimary, int colorSecondary) {

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); ++i) {
                View nextChild = ((ViewGroup) view).getChildAt(i);
                setViewStyle(nextChild, colorPrimary, colorSecondary);
            }
        }
        if (view instanceof TextView) {

            TextView v = (TextView) view;
            v.setTextColor(colorPrimary);
            v.setHintTextColor(colorSecondary);
            v.setLinkTextColor(colorSecondary);

        } else if (view instanceof ImageView) {
            ImageView v = (ImageView) view;
            v.setColorFilter(colorPrimary);
        }
    }

    public static int getNavBarHeightIgnoreOrienCached(Context c) {

        if (navBarHeight == -1)
            navBarHeight = getNavBarHeightIgnoreOrient(c);

        return navBarHeight;
    }


    public static int getNavBarHeightIgnoreOrient(Context c) {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey || !hasBackKey) {
            //The device has a navigation bar
            Resources resources = c.getResources();

            int resourceId;
            if (isTablet(c)) {
                resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            } else {
                resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            }

            if (resourceId > 0) {
                return c.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    public static int getNavBarHeight(Context c) {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            //The device has a navigation bar
            Resources resources = c.getResources();

            int orientation = c.getResources().getConfiguration().orientation;
            int resourceId;
            if (isTablet(c)) {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
            } else {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_width", "dimen", "android");
            }

            if (resourceId > 0) {
                return c.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }


    private static boolean isTablet(Context c) {
        return (c.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    public static void dismissSafe(ListPopupWindow popup) {
        if (popup == null) return;
        if (popup.isShowing())//if not checked you could get IllegalArgumentException:"View not attached to window manager"
            popup.dismiss();
    }

    public static void dismissSafe(PopupWindow popup) {
        if (popup == null) return;

        if (popup.isShowing()) //if not checked you could get IllegalArgumentException:"View not attached to window manager"
            try {
                popup.dismiss();
            } catch (Exception ignored) {
            }
    }

    public static void dismissSafe(DialogFragment dlg) {
        if (dlg == null) return;
        try {
            dlg.dismiss();
        } catch (Exception ignored) {
        }
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getAttrDrawableRes(View themeFromView, @AttrRes int attr)
    {
        TypedArray a = themeFromView.getContext().getTheme().obtainStyledAttributes(new int[] {attr});
        int attributeResourceId = a.getResourceId(0, 0);
        a.recycle();
        return attributeResourceId;
    }

    public static int getAttrDrawableRes(Resources.Theme theme, @AttrRes int attr)
    {
        TypedArray a = theme.obtainStyledAttributes(new int[]{attr});
        int attributeResourceId = a.getResourceId(0, 0);
        a.recycle();
        return attributeResourceId;
    }

    public static int getAttrColor(View themeFromView, @AttrRes int attr)
    {
        TypedArray a = themeFromView.getContext().getTheme().obtainStyledAttributes(new int[]{attr});
        int color = a.getColor(0, 0xffffffff);
        a.recycle();
        return color;
    }

    public static int getAttrColor(Resources.Theme theme, @AttrRes int attr)
    {
        TypedArray a = theme.obtainStyledAttributes(new int[]{attr});
        int color = a.getColor(0, 0xffffffff);
        a.recycle();
        return color;
    }

    public static int getAttrColorRes(View themeFromView, @AttrRes int attr)
    {
        TypedArray a = themeFromView.getContext().getTheme().obtainStyledAttributes(new int[]{attr});
        int attributeResourceId = a.getResourceId(0, 0);
        a.recycle();
        return attributeResourceId;
    }
}
