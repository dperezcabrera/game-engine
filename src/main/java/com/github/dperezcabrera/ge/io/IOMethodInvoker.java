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
package com.github.dperezcabrera.ge.io;

import com.github.dperezcabrera.ge.MethodInvoker;
import java.lang.reflect.Method;

import lombok.AllArgsConstructor;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
@AllArgsConstructor
public class IOMethodInvoker implements MethodInvoker, AutoCloseable {

    public static final String ASYNC_CALL_CMD = "A";
    public static final String CALL_CMD = "C";
    public static final String RESPONSE_CMD = "R";

    private IOConnector connector;
    private Serializer<MethodCall, byte[]> serializer;

    @Override
    public synchronized void asyncCall(Method m, Object[] parameters) {
        connector.send(new GameEvent(ASYNC_CALL_CMD, serializer.serialize(new MethodCall(m, parameters, null))));
    }

    @Override
    public synchronized Object call(Method m, Object[] parameters) {
        connector.send(new GameEvent(CALL_CMD, serializer.serialize(new MethodCall(m, parameters, null))));
        return serializer.deserialize(connector.receive().getPayload()).getResponse();
    }

    public synchronized boolean readCommand(MethodInvoker invoker) {
        try {
            GameEvent e = connector.receive();
            if (ASYNC_CALL_CMD.equals(e.getCommand())) {
                MethodCall mc = serializer.deserialize(e.getPayload());
                invoker.asyncCall(mc.getMethod(), mc.getParameters());
            } else if (CALL_CMD.equals(e.getCommand())) {
                MethodCall mc = serializer.deserialize(e.getPayload());
                Object response = invoker.call(mc.getMethod(), mc.getParameters());
                connector.send(new GameEvent(RESPONSE_CMD, serializer.serialize(new MethodCall(mc.getMethod(), null, response))));
            } else if (GameEvent.EXIT.getCommand().equals(e.getCommand())) { // exit command
                connector.close();
            } else {
                throw new IOGameException("Unknown command '" + e.getCommand() + "'");
            }
        } catch (IOGameException e) {
            close();
            throw new IOGameException("Error in readCommand", e);
        }
        return !connector.isClosed();
    }

    @Override
    public synchronized void close() {
        connector.close();
    }
}
