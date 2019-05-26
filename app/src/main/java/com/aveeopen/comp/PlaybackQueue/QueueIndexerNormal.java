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

package com.aveeopen.comp.PlaybackQueue;

import java.util.List;

public class QueueIndexerNormal implements IQueueIndexer {

    private int currentQueueIndex;
    private QueueIndexesChangedListener indexesListener;

    public QueueIndexerNormal() {
        indexesListener = null;
        currentQueueIndex = 0;
    }

    //queue indexer must be assigned before we attemp to firr evens, so we use init
    public void init(int startSongIndex, QueueIndexesChangedListener indexesListener) {
        this.indexesListener = indexesListener;
        currentQueueIndex = 0;
        setQueuePosBySongIndex(startSongIndex);

        if (indexesListener != null)
            indexesListener.onQueueIndexesChanged(this, false, true);
    }

    @Override
    public boolean onQueueChanged(int first, int last, int sign, boolean swap, int listSize) {
        boolean currentSongIndexChanged = false;

        currentQueueIndex = QueueCore.fixQueueIndex_(currentQueueIndex, first, last, sign, swap);

        if (currentQueueIndex < 0) {//removed?
            currentSongIndexChanged = true;
            currentQueueIndex = first - 1;
        }

        if (currentQueueIndex < 0) {
            currentSongIndexChanged = true;
            currentQueueIndex = 0;
        }
        if (currentQueueIndex >= listSize) {
            currentSongIndexChanged = true;
            currentQueueIndex = listSize - 1;
        }

        if (indexesListener != null)
            indexesListener.onQueueIndexesChanged(this, true, currentSongIndexChanged);

        return currentSongIndexChanged;
    }

    @Override
    public boolean onQueueChanged(List<Integer> itemsIndex, int insertIndex, int removeIndex, boolean swap, int listSize) {
        boolean currentSongIndexChanged = false;

        currentQueueIndex = QueueCore.fixQueueIndex(currentQueueIndex, itemsIndex, insertIndex, removeIndex, swap);

        if (currentQueueIndex < 0) {//removed?
            currentSongIndexChanged = true;
            currentQueueIndex = QueueCore.fixRemovedQueueIndex(currentQueueIndex, itemsIndex, removeIndex);
        }

        if (currentQueueIndex < 0) {
            currentSongIndexChanged = true;
            currentQueueIndex = 0;
        }
        if (currentQueueIndex >= listSize) {
            currentSongIndexChanged = true;
            currentQueueIndex = listSize - 1;
        }

        if (indexesListener != null)
            indexesListener.onQueueIndexesChanged(this, true, currentSongIndexChanged);

        return currentSongIndexChanged;
    }

    public int getPrevSongIndex(boolean forced) {
        return currentQueueIndex - 1;
    }

    public int getCurrentSongIndex(boolean forced) {
        return currentQueueIndex;
    }

    public int getNextSongIndex(boolean forced) {
        return currentQueueIndex + 1;
    }

    public void goTo(int queueIndex) {
        currentQueueIndex = queueIndex;
    }

    public void goToStart() {
        currentQueueIndex = 0;
    }

    public boolean goToNext(int listSize) {
        currentQueueIndex = getNextSongIndex(false);
        if (currentQueueIndex >= listSize) {
            //I
            currentQueueIndex = listSize - 1;
            //II
            //goToStart();
            return true;
        }

        return false;
    }

    public void goToPrev() {
        currentQueueIndex = getPrevSongIndex(false);
        if (currentQueueIndex < 0) currentQueueIndex = 0;
    }

    public int getQueueIndex() {
        return currentQueueIndex;
    }

    public void setQueuePosBySongIndex(int songIndex) {
        currentQueueIndex = songIndex;
    }

    @Override
    public int getQueueIndexCount(int listSize) {
        return listSize;
    }

    @Override
    public int getSongIndexByQueueIndex(int queueIndex, int listSize) {
        return queueIndex;
    }
}
