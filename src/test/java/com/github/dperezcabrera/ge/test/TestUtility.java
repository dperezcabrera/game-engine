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
package com.github.dperezcabrera.ge.test;

import com.googlecode.catchexception.apis.BDDCatchException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import org.mockito.BDDMockito;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class TestUtility {

    public static class UtilityException extends RuntimeException {
        public UtilityException(Throwable cause) {
            super(cause);
        }
    }
    
    public interface RunnableExcp<T extends Exception> {
        public void run() throws T;
    }
    
    private static ThreadLocal<Object> whenReturn = new ThreadLocal<>();

    private static void run(RunnableExcp r){
        try {
            r.run();
        } catch (Exception e){
            throw new UtilityException(e);
        }
    }
    
    public static void given(RunnableExcp r){
        run(r);
    }
    
    public static void when(RunnableExcp r) {
        run(BDDCatchException.when(r));
    }

    public static void thenCheck(RunnableExcp r) {
        run(r);
    }

    public static <T> void when(Supplier<T> s) {
        whenReturn.set(BDDCatchException.when(s).get());
    }

    public static <T> T returnedObject(Class<T> type) {
        return (T) whenReturn.get();
    }

    public static Object returnedObject() {
        return whenReturn.get();
    }

    public static <T> UtilStubber<T> givenMock(T mock) {
        return new UtilStubber<>(mock);
    }

    public static void execute(Executor executor, long time, Runnable runnable) {
        executor.execute(() -> {
            Object mutex = new Object();
            synchronized (mutex) {
                try {
                    mutex.wait(time);
                } catch (InterruptedException ex) {
                    //
                }
            }
            runnable.run();
        });
    }

    @AllArgsConstructor
    public static class UtilStubber<T> {

        private final T mock;

        public CallTypeThrowableStubber<T> willThrow(Class<? extends Throwable> throwableType) {
            return new CallTypeThrowableStubber<>(mock, throwableType);
        }

        public <U extends Throwable> CallTrowableStubber willThrow(@NonNull U throwable) {
            return new CallTrowableStubber(mock, throwable);
        }
    }

    @AllArgsConstructor
    public static class CallTrowableStubber<T> {

        private final T mock;
        private final Throwable throwable;

        public T callingTo() {
            return BDDMockito.willThrow(throwable).given(mock);
        }
    }

    @AllArgsConstructor
    public static class CallTypeThrowableStubber<T> {

        private final T mock;
        private final Class<? extends Throwable> throwableType;

        public T callingTo() {
            return BDDMockito.willThrow(throwableType).given(mock);
        }
    }
}
