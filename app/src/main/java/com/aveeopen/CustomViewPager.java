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

package com.aveeopen;

import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;

import com.aveeopen.R;

public class CustomViewPager extends ViewPager {

    float startDragX;
    OnSwipeOutListener listener;
    boolean eventFired = false;
    float swipeDistMin = 0.0f;
    float lastProgress = 0.0f;
    float maxProgress = 0.0f;

    public CustomViewPager(android.content.Context context) {
        super(context);
        swipeDistMin = context.getResources().getDimension(R.dimen.out_of_bound_swipe_dist);
    }

    public CustomViewPager(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        swipeDistMin = context.getResources().getDimension(R.dimen.out_of_bound_swipe_dist);

    }

    public void setOnSwipeOutListener(OnSwipeOutListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        float x = ev.getX();
        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startDragX = x;
                eventFired = false;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();
        float x = ev.getX();

        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startDragX = x;
                eventFired = false;
                lastProgress = 0.0f;
                maxProgress = 0.0f;
                listener.onSwipeProgressUpdate(maxProgress);

                break;
            case MotionEvent.ACTION_MOVE:
                if (eventFired) break;

                float progress = 0.0f;
                if (getCurrentItem() == 0) {
                    progress = (x - startDragX) / swipeDistMin;
                } else if (getCurrentItem() == getAdapter().getCount() - 1) {
                    progress = (startDragX - x) / swipeDistMin;
                }

                maxProgress = Math.max(maxProgress, progress);

                listener.onSwipeProgressUpdate(maxProgress);

                if ((x - startDragX) > swipeDistMin && getCurrentItem() == 0) {
                    maxProgress = 0.0f;
                    listener.onSwipeProgressUpdate(maxProgress);
                    listener.onSwipeOutAtStart();
                    eventFired = true;
                } else if ((startDragX - x) > swipeDistMin && getCurrentItem() == getAdapter().getCount() - 1) {
                    maxProgress = 0.0f;
                    listener.onSwipeProgressUpdate(maxProgress);
                    listener.onSwipeOutAtEnd();

                    eventFired = true;
                }


                break;
            case MotionEvent.ACTION_UP:
                if (eventFired) break;
                maxProgress = 0.0f;
                listener.onSwipeProgressUpdate(maxProgress);
                break;
        }
        return super.onTouchEvent(ev);
    }

    public interface OnSwipeOutListener {
        void onSwipeOutAtStart();

        void onSwipeOutAtEnd();

        void onSwipeProgressUpdate(float val);
    }

}