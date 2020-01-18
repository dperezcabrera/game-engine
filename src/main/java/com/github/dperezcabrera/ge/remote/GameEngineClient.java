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
package com.github.dperezcabrera.ge.remote;

import com.github.dperezcabrera.ge.GameException;
import com.github.dperezcabrera.ge.MethodInvoker;
import com.github.dperezcabrera.ge.impl.LocalMethodInvoker;
import com.github.dperezcabrera.ge.io.IOConnectorBase;
import com.github.dperezcabrera.ge.io.IOGameException;
import com.github.dperezcabrera.ge.io.IOMethodInvoker;
import com.github.dperezcabrera.ge.io.MethodCall;
import com.github.dperezcabrera.ge.io.Serializer;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executor;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
@Slf4j
public class GameEngineClient implements AutoCloseable {

    private volatile boolean exit = false;
    private IOMethodInvoker methodInvoker;

    public GameEngineClient(IOMethodInvoker methodInvoker) {
        this.methodInvoker = methodInvoker;
    }

    public static <P> GameEngineClient start(String host, int port, P player, String name, AuthenticationClient authentication, Serializer<MethodCall, byte[]> serializer) {
        return start(host, port, player, name, authentication, serializer, r -> new Thread(r).start());
    }

    public static <P> GameEngineClient start(String host, int port, P player, String name, AuthenticationClient authentication, Serializer<MethodCall, byte[]> serializer, Executor executor) {
        try {
            log.info("Connecting...  {}:{} > {} ", host, port, name);
            Socket socket = new Socket(host, port);
            log.info("Connected [Ok] {}:{} > {} ", host, port, name);
            IOConnectorBase connector = new IOConnectorBase(socket.getInputStream(), socket.getOutputStream());
            log.info("Authenticating...  {}:{} > {} ", host, port, name);
            if (authentication.authenticate(connector)) {
                log.info("Authenticated [Ok] {}:{} > {} ", host, port, name);
                GameEngineClient result = new GameEngineClient(new IOMethodInvoker(connector, serializer));
                executor.execute(result.getRunnable(new LocalMethodInvoker(player)));
                return result;
            } else {
                log.info("Authentication Error {}:{} > {} ", host, port, name);
                connector.close();
                throw new GameException("Autentication error");
            }
        } catch (IOException e) {
            throw new IOGameException("Error in start", e);
        }
    }

    private Runnable getRunnable(MethodInvoker target) {
        return () -> {
            while (!exit) {
                exit = !methodInvoker.readCommand(target);
            }
        };
    }

    @Override
    public void close() {
        methodInvoker.close();
    }

    public boolean isEnded() {
        return exit;
    }
}
