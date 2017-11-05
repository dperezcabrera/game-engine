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

import org.junit.Test;

import static com.github.dperezcabrera.ge.test.TestUtility.given;
import static com.github.dperezcabrera.ge.test.TestUtility.returnedObject;
import static com.github.dperezcabrera.ge.test.TestUtility.when;
import static org.assertj.core.api.BDDAssertions.then;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class GameEventTest {

    GameEvent instance;

    @Test
    public void testConstructorCommand() {
        given(() -> instance = new GameEvent("command"));

        when(() -> new GameEvent(instance.toFrame()));

        then(returnedObject()).isEqualTo(instance);
    }

    @Test
    public void testConstructorMessage() {
        given(() -> instance = new GameEvent("command", "01234567"));

        when(() -> GameEvent.fromFrame(instance.toFrame()));

        then(returnedObject()).isEqualTo(instance);
    }

    @Test
    public void testConstructorBytes() {
        given(() -> instance = new GameEvent("command", new byte[]{0, 1, 2, 3}));

        when(() -> new GameEvent(instance.toFrame()));

        then(returnedObject()).isEqualTo(instance);
    }
}
