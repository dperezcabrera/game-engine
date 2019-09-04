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
package com.github.dperezcabrera.ge.io;

import com.github.dperezcabrera.ge.GameException;
import com.github.dperezcabrera.ge.util.Utilities;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class JsonSerializer implements Serializer<MethodCall, byte[]> {

    private static final String PREFIX_CALL = " ";
    private static final String PREFIX_RESPONSE = "$";

    private static final String NULL_VALUE = "null";
    private static final char CLOSE_ARRAY = ']';
    private static final char OPEN_ARRAY = '[';
    private Map<String, Method> commandIndex = new HashMap<>();
    private Map<Method, String> methodIndex = new HashMap<>();

    public JsonSerializer(@NonNull Class<?> type) {
        for (Method m : type.getMethods()) {
            String methodName = Utilities.getCommandName(m);
            if (commandIndex.containsKey(methodName)) {
                throw new GameException("Command name '" + methodName + "' was already used in " + m.toGenericString() + "\n and " + commandIndex.get(methodName).toGenericString());
            }
            commandIndex.put(methodName, m);
            methodIndex.put(m, methodName);
        }
        commandIndex = Collections.unmodifiableMap(commandIndex);
        methodIndex = Collections.unmodifiableMap(methodIndex);
    }

    @Override
    public byte[] serialize(@NonNull MethodCall obj) {
        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        sb.append(OPEN_ARRAY);
        String prefix;
        if (obj.getParameters() != null) {
            prefix = PREFIX_CALL;
            sb.append(String.join(", ", Arrays.stream(obj.getParameters()).map(o -> o == null ? NULL_VALUE : gson.toJson(o)).toArray(String[]::new)));
        } else {
            prefix = PREFIX_RESPONSE;
            sb.append(gson.toJson(obj.getResponse()));
        }
        sb.append(CLOSE_ARRAY);
        sb.append("\n");
        return new GameEvent(prefix + methodIndex.get(obj.getMethod()), sb.toString()).toFrame();
    }

    @Override
    public MethodCall deserialize(@NonNull byte[] data) {
        Gson gson = new Gson();
        GameEvent ge = GameEvent.fromFrame(data);
        JsonElement[] elements = gson.fromJson(new String(ge.getPayload()), JsonElement[].class);
        boolean response = ge.getCommand().startsWith(PREFIX_RESPONSE);
        Method method;
        Object[] parameters = null;
        Object resultMethod = null;
        if (response) {
            method = commandIndex.get(ge.getCommand().substring(PREFIX_RESPONSE.length()));
            if (!NULL_VALUE.equals(elements[0].toString())) {
                resultMethod = gson.fromJson(elements[0].toString(), method.getGenericReturnType());
            }
        } else {
            method = commandIndex.get(ge.getCommand().substring(PREFIX_CALL.length()));
            if (elements.length > 0) {
                parameters = new Object[elements.length];
                for (int i = 0; i < parameters.length; i++) {
                    JsonElement element = elements[i];
                    if (!NULL_VALUE.equals(element.toString())) {
                        parameters[i] = gson.fromJson(element.toString(), method.getGenericParameterTypes()[i]);
                    }
                }
            }
        }
        return new MethodCall(method, parameters, resultMethod);
    }
}
