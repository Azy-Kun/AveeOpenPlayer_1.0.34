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

import android.app.Activity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.tlog;
import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.ContextData;
import com.aveeopen.MainActivity;
import com.aveeopen.R;

import junit.framework.Assert;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextualActionBar {

    public static WeakEvent1<Boolean /*selectingEnabled*/> onSelectModeChanged = new WeakEvent1<>();
    public static WeakEvent2<ItemSelection.One<Object> /*itemSelection*/, Boolean /*select*/> onItemSelectionChanged = new WeakEvent2<>();
    public static WeakEvent1<IGeneralItemContainerIdentifier /*containerIdentifier*/> onContainerItemsDeselected = new WeakEvent1<>();
    public static WeakEvent onAllItemsDeselected = new WeakEvent();

    private static final Object createInstanceLock = new Object();
    private static volatile WeakReference<ContextualActionBar> instanceWeak = new WeakReference<>(null);

    private HashMap<ItemActionBase, Integer> availableActions = new HashMap<>();//available actions per item
    private HashMap<ItemSelection.One<Object>, ItemEntry> selectedItems = new HashMap<>();
    private boolean actionModeShouldbeRecreated = false;
    private ContextData contextData;
    private ActionMode actionMode;

    public ContextualActionBar(Activity activity) {
        contextData = new ContextData(activity);
    }

    public static ContextualActionBar createInstance(Activity activity) {

        ContextualActionBar inst0 = instanceWeak.get();
        if (inst0 != null) return inst0;

        synchronized (createInstanceLock) {
            ContextualActionBar inst = instanceWeak.get();
            if (inst == null) {
                inst = new ContextualActionBar(activity);
                instanceWeak = new WeakReference<>(inst);
            }

            return inst;
        }
    }

    public static ContextualActionBar getInstance() {
        return instanceWeak.get();
    }

    public void onItemSelected(ActionListenerBase[] itemActions, Boolean select, ItemSelection.One<Object> itemSelection) {

        Object itemActionObj = itemSelection.getItemIdentifier();

        if (select) {
            ItemEntry itemEntry = new ItemEntry();
            itemEntry.itemActions = itemActions;
            itemEntry.itemActionObj = itemActionObj;

            selectItem(itemSelection, itemEntry);
        } else {
            deselectItem(itemSelection);
        }

    }

    void selectItem(final ItemSelection.One<Object> itemSelection, ItemEntry itemEntry) {
        onItemSelectionChanged.invoke(itemSelection, true);

        selectedItems.put(itemSelection, itemEntry);

        HashMap<ItemActionBase, Integer> uniqueActionsInItem = new HashMap<>();

        for (ActionListenerBase itemAction : itemEntry.itemActions) {
            ItemActionBase actionType = itemAction.getItemActionBase();

            //check if there isn't same action more than once, or it would mess up counter
            Integer val = uniqueActionsInItem.get(actionType);
            if (val != null) {
                Assert.fail("There can only be unique ItemActionBase actions per item");
                continue;
            }
            uniqueActionsInItem.put(actionType, 1);
            //

            Integer oldvalue = availableActions.get(actionType);
            if (oldvalue == null) oldvalue = 0;
            availableActions.put(actionType, oldvalue + 1);
        }

        updateMenu();
    }

    void deselectItem(final ItemSelection.One<Object> itemSelection) {
        onItemSelectionChanged.invoke(itemSelection, false);

        ItemEntry itemEntry = selectedItems.remove(itemSelection);

        if (itemEntry != null) {
            for (ActionListenerBase itemAction : itemEntry.itemActions) {
                ItemActionBase actionType = itemAction.getItemActionBase();

                Integer oldvalue = availableActions.get(actionType);
                if (oldvalue == null)
                    continue;

                Integer newValue = oldvalue - 1;

                if (newValue > 0)
                    availableActions.put(actionType, newValue);
                else
                    availableActions.remove(actionType);
            }
        }

        updateMenu();
    }

    private void deselectAllItems() {
        onAllItemsDeselected.invoke();
        availableActions.clear();
        selectedItems.clear();
    }

    public void onActivityDestroyed() {
        if (actionMode != null) {
            actionModeShouldbeRecreated = true;
            actionMode.finish();
            actionMode = null;
        }
    }

    public void updateMenu() {

        Activity mainActivity = MainActivity.getInstance();
        if (mainActivity == null) {
            actionModeShouldbeRecreated = true;
            if (actionMode != null) {
                actionMode.finish();
            }
            return;
        }

        if (selectedItems.size() > 0) {
            if (actionMode == null) {
                actionMode = mainActivity.startActionMode(new ActionModeCallback());
                onSelectModeChanged.invoke(true);
            }

        } else {
            if (actionMode != null)
                actionMode.finish();
        }

        if (actionMode != null)
            actionMode.setTitle(selectedItems.size() + " selected");
    }

    boolean executeAction(ContextData contextData, ItemActionBase actionType) {

        List<ActionListenerBase> executeActionListeners = new ArrayList<>();
        List<Object> executeItemObj = new ArrayList<>();

        for (ItemEntry entry : selectedItems.values()) {
            for (ActionListenerBase action : entry.itemActions) {
                if (action.getItemActionBase().equals(actionType)) {
                    executeItemObj.add(entry.itemActionObj);
                    executeActionListeners.add(action);
                    break;//there is supposedly one action in every entry , so we can stop
                }
            }
        }

        if (executeActionListeners.size() > 0) {
            actionType.executeListBase(contextData, executeItemObj, executeActionListeners);
            return true;
        }

        return false;
    }

    class ItemEntry {
        ActionListenerBase[] itemActions;
        Object itemActionObj;
    }

    private class ActionModeCallback implements ActionMode.Callback {

        List<ItemActionBase> actionsToShow = new ArrayList<>();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionModeShouldbeRecreated = false;
            mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);

            MenuItem itemOverflowItem = menu.findItem(R.id.action_overflow);
            itemOverflowItem.getSubMenu();

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;// Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {


            if (item.getItemId() == R.id.action_overflow) {
                SubMenu itemOverflow = item.getSubMenu();
                itemOverflow.clear();//remove already created items
                //
                int itemsSelected = selectedItems.size();
                int maxActionCount = itemsSelected;

                actionsToShow.clear();
                if (itemsSelected > 0) {
                    for (Map.Entry<ItemActionBase, Integer> entry : availableActions.entrySet()) {

                        tlog.w("" + entry.getKey().toString() + " count: " + entry.getValue());

                        if (entry.getValue() == maxActionCount) {
                            if (entry.getKey().getShouldShow()) {
                                if ((itemsSelected == 1 && entry.getKey().isAllowSingle()) ||
                                        (itemsSelected > 1 && entry.getKey().isAllowMultiple())) {
                                    actionsToShow.add(entry.getKey());
                                }
                            }

                        }
                    }
                }

                for (int i = 0; i < actionsToShow.size(); i++) {
                    itemOverflow.add(Menu.NONE, i + 10, actionsToShow.get(i).getActionId(), actionsToShow.get(i).getNameStrResId());
                    //menuItem.setIcon(actionsToShow.get(i).iconResId);
                }

                return true;
            } else {

                int id = item.getItemId() - 10;
                if (id >= 0 && id < availableActions.size()) {
                    if (executeAction(contextData, actionsToShow.get(id))) {
                        mode.finish();
                        return true;
                    }
                }
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;

            // remove selection
            if (!actionModeShouldbeRecreated)
                deselectAllItems();


            onSelectModeChanged.invoke(false);
        }

    }
}
