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

import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.comp.Common.ISearchEntry;

public class SearchEntry extends SearchEntryOptions implements ISearchEntry {
    private final int index;
    String query = "";

    SearchEntry(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getHint() {
        return hint;
    }

    @Override
    public IGeneralItemContainerIdentifier getContainerIdentifier() {
        return containerIdentifier;
    }
}
