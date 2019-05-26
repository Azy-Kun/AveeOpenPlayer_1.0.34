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

package com.aveeopen.comp.LibraryQueueUI;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private Paint myPaint = new Paint();
    private int space;

    public SpacesItemDecoration(int space, int bgColor) {
        this.space = space;
        myPaint.setColor(bgColor);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount-1; i++) {//skip last
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + space;

            c.drawRect(left, top, right, bottom, myPaint);
        }

//        //workaround
//        //to fill space below items, when there aren't enough items to fill screen,
//        // and we don't want to change background color!
//        int count = parent.getChildCount();
//
//        int top = 0;
//
//        if (count > 0) {
//            View lastChild = parent.getChildAt(count - 1);
//            top = lastChild.getBottom();
//        }
//
//        c.drawRect(0.0f, top, parent.getWidth(), parent.getHeight(), myPaint);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = 0;
        outRect.right = 0;
        outRect.bottom = space;
        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) == 0)
            outRect.top = space;

//        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1)
//            outRect.bottom = space;

    }
}