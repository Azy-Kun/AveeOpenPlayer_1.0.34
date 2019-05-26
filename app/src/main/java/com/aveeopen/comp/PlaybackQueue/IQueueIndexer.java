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

public interface IQueueIndexer {
    //return true - current song index changed
    boolean onQueueChanged(int first, int last, int sign, boolean swap, int listSize);

    //return true - current song index changed
    boolean onQueueChanged(List<Integer> itemsIndex, int insertIndex, int removeIndex, boolean swap, int listSize);

    int getPrevSongIndex(boolean forced);

    int getCurrentSongIndex(boolean forced);

    int getNextSongIndex(boolean forced);

    void goTo(int queueIndex);

    void goToStart();

    //return: true - playlist end
    boolean goToNext(int listSize);

    void goToPrev();

    int getQueueIndex();

    void setQueuePosBySongIndex(int songIndex);

    int getQueueIndexCount(int listSize);

    int getSongIndexByQueueIndex(int queueIndex, int listSize);

    interface QueueIndexesChangedListener {
        void onQueueIndexesChanged(IQueueIndexer queueIndexer, boolean eventFromOnQueueChanged, boolean currentSongIndexChanged);
    }
}
