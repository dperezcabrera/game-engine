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
package com.github.dperezcabrera.ge;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class ConnectorAdapterBuilderBase implements ConnectorAdapterBuilder {
    private static final Map<Class<?>, Object> DEFAULT_FALUE_PRIMITIVES = initDefaultValuePrimitiveTypes();

    @Override
    public <T> T connector(Class<T> type, MethodInvoker invoker, Map<Method, Long> timeouts) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new PlayerConnector(invoker, timeouts));
    }

    private static Map<Class<?>, Object> initDefaultValuePrimitiveTypes() {
        Map<Class<?>, Object> result = new HashMap<>();
        result.put(boolean.class, false);
        result.put(byte.class, (byte) 0);
        result.put(char.class, (char) 0);
        result.put(double.class, 0d);
        result.put(float.class, 0f);
        result.put(int.class, 0);
        result.put(long.class, 0L);
        result.put(short.class, (short) 0);
        return Collections.unmodifiableMap(result);
    }

    @AllArgsConstructor
    private static class PlayerConnector implements InvocationHandler {

        private MethodInvoker invoker;
        private Map<Method, Long> timeouts;

        @Override
        public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
            Long time = timeouts.get(method);
            if (time == null) {
                invoker.asyncCall(method, args);
                return DEFAULT_FALUE_PRIMITIVES.get(method.getReturnType());
            } else {
                return invoker.call(method, args);
            }
        }
    }
}
