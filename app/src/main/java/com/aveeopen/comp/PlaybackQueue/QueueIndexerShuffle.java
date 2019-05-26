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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class QueueIndexerShuffle implements IQueueIndexer {

    private int currentQueueIndex;
    private QueueIndexesChangedListener indexesListener;
    private List<Integer> shuffleIndices;

    public QueueIndexerShuffle() {

        currentQueueIndex = 0;
        shuffleIndices = new ArrayList<>();
        indexesListener = null;
    }

    //queue indexer must be assigned before we attempt to fire events, so we use init
    public void init(int startSongIndex, List<Integer> shuffleIndices, QueueIndexesChangedListener indexesListener) {
        this.indexesListener = indexesListener;
        this.shuffleIndices = shuffleIndices;

        currentQueueIndex = 0;
        setQueuePosBySongIndex(startSongIndex);

        if (indexesListener != null)
            indexesListener.onQueueIndexesChanged(this, false, true);
    }


    public boolean onQueueChanged(int first, int last, int sign, boolean swap, int listSize) {
        boolean currentSongIndexChanged = false;

        for (ListIterator<Integer> it = shuffleIndices.listIterator(); it.hasNext(); ) {
            int i = it.nextIndex();//before next()
            Integer songIndex = it.next();


            int newindex = QueueCore.fixQueueIndex_(songIndex, first, last, sign, swap);
            if (newindex < 0)//indexed song removed?
            {
                it.remove();

                int newQueueIndex = QueueCore.fixQueueIndexSingle(currentQueueIndex, i, -1);
                if (newQueueIndex < 0) //currentQueueIndex entry removed?
                {
                    currentSongIndexChanged = true;
                    //currentQueueIndex = i-1;//jump backward
                    currentQueueIndex = i;//jump forward

                    if (currentQueueIndex < 0) currentQueueIndex = 0;
                    if (currentQueueIndex >= shuffleIndices.size())
                        currentQueueIndex = shuffleIndices.size() - 1;
                }
            } else {
                it.set(newindex);
            }
        }

        if (indexesListener != null)
            indexesListener.onQueueIndexesChanged(this, true, currentSongIndexChanged);

        return currentSongIndexChanged;
    }

    @Override
    public boolean onQueueChanged(List<Integer> itemsIndex, int insertIndex, int removeIndex, boolean swap, int listSize) {
        boolean currentSongIndexChanged = false;

        for (ListIterator<Integer> it = shuffleIndices.listIterator(); it.hasNext(); ) {
            int i = it.nextIndex();//before next()
            Integer songIndex = it.next();


            int newindex = QueueCore.fixQueueIndex(songIndex, itemsIndex, insertIndex, removeIndex, swap);
            if (newindex < 0)//indexed song removed?
            {
                it.remove();

                int newQueueIndex = QueueCore.fixRemovedQueueIndexSingle(currentQueueIndex, i);
                if (newQueueIndex < 0) //currentQueueIndex entry removed?
                {
                    currentSongIndexChanged = true;
                    //currentQueueIndex = i-1;//jump backward
                    currentQueueIndex = i;//jump forward

                    if (currentQueueIndex < 0) currentQueueIndex = 0;
                    if (currentQueueIndex >= shuffleIndices.size())
                        currentQueueIndex = shuffleIndices.size() - 1;
                }
            } else {
                it.set(newindex);
            }
        }

        if (indexesListener != null)
            indexesListener.onQueueIndexesChanged(this, true, currentSongIndexChanged);

        return currentSongIndexChanged;
    }

    public int getPrevSongIndex(boolean forced) {
        int inx = currentQueueIndex - 1;
        if (inx < 0 || inx >= shuffleIndices.size()) return -1;
        return shuffleIndices.get(inx);
    }

    public int getCurrentSongIndex(boolean forced) {
        if (currentQueueIndex < 0 || currentQueueIndex >= shuffleIndices.size()) return -1;
        return shuffleIndices.get(currentQueueIndex);
    }

    public int getNextSongIndex(boolean forced) {
        int inx = currentQueueIndex + 1;
        if (inx < 0 || inx >= shuffleIndices.size()) return -1;
        return shuffleIndices.get(inx);
    }

    public void goTo(int queueIndex) {
        currentQueueIndex = queueIndex;
    }

    public void goToStart() {
        currentQueueIndex = 0;
    }

    public boolean goToNext(int listSize) {
        currentQueueIndex = currentQueueIndex + 1;
        if (currentQueueIndex >= shuffleIndices.size()) {
            //I
            currentQueueIndex = shuffleIndices.size() - 1;
            //II
            //goToStart();
            return true;
        }

        return false;
    }

    public void goToPrev() {
        currentQueueIndex = currentQueueIndex - 1;
        if (currentQueueIndex < 0) currentQueueIndex = 0;
    }

    public int getQueueIndex() {
        return currentQueueIndex;
    }

    public void setQueuePosBySongIndex(int newSongIndex) {
        for (ListIterator<Integer> it = shuffleIndices.listIterator(); it.hasNext(); ) {
            int i = it.nextIndex();//before next()!
            Integer songIndex = it.next();

            if (songIndex == newSongIndex) {
                currentQueueIndex = i;
                return;
            }
        }
    }

    @Override
    public int getQueueIndexCount(int listSize) {
        return Math.min(shuffleIndices.size(), listSize);
    }

    @Override
    public int getSongIndexByQueueIndex(int queueIndex, int listSize) {
        if (queueIndex < 0 || queueIndex >= shuffleIndices.size()) return -1;
        return shuffleIndices.get(queueIndex);
    }
}