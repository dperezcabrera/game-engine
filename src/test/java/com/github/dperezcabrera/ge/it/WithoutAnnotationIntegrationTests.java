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
package com.github.dperezcabrera.ge.it;

import com.github.dperezcabrera.ge.ConnectorAdapterBuilderBase;
import com.github.dperezcabrera.ge.GameContext;
import com.github.dperezcabrera.ge.GameController;
import com.github.dperezcabrera.ge.GameControllerBase;
import com.github.dperezcabrera.ge.st.StateMachineDefinition;
import com.github.dperezcabrera.ge.st.StateMachineDefinitionBuilder;
import com.github.dperezcabrera.ge.st.StateMachineDefinitionBuilder.StateTriggerBuilder;
import com.github.dperezcabrera.ge.st.StateTrigger;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class WithoutAnnotationIntegrationTests {

    StateMachineDefinition<State, Model> stateMachine;

    StateTrigger<Model> trigger0 = null;
    StateTrigger<Model> trigger1Mock = mock(StateTrigger.class);
    StateTrigger<Model> trigger2Mock = mock(StateTrigger.class);

    PlayerStrategy player0Mock = mock(PlayerStrategy.class);
    PlayerStrategy player1Mock = mock(PlayerStrategy.class);
    PlayerStrategy player2Mock = mock(PlayerStrategy.class);
    Properties propertiesMock = mock(Properties.class);

    @Test
    public synchronized void testMain() throws InterruptedException {

        trigger0 = c -> c.getPlayersConnector()
                .forEach((n, p) -> assertEquals(null, p.getRandom(0L, Integer.valueOf(n))));
        given(propertiesMock.getProperty("timeout.getRandom")).willReturn("500");
        given(propertiesMock.containsKey("timeout.getRandom")).willReturn(true);
        given(player0Mock.getRandom(0L, 0)).willReturn(1);
        given(player2Mock.getRandom(0L, 2)).willThrow(RuntimeException.class);
        given(player1Mock.getRandom(0L, 1)).willAnswer((m) -> {
            synchronized (m) {
                m.wait(600);
            }
            return 2;
        });
        stateMachine = StateMachineDefinitionBuilder.<State, Model> create(State.A)
                .add(state(State.A).trigger(trigger0).transition(State.B))
                .add(state(State.B).transition(State.D, c -> c.getScores() != null).transition(State.C))
                .add(state(State.C).trigger(trigger2Mock).transition(State.D)).add(state(State.D).trigger(trigger1Mock))
                .build();

        // when
        GameController gc = new GameControllerBase(PlayerStrategy.class, stateMachine, () -> new Model(),
                propertiesMock, new ConnectorAdapterBuilderBase());
        Map<String, PlayerStrategy> players = new HashMap<>(3);
        players.put("0", player0Mock);
        players.put("1", player1Mock);
        players.put("2", player2Mock);
        gc.play(players);
        wait(700);

        then(player0Mock).should(times(1)).getRandom(0L, 0);
        then(player1Mock).should(times(1)).getRandom(0L, 1);
        then(player2Mock).should(times(1)).getRandom(0L, 2);
    }

    public static StateMachineDefinitionBuilder.StateTriggerBuilder<State, Model> state(State state) {
        return StateTriggerBuilder.<State, Model> state(state);

    }

    public interface PlayerStrategy {

        public Integer getRandom(Long seed, Integer size);

    }

    public static class Model extends GameContext<PlayerStrategy> {
    }

    public enum State {
        A, B, C, D
    }
}
