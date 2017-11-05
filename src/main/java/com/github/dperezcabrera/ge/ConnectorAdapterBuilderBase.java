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
package com.github.dperezcabrera.ge;

import com.github.dperezcabrera.ge.util.Utilities;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import lombok.AllArgsConstructor;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class ConnectorAdapterBuilderBase implements ConnectorAdapterBuilder {

    @Override
    public <T> T connector(Class<T> type, MethodInvoker invoker, Map<Method, Long> timeouts) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new PlayerConnector(invoker, timeouts));
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
                return Utilities.getDefaultValue(method.getReturnType());
            } else {
                return invoker.call(method, args);
            }
        }
    }
}
