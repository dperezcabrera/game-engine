/* 
 * Copyright 2017 David Pérez Cabrera <dperezcabrera@gmail.com>.
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
package com.github.dperezcabrera.ge.combinatorial;

import com.github.dperezcabrera.ge.util.Utilities;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class Combination implements ICombinatorial {

    private final int items;
    private final int[] indexes;

    public Combination(int subItems, int items) {
        Utilities.checkMinValueArgument(subItems, 1, "subItems");
        Utilities.checkMinValueArgument(items, subItems, "items");
        this.indexes = new int[subItems];
        this.items = items;
        init();
    }

    @Override
    public long combinations() {
        return combinations(indexes.length, items);
    }

    @Override
    public int size() {
        return indexes.length;
    }

    public int getSubItems() {
        return indexes.length;
    }

    public int getItems() {
        return items;
    }

    private boolean hasNext(int index) {
        return indexes[index] + (indexes.length - index) < items;
    }

    private void move(int index) {
        if (hasNext(index)) {
            indexes[index]++;
            int last = indexes[index];
            for (int i = index + 1; i < indexes.length; i++) {
                this.indexes[i] = ++last;
            }
        } else {
            move(index - 1);
        }
    }

    @Override
    public int[] next(int[] items) {
        if (hasNext()) {
            move(indexes.length - 1);
            System.arraycopy(indexes, 0, items, 0, indexes.length);
        }
        return items;
    }

    @Override
    public boolean hasNext() {
        return hasNext(0) || hasNext(indexes.length - 1);
    }

    private void init() {
        int index = indexes.length;
        for (int i = 0; i < indexes.length; i++) {
            this.indexes[i] = i;
        }
        this.indexes[index - 1]--;
    }

    @Override
    public void clear() {
        init();
    }

    public static long combinations(int subItems, int items) {
        long result = 1;
        int sub = Math.max(subItems, items - subItems);
        for (int i = sub + 1; i <= items; i++) {
            result = (result * i) / (i - sub);
        }
        return result;
    }
}
