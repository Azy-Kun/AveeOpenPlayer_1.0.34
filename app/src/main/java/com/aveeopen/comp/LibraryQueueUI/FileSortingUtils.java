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

package com.aveeopen.comp.LibraryQueueUI;

import com.aveeopen.comp.LibraryQueueUI.Containers.ContainerFile;
import com.aveeopen.Design.SortDesign;

import java.util.Collections;
import java.util.Comparator;

public class FileSortingUtils {

    static Comparator<ContainerFile.Item> comparatorName = new Comparator<ContainerFile.Item>() {
        @Override
        public int compare(ContainerFile.Item lhs, ContainerFile.Item rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    };
    static Comparator<ContainerFile.Item> comparatorPath = new Comparator<ContainerFile.Item>() {
        @Override
        public int compare(ContainerFile.Item lhs, ContainerFile.Item rhs) {
            return lhs.getPath().compareTo(rhs.getPath());
        }
    };
    static Comparator<ContainerFile.Item> comparatorDateModified = new Comparator<ContainerFile.Item>() {
        @Override
        public int compare(ContainerFile.Item lhs, ContainerFile.Item rhs) {
            return Long_compare(lhs.getLastModified(), rhs.getLastModified());
        }
    };
    static Comparator<ContainerFile.Item> comparatorSize = new Comparator<ContainerFile.Item>() {
        @Override
        public int compare(ContainerFile.Item lhs, ContainerFile.Item rhs) {
            return Long_compare(lhs.getCountOrSize(), rhs.getCountOrSize());
        }
    };

    static int Long_compare(long lhs, long rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }

    public static Comparator<ContainerFile.Item> getSortComparator(SortDesign.SortDesc sortDesc) {
        Comparator<ContainerFile.Item> comparator = null;

        if (sortDesc == null) {
            return null;
        }

        boolean descending = sortDesc.sortDescending;

        switch (sortDesc.sortModeIndex) {
            case SortDesign.Sort_Mode_Title:
                comparator = comparatorName;
                break;
            case SortDesign.Sort_Mode_Album:
                comparator = comparatorName;
                break;
            case SortDesign.Sort_Mode_Artist:
                comparator = comparatorName;
                break;
            case SortDesign.Sort_Mode_Path:
                comparator = comparatorPath;
                break;
            case SortDesign.Sort_Mode_DateAdded:
                comparator = comparatorDateModified;
                descending = !descending;
                break;
            case SortDesign.Sort_Mode_DateModified:
                comparator = comparatorDateModified;
                descending = !descending;
                break;
            case SortDesign.Sort_Mode_Duration:
                comparator = comparatorSize;
                descending = !descending;
                break;
            case SortDesign.Sort_Mode_Size:
                comparator = comparatorSize;
                descending = !descending;
                break;
            default:
        }

        if (descending)
            return Collections.reverseOrder(comparator);
        else
            return comparator;
    }
}
