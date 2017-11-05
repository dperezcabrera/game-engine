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
package com.github.dperezcabrera.ge.remote;

import com.github.dperezcabrera.ge.ConnectorAdapterBuilder;
import com.github.dperezcabrera.ge.io.GameEvent;
import com.github.dperezcabrera.ge.io.IOConnectorBase;
import com.github.dperezcabrera.ge.io.IOMethodInvoker;
import com.github.dperezcabrera.ge.io.MethodCall;
import com.github.dperezcabrera.ge.io.Serializer;
import com.github.dperezcabrera.ge.util.Utilities;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
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
public class GameEngineServer implements AutoCloseable {

    public static final String EXIT_COMMAND = "exit";

    private ConnectorAdapterBuilder adapterFactory;
    private Serializer<MethodCall, byte[]> serializer;
    private List<IOConnectorBase> connectors = new ArrayList<>();

    public GameEngineServer(ConnectorAdapterBuilder adapterFactory, Serializer<MethodCall, byte[]> serializer) {
        this.adapterFactory = adapterFactory;
        this.serializer = serializer;
    }

    public synchronized <P> Map<String, P> getPlayers(Class<P> type, int port, long connectionTimeOut, long authenticacionTimeOut, int players, AuthenticationServer autentication, Properties properties) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return getPlayers(type, serverSocket, connectionTimeOut, authenticacionTimeOut, players, autentication, properties);
        }
    }

    public synchronized <P> Map<String, P> getPlayers(Class<P> type, ServerSocket serverSocket, long connectionTimeOut, long authenticacionTimeOut, int players, AuthenticationServer autentication, Properties properties) throws IOException {
        Map<Method, Long> timeouts = Utilities.calculateTimeouts(type, properties);
        Map<String, P> result = new HashMap<>(players);
        try {
            long maxTime = System.currentTimeMillis() + connectionTimeOut;
            while (result.size() < players) {
                serverSocket.setSoTimeout((int) (maxTime - System.currentTimeMillis()));
                log.info("Waiting players({}), timeout: {} ms", players - result.size(), serverSocket.getSoTimeout());
                Socket socket = serverSocket.accept();
                IOConnectorBase connector = new IOConnectorBase(socket.getInputStream(), socket.getOutputStream());
                String name = autentication.login(connector, authenticacionTimeOut);
                if (name != null && !result.containsKey(name)) {
                    IOMethodInvoker mi = new IOMethodInvoker(connector, serializer);
                    P player = adapterFactory.connector(type, mi, timeouts);
                    connectors.add(connector);
                    result.put(name, player);
                } else {
                    connector.close();
                }
            }
            log.info("All players are ready");
        } catch (SocketTimeoutException e) {
            log.info("Time is end: connected players: {}", result.size());
        } catch (IOException ex) {
            log.error("Error: getPlayers", ex);
        }
        return result;
    }

    @Override
    public void close() {
        connectors.forEach(c -> {
            c.send(GameEvent.EXIT);
            c.close();
        });
    }
}
