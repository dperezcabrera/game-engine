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

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class GameEventTest {

    GameEvent instance;

    @Test
    public void testConstructorCommand() {
        instance = new GameEvent("command");

        GameEvent result = new GameEvent(instance.toFrame());

        assertEquals(instance, result);
    }

    @Test
    public void testConstructorMessage() {
        instance = new GameEvent("command", "01234567");

        GameEvent result = GameEvent.fromFrame(instance.toFrame());

        assertEquals(instance, result);
    }

    @Test
    public void testConstructorBytes() {
        instance = new GameEvent("command", new byte[]{0, 1, 2, 3});

        GameEvent result = new GameEvent(instance.toFrame());

        assertEquals(instance, result);
    }
}
