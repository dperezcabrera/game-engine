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

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class CombinationContructorTest {

    private Combination instance;

    @Test
    public void testSize() {
        int subItems = 2;
        int items = 4;
        instance = new Combination(subItems, items);

        int result = instance.size();

        assertEquals(subItems, result);
    }
    
    @Test
    public void testCombinations() {
        long expectCombinations = 6L;
        int subItems = 2;
        int items = 4;
        instance = new Combination(subItems, items);

        long result = instance.combinations();

        assertEquals(expectCombinations, result);
    }

    /**
     * Test of constructor, of class Combination.
     */
    @Test
    public void testContructorSubItemError() {
        int subItems = 0;
        int items = 1;
        
        assertThrows(IllegalArgumentException.class, () -> new Combination(subItems, items));
    }

    /**
     * Test of constructor, of class Combination.
     */
    @Test
    public void testContructorItemError() {
        int subItems = 5;
        int items = 2;

        assertThrows(IllegalArgumentException.class, () -> new Combination(subItems, items));
    }
}
