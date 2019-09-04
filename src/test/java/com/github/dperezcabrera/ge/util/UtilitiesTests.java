/* 
 * Copyright 2019 David Pérez Cabrera <dperezcabrera@gmail.com>.
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
package com.github.dperezcabrera.ge.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import static org.mockito.Mockito.mock;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class UtilitiesTests {

    @Test
    public void testClose() throws IOException {
        Closeable c = mock(Closeable.class);

        boolean result = Utilities.close(c);

        assertTrue(result);

        then(c).should().close();
    }

    @Test
    public void testCloseNull() throws IOException {

        boolean result = Utilities.close(null);

        assertFalse(result);
    }

    @Test
    public void testCloseException() throws IOException {
        Closeable c = mock(Closeable.class);

        willThrow(IOException.class).given(c).close();

        boolean result = Utilities.close(c);

        assertFalse(result);
    }

    @Test
    public void testCheckMinValueArgument_3args_1() {
        int o = 0;
        int minValue = 0;
        String name = "name";

        Utilities.checkMinValueArgument(o, minValue, name);
    }

    @Test
    public void testCheckMinValueArgument_3args_1_Exception() {
        int o = 0;
        int minValue = 1;
        String name = "name";

        assertThrows(IllegalArgumentException.class, () -> Utilities.checkMinValueArgument(o, minValue, name),
                "The argument " + name + " can not be less than <" + minValue + "> and it is <" + o + ">.");
    }

    @Test
    public void testCheckMinValueArgument_3args_2() {
        long o = 0L;
        long minValue = 0L;
        String name = "name";

        Utilities.checkMinValueArgument(o, minValue, name);
    }

    @Test
    public void testCheckMinValueArgument_3args_2_exception() {
        long o = 0L;
        long minValue = 1L;
        String name = "name";

        assertThrows(IllegalArgumentException.class, () -> Utilities.checkMinValueArgument(o, minValue, name),
                "The argument " + name + " can not be less than <" + minValue + "> and it is <" + o + ">.");
    }

    @Test
    public void testCheckNullOrEmptyArgument_Collection() {
        Utilities.checkNullOrEmptyArgument(Arrays.asList("a"), "name");
    }

    @Test
    public void testCheckNullOrEmptyArgument_Collection_null() {
        String name = "name";

        assertThrows(IllegalArgumentException.class, () -> Utilities.checkNullOrEmptyArgument((List) null, name),
                "The argument " + name + " can not be null or empty.");
    }

    @Test
    public void testCheckNullOrEmptyArgument_Collection_empty() {
        String name = "name";

        assertThrows(IllegalArgumentException.class, () -> Utilities.checkNullOrEmptyArgument(Arrays.asList(), name),
                "The argument " + name + " can not be null or empty.");
    }

    @Test
    public void testCheckNullOrEmptyArgument_Map() {
        String name = "name";
        Map<String, String> map = new HashMap<>();
        map.put(name, name);

        Utilities.checkNullOrEmptyArgument(map, name);
    }

    @Test
    public void testCheckNullOrEmptyArgument_Map_null() {
        String name = "name";
        Map<String, String> map = null;

        assertThrows(IllegalArgumentException.class, () -> Utilities.checkNullOrEmptyArgument(map, name),
                "The argument " + name + " can not be null or empty.");
    }

    @Test
    public void testCheckNullOrEmptyArgument_Map_empty() {
        String name = "name";
        Map<String, String> map = new HashMap<>();

        assertThrows(IllegalArgumentException.class, () -> Utilities.checkNullOrEmptyArgument(map, name),
                "The argument " + name + " can not be null or empty.");
    }

    @Test
    public void testCheckListSizeArgument() {
        List<Integer> a = Arrays.asList(1);
        String name = "name";
        int length = 1;

        Utilities.checkListSizeArgument(a, name, length);
    }

    @Test
    public void testCheckListSizeArgument_exception() {
        List<Integer> a = Arrays.asList(1, 3);
        String name = "name";
        int length = 1;

        assertThrows(IllegalArgumentException.class, () -> Utilities.checkListSizeArgument(a, name, length),
                "The argument " + name + " can not be null and it must to contain " + length + " elements.");
    }

    @Test
    public void testCheckListSizeArgument_exception_null() {
        List<Integer> a = null;
        String name = "name";
        int length = 1;

        assertThrows(IllegalArgumentException.class, () -> Utilities.checkListSizeArgument(a, name, length),
                "The argument " + name + " can not be null and it must to contain " + length + " elements.");
    }

    @Test
    public void testCheckArrayLengthArgument() {
        System.out.println("checkArrayLengthArgument");
        int length = 1;
        Object[] a = new Object[length];
        String name = "name";

        Utilities.checkArrayLengthArgument(a, name, length);
    }

    @Test
    public void testCheckArrayLengthArgument_exception_null() {
        int length = 1;
        Object[] a = null;
        String name = "name";

        assertThrows(IllegalArgumentException.class, () -> Utilities.checkArrayLengthArgument(a, name, length),
                "The argument " + name + " can not be null and its length must be " + length + ".");
    }

    @Test
    public void testCheckArrayLengthArgument_exception() {
        int length = 1;
        Object[] a = new Object[length + 1];
        String name = "name";

        assertThrows(IllegalArgumentException.class, () -> Utilities.checkArrayLengthArgument(a, name, length),
                "The argument " + name + " can not be null and its length must be " + length + ".");
    }

    @Test
    public void testCheckArgument() {
        boolean throwException = false;
        String message = "error";
        Object[] args = null;

        Utilities.checkArgument(throwException, message, args);
    }
}
