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

import com.github.dperezcabrera.ge.io.GameEvent;
import java.util.Map;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
@Slf4j
public enum AuthenticationLoginPassword {
    ;// without instances
        
    private static final String ACK_COMMAND = "Ack";
    private static final String AUTH_COMMAND = "Auth";
    private static final String ERROR_COMMAND = "Error";

    public static AuthenticationServer getAuthenticationServer(Map<String, String> credentials) {
        return (connector, timeout) -> {
            try {
                GameEvent e = connector.receive(timeout);
                if (e != null) {
                    String[] currentCredentials = new String(e.getPayload()).split("\n");
                    if (currentCredentials != null && currentCredentials.length == 2) {
                        String login = currentCredentials[0].substring("login=".length());
                        String password = currentCredentials[1].substring("password=".length());
                        if (password.equals(credentials.get(login))) {
                            connector.send(new GameEvent(ACK_COMMAND));
                            return login;
                        } else {
                            connector.send(new GameEvent(ERROR_COMMAND, "Authentication Error\nincorrect login or password"));
                        }
                    }
                } else {
                    connector.send(new GameEvent(ERROR_COMMAND, "Timeout authentication Error"));
                }
            } catch (Exception ex) {
                log.error("Login error", ex);
                connector.send(new GameEvent(ERROR_COMMAND, "Authentication Error"));
            }
            return null;
        };
    }

    public static AuthenticationClient getAuthenticationClient(@NonNull final String login, @NonNull final String password) {
        return connector -> {
            boolean result = false;
            connector.send(new GameEvent(AUTH_COMMAND, "login=" + login + "\npassword=" + password));
            GameEvent e = connector.receive();
            if (ACK_COMMAND.equals(e.getCommand())) {
                result = true;
            }
            return result;
        };
    }
}
