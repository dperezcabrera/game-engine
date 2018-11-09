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
package com.github.dperezcabrera.ge.remote.it;

import com.github.dperezcabrera.ge.ConnectorAdapterBuilderBase;
import com.github.dperezcabrera.ge.GameContext;
import com.github.dperezcabrera.ge.GameController;
import com.github.dperezcabrera.ge.GameControllerBase;
import com.github.dperezcabrera.ge.annotations.Timeout;
import com.github.dperezcabrera.ge.io.JsonSerializer;
import com.github.dperezcabrera.ge.io.MethodCall;
import com.github.dperezcabrera.ge.io.Serializer;
import com.github.dperezcabrera.ge.remote.AuthenticationLoginPassword;
import com.github.dperezcabrera.ge.remote.GameEngineClient;
import com.github.dperezcabrera.ge.remote.GameEngineServer;
import com.github.dperezcabrera.ge.st.StateMachineDefinition;
import com.github.dperezcabrera.ge.st.StateMachineDefinitionBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
@Slf4j
public class RemoteIntegrationTests {

    private static final Serializer<MethodCall, byte[]> SERIALIZER = new JsonSerializer(PlayerStrategy.class);
    private static final int PORT = 3333;
    private static final int CONNECTION_TIMEOUT = 3000;
    private static final int AUTENTICATION_TIMEOUT = 1000;
    private static final int PLAYERS = 3;

    StateMachineDefinition<State, Model> stateMachine;
    Properties properties;

    private static StateMachineDefinitionBuilder.StateTriggerBuilder<State, Model> state(State state) {
        return StateMachineDefinitionBuilder.StateTriggerBuilder.<State, Model> state(state);
    }

    @BeforeEach
    public void prepareTest() {
        properties = new Properties();
        properties.setProperty("timeout.getRandom", "500");

        stateMachine = StateMachineDefinitionBuilder.<State, Model> create(State.BEGIN)
                .add(state(State.BEGIN).trigger(c -> {
                    int size = c.getPlayersConnector().size();
                    for (Entry<String, PlayerStrategy> e : c.getPlayersConnector().entrySet()) {
                        try {
                            c.commands.put(e.getKey(), e.getValue().getRandom(size));
                        } catch (Exception ex) {
                            log.info("El jugador '" + e.getKey() + "' esta descalificado", ex);
                            c.commands.put(e.getKey(), -1);
                        }
                    }
                }).transition(State.RESULT)).add(state(State.RESULT).trigger(c -> {
                    int counter = 0;
                    int players = 0;
                    Map<Integer, String> candidates = new HashMap<>();
                    Map<String, Double> scores = new HashMap<>();
                    for (Entry<String, PlayerStrategy> e : c.getPlayersConnector().entrySet()) {
                        int command = c.commands.get(e.getKey());
                        if (command >= 0) {
                            scores.put(e.getKey(), 0d);
                            counter += command;
                            candidates.put(players++, e.getKey());
                        } else {
                            scores.put(e.getKey(), -1d);
                        }
                    }
                    counter %= candidates.size();
                    String winner = candidates.get(counter);
                    scores.put(winner, 1.0);
                    c.setScores(scores);
                    for (PlayerStrategy p : c.getPlayersConnector().values()) {
                        p.sendResult(candidates, c.commands, winner);
                    }
                })).build();
    }

    @Test
    public void test() throws IOException {
        ExecutorService executors = Executors.newFixedThreadPool(PLAYERS);
        final Map<String, String> loginPassword = new HashMap<>();
        for (int i = 0; i < PLAYERS; i++) {
            final int index = i;
            final String login = "player-" + index;
            loginPassword.put(login, UUID.randomUUID().toString());
            execute(executors, 500L,
                    () -> GameEngineClient.start("127.0.0.1", PORT, new PlayerStrategyRandom((login)), login,
                            AuthenticationLoginPassword.getAuthenticationClient(login, loginPassword.get(login)),
                            SERIALIZER));
        }
        try (GameEngineServer server = new GameEngineServer(new ConnectorAdapterBuilderBase(), SERIALIZER)) {
            Map<String, PlayerStrategy> players = server.getPlayers(PlayerStrategy.class, PORT, CONNECTION_TIMEOUT,
                    AUTENTICATION_TIMEOUT, PLAYERS, AuthenticationLoginPassword.getAuthenticationServer(loginPassword),
                    properties);
            if (players.size() >= 1) {
                GameController<PlayerStrategy> gc = new GameControllerBase(PlayerStrategy.class, stateMachine,
                        () -> new Model(), properties, new ConnectorAdapterBuilderBase());
                Map<String, Double> scores = gc.play(players);
                log.info("scores: \n{}", scores);
            }
        }
    }

    private static void execute(Executor executor, long time, Runnable runnable) {
        executor.execute(() -> {
            Object mutex = new Object();
            synchronized (mutex) {
                try {
                    mutex.wait(time);
                } catch (InterruptedException ex) {
                    //
                }
            }
            runnable.run();
        });
    }

    @Timeout("timeout.getRandom")
    public interface PlayerStrategy {

        public Integer getRandom(Integer size);

        public void sendResult(Map<Integer, String> numbers, Map<String, Integer> commands, String winner);
    }

    public static class Model extends GameContext<PlayerStrategy> {

        private Map<String, Integer> commands = new HashMap<>();
    }

    public enum State {
        BEGIN, RESULT,
    }

    public static class PlayerStrategyRandom implements PlayerStrategy {

        private String name;

        public PlayerStrategyRandom(String name) {
            this.name = name;
        }

        @Override
        public Integer getRandom(Integer size) {
            return new Random().nextInt(size);
        }

        @Override
        public void sendResult(Map<Integer, String> numbers, Map<String, Integer> commands, String winner) {
            if (name.equals(winner)) {
                log.info("{}: I won!", name);
                numbers.forEach((n, p) -> log.info("{}: {}", p, n));
            }
        }
    }
}
