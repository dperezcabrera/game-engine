/* 
 * Copyright 2020 David Pérez Cabrera <dperezcabrera@gmail.com>.
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

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class FactorialPermutation implements ICombinatorial {

    private final int[] items;
    private long permutation;
    private final long maxPermutation;

    public FactorialPermutation(int size) {
        this.items = new int[size];
        this.maxPermutation = combinations(size);
        this.permutation = 0;
        setPermutationState(0);
    }

    @Override
    public int size() {
        return items.length;
    }

    @Override
    public void clear() {
        permutation = 0;
        setPermutationState(0);
    }

    private void setPermutationState(long permutation) {
        int size = items.length - 1;
        int item = size;
        long fact = combinations(item);
        long value = permutation;
        for (int i = 0; i < items.length; i++) {
            items[i] = i;
        }
        while (item > 0) {
            swap(items, (int) (value / fact), item);
            value %= fact;
            fact /= item;
            item--;
        }
    }

    @Override
    public int[] next(int[] items) {
        if (hasNext()) {
            setPermutationState(permutation);
            System.arraycopy(this.items, 0, items, 0, items.length);
            permutation++;
        }
        return items;
    }

    @Override
    public boolean hasNext() {
        return permutation < maxPermutation;
    }

    private static void swap(int[] items, int p0, int p1) {
        int swap = items[p0];
        items[p0] = items[p1];
        items[p1] = swap;
    }

    public static long combinations(int value) {
        long result = 1;
        if (value > 1) {
            result = value * combinations(value - 1);
        }
        return result;
    }

    @Override
    public long combinations() {
        return combinations(items.length);
    }
}
