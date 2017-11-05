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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @param <S>
 * @param <T>
 */
public class StateMachineDefinitionBuilder<S extends Enum, T> {

    private static final Checker<?> DEFAULT_CHECKER = c -> true;

    private S initState = null;
    private Map<S, StateTrigger<T>> triggersByState;
    private Map<S, List<Transition<S, T>>> transitions;
    
    private StateMachineDefinitionBuilder(S initState) {
        this.initState = initState;
        init();
    }

    public static <E extends Enum, T> StateMachineDefinitionBuilder<E, T> create(E initState) {
        return new StateMachineDefinitionBuilder<>(initState);
    }

    private void init() {
        triggersByState = new HashMap<>();
        transitions = new HashMap<>();
    }

    public synchronized StateMachineDefinitionBuilder<S, T> add(StateTriggerBuilder<S, T> stb) {
        triggersByState.put(stb.state, stb.trigger);
        transitions.put(stb.state, stb.listTransitions);
        return this;
    }

    public synchronized StateMachineDefinition<S, T> build() {
        StateMachineDefinition<S, T> result = new StateMachineDefinition<>(initState, triggersByState, transitions);
        init();
        return result;
    }

    public static class StateTriggerBuilder<S extends Enum, T> {

        private S state;
        private StateTrigger<T> trigger;
        private List<Transition<S, T>> listTransitions = new ArrayList<>();

        private StateTriggerBuilder(S state) {
            this.state = state;
        }

        public static <S extends Enum, T> StateTriggerBuilder<S, T> state(S state) {
            return new StateTriggerBuilder<>(state);
        }

        public StateTriggerBuilder<S, T> trigger(StateTrigger<T> trigger) {
            this.trigger = trigger;
            return this;
        }

        public StateTriggerBuilder<S, T> transition(S target) {
            return addTransition(new Transition<>(target, (Checker<T>) DEFAULT_CHECKER));
        }

        public StateTriggerBuilder<S, T> transition(S target, Checker<T> checker) {
            return addTransition(new Transition<>(target, checker));
        }

        private synchronized StateTriggerBuilder<S, T> addTransition(Transition<S, T> transition) {
            listTransitions.add(transition);
            return this;
        }
    }
}
