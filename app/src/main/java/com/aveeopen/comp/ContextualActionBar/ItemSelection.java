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

import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

public class ItemSelection<T> {

    private Object containerIdentifier;
    private List<T> items = new ArrayList<>();//Item Identifiers

    public ItemSelection(Object containerIdentifier) {
        Assert.assertNotNull(containerIdentifier);
        this.containerIdentifier = containerIdentifier;
    }

    public Object getContainerIdentifier() {
        return containerIdentifier;
    }

    public boolean containsItem(T item) {
        return items.contains(item);
    }

    public void addSelection(One<T> item) {
        if (!this.containerIdentifier.equals(item.containerIdentifier)) return;

        if (items.contains(item.item)) return;
        items.add(item.item);
    }

    public void subtractSelection(One<T> item) {
        if (!this.containerIdentifier.equals(item.containerIdentifier)) return;

        items.remove(item.item);
    }

    public static class One<T> {

        private IGeneralItemContainerIdentifier containerIdentifier;
        private T item;

        public One(IGeneralItemContainerIdentifier containerIdentifier, T item) {
            Assert.assertNotNull(containerIdentifier);
            Assert.assertNotNull(item);
            this.containerIdentifier = containerIdentifier;
            this.item = item;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof One)) return false;
            One ob = (One) o;
            return this.containerIdentifier.equals(ob.containerIdentifier) && this.item.equals(ob.item);
        }

        @Override
        public int hashCode() {
            return containerIdentifier.hashCode() + item.hashCode();
        }

        public IGeneralItemContainerIdentifier getContainerIdentifier() {
            return containerIdentifier;
        }

        public boolean containsItem(T item) {
            return this.item.equals(item);
        }

        public T getItemIdentifier() {
            return item;
        }
    }

}
