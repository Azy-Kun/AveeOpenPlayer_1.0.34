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

package com.aveeopen.comp.MediaControlsUI;

import android.text.Layout;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

class MyTitleScrollingMovementMethod implements MovementMethod {

    private static MyTitleScrollingMovementMethod sInstance;

    public static MovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new MyTitleScrollingMovementMethod();

        return sInstance;
    }

    @Override
    public void initialize(TextView widget, Spannable text) {
    }

    @Override
    public boolean onKeyDown(TextView widget, Spannable text, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(TextView widget, Spannable text, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyOther(TextView view, Spannable text, KeyEvent event) {
        return false;
    }

    @Override
    public void onTakeFocus(TextView widget, Spannable text, int dir) {
        Layout layout = widget.getLayout();

        if (layout != null && (dir & View.FOCUS_FORWARD) != 0) {
            widget.scrollTo(widget.getScrollX(),
                    layout.getLineTop(0));
        }
        if (layout != null && (dir & View.FOCUS_BACKWARD) != 0) {
            int padding = widget.getTotalPaddingTop() +
                    widget.getTotalPaddingBottom();
            int line = layout.getLineCount() - 1;

            widget.scrollTo(widget.getScrollX(),
                    layout.getLineTop(line + 1) -
                            (widget.getHeight() - padding));
        }
    }

    @Override
    public boolean onTrackballEvent(TextView widget, Spannable text, MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        return false;
    }

    @Override
    public boolean onGenericMotionEvent(TextView widget, Spannable text, MotionEvent event) {
        return false;
    }

    @Override
    public boolean canSelectArbitrarily() {
        return false;
    }
}
