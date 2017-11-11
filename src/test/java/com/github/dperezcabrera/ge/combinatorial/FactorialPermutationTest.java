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
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class FactorialPermutationTest {

    private static Collection<Object[]> data() {
        Object data[][] = {
            {1, 1},
            {2, 2},
            {3, 6},
            {4, 24},
            {5, 120},};
        return Arrays.asList(data);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCombinations(int items, int combinationsExpected) {
        long result = FactorialPermutation.combinations(items);
        assertEquals(combinationsExpected, result);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void integrationTest(int items, int combinationsExpected) {
        FactorialPermutation c = new FactorialPermutation(items);
        int sizeResult = c.size();
        assertEquals("Size assert, expected: <" + items + "> and result: <" + sizeResult + ">", items, sizeResult);
        Set<Integer> set = new HashSet<>(items);
        int results[][] = new int[combinationsExpected][];

        for (int i = 0; i < combinationsExpected; i++) {
            assertTrue("hasNext assert-" + i + " from " + combinationsExpected, c.hasNext());
            results[i] = new int[items];
            c.next(results[i]);
            set.clear();
            for (int j = 0; j < items; j++) {
                assertThat(results[i][j]).isBetween(0, items - 1);
                assertThat(set, not(hasItem(results[i][j])));
                set.add(results[i][j]);
            }
            // No repeat combinations assert
            for (int k = 0; k < i; k++) {
                assertThat(results[i], not(equalTo(results[k])));
            }
        }
        assertFalse("hasNext last assert", c.hasNext());

        int temp[] = new int[items];
        int expTemp[] = new int[items];
        Arrays.fill(temp, -1);
        Arrays.fill(expTemp, -1);

        // ignore next
        c.next(temp);
        assertArrayEquals("next after last assert", temp, expTemp);

        // after clear asserts
        temp = new int[items];
        c.clear();
        assertTrue("hasNext after clear assert", c.hasNext());
        for (int j = 0; j < combinationsExpected - 1; j++) {
            c.next(temp);
            // Check equals results for the same step
            assertArrayEquals(results[j], temp);
            assertTrue("hasNext after clear assert", c.hasNext());
        }
        c.next(temp);
        assertArrayEquals(results[combinationsExpected - 1], temp);

        assertFalse("hasNext last assert after clear", c.hasNext());
    }
}
