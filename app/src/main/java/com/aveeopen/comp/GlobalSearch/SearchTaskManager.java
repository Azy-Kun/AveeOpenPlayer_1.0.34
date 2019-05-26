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

package com.aveeopen.comp.GlobalSearch;

import android.os.AsyncTask;

import com.aveeopen.Common.Events.WeakEvent2;

import junit.framework.Assert;

public class SearchTaskManager {

    public static WeakEvent2<Integer /*index*/, String /*query*/> onUISearchQueryTextChangeWithIndex = new WeakEvent2<>();

    private int taskIndex = -1;
    private AsyncTask asyncTask = null;

    public void setTask(AsyncTask tsk, int tskIndex) {
        clearTask(this.taskIndex == tskIndex);
        this.taskIndex = tskIndex;
        this.asyncTask = tsk;
    }

    public boolean compareTask(AsyncTask tsk, int tskIndex) {
        return taskIndex == tskIndex && asyncTask != null && !tsk.isCancelled() && (tsk == asyncTask);
    }

    public void clearTaskIfMatch(int tskIndex) {
        if (taskIndex == tskIndex)
            clearTask(true);
    }

    protected void clearTask(boolean samePageSlot) {
        if (asyncTask == null) return;

            final int taskIndexFinal = taskIndex;

            Assert.assertNotNull(asyncTask);
            asyncTask.cancel(false);
            asyncTask = null;
            taskIndex = -1;

            if (!samePageSlot) {
                //call this event last, or it will may clear task first
                onUISearchQueryTextChangeWithIndex.invoke(taskIndexFinal, "");//disable search
            }
    }

}
