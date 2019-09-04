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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @param <E>
 * @param <M>
 */
@Slf4j
public class StateMachineInstance<E extends Enum, M> {

    @Getter
    private final M context;
    private final StateMachineDefinition<E, M> parent;
    private E state;
    private volatile boolean finish;
    private volatile boolean running;

    public StateMachineInstance(M context, StateMachineDefinition<E, M> parent, E state) {
        this.context = context;
        this.parent = parent;
        this.state = state;
        this.finish = false;
    }

    public boolean isFinish() {
        return finish;
    }

    public M execute() {
        if (running) {
            throw new StateMachineException("This instance is already running");
        } else if (finish) {
            throw new StateMachineException("This instance has been executed");
        } else {
            running = true;
            try {
                while (state != null) {
                    log.debug("state \"{}\" executing...", state);
                    parent.getTrigger(state).execute(context);
                    log.debug("state \"{}\" [executed]", state);
                    state = nextState(state);
                }
                log.debug("execute finish");
                running = false;
                finish = true;
            } catch (Exception e) {
                running = false;
                throw new StateMachineException("There is an error", e);
            }
        }
        return context;
    }

    private E nextState(E state) {
        for (Transition<E, M> transition : parent.getTransitionsByOrigin(state)) {
            if (transition.getChecker().check(context)) {
                return transition.getStateTarget();
            }
        }
        return null;
    }
}
