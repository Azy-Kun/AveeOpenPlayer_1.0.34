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

import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.MultiList;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.comp.ContextualActionBar.ActionListenerBase;
import com.aveeopen.comp.ContextualActionBar.ItemActionBase;
import com.aveeopen.ContextData;
import com.aveeopen.R;

import java.util.List;

public class ItemActionsFolders {

    public static WeakEvent3<Context /*context*/, String /*idhash*/, String /*path*/> onActionRemoveFolder = new WeakEvent3<>();

    public static class RemoveFolderAction extends ItemActionBase {
        public static ItemActionBase baseInstance = new RemoveFolderAction();
        public RemoveFolderAction() {
            super(4, false, true, R.drawable.ic_close, R.string.libItemAction_removeFolder);
        }

        @Override
        public void executeListBase(final ContextData contextData, List<Object> items, List<ActionListenerBase> listeners) {
            MultiList<String, String> folderOut = new MultiList<>();

            for (int i = 0; i < items.size(); i++) {
                RemoveFolderActionListener actionListener = (RemoveFolderActionListener) listeners.get(i);
                actionListener.onRemoveFolder(contextData.getContext(), items.get(i), folderOut);
            }

            Tuple2<String, String> item = null;
            if (folderOut.size() > 0)
                item = folderOut.get(folderOut.size() - 1);

            if (item != null) {
                final String idhash = item.obj1;
                final String path = item.obj2;

                onActionRemoveFolder.invoke(contextData.getContext(), idhash, path);
            }
        }

        public static abstract class RemoveFolderActionListener extends ActionListenerBase {
            public RemoveFolderActionListener() {
                super(baseInstance);
            }

            protected abstract void onRemoveFolder(Context context, Object item, MultiList<String, String> folderOut);
        }
    }

}
