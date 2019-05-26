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

package com.aveeopen.comp.ContextualActionBar;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;

import com.aveeopen.comp.LibraryQueueUI.ViewHolders.ContentItemViewHolder;
import com.aveeopen.ContextData;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemActionBase {

    private final int actionId;//used for ordering
    private final int iconResId;
    private final int nameStrResId;
    private final boolean allowMultiple, allowSingle;

    public ItemActionBase(int actionId, boolean allowMultiple, @DrawableRes int iconResId, @StringRes int nameStrResId) {
        this(actionId, allowMultiple, true, iconResId, nameStrResId);
    }

    public ItemActionBase(int actionId, boolean allowMultiple, boolean allowSingle, @DrawableRes int iconResId, @StringRes int nameStrResId) {
        this.allowMultiple = allowMultiple;
        this.allowSingle = allowSingle;
        this.actionId = actionId;
        this.iconResId = iconResId;
        this.nameStrResId = nameStrResId;
    }

    public void executeBase(ContextData contextData, Object item, ActionListenerBase listener) {
        List<Object> items = new ArrayList<>();
        List<ActionListenerBase> listeners = new ArrayList<>();

        items.add(item);
        listeners.add(listener);
        executeListBase(contextData, items, listeners);
    }

    public abstract void executeListBase(ContextData contextData, List<Object> items, List<ActionListenerBase> listeners);

    public boolean getShouldShow() {
        return true;
    }

    public interface OnClickListener {
        void onClick(View v, ContentItemViewHolder viewHolder);
    }

    public int getActionId() {
        return actionId;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getNameStrResId() {
        return nameStrResId;
    }

    public boolean isAllowMultiple() {
        return allowMultiple;
    }

    public boolean isAllowSingle() {
        return allowSingle;
    }
}
