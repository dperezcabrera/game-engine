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
package com.github.dperezcabrera.ge.util;

import com.github.dperezcabrera.ge.GameException;
import com.github.dperezcabrera.ge.annotations.Command;
import com.github.dperezcabrera.ge.annotations.Timeout;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
@Slf4j
public enum Utilities {
    ;// without instances

    public static final String MIN_VALUE_ERR_MSG = "The argument {0} can not be less than <{1}> and it is <{2}>.";
    public static final String MAX_VALUE_ERR_MSG = "The argument {0} can not be greather than <{1}> and it is <{2}>.";
    public static final String NULL_ERR_MSG = "The argument {0} can not be null.";
    public static final String NULL_OR_EMPTY_ERR_MSG = "The argument {0} can not be null or empty.";
    public static final String LENGTH_ERR_MSG = "The argument {0} can not be null and its length must be {1}.";
    public static final String SIZE_ERR_MSG = "The argument {0} can not be null and it must to contain {1} elements.";
    
    public static boolean close(Closeable c) {
        boolean result = false;
        try {
            if (c != null) {
                c.close();
                result = true;
            }
        } catch (IOException ex) {
            log.error("Closeable '"+c+"' throws IOException when it was closed", ex);
        }
        return result;
    }

    public static Map<Method, Long> calculateTimeouts(Class<?> type, Properties properties) {
        try {
            Map<Method, Long> result = new HashMap<>();
            Timeout timeoutDefault = type.getDeclaredAnnotation(Timeout.class);
            for (Method method : type.getMethods()) {
                Timeout timeout = get(method.getAnnotation(Timeout.class), timeoutDefault);
                if (method.getReturnType() == void.class || timeout == null) {
                    result.put(method, null);
                } else if (!properties.containsKey(timeout.value())) {
                    log.warn("Propery \"{}\" not defined", timeout.value());
                    result.put(method, null);
                } else {
                    result.put(method, Long.parseLong(properties.getProperty(timeout.value())));
                }
            }
            return Collections.unmodifiableMap(result);
        } catch (Exception e) {
            throw new GameException("Property timeout error", e);
        }
    }
    
    public static String getCommandName(Method m){
        String result;
        Command commandName = m.getAnnotation(Command.class);
        if (commandName == null){
            result = m.getName();
        } else {
            result = commandName.name();
        }
        return result;
    }

    private static <T> T get(T element, T alternativeIfNull) {
        T result = element;
        if (result == null) {
            result = alternativeIfNull;
        }
        return result;
    }

    
    private static void doCheckArgument(boolean throwException, String message, Object... args) {
        if (throwException) {
            throw new IllegalArgumentException(MessageFormat.format(message, args));
        }
    }
    
    public static void checkMinValueArgument(long o, long minValue, String name) {
        doCheckArgument(o < minValue, MIN_VALUE_ERR_MSG, name, minValue, o);
    }

    public static void checkMaxValueArgument(long o, long minValue, String name) {
        doCheckArgument(o > minValue, MAX_VALUE_ERR_MSG, name, minValue, o);
    }

    public static void checkNullOrEmptyArgument(Collection<?> o, String name) {
        doCheckArgument(o == null || o.isEmpty(), NULL_OR_EMPTY_ERR_MSG, name);
    }
    
    public static void checkNullOrEmptyArgument(Map<?, ?> o, String name) {
        doCheckArgument(o == null || o.isEmpty(), NULL_OR_EMPTY_ERR_MSG, name);
    }
    
    public static void checkListSizeArgument(List a, String name, int length) {
        doCheckArgument(a == null || a.size() != length, SIZE_ERR_MSG, name, length);
    }

    public static <T> void checkArrayLengthArgument(T[] a, String name, int length) {
        doCheckArgument(a == null || a.length != length, LENGTH_ERR_MSG, name, length);
    }

    public static void checkArgument(boolean throwException, String message, Object... args) {
        doCheckArgument(throwException, message, args);
    }
}
