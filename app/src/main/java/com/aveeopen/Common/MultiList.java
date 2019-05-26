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

package com.aveeopen.Common;

import android.support.annotation.NonNull;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MultiList<L1, L2> implements List<Tuple2<L1, L2>> {


    private List<L1> list1;
    private List<L2> list2;

    public MultiList() {
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
    }

    public MultiList(int capacity) {
        list1 = new ArrayList<>(capacity);
        list2 = new ArrayList<>(capacity);
    }

    public MultiList(List<L1> list1, List<L2> list2) {
        this.list1 = list1;
        this.list2 = list2;
    }

    public static <L1, L2> MultiList<L1, L2> fromList1FillWith2(List<L1> list1, L2 fill2) {
        List<L2> list2 = new ArrayList<>(list1.size());
        for (int i = 0; i < list1.size(); i++)
            list2.add(fill2);

        return new MultiList<>(list1, list2);
    }

    public static <L1, L2> MultiList<L1, L2> fromList2FillWith1(List<L2> list2, L1 fill1) {
        List<L1> list1 = new ArrayList<>(list2.size());
        for (int i = 0; i < list2.size(); i++)
            list1.add(fill1);

        return new MultiList<>(list1, list2);
    }

    public void add(int location, L1 object1, L2 object2) {
        list1.add(location, object1);
        list2.add(location, object2);
    }

    public boolean add(L1 object1, L2 object2) {
        list1.add(object1);
        list2.add(object2);
        return true;
    }

    public boolean addAll(int location, Collection<? extends L1> collection1, Collection<? extends L2> collection2) {

        boolean b1 = list1.addAll(location, collection1);
        boolean b2 = list2.addAll(location, collection2);

        Assert.assertEquals(list1.size(), list2.size());

        return b1 | b2;
    }

    public void clear() {
        list1.clear();
        list2.clear();
    }

    public boolean contains1(L1 object1) {
        return list1.contains(object1);
    }

    public boolean contains2(L2 object2) {
        return list2.contains(object2);
    }

    public L1 get1(int location) {
        return list1.get(location);
    }

    public L2 get2(int location) {
        return list2.get(location);
    }

    public int indexOf1(L1 object1) {
        return list1.indexOf(object1);
    }

    public int indexOf2(L2 object2) {
        return list2.indexOf(object2);
    }

    public boolean isEmpty() {
        return list1.isEmpty();
    }


    @NonNull
    public ListIterator<L1, L2> multiListIterator() {
        return new ListIterator<>(list1.listIterator(), list2.listIterator());
    }

    public void set(int location, L1 object1, L2 object2) {
        list1.set(location, object1);
        list2.set(location, object2);
    }

    public int size() {
        Assert.assertEquals(list1.size(), list2.size());
        return list1.size();
    }

    @NonNull
    public MultiList<L1, L2> subList(int start, int end) {
        return new MultiList<>(list1.subList(start, end), list2.subList(start, end));
    }

    public void swap(int index1, int index2) {
        Collections.swap(list1, index1, index2);
        Collections.swap(list2, index1, index2);
    }

    public List<L1> unmodifiableList1() {
        return Collections.unmodifiableList(list1);
    }

    public List<L2> unmodifiableList2() {
        return Collections.unmodifiableList(list2);
    }

    public MultiList<L1, L2> unmodifiableList() {
        return new MultiList<>(
                Collections.unmodifiableList(list1),
                Collections.unmodifiableList(list2)
        );
    }

    @Override
    public void add(int location, Tuple2<L1, L2> object) {
        list1.add(location, object.obj1);
        list2.add(location, object.obj2);
    }

    @Override
    public boolean add(Tuple2<L1, L2> object) {
        list1.add(object.obj1);
        list2.add(object.obj2);
        return true;
    }

    @Override
    public boolean addAll(int location, @NonNull Collection<? extends Tuple2<L1, L2>> collection) {
        return false;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Tuple2<L1, L2>> collection) {
        return false;
    }

    @Override
    public boolean contains(Object object) {
        return false;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return false;
    }

    @Override
    public Tuple2<L1, L2> get(int location) {
        return new Tuple2<>(list1.get(location), list2.get(location));
    }

    @Override
    public int indexOf(Object object) {
        return 0;
    }

    @NonNull
    @Override
    public java.util.Iterator<Tuple2<L1, L2>> iterator() {
        return new Iterator<>(list1.iterator(), list2.iterator());
    }

    @Override
    public int lastIndexOf(Object object) {
        return 0;
    }

    @NonNull
    @Override
    public java.util.ListIterator<Tuple2<L1, L2>> listIterator(int location) {
        return new ListIterator<>(list1.listIterator(location), list2.listIterator(location));
    }

    @NonNull
    @Override
    public java.util.ListIterator<Tuple2<L1, L2>> listIterator() {
        return new ListIterator<>(list1.listIterator(), list2.listIterator());
    }

    @Override
    public Tuple2<L1, L2> remove(int location) {
        return new Tuple2<>(list1.remove(location), list2.remove(location));
    }

    @Override
    public boolean remove(Object object) {
        return false;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        return false;
    }

    @Override
    public Tuple2<L1, L2> set(int location, Tuple2<L1, L2> object) {
        return null;
    }

    @NonNull
    @Override
    public Object[] toArray() {

        Object[] result = new Object[list1.size()];
        for (int i = 0; i < list1.size(); i++) {
            result[i] = new Tuple2<>(list1.get(i), list2.get(i));
        }
        return result;
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] array) {
        if (array.length < size()) {
            T[] result = (T[]) new Object[list1.size()];
            for (int i = 0; i < list1.size(); i++) {
                result[i] = (T) new Tuple2<>(list1.get(i), list2.get(i));
            }
            return result;
        }

        for (int i = 0; i < list1.size(); i++) {
            array[i] = (T) new Tuple2<>(list1.get(i), list2.get(i));
        }

        if (array.length > size())
            array[size()] = null;
        return array;
    }

    public static class ListIterator<L1, L2> implements java.util.ListIterator<Tuple2<L1, L2>> {

        java.util.ListIterator<L1> iterator1;
        java.util.ListIterator<L2> iterator2;


        public ListIterator(java.util.ListIterator<L1> _iterator1, java.util.ListIterator<L2> _iterator2) {
            iterator1 = _iterator1;
            iterator2 = _iterator2;
        }


        public void add(L1 object1, L2 object2) {
            iterator1.add(object1);
            iterator2.add(object2);
        }

        public boolean hasNext() {
            return iterator1.hasNext();
        }

        public boolean hasPrevious() {
            return iterator1.hasPrevious();
        }

        public L1 next1() {
            iterator2.next();
            return iterator1.next();
        }

        public L2 next2() {
            iterator1.next();
            return iterator2.next();
        }

        public int nextIndex() {
            return iterator1.nextIndex();
        }

        public L1 previous1() {
            iterator2.previous();
            return iterator1.previous();
        }

        public L2 previous2() {
            iterator1.previous();
            return iterator2.previous();
        }

        public int previousIndex() {
            return iterator1.previousIndex();
        }

        public void remove() {
            iterator1.remove();
            iterator2.remove();
        }

        public void set(L1 object1, L2 object2) {
            iterator1.set(object1);
            iterator2.set(object2);
        }

        @Override
        public void add(Tuple2<L1, L2> object) {
            iterator1.add(object.obj1);
            iterator2.add(object.obj2);
        }

        @Override
        public Tuple2<L1, L2> next() {
            return new Tuple2<>(iterator1.next(), iterator2.next());
        }

        @Override
        public Tuple2<L1, L2> previous() {
            return new Tuple2<>(iterator1.previous(), iterator2.previous());
        }

        @Override
        public void set(Tuple2<L1, L2> object) {
            iterator1.set(object.obj1);
            iterator2.set(object.obj2);
        }
    }

    public static class Iterator<L1, L2> implements java.util.Iterator<Tuple2<L1, L2>> {

        java.util.Iterator<L1> iterator1;
        java.util.Iterator<L2> iterator2;

        public Iterator(java.util.Iterator<L1> iterator1, java.util.Iterator<L2> iterator2) {
            this.iterator1 = iterator1;
            this.iterator2 = iterator2;
        }

        @Override
        public boolean hasNext() {
            return iterator1.hasNext();
        }

        public L1 next1() {
            iterator2.next();
            return iterator1.next();
        }

        public L2 next2() {
            iterator1.next();
            return iterator2.next();
        }

        @Override
        public Tuple2<L1, L2> next() {
            return new Tuple2<>(iterator1.next(), iterator2.next());
        }

        @Override
        public void remove() {
            iterator1.remove();
            iterator2.remove();
        }

    }
}
