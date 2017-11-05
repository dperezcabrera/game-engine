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
package com.github.dperezcabrera.ge;

import com.github.dperezcabrera.ge.annotations.Timeout;
import com.github.dperezcabrera.ge.st.StateMachineDefinition;
import com.github.dperezcabrera.ge.st.StateMachineInstance;
import com.github.dperezcabrera.ge.util.Builder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Test;

import static com.github.dperezcabrera.ge.test.TestUtility.given;
import static com.github.dperezcabrera.ge.test.TestUtility.returnedObject;
import static com.github.dperezcabrera.ge.test.TestUtility.when;
import static com.googlecode.catchexception.apis.BDDCatchException.caughtException;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class GameControllerBaseTest {

    GameControllerBase instance;
    
    StateMachineDefinition<State, Model> stateMachineMock = mock(StateMachineDefinition.class);
    ConnectorAdapterBuilderBase connectorAdapterFactoryMock = mock(ConnectorAdapterBuilderBase.class);
    Properties propertiesMock = mock(Properties.class);
    StateMachineInstance<State, Model> stateMachineInstanceMock = mock(StateMachineInstance.class);
    Builder<Model> contextFactoryMock = mock(Builder.class);
    PlayerStrategy playerStrategyMock = mock(PlayerStrategy.class);
    Model modelMock = mock(Model.class);
    Map<String, Double> expectResultMock = mock(Map.class);

    @Test
    public void testConstructorError() {
        given(propertiesMock.getProperty("timeout.getRandom")).willReturn("..");
        given(propertiesMock.containsKey("timeout.getRandom")).willReturn(true);
        given(() -> instance = new GameControllerBase(PlayerStrategy.class, stateMachineMock, () -> new Model(), propertiesMock, connectorAdapterFactoryMock));
        
        when(() -> instance.play(map("a", playerStrategyMock)));

        then(caughtException())
                .isInstanceOf(GameException.class)
                .hasMessage("Property timeout error");
    }

    @Test
    public void testPlayNullNamePlayer() {
        given(propertiesMock.getProperty("timeout.getRandom")).willReturn("200");
        given(propertiesMock.containsKey("timeout.getRandom")).willReturn(true);
        given(contextFactoryMock.build()).willReturn(modelMock);
        given(stateMachineMock.startInstance(modelMock)).willReturn(stateMachineInstanceMock);
        given(stateMachineInstanceMock.execute()).willReturn(modelMock);
        given(modelMock.getScores()).willReturn(expectResultMock);
        given(() -> instance = new GameControllerBase(PlayerStrategy.class, stateMachineMock, contextFactoryMock, propertiesMock, connectorAdapterFactoryMock));

        when(() -> instance.play(map(null, playerStrategyMock)));

        then(caughtException())
                .isInstanceOf(NullPointerException.class);

    }

    @Test
    public void testPlayNullPlayers() {
        given(propertiesMock.getProperty("timeout.getRandom")).willReturn("200");
        given(propertiesMock.containsKey("timeout.getRandom")).willReturn(true);
        given(contextFactoryMock.build()).willReturn(modelMock);
        given(stateMachineMock.startInstance(modelMock)).willReturn(stateMachineInstanceMock);
        given(stateMachineInstanceMock.execute()).willReturn(modelMock);
        given(modelMock.getScores()).willReturn(expectResultMock);
        given(() -> instance = new GameControllerBase(PlayerStrategy.class, stateMachineMock, contextFactoryMock, propertiesMock, connectorAdapterFactoryMock));

        when(() -> instance.play(null));

        then(caughtException())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The argument players can not be null or empty.");
    }

    @Test
    public void testPlayEmptyPlayers() {
        given(propertiesMock.getProperty("timeout.getRandom")).willReturn("200");
        given(propertiesMock.containsKey("timeout.getRandom")).willReturn(true);
        given(contextFactoryMock.build()).willReturn(modelMock);
        given(stateMachineMock.startInstance(modelMock)).willReturn(stateMachineInstanceMock);
        given(stateMachineInstanceMock.execute()).willReturn(modelMock);
        given(modelMock.getScores()).willReturn(expectResultMock);
        given(() -> instance = new GameControllerBase(PlayerStrategy.class, stateMachineMock, contextFactoryMock, propertiesMock, connectorAdapterFactoryMock));

        when(() -> instance.play(new HashMap<>()));

        then(caughtException())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The argument players can not be null or empty.");
    }

    @Test
    public void testPlayNullPlayer() {
        given(propertiesMock.getProperty("timeout.getRandom")).willReturn("200");
        given(propertiesMock.containsKey("timeout.getRandom")).willReturn(true);
        given(contextFactoryMock.build()).willReturn(modelMock);
        given(stateMachineMock.startInstance(modelMock)).willReturn(stateMachineInstanceMock);
        given(stateMachineInstanceMock.execute()).willReturn(modelMock);
        given(modelMock.getScores()).willReturn(expectResultMock);
        given(() -> instance = new GameControllerBase(PlayerStrategy.class, stateMachineMock, contextFactoryMock, propertiesMock, connectorAdapterFactoryMock));

        when(() -> instance.play(map("player", null)));

        then(caughtException())
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testPlay() {
        given(propertiesMock.getProperty("timeout.getRandom")).willReturn("200");
        given(propertiesMock.containsKey("timeout.getRandom")).willReturn(true);
        given(contextFactoryMock.build()).willReturn(modelMock);
        given(stateMachineMock.startInstance(modelMock)).willReturn(stateMachineInstanceMock);
        given(stateMachineInstanceMock.execute()).willReturn(modelMock);
        given(modelMock.getScores()).willReturn(expectResultMock);
        given(() -> instance = new GameControllerBase(PlayerStrategy.class, stateMachineMock, contextFactoryMock, propertiesMock, connectorAdapterFactoryMock));

        when(() -> instance.play(map("player", playerStrategyMock)));

        then(returnedObject()).isEqualToComparingFieldByField(expectResultMock);
    }

    private static <K, V> Map<K, V> map(K key, V value) {
        Map<K, V> result = new HashMap<>();
        result.put(key, value);
        return result;
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
}
