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
package com.github.dperezcabrera.ge.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.BDDMockito;

import static com.github.dperezcabrera.ge.test.TestUtility.givenMock;
import static com.github.dperezcabrera.ge.test.TestUtility.returnedObject;
import static com.github.dperezcabrera.ge.test.TestUtility.when;
import static com.googlecode.catchexception.apis.BDDCatchException.caughtException;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.mock;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class UtilitiesTest {

    @Test
    public void testGetDefaultValue() {
        Class type = double.class;
        Object expResult = 0d;

        when(() -> Utilities.getDefaultValue(type));

        then(returnedObject()).isEqualTo(expResult);
    }

    @Test
    public void testClose() throws IOException {
        Closeable c = mock(Closeable.class);

        when(() -> Utilities.close(c));

        then(returnedObject(Boolean.class)).isTrue();

        BDDMockito.then(c).should().close();
    }

    @Test
    public void testCloseNull() throws IOException {

        when(() -> Utilities.close(null));

        then(returnedObject(Boolean.class)).isFalse();
        then(caughtException()).isNull();
    }

    @Test
    public void testCloseException() throws IOException {
        Closeable c = mock(Closeable.class);

        givenMock(c).willThrow(IOException.class).callingTo().close();

        when(() -> Utilities.close(c));

        then(returnedObject(Boolean.class)).isFalse();
        then(caughtException()).isNull();
    }

    @Test
    public void testCheckMinValueArgument_3args_1() {
        int o = 0;
        int minValue = 0;
        String name = "name";

        when(() -> Utilities.checkMinValueArgument(o, minValue, name));

        then(caughtException()).isNull();
    }

    @Test
    public void testCheckMinValueArgument_3args_1_Exception() {
        int o = 0;
        int minValue = 1;
        String name = "name";

        when(() -> Utilities.checkMinValueArgument(o, minValue, name));

        then(caughtException())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The argument " + name + " can not be less than <" + minValue + "> and it is <" + o + ">.")
                .hasNoCause();
    }

    @Test
    public void testCheckMinValueArgument_3args_2() {
        long o = 0L;
        long minValue = 0L;
        String name = "name";

        when(() -> Utilities.checkMinValueArgument(o, minValue, name));

        then(caughtException()).isNull();
    }

    @Test
    public void testCheckMinValueArgument_3args_2_exception() {
        long o = 0L;
        long minValue = 1L;
        String name = "name";

        when(() -> Utilities.checkMinValueArgument(o, minValue, name));

        then(caughtException())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The argument " + name + " can not be less than <" + minValue + "> and it is <" + o + ">.")
                .hasNoCause();
    }

    @Test
    public void testCheckNullOrEmptyArgument_Collection() {
        when(() -> Utilities.checkNullOrEmptyArgument(Arrays.asList("a"), "name"));

        then(caughtException()).isNull();
    }

    @Test
    public void testCheckNullOrEmptyArgument_Collection_null() {
        String name = "name";

        when(() -> Utilities.checkNullOrEmptyArgument((List) null, name));

        then(caughtException())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The argument " + name + " can not be null or empty.")
                .hasNoCause();
    }

    @Test
    public void testCheckNullOrEmptyArgument_Collection_empty() {
        String name = "name";

        when(() -> Utilities.checkNullOrEmptyArgument(Arrays.asList(), name));

        then(caughtException())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The argument " + name + " can not be null or empty.")
                .hasNoCause();
    }

    @Test
    public void testCheckNullOrEmptyArgument_Map() {
        String name = "name";
        Map<String, String> map = new HashMap<>();
        map.put(name, name);

        when(() -> Utilities.checkNullOrEmptyArgument(map, name));

        then(caughtException()).isNull();
    }

    @Test
    public void testCheckNullOrEmptyArgument_Map_null() {
        String name = "name";
        Map<String, String> map = null;

        when(() -> Utilities.checkNullOrEmptyArgument(map, name));

        then(caughtException())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The argument " + name + " can not be null or empty.")
                .hasNoCause();
    }

    @Test
    public void testCheckNullOrEmptyArgument_Map_empty() {
        String name = "name";
        Map<String, String> map = new HashMap<>();

        when(() -> Utilities.checkNullOrEmptyArgument(map, name));

        then(caughtException())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The argument " + name + " can not be null or empty.")
                .hasNoCause();
    }

    @Test
    public void testCheckListSizeArgument() {
        List<Integer> a = Arrays.asList(1);
        String name = "name";
        int length = 1;

        when(() -> Utilities.checkListSizeArgument(a, name, length));

        then(caughtException()).isNull();
    }

    @Test
    public void testCheckListSizeArgument_exception() {
        List<Integer> a = Arrays.asList(1, 3);
        String name = "name";
        int length = 1;

        when(() -> Utilities.checkListSizeArgument(a, name, length));

        then(caughtException())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The argument " + name + " can not be null and it must to contain " + length + " elements.")
                .hasNoCause();
    }

    @Test
    public void testCheckListSizeArgument_exception_null() {
        List<Integer> a = null;
        String name = "name";
        int length = 1;

        when(() -> Utilities.checkListSizeArgument(a, name, length));

        then(caughtException())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The argument " + name + " can not be null and it must to contain " + length + " elements.")
                .hasNoCause();
    }

    @Test
    public void testCheckArrayLengthArgument() {
        System.out.println("checkArrayLengthArgument");
        int length = 1;
        Object[] a = new Object[length];
        String name = "name";

        when(() -> {
            Utilities.checkArrayLengthArgument(a, name, length);
        });

        then(caughtException()).isNull();
    }

    @Test
    public void testCheckArrayLengthArgument_exception_null() {
        int length = 1;
        Object[] a = null;
        String name = "name";

        when(() -> {
            Utilities.checkArrayLengthArgument(a, name, length);
        });

        then(caughtException())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The argument " + name + " can not be null and its length must be " + length + ".")
                .hasNoCause();
    }

    @Test
    public void testCheckArrayLengthArgument_exception() {
        int length = 1;
        Object[] a = new Object[length + 1];
        String name = "name";

        when(() -> {
            Utilities.checkArrayLengthArgument(a, name, length);
        });

        then(caughtException())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The argument " + name + " can not be null and its length must be " + length + ".")
                .hasNoCause();
    }

    @Test
    public void testCheckArgument() {
        boolean throwException = false;
        String message = "error";
        Object[] args = null;

        when(() -> Utilities.checkArgument(throwException, message, args));

        then(caughtException()).isNull();
    }
}
