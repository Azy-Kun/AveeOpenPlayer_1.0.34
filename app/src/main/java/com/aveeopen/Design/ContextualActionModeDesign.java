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

import com.aveeopen.comp.LibraryQueueUI.LibraryQueueFragmentBase;
import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.Events.WeakEventR1;
import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.comp.ContextualActionBar.ActionListenerBase;
import com.aveeopen.comp.ContextualActionBar.ContextualActionBar;
import com.aveeopen.comp.ContextualActionBar.ItemSelection;
import com.aveeopen.ContextData;
import com.aveeopen.MainActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ContextualActionModeDesign {

    private boolean selectingEnabled = false;

    private HashMap<IGeneralItemContainerIdentifier, ItemSelection<Object>> itemSelectionContainers = new HashMap<>();
    private List<Object> listenerRefHolder = new LinkedList<>();

    public ContextualActionModeDesign() {

        ContextualActionBar.onSelectModeChanged.subscribeWeak(new WeakEvent1.Handler<Boolean>() {
            @Override
            public void invoke(Boolean selectingEnabled) {
                ContextualActionModeDesign.this.selectingEnabled = selectingEnabled;
            }
        }, listenerRefHolder);

        ContextualActionBar.onItemSelectionChanged.subscribeWeak(new WeakEvent2.Handler<ItemSelection.One<Object>, Boolean>() {
            @Override
            public void invoke(ItemSelection.One<Object> newItemSelection, Boolean select) {
                ItemSelection<Object> containerItemSel = itemSelectionContainers.get(newItemSelection.getContainerIdentifier());

                if (containerItemSel == null) {
                    containerItemSel = new ItemSelection<>(newItemSelection.getContainerIdentifier());
                    itemSelectionContainers.put(newItemSelection.getContainerIdentifier(), containerItemSel);
                }

                if (select)
                    containerItemSel.addSelection(newItemSelection);
                else
                    containerItemSel.subtractSelection(newItemSelection);
            }
        }, listenerRefHolder);

        ContextualActionBar.onContainerItemsDeselected.subscribeWeak(new WeakEvent1.Handler<IGeneralItemContainerIdentifier>() {
            @Override
            public void invoke(IGeneralItemContainerIdentifier containerIdentifier) {
                itemSelectionContainers.remove(containerIdentifier);
            }
        }, listenerRefHolder);

        ContextualActionBar.onAllItemsDeselected.subscribeWeak(new WeakEvent.Handler() {
            @Override
            public void invoke() {
                Collection<IGeneralItemContainerIdentifier> itemSelectionContainersCopy = new ArrayList<>(itemSelectionContainers.keySet());

                for (final IGeneralItemContainerIdentifier containerId : itemSelectionContainersCopy) {
                    ContextualActionBar.onContainerItemsDeselected.invoke(containerId);
                }

                itemSelectionContainers.clear();
            }
        }, listenerRefHolder);

        MainActivity.onCreate.subscribeWeak(new WeakEvent1.Handler<Activity>() {
            @Override
            public void invoke(Activity activity) {

                ContextualActionBar contextualActionBar = ContextualActionBar.getInstance();
                if (contextualActionBar != null)
                    contextualActionBar.updateMenu();

            }
        }, listenerRefHolder);


        MainActivity.onDestroy.subscribeWeak(new WeakEvent1.Handler<ContextData>() {
            @Override
            public void invoke(ContextData contextData) {
                ContextualActionBar contextualActionBar = ContextualActionBar.getInstance();
                if (contextualActionBar != null)
                    contextualActionBar.onActivityDestroyed();
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onItemSelected.subscribeWeak(new WeakEvent3.Handler<ActionListenerBase[], Boolean, ItemSelection.One<Object>>() {

            @Override
            public void invoke(ActionListenerBase[] itemActions, Boolean select, ItemSelection.One<Object> itemSelection) {
                ContextualActionBar contextualActionBar = ContextualActionBar.getInstance();
                if (contextualActionBar != null)
                    contextualActionBar.onItemSelected(itemActions, select, itemSelection);
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onRequestIsSelectingEnabled.subscribeWeak(new WeakEventR.Handler<Boolean>() {
            @Override
            public Boolean invoke() {
                return selectingEnabled;
            }
        }, listenerRefHolder);

        LibraryQueueFragmentBase.onRequestContainsItemSelection.subscribeWeak(new WeakEventR1.Handler<ItemSelection.One, Boolean>() {
            @Override
            public Boolean invoke(ItemSelection.One itemSelection) {
                return containsItemSelection(itemSelection);
            }
        }, listenerRefHolder);
    }

    boolean containsItemSelection(ItemSelection.One itemSelection) {
        ItemSelection<Object> container = itemSelectionContainers.get(itemSelection.getContainerIdentifier());
        return container != null && container.containsItem(itemSelection.getItemIdentifier());
    }
}