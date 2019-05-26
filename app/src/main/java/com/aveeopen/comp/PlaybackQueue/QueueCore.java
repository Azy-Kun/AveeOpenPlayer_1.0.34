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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.aveeopen.Common.Events.WeakEvent1;
import com.aveeopen.Common.Events.WeakEvent2;
import com.aveeopen.Common.Events.WeakEvent5;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.MultiList;
import com.aveeopen.Common.Tuple2;
import com.aveeopen.Common.Utils;
import com.aveeopen.Common.UtilsMusic;
import com.aveeopen.PlayerCore;
import com.aveeopen.comp.AppPreferences.AppPreferences;
import com.aveeopen.comp.Common.IItemIdentifier;
import com.aveeopen.comp.playback.MediaPlaybackServiceDefs;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.EventsGlobal.EventsGlobalTextNotifier;
import com.aveeopen.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class QueueCore implements MediaPlaybackServiceDefs, IQueueIndexer.QueueIndexesChangedListener {

    public static WeakEventR<Boolean> onRequestShouldReloadInitalSongs = new WeakEventR<>();
    public static WeakEvent5<Tuple2<PlaylistSong, IItemIdentifier> /*queueEntry*/, Integer /*songIndex*/, Boolean /*playlistEnd*/, Boolean /*activeChange*/, Object /*params*/> onQueuePosChanged = new WeakEvent5<>();
    public static WeakEvent2<MultiList<PlaylistSong, IItemIdentifier> /*list*/, IPlaylistSongContainerIdentifier /*songContainerIdentifier*/> onQueueStateChanged = new WeakEvent2<>();
    public static WeakEvent1<Integer /*shuffleMode*/> onShuffleModeChanged = new WeakEvent1<>();

    private static final Object createInstanceLock = new Object();
    private static volatile WeakReference<QueueCore> instanceWeak = new WeakReference<>(null);
    private IPlaylistSongContainerIdentifier songContainerIdentifier = null;
    private MultiList<PlaylistSong, IItemIdentifier> playList = new MultiList<>();
    private IQueueIndexer queueIndexer = new QueueIndexerNormal();
    private int shuffleMode = -1;//uninitialized so setShuffle works

    public QueueCore() {
        setShuffleMode(SHUFFLE_NONE, false);
        reloadQueue();
    }

    public static QueueCore createOrGetInstance() {
        QueueCore inst0 = instanceWeak.get();
        if (inst0 != null) return inst0;

        synchronized (createInstanceLock) {
            QueueCore inst = instanceWeak.get();
            if (inst == null) {
                inst = new QueueCore();
                instanceWeak = new WeakReference<>(inst);
            }

            return inst;
        }
    }

    private Resources getResources() {
        Context context = PlayerCore.s().getAppContext();
        if (context == null) return null;

        return context.getResources();
    }

    private void notifyMessage(final String msg) {
        EventsGlobalTextNotifier.onTextMsg.invoke(msg);
    }


    private void notifyQueueChange() {
        final MultiList<PlaylistSong, IItemIdentifier> list = getQueue();
        onQueueStateChanged.invoke(list, songContainerIdentifier);
    }

    public void setShuffleMode(int shuffleMode, boolean allowTextMessages) {
        setShuffleMode(shuffleMode, allowTextMessages, false);
    }

    public void setShuffleMode(int shuffleMode, boolean allowTextMessages, boolean reloadForce) {
        if (!reloadForce)
            if (this.shuffleMode == shuffleMode) return;

        this.shuffleMode = shuffleMode;

        int currentSongIndex = queueIndexer == null ? 0 : queueIndexer.getCurrentSongIndex(true);

        if (this.shuffleMode == SHUFFLE_NONE) {
            queueIndexer = new QueueIndexerNormal();
            ((QueueIndexerNormal) queueIndexer).init(currentSongIndex, this);

            if (allowTextMessages) {
                Context context = PlayerCore.s().getAppContext();
                if (context != null)
                    notifyMessage(context.getString(R.string.playback_shuffle_off));
            }

        } else if (this.shuffleMode == SHUFFLE_NORMAL) {
            List<Integer> shuffleIndices = new ArrayList<>(playList.size());
            List<Integer> songsToShuffle = new ArrayList<>();

            int shuffleHorizon = currentSongIndex;
            if (shuffleHorizon < 0) shuffleHorizon = 0;
            if (shuffleHorizon > playList.size()) shuffleHorizon = playList.size() - 1;

            //first part
            for (int i = 0; i < shuffleHorizon; i++)
                shuffleIndices.add(i);

            //middle part
            shuffleIndices.add(shuffleHorizon);

            //last part
            for (int i = shuffleHorizon + 1; i < playList.size(); i++)
                songsToShuffle.add(i);

            Collections.shuffle(songsToShuffle);

            for (int i = 0; i < songsToShuffle.size(); i++)
                shuffleIndices.add(songsToShuffle.get(i));

            if (shuffleIndices.size() > 0) {
                queueIndexer = new QueueIndexerShuffle();
                ((QueueIndexerShuffle) queueIndexer).init(currentSongIndex, shuffleIndices, this);

                if (allowTextMessages) {
                    Resources res = this.getResources();
                    if (res != null) {
                        int num = songsToShuffle.size();
                        final String message = this.getResources().getQuantityString(
                                R.plurals.x_items_shuffled, num, num);
                        notifyMessage(message);
                    }
                }
            }

        }

        //notify that  song indexes changed
        notifyQueueChange();

        onShuffleModeChanged.invoke(this.shuffleMode);
    }

    public int getShuffleMode() {
        return shuffleMode;
    }

    public void previewOpen(List<PlaylistSong> list, int startPlayPosition) {
        //TODO: Implement
    }

    @Override
    public void onQueueIndexesChanged(IQueueIndexer indexer, boolean eventFromOnQueueChanged, boolean currentSongIndexChanged) {
        int count = playList.size();
        for (int i = 0; i < count; i++) {
            ((QueueItemIdentifier) playList.get2(i)).setQueueIndex(-1);
        }

        int cnt = indexer.getQueueIndexCount(playList.size());

        for (int i = 0; i < cnt; i++) {
            int songIndex = indexer.getSongIndexByQueueIndex(i, playList.size());
            if (songIndex < playList.size())
                ((QueueItemIdentifier) playList.get2(songIndex)).setQueueIndex(i);
        }

        notifyQueueChange();
        if (currentSongIndexChanged) {
            final int posFinal = queueIndexer.getCurrentSongIndex(true);
            onQueuePosChanged(posFinal, false, false, null);
        }
    }

    // insert the list of songs at the specified position in the playlist
    private int addToPlayList(Collection<PlaylistSong> list, int position, boolean clear, IPlaylistSongContainerIdentifier songContainerIdentifier) {
        if (clear) { // overwrite
            playList.clear();
            position = 0;
        }

        if (position > playList.size()) {
            position = playList.size();
        }

        List<IItemIdentifier> list2 = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); ++i)
            list2.add(i, new QueueItemIdentifier());

        playList.addAll(position, list, list2);

        if (clear)
            onQueueChanged2(position, position + list.size(), 0, false, songContainerIdentifier, true);
        else
            onQueueChanged2(position, (position + list.size()) - 1, +1, false, null);

        return position;
    }

    public void enqueue(Collection<PlaylistSong> list, int action) {
        if (action == NEXT) {// && mOpenedSongIndex + 1 < playList.size()
            addToPlayList(list, queueIndexer.getCurrentSongIndex(true) + 1, false, null);
        } else {
            addToPlayList(list, Integer.MAX_VALUE, false, null);
            if (action == NOW) {
                queueIndexer.goTo(playList.size() - list.size());
            }
        }
    }


    public void removeQueueItems(List<IItemIdentifier> itemIdentifiers) {
        List<Integer> itemsIndexes = new ArrayList<>(itemIdentifiers.size());

        MultiList.ListIterator<PlaylistSong, IItemIdentifier> it;
        for (IItemIdentifier itemToRemove : itemIdentifiers) {

            it = playList.multiListIterator();
            while (it.hasNext()) {
                int i = it.nextIndex();//before next()!
                Tuple2<PlaylistSong, IItemIdentifier> item = it.next();
                if (itemToRemove.equals(item.obj2)) {
                    itemsIndexes.add(i);
                    it.remove();
                }
            }
        }

        onQueueChanged22(itemsIndexes, -1, 0, false, null);
    }


    public int removeTracks(int first, int last) {
        if (last < first) return 0;
        if (first < 0) first = 0;
        if (last >= playList.size()) last = playList.size() - 1;

        int numRemoved;

        playList.subList(first, last + 1).clear();
        onQueueChanged2(first, last, -1, false, null);

        numRemoved = last - first + 1;

        return numRemoved;
    }


    public int removeTrack(PlaylistSong id) {
        int numRemoved = 0;

        for (final MultiList.ListIterator<PlaylistSong, IItemIdentifier> iterator = playList.multiListIterator(); iterator.hasNext(); ) {
            int index = iterator.nextIndex();
            final PlaylistSong o = iterator.next1();

            if (o.compare(id)) {
                iterator.remove();
                numRemoved++;

                onQueueChanged2(index, index, -1, false, null);
            }
        }

        return numRemoved;
    }

    public void swapQueueItem(int index1, int index2) {
        if (index1 >= playList.size()) {
            index1 = playList.size() - 1;
        }
        if (index2 >= playList.size()) {
            index2 = playList.size() - 1;
        }

        playList.swap(index1, index2);
        onQueueChanged2(index1, index2, 0, true, null);
    }

    public void moveQueueItems(int from, int to, List<Integer> itemOffsets) {
        Tuple2<PlaylistSong, IItemIdentifier>[] itemsToMove = new Tuple2[itemOffsets.size()];

        for (int i = 0; i < itemOffsets.size(); i++) {
            int index = itemOffsets.get(i);
            itemsToMove[i] = new Tuple2<>(playList.get1(from + index), playList.get2(from + index));
        }

        for (int i = itemOffsets.size() - 1; i >= 0; i--) {
            int index = itemOffsets.get(i);
            playList.remove(from + index);
        }

        for (int i = itemOffsets.size() - 1; i >= 0; i--) {
            int index = itemOffsets.get(i);
            playList.add(to + index, itemsToMove[i]);
        }

        onQueueChanged22(itemOffsets, to, from, false, null);
    }

    public List<PlaylistSong> getQueue1() {
        return playList.unmodifiableList1();
    }

    public MultiList<PlaylistSong, IItemIdentifier> getQueue() {
        return playList.unmodifiableList();
    }

    void onQueueChanged2(int first, int last, int sign, boolean swap, IPlaylistSongContainerIdentifier songContainerIdentifier) {
        onQueueChanged2(first, last, sign, swap, songContainerIdentifier, false);
    }

    //first: inclusive
    //last: inclusive
    void onQueueChanged2(int first, int last, int sign, boolean swap, IPlaylistSongContainerIdentifier songContainerIdentifier, boolean hintWasCleared) {
        this.songContainerIdentifier = songContainerIdentifier;

        if (first > last) return;
        if (first < 0) first = 0;
        if (last >= playList.size()) last = playList.size() - 1;

        if (hintWasCleared)
            setShuffleMode(SHUFFLE_NONE, true);

        boolean currentSongIndexChanged = queueIndexer.onQueueChanged(first, last, sign, swap, playList.size());

        if (!swap) {

            int numCount = (last - first) + 1;

            Resources res = this.getResources();

            if (res != null) {
                if (sign == 1) {
                    final String message = this.getResources().getQuantityString(
                            R.plurals.x_items_added_to_queue, numCount, numCount);
                    EventsGlobalTextNotifier.onTextMsg.invoke(message);
                } else if (sign == -1) {
                    final String message = this.getResources().getQuantityString(
                            R.plurals.x_items_removed_from_queue, numCount, numCount);
                    EventsGlobalTextNotifier.onTextMsg.invoke(message);

                } else if (sign == 0 && hintWasCleared) {
                    final String message = this.getResources().getQuantityString(
                            R.plurals.x_items_opened_in_queue, numCount, numCount);
                    EventsGlobalTextNotifier.onTextMsg.invoke(message);
                }
            }
        }
    }

    //first: inclusive
    //last: inclusive
    void onQueueChanged22(List<Integer> itemsIndex, int insertIndex, int removeIndex, boolean swap, IPlaylistSongContainerIdentifier songContainerIdentifier) {
        this.songContainerIdentifier = songContainerIdentifier;
        queueIndexer.onQueueChanged(itemsIndex, insertIndex, removeIndex, swap, playList.size());

    }

    public Tuple2<PlaylistSong, IItemIdentifier> getCurrentQueueEntry() {
        int currentSongIndex = queueIndexer == null ? 0 : queueIndexer.getCurrentSongIndex(true);

        return (currentSongIndex >= 0 && currentSongIndex < playList.size()) ?
                playList.get(currentSongIndex) : null;
    }

    public IPlaylistSongContainerIdentifier getSongContainerIdentifier() {
        return songContainerIdentifier;
    }

    public int getQueuePosition() {
        return queueIndexer.getCurrentSongIndex(true);
    }

    public void setQueuePosition(int pos) {
        setQueuePosition(pos, null);
    }

    public void setQueuePosition(int pos, Object params) {
        queueIndexer.setQueuePosBySongIndex(pos);
        onQueuePosChanged(pos, false, true, params);
    }

    public void setQueueItem(IItemIdentifier item, Object params) {
        if (item == null)
            return;

        //TODO: Get correct hintPossiblePos for shuffle indexer
        int queuePos = findPlaylistEntryByItemIdent(item, item.getQueueIndex());//getQueueIndex will match using normal queue indexer
        setQueuePosition(queuePos, params);
    }

    private int findPlaylistEntryByItemIdent(IItemIdentifier itemIdent, int hintPossiblePos) {
        if (itemIdent == null)
            return -1;

        if (hintPossiblePos >= 0 && hintPossiblePos < playList.size()) {
            Tuple2<PlaylistSong, IItemIdentifier> item = playList.get(hintPossiblePos);
            if (item.obj2 != null && item.obj2.equals(itemIdent)) {
                return hintPossiblePos;
            }
        }

        for (MultiList.ListIterator<PlaylistSong, IItemIdentifier> it = playList.multiListIterator(); it.hasNext(); ) {
            int i = it.nextIndex();//before next()!
            Tuple2<PlaylistSong, IItemIdentifier> item = it.next();

            if (item.obj2 != null && item.obj2.equals(itemIdent)) {
                return i;
            }
        }

        return -1;
    }

    public void onDataSaveTime(Context context) {
        saveQueue(context);
    }

    private void saveQueue(Context context) {
        SharedPreferences mPreferences = AppPreferences.createOrGetInstance().getPreferences(context);
        SharedPreferences.Editor ed = mPreferences.edit();

        {
            StringBuilder q = new StringBuilder();
            StringBuilder partSizes = new StringBuilder();

            int len = playList.size();
            for (int i = 0; i < len; i++) {
                String s = playList.get1(i).getConstrucPath();
                if (s != null) {
                    q.append(s);
                    partSizes.append(s.length()).append(',');
                }
            }

            ed.putString("queue", q.toString());
            ed.putString("queueSizes", partSizes.toString());
        }

        ed.putInt("curpos", queueIndexer.getQueueIndex());
        ed.putInt("shufflemode", shuffleMode);
        ed.apply();

    }

    public void reloadQueue() {
        Context context = PlayerCore.s().getAppContext();
        if (context == null) return;

        SharedPreferences mPreferences = AppPreferences.createOrGetInstance().getPreferences(context);

        String q;
        String partSizes;

        q = AppPreferences.preferencesGetStringSafe(mPreferences, "queue", "");
        partSizes = AppPreferences.preferencesGetStringSafe(mPreferences, "queueSizes", "");


        int qlen = q != null ? q.length() : 0;
        int partSizesLen = partSizes != null ? partSizes.length() : 0;

        playList.clear();

        if (qlen > 1 && partSizesLen > 0) {
            String[] sizesStr = partSizes.split(",");

            int posstart = 0;
            for (String sizestr : sizesStr) {
                int s = Utils.strToIntSafe(sizestr);
                String constructPath = q.substring(posstart, posstart + s);
                playList.add(new PlaylistSong(-1, constructPath), new QueueItemIdentifier());
                posstart += s;
            }
        }

        if (playList.size() == 0 && onRequestShouldReloadInitalSongs.invoke(false)) {
            List<PlaylistSong> initalSongs = UtilsMusic.getMostRecentTrackListByCount(context, 30);

            for (PlaylistSong s : initalSongs)
                playList.add(s, new QueueItemIdentifier());
        }

        int pos = AppPreferences.preferencesGetIntSafe(mPreferences, "curpos", 0);

        queueIndexer.goTo(pos);


        int shufmode = AppPreferences.preferencesGetIntSafe(mPreferences, "shufflemode", SHUFFLE_NONE);
        if (shufmode != SHUFFLE_NORMAL) {
            shufmode = SHUFFLE_NONE;
        }

        //after queue pos set
        setShuffleMode(shufmode, false, true);

        onQueueChanged2(0, playList.size(), 0, false, null);
    }

    public void open(List<PlaylistSong> list, int startPlayPosition, int addAction, IPlaylistSongContainerIdentifier songContainerIdentifier, Object params) {
        int addPosition = -1;

        if (addAction == CLEAR) {
            addPosition = -1;
        } else if (addAction == FIRST) {
            addPosition = 0;
        } else if (addAction == NOW) {
            //?
            addPosition = Integer.MAX_VALUE;
        } else if (addAction == NEXT) {
            addPosition = queueIndexer.getCurrentSongIndex(true) + 1;
        } else if (addAction == LAST) {
            addPosition = Integer.MAX_VALUE;
        }


        int addedToPosition = addToPlayList(list, addPosition, addPosition < 0, songContainerIdentifier);

        if (startPlayPosition >= 0)
            queueIndexer.goTo(startPlayPosition + addedToPosition);

        final int posFinal = queueIndexer.getCurrentSongIndex(true);
        onQueuePosChanged(posFinal, false, true, params);
    }

    public boolean isNextPlaylistEnd() {
        int nextIndex = queueIndexer.getNextSongIndex(false);

        //playlist end?
        return nextIndex == -1 || nextIndex >= playList.size();
    }

    public void playFirst(Object params) {

        queueIndexer.goToStart();

        int pos = queueIndexer.getCurrentSongIndex(true);
        onQueuePosChanged(pos, false, true, params);

    }

    public void playCurrent(Object params) {

        int pos = queueIndexer.getCurrentSongIndex(true);
        onQueuePosChanged(pos, false, true, params);
    }

    public void prev(Object params) {
        queueIndexer.goToPrev();

        int pos = queueIndexer.getCurrentSongIndex(true);
        onQueuePosChanged(pos, false, true, params);

    }

    public void next(Object params) {

        boolean playlistEnd = queueIndexer.goToNext(playList.size());
        int pos = queueIndexer.getCurrentSongIndex(true);

        onQueuePosChanged(pos, playlistEnd, true, params);

    }

    public void nextOrFirst(Object params) {

        if (isNextPlaylistEnd())
            playFirst(params);
        else
            next(params);
    }

    public void open(List<PlaylistSong> list, int startPlayPosition, int addAction, IPlaylistSongContainerIdentifier songContainerIdentifier) {
        open(list, startPlayPosition, addAction, songContainerIdentifier, null);
    }

    public void setQueueItem(IItemIdentifier item) {
        setQueueItem(item, null);
    }

    public void playFirst() {
        playFirst(null);
    }

    public void playCurrent() {
        playCurrent(null);
    }

    public void prev() {
        prev(null);
    }

    public void next() {
        next(null);
    }

    public void nextOrFirst() {
        nextOrFirst(null);
    }


    void onQueuePosChanged(final int songIndex, final boolean playlistEnd, final boolean activeChange, final Object params) {

        final Tuple2<PlaylistSong, IItemIdentifier> queueEntry =
                (songIndex >= 0 && songIndex < playList.size()) ?
                        playList.get(songIndex) : null;


        onQueuePosChanged.invoke(queueEntry, songIndex, playlistEnd, activeChange, params);
    }

    //supports removed, inserted, swapped
    //return -1 if id removed
     static int fixQueueIndex_(int queueIndex, int first, int last, int sign, boolean swap) {

        if (swap) {
            if (queueIndex == last)
                return first;
            else if (queueIndex == first)
                return last;

            return queueIndex;
        }

        if (queueIndex < first) {
            //no effect
            return queueIndex;
        } else {

            int count = (last - first) + 1;
            int newpos = queueIndex + (sign * count);

            if (newpos <= first)//deleted current song?
                newpos = -1;

            return newpos;
        }

    }

    //supports removed, inserted, swapped
    static int fixQueueIndexSingle(int queueIndex, int first, int sign) {
        if (queueIndex < first) {
            //no effect
            return queueIndex;
        } else {
            int newpos = queueIndex + sign;

            if (newpos <= first)//deleted current song?
                newpos = -1;

            return newpos;
        }
    }

    static int fixRemovedQueueIndexSingle(int queueIndex, int removeIndex) {
        List<Integer> itemsIndex = new ArrayList<>();
        itemsIndex.add(0);
        return fixRemovedQueueIndex(queueIndex, itemsIndex, removeIndex);
    }

    //supports random ordered items, removed, inserted, moved, swapped
    static int fixRemovedQueueIndex(int queueIndex, List<Integer> itemsIndex, int removeIndex) {
        int index;

        for (index = itemsIndex.size() - 1; index >= 0; index--) {
            if (index + removeIndex == queueIndex) {
                queueIndex--;
            }
        }

        if (queueIndex < 0) queueIndex = 0;

        return queueIndex;
    }

    //supports random ordered items, removed, inserted, moved, swapped
    //return -1 id if removed
    static int fixQueueIndex(int queueIndex, List<Integer> itemsIndex, int insertIndex, int removeIndex, boolean swap) {

        if (swap) {
            for (Integer index : itemsIndex) {
                if (index + insertIndex == queueIndex)
                    return index + removeIndex;

                if (index + removeIndex == queueIndex)
                    return index + insertIndex;
            }

            return queueIndex;
        }

        int newPos = queueIndex;
        if (removeIndex >= 0) {
            for (Integer index : itemsIndex) {
                if (index + removeIndex < queueIndex)
                    newPos--;

                if (index + removeIndex == queueIndex) {
                    if (insertIndex >= 0) //move?
                        return index + insertIndex;
                    else
                        return -1;//removed
                }
            }
        }

        if (insertIndex >= 0) {
            for (Integer index : itemsIndex) {
                if (index + insertIndex <= queueIndex)
                    newPos++;
            }
        }

        return newPos;
    }
}
