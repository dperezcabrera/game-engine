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
package com.github.dperezcabrera.ge.impl;

import com.github.dperezcabrera.ge.GameException;
import com.github.dperezcabrera.ge.MethodInvoker;
import java.lang.reflect.Method;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class LocalMethodInvoker implements MethodInvoker {

    private Object target;

    public LocalMethodInvoker(Object target) {
        this.target = target;
    }

    @Override
    public void asyncCall(Method m, Object[] parameters) {
        doInvoke(m, parameters);
    }

    @Override
    public Object call(Method m, Object[] parameters) {
        return doInvoke(m, parameters);
    }

    private Object doInvoke(Method m, Object[] parameters) {
        try {
            return m.invoke(target, parameters);
        } catch (Exception ex) {
            throw new GameException("Error in invocation target: " + target, ex);
        }
    }
}
