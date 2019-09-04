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
package com.github.dperezcabrera.ge.st;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @param <S>
 * @param <T>
 */
public class StateMachineDefinition<S extends Enum, T> {
    
    private static final StateTrigger DEFAULT_TRIGGER = c -> {/*Do nothing*/};
    
    private final S initState;
    private final Map<S, StateTrigger<T>> triggersByState;
    private final Map<S, List<Transition<S, T>>> transitions;

    StateMachineDefinition(S initState, Map<S, StateTrigger<T>> triggersByState, Map<S, List<Transition<S, T>>> transitions) {
        this.initState = initState;
        this.triggersByState = new HashMap<>(triggersByState);
        this.transitions = new HashMap<>(transitions.size());
        transitions.entrySet().stream().forEach(e -> this.transitions.put(e.getKey(), Collections.unmodifiableList(new ArrayList<>(e.getValue()))));
    }
    
    List<Transition<S, T>> getTransitionsByOrigin(S state) {
        List<Transition<S, T>> result = transitions.get(state);
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    private StateTrigger<T> getTriggerFrom(Map<S, StateTrigger<T>> triggers , S state) {
        StateTrigger<T> result = triggers.get(state);
        if (result == null){
            result = (StateTrigger<T>) DEFAULT_TRIGGER;
        }
        return result;
    }
    
    public StateTrigger<T> getTrigger(S state) {
        return getTriggerFrom(triggersByState, state);
    }
    
    public StateMachineInstance<S, T> startInstance(T data) {
        return new StateMachineInstance(data, this, initState);
    }
}
