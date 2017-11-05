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

import org.junit.Test;

import static com.github.dperezcabrera.ge.test.TestUtility.given;
import static com.github.dperezcabrera.ge.test.TestUtility.returnedObject;
import static com.github.dperezcabrera.ge.test.TestUtility.when;
import static com.googlecode.catchexception.apis.BDDCatchException.caughtException;
import static org.assertj.core.api.BDDAssertions.then;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class CombinationContructorTest {

    Combination instance;
    int subItems;
    int items;

    @Test
    public void testSize() {
        given(() -> subItems = 2);
        given(() -> items = 4);
        given(() -> instance = new Combination(subItems, items));

        when(() -> instance.size());

        then(returnedObject()).isEqualTo(subItems);
    }
    
    @Test
    public void testCombinations() {
        long expectCombinations = 6L;
        given(() -> subItems = 2);
        given(() -> items = 4);
        given(() -> instance = new Combination(subItems, items));

        when(() -> instance.combinations());

        then(returnedObject()).isEqualTo(expectCombinations);
    }

    /**
     * Test of constructor, of class Combination.
     */
    @Test
    public void testContructorSubItemError() {
        given(() -> subItems = 0);
        given(() -> items = 1);
        
        when(() -> new Combination(subItems, items));

        then(caughtException())
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test of constructor, of class Combination.
     */
    @Test
    public void testContructorItemError() {
        given(() -> subItems = 5);
        given(() -> items = 1);

        when(() -> new Combination(subItems, items));

        then(caughtException())
                .isInstanceOf(IllegalArgumentException.class);
    }
}
