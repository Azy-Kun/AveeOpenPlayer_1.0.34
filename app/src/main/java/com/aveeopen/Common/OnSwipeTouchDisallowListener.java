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

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewParent;

public class OnSwipeTouchDisallowListener implements OnTouchListener {

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        attemptClaimDrag(v);
        return v.onTouchEvent(event);
    }

    /**
     * Tries to claim the user's drag motion, and requests disallowing any
     * ancestors from stealing events in the drag.
     */
    private void attemptClaimDrag(View v) {
        if (v.getParent() != null) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
        }
    }
}

class OnSwipeTouchDisallowParentListener implements OnTouchListener {

    ViewParent disallowParent;

    public OnSwipeTouchDisallowParentListener(ViewParent disallowParent) {
        this.disallowParent = disallowParent;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        disallowParent.requestDisallowInterceptTouchEvent(true);
        return v.onTouchEvent(event);
    }

}

