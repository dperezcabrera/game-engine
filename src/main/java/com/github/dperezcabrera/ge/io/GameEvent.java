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
package com.github.dperezcabrera.ge.io;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class GameEvent {

    public static final GameEvent EXIT = new GameEvent("exit");
    private static final char SEPARATOR = ':';

    @NonNull
    private String command;
    
    @NonNull
    private byte[] payload;

    public GameEvent(@NonNull String command) {
        this.command = command;
        this.payload = new byte[]{SEPARATOR};
    }

    public GameEvent(@NonNull String command, @NonNull String message) {
        this.command = command;
        this.payload = message.getBytes();
    }

    public GameEvent(@NonNull byte[] frame) {
        int i = 0;
        while (i < frame.length && command == null) {
            if (frame[i] == SEPARATOR) {
                command = new String(frame, 0, i);
            }
            i++;
        }
        if (command == null) {
            throw new NullPointerException("Command can not be null");
        }
        payload = Arrays.copyOfRange(frame, i, frame.length);
    }

    public static GameEvent fromFrame(@NonNull byte[] frame) {
        return new GameEvent(frame);
    }

    public byte[] toFrame() {
        byte[] commandBytes = this.command.getBytes();
        int size = commandBytes.length + 1 + payload.length;
        byte[] result = new byte[size];
        System.arraycopy(commandBytes, 0, result, 0, commandBytes.length);
        result[commandBytes.length] = SEPARATOR;
        System.arraycopy(payload, 0, result, commandBytes.length + 1, payload.length);
        return result;
    }
}
