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

import java.util.Arrays;
import java.util.Collection;

import static com.github.dperezcabrera.ge.test.TestUtility.given;
import static com.github.dperezcabrera.ge.test.TestUtility.returnedObject;
import static com.github.dperezcabrera.ge.test.TestUtility.thenCheck;
import static com.github.dperezcabrera.ge.test.TestUtility.when;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class CombinationTest {

    Combination instance;
    int[][] allCombinations;

    private static Collection<Object[]> data() {
        Object data[][] = {
            {1, 1, 1},
            {1, 2, 2},
            {2, 2, 1},
            {1, 3, 3},
            {2, 3, 3},
            {3, 3, 1},
            {1, 4, 4},
            {2, 4, 6},
            {3, 4, 4},
            {4, 4, 1},
            {1, 5, 5},
            {2, 5, 10},
            {5, 7, 21}
        };
        return Arrays.asList(data);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCombinationsStatic(int subItems, int items, int combinationsExpected) {
        when(() -> Combination.combinations(subItems, items));

        then(returnedObject(long.class)).isEqualTo(combinationsExpected);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testSize(int subItems, int items, int combinationsExpected) {
        given(() -> instance = new Combination(subItems, items));

        when(() -> instance.size());

        then(returnedObject(int.class)).isEqualTo(subItems);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCombinations(int subItems, int items, int combinationsExpected) {
        given(() -> instance = new Combination(subItems, items));

        when(() -> instance.combinations());

        then(returnedObject(long.class)).isEqualTo(combinationsExpected);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tesHasNextFirst(int subItems, int items, int combinationsExpected) {
        when(() -> new Combination(subItems, items));

        then(returnedObject(Combination.class).hasNext()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHasNextPreLast(int subItems, int items, int combinationsExpected) {
        given(() -> instance = new Combination(subItems, items));

        when(() -> {
            int indexes[] = new int[subItems];
            for (int i = 0; i < combinationsExpected - 1; i++) {
                instance.next(indexes);
            }
        });

        then(instance.hasNext()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHasNextLast(int subItems, int items, int combinationsExpected) {
        given(() -> instance = new Combination(subItems, items));

        when(() -> {
            int indexes[] = new int[subItems];
            while (instance.hasNext()) {
                instance.next(indexes);
            }
        });

        then(instance.hasNext()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHasNextAfterClear(int subItems, int items, int combinationsExpected) {
        given(() -> instance = new Combination(subItems, items));

        when(() -> instance.clear());

        then(instance.hasNext()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHasNextAfterClearWithNext(int subItems, int items, int combinationsExpected) {
        given(() -> instance = new Combination(subItems, items));

        when(() -> {
            instance.next(new int[subItems]);
            instance.clear();
        });

        then(instance.hasNext()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testHasNextAfterFullLoopClear(int subItems, int items, int combinationsExpected) {
        given(() -> instance = new Combination(subItems, items));

        when(() -> {
            int indexes[] = new int[subItems];
            while (instance.hasNext()) {
                instance.next(indexes);
            }
            instance.clear();
        });

        then(instance.hasNext()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testNextItemsRange(int subItems, int items, int combinationsExpected) {
        given(() -> instance = new Combination(subItems, items));

        when(() -> {
            allCombinations = new int[combinationsExpected][];
            for (int i = 0; i < combinationsExpected; i++) {
                instance.next(allCombinations[i] = new int[subItems]);
            }
        });

        // Sort and range assert
        thenCheck(() -> {
            for (int i = 0; i < combinationsExpected; i++) {
                assertThat(allCombinations[i][0]).isBetween(0, items - 1);
                for (int j = 1; j < subItems; j++) {
                    assertThat(allCombinations[i][j]).isBetween(allCombinations[i][j - 1] + 1, items - 1);
                }
            }
        });
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testNextDontRepeat(int subItems, int items, int combinationsExpected) {
        given(() -> instance = new Combination(subItems, items));

        when(() -> {
            allCombinations = new int[combinationsExpected][];
            for (int i = 0; i < combinationsExpected; i++) {
                instance.next(allCombinations[i] = new int[subItems]);
            }
        });

        // No repeat previous combinations assert
        thenCheck(() -> {
            for (int i = 0; i < combinationsExpected; i++) {
                for (int k = 0; k < i; k++) {
                    assertThat(allCombinations[i], not(equalTo(allCombinations[k])));
                }
            }
        });
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testNextIgnored(int subItems, int items, int combinationsExpected) {
        given(() -> instance = new Combination(subItems, items));

        when(() -> {
            int indexes[] = new int[subItems];
            for (int i = 0; i < combinationsExpected; i++) {
                instance.next(indexes);
            }
            Arrays.fill(indexes, -1);
            instance.next(indexes);
            return indexes;
        });

        then(returnedObject(int[].class)).containsOnly(-1);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testNextAfterClear(int subItems, int items, int combinationsExpected) {
        given(() -> instance = new Combination(subItems, items));
        given(() -> {
            allCombinations = new int[combinationsExpected][];
            for (int i = 0; i < combinationsExpected; i++) {
                instance.next(allCombinations[i] = new int[subItems]);
            }
        });

        // Check equals results for the same step
        when(() -> {
            instance.clear();
            int[][] combinations = new int[combinationsExpected][];
            for (int i = 0; i < combinationsExpected; i++) {
                instance.next(combinations[i] = new int[subItems]);
            }
            return combinations;
        });

        then(returnedObject()).isEqualTo(allCombinations);
    }
}
