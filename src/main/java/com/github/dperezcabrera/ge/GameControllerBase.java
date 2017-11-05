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

import com.github.dperezcabrera.ge.impl.ExecutorMethodInvoker;
import com.github.dperezcabrera.ge.st.StateMachineDefinition;
import com.github.dperezcabrera.ge.util.Builder;
import com.github.dperezcabrera.ge.util.Utilities;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @param <P>
 * @param <E>
 * @param <M>
 */
@AllArgsConstructor
public class GameControllerBase<P, E extends Enum, M extends GameContext<P>> implements GameController<P> {

    private Class<P> typePlayer;
    private StateMachineDefinition<E, M> stateMachine;
    private Builder<M> contextFactory;
    private Properties properties;
    private ConnectorAdapterBuilder playerConnectorFactory;    
    
    private void addPlayer(@NonNull String playerName, @NonNull P player, List<ExecutorService> executors, Map<String, P> connectors, Map<Method, Long> timeouts) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        connectors.put(playerName, playerConnectorFactory.connector(typePlayer, new ExecutorMethodInvoker(player, executor, timeouts), timeouts));
        executors.add(executor);
    }

    @Override
    public synchronized Map<String, Double> play(Map<String, P> players) {
        Utilities.checkNullOrEmptyArgument(players, "players");
        Map<Method, Long> timeouts = Utilities.calculateTimeouts(this.typePlayer, this.properties);
        List<ExecutorService> executors = new ArrayList<>();
        try {
            Map<String, P> connectors = new HashMap<>();
            players.forEach((playerName, player) -> addPlayer(playerName, player, executors, connectors, timeouts));
            M context = contextFactory.build();
            context.setPlayersConnector(Collections.unmodifiableMap(connectors));
            context.setProperties(new Properties(properties));
            return stateMachine.startInstance(context).execute().getScores();
        } finally {
            executors.forEach(e -> e.shutdown());
        }
    }
}
