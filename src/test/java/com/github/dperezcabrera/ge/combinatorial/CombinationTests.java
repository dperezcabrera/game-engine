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

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class CombinationTests {

    private static Collection<Object[]> data() {
        Object data[][] = { { 1, 1, 1 }, { 1, 2, 2 }, { 2, 2, 1 }, { 1, 3, 3 }, { 2, 3, 3 }, { 3, 3, 1 }, { 1, 4, 4 },
                { 2, 4, 6 }, { 3, 4, 4 }, { 4, 4, 1 }, { 1, 5, 5 }, { 2, 5, 10 }, { 5, 7, 21 } };
        return Arrays.asList(data);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCombinationsStatic(int subItems, int items, int combinationsExpected) {
        long result = Combination.combinations(subItems, items);

        assertEquals(combinationsExpected, result);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSize(int subItems, int items, int combinationsExpected) {
        Combination instance = new Combination(subItems, items);

        int result = instance.size();

        assertEquals(subItems, result);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCombinations(int subItems, int items, int combinationsExpected) {
        Combination instance = new Combination(subItems, items);

        long result = instance.combinations();

        assertEquals(combinationsExpected, result);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tesHasNextFirst(int subItems, int items, int combinationsExpected) {
        Combination instance = new Combination(subItems, items);

        assertTrue(instance.hasNext());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHasNextPreLast(int subItems, int items, int combinationsExpected) {
        Combination instance = new Combination(subItems, items);
        int indexes[] = new int[subItems];
        for (int i = 0; i < combinationsExpected - 1; i++) {
            instance.next(indexes);
        }

        assertTrue(instance.hasNext());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHasNextLast(int subItems, int items, int combinationsExpected) {
        Combination instance = new Combination(subItems, items);

        int indexes[] = new int[subItems];
        while (instance.hasNext()) {
            instance.next(indexes);
        }

        assertFalse(instance.hasNext());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHasNextAfterClear(int subItems, int items, int combinationsExpected) {
        Combination instance = new Combination(subItems, items);

        instance.clear();

        assertTrue(instance.hasNext());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHasNextAfterClearWithNext(int subItems, int items, int combinationsExpected) {
        Combination instance = new Combination(subItems, items);

        instance.next(new int[subItems]);
        instance.clear();

        assertTrue(instance.hasNext());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHasNextAfterFullLoopClear(int subItems, int items, int combinationsExpected) {
        Combination instance = new Combination(subItems, items);

        int indexes[] = new int[subItems];
        while (instance.hasNext()) {
            instance.next(indexes);
        }
        instance.clear();

        assertTrue(instance.hasNext());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testNextItemsRange(int subItems, int items, int combinationsExpected) {
        Combination instance = new Combination(subItems, items);

        int[][] allCombinations = new int[combinationsExpected][];
        for (int i = 0; i < combinationsExpected; i++) {
            instance.next(allCombinations[i] = new int[subItems]);
        }

        // Sort and range assert
        for (int i = 0; i < combinationsExpected; i++) {
            assertThat(allCombinations[i][0]).isBetween(0, items - 1);
            for (int j = 1; j < subItems; j++) {
                assertThat(allCombinations[i][j]).isBetween(allCombinations[i][j - 1] + 1, items - 1);
            }
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testNextDontRepeat(int subItems, int items, int combinationsExpected) {
        Combination instance = new Combination(subItems, items);

        int[][] allCombinations = new int[combinationsExpected][];
        for (int i = 0; i < combinationsExpected; i++) {
            instance.next(allCombinations[i] = new int[subItems]);
        }

        // No repeat previous combinations assert
        for (int i = 0; i < combinationsExpected; i++) {
            for (int k = 0; k < i; k++) {
                assertThat(allCombinations[i], not(equalTo(allCombinations[k])));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testNextIgnored(int subItems, int items, int combinationsExpected) {
        Combination instance = new Combination(subItems, items);

        int result[] = new int[subItems];
        for (int i = 0; i < combinationsExpected; i++) {
            instance.next(result);
        }
        Arrays.fill(result, -1);
        instance.next(result);

        assertThat(result).containsOnly(-1);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testNextAfterClear(int subItems, int items, int combinationsExpected) {
        Combination instance = new Combination(subItems, items);

        int[][] allCombinations = new int[combinationsExpected][];
        for (int i = 0; i < combinationsExpected; i++) {
            instance.next(allCombinations[i] = new int[subItems]);
        }

        // Check equals results for the same step
        instance.clear();
        int[][] result = new int[combinationsExpected][];
        for (int i = 0; i < combinationsExpected; i++) {
            instance.next(result[i] = new int[subItems]);
        }

        assertArrayEquals(allCombinations, result);
    }
}
