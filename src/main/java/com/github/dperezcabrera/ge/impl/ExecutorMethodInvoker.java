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
package com.github.dperezcabrera.ge.impl;

import com.github.dperezcabrera.ge.GameException;
import com.github.dperezcabrera.ge.MethodInvoker;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
@Slf4j
public class ExecutorMethodInvoker implements MethodInvoker {

    private Object player;
    private Executor executor;
    private Map<Method, Long> timeouts;
    private Map<Object, Response> responses = new HashMap<>();

    public ExecutorMethodInvoker(Object player, Executor executor, Map<Method, Long> timeouts) {
        this.player = player;
        this.executor = executor;
        this.timeouts = timeouts;
    }

    private void doCall(final Method method, final Object[] args, Object mutex) {
        try {
            Object result = method.invoke(player, args);
            synchronized (mutex) {
                responses.put(mutex, () -> result);
                mutex.notify();
            }
        } catch (Exception e) {
            synchronized (mutex) {
                responses.put(mutex, () -> {
                    throw new GameException("Error in method " + method, e);
                });
            }
        }
    }

    private void doAsyncCall(final Method method, final Object[] args) {
        try {
            method.invoke(player, args);
        } catch (Exception e) {
            log.warn("Error in method " + method, e);
        }
    }

    @Override
    public void asyncCall(final Method m, final Object[] parameters) {
        executor.execute(() -> doAsyncCall(m, parameters));
    }

    @Override
    public Object call(Method method, Object[] parameters) {
        final Object mutex = new Object();
        synchronized (mutex) {
            executor.execute(() -> doCall(method, parameters, mutex));
            try {
                mutex.wait(timeouts.get(method));
            } catch (InterruptedException ex) {
                throw new GameException("Interrupted Exception in call to "+method, ex);
            }
            if (!responses.containsKey(mutex)) {
                responses.put(mutex, () -> {
                    throw new GameException("TimeOut error in method " + method);
                });
            }
        }
        return responses.remove(mutex).get();
    }

    @FunctionalInterface
    private interface Response {

        public Object get();
    }

}
