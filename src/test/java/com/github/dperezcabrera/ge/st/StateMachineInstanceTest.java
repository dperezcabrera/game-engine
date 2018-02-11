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
package com.github.dperezcabrera.ge.st;

import com.github.dperezcabrera.ge.GameContext;
import com.github.dperezcabrera.ge.annotations.Timeout;
import java.util.Properties;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class StateMachineInstanceTest {

    StateMachineInstance instance;
    StateMachineDefinition<State, Model> stateMachine;

    Model modelMock = mock(Model.class);
    StateTrigger<Model> trigger0Mock = mock(StateTrigger.class);
    StateTrigger<Model> trigger1Mock = mock(StateTrigger.class);
    StateTrigger<Model> trigger2Mock = mock(StateTrigger.class);
    PlayerStrategy player0Mock = mock(PlayerStrategy.class);
    PlayerStrategy player1Mock = mock(PlayerStrategy.class);
    PlayerStrategy player2Mock = mock(PlayerStrategy.class);
    Properties propertiesMock = mock(Properties.class);

    @Test
    public void testIsFinish() {
        stateMachine = StateMachineDefinitionBuilder.<State, Model>create(State.A)
                .add(state(State.A).trigger(trigger0Mock).transition(State.B))
                .add(state(State.B).transition(State.D, c -> c.getScores() != null).transition(State.C))
                .add(state(State.C).trigger(trigger2Mock).transition(State.D))
                .build();

        given(propertiesMock.getProperty("timeout.getRandom")).willReturn("1000");
        given(propertiesMock.containsKey("timeout.getRandom")).willReturn(true);

        given(player0Mock.getRandom(0L, 0)).willReturn(0);
        given(player1Mock.getRandom(0L, 1)).willThrow(RuntimeException.class);
        given(player2Mock.getRandom(0L, 2)).willAnswer((m) -> {
            synchronized (m) {
                m.wait(500);
            }

            return 1;
        });

        instance = stateMachine.startInstance(modelMock);

        then(instance.isFinish()).isFalse();
    }

    public static StateMachineDefinitionBuilder.StateTriggerBuilder<State, Model> state(State state) {
        return StateMachineDefinitionBuilder.StateTriggerBuilder.<State, Model>state(state);
    }

    @Timeout("timeout.getRandom")
    public interface PlayerStrategy {

        public Integer getRandom(Long seed, Integer size);

    }

    public static class Model extends GameContext<PlayerStrategy> {
    }

    public enum State {
        A,
        B,
        C,
        D
    }

    /**
     * Test of execute method, of class StateMachineInstance.
     *
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testExecuteAlreadyRunning() throws InterruptedException {

        final Object mutex = new Object();
        trigger2Mock = c -> {
            try {
                synchronized (mutex) {
                    mutex.notify();
                }
                synchronized (c) {
                    c.wait();
                }
            } catch (InterruptedException ex) {
            }
        };
        StateMachineDefinition<State, Model> stateMachine = StateMachineDefinitionBuilder.<State, Model>create(State.A)
                .add(state(State.A).trigger(trigger0Mock).transition(State.B))
                .add(state(State.B).transition(State.C))
                .add(state(State.C).trigger(trigger2Mock).transition(State.D)).build();

        given(player0Mock.getRandom(0L, 0)).willReturn(0);
        given(player1Mock.getRandom(0L, 1)).willReturn(0);
        given(player2Mock.getRandom(0L, 2)).willReturn(0);

        StateMachineInstance instance = stateMachine.startInstance(modelMock);
        
        Executors.newFixedThreadPool(1).submit(() -> instance.execute());
        synchronized (mutex) {
            mutex.wait();
        }

        assertThrows(StateMachineException.class, () -> instance.execute(), "This instance is already running");
    }

    @Test
    public void testExecuteFinished() {

        StateMachineDefinition<State, Model> stateMachine = StateMachineDefinitionBuilder.<State, Model>create(State.A)
                .add(state(State.A).trigger(trigger0Mock).transition(State.B))
                .add(state(State.B).transition(State.D, c -> c.getScores() != null).transition(State.C))
                .add(state(State.C).trigger(trigger2Mock).transition(State.D)).build();

        given(player0Mock.getRandom(0L, 0)).willReturn(0);
        given(player1Mock.getRandom(0L, 1)).willReturn(1);
        given(player2Mock.getRandom(0L, 2)).willReturn(2);

        StateMachineInstance instance = stateMachine.startInstance(modelMock);
        instance.execute();

        assertThrows(StateMachineException.class, () -> instance.execute(), "This instance has been executed");
    }

    @Test
    public void testExecuteException() {
        StateMachineDefinition<State, Model> stateMachine = StateMachineDefinitionBuilder.<State, Model>create(State.A)
                .add(state(State.A).trigger(trigger0Mock).transition(State.B))
                .add(state(State.B).transition(State.D, c -> c.getScores() != null).transition(State.C))
                .add(state(State.C).trigger(trigger2Mock).transition(State.D)).build();

        willThrow(new RuntimeException("Unexpected Error")).given(trigger0Mock).execute(any(Model.class));

        given(player0Mock.getRandom(0L, 0)).willReturn(0);
        given(player1Mock.getRandom(0L, 1)).willReturn(1);
        given(player2Mock.getRandom(0L, 2)).willReturn(2);

        assertThrows(StateMachineException.class, () -> stateMachine.startInstance(modelMock).execute(), "There is an error");
    }

    /**
     * Test of getContext method, of class StateMachineInstance.
     */
    @Test
    public void testGetContext() {
        StateMachineDefinition<State, Model> stateMachineMock = mock(StateMachineDefinition.class);

        Model result = new StateMachineInstance<>(modelMock, stateMachineMock, State.A).getContext();

        then(result).isEqualTo(modelMock);
    }
}
