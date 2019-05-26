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

package com.aveeopen.comp.LibraryQueueUI.ContextualActions;

import android.content.Context;

import com.aveeopen.Common.Events.WeakEventR1;
import com.aveeopen.comp.ContextualActionBar.ActionListenerBase;
import com.aveeopen.comp.ContextualActionBar.ItemActionBase;
import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.Common.IItemIdentifier;
import com.aveeopen.ContextData;
import com.aveeopen.R;

import java.util.ArrayList;
import java.util.List;

public class ItemActionsQueue {

    public static WeakEventR1<Integer /*tipId*/, Boolean> onRequestShowTipState = new WeakEventR1<>();
    public static WeakEvent1<ContextData> onActionShowReorderTip = new WeakEvent1<>();
    public static WeakEvent1<List<IItemIdentifier> /*itemIdentifiers*/> onRemoveQueueItems = new WeakEvent1<>();
    public static WeakEvent1<IItemIdentifier /*item*/> onSetCurrentQueueItem = new WeakEvent1<>();

    public static class PlayQueueItemAction extends ItemActionBase {
        public static ItemActionBase itemActionBasePlayQueue = new PlayQueueItemAction();

        public PlayQueueItemAction() {
            super(1, false, R.drawable.ic_playlist4, R.string.libItemAction_playQueue);
        }

        @Override
        public void executeListBase(ContextData contextData, List<Object> items, List<ActionListenerBase> listeners) {
            int i = items.size() - 1;
            if (i < 0) return;

            PlayQueueItemActionListener2 actionListener = (PlayQueueItemActionListener2) listeners.get(i);
            final IItemIdentifier itemPosition = actionListener.onPlay(contextData.getContext(), items.get(i));

            onSetCurrentQueueItem.invoke(itemPosition);
        }

        public static abstract class PlayQueueItemActionListener2 extends ActionListenerBase {

            public PlayQueueItemActionListener2() {
                super(itemActionBasePlayQueue);
            }

            protected abstract IItemIdentifier onPlay(Context context, Object item);
        }
    }

    public static class RemoveQueueItemAction extends ItemActionBase {
        public static ItemActionBase itemActionBaseRemoveQueue = new RemoveQueueItemAction();

        public RemoveQueueItemAction() {
            super(5, true, R.drawable.ic_playlist4, R.string.libItemAction_removeQueueItem);
        }

        @Override
        public void executeListBase(ContextData contextData, List<Object> items, List<ActionListenerBase> listeners) {
            final List<Integer> itemIndexesOut = new ArrayList<>();
            final List<IItemIdentifier> itemIdentifiersOut = new ArrayList<>();

            for (int i = 0; i < items.size(); i++) {
                RemoveQueueItemActionListener2 actionListener = (RemoveQueueItemActionListener2) listeners.get(i);
                actionListener.onRemove(contextData.getContext(), items.get(i), itemIndexesOut, itemIdentifiersOut);
            }
            onRemoveQueueItems.invoke(itemIdentifiersOut);
        }

        public static abstract class RemoveQueueItemActionListener2 extends ActionListenerBase {

            public RemoveQueueItemActionListener2() {
                super(RemoveQueueItemAction.itemActionBaseRemoveQueue);
            }

            protected abstract void onRemove(Context context, Object item, List<Integer> itemIndexesOut, List<IItemIdentifier> itemIdentifiersOut);
        }
    }

    public static class TipReorderItemAction extends ItemActionBase {
        private static ItemActionBase baseInstance = new TipReorderItemAction();

        public TipReorderItemAction() {
            super(5, true, R.drawable.ic_info2, R.string.libItemAction_tipReorder);
        }

        @Override
        public void executeListBase(final ContextData contextData, List<Object> items, List<ActionListenerBase> listeners) {
            onActionShowReorderTip.invoke(contextData);
        }

        @Override
        public boolean getShouldShow() {
            return onRequestShowTipState.invoke(AppPreferences.PREF_Bool_tipShow_reorder, false);
        }

        public static abstract class TipReorderItemActionListener2 extends ActionListenerBase {
            public TipReorderItemActionListener2() {
                super(TipReorderItemAction.baseInstance);
            }
        }
    }

}
