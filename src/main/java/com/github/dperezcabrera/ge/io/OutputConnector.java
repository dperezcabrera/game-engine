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

import java.io.IOException;
import java.io.OutputStream;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
@Slf4j
public class OutputConnector implements IOConnector {

    private OutputStream outStream;
    private boolean closed = false;

    public OutputConnector(OutputStream outStream) {
        this.outStream = outStream;
    }

    @Override
    public void close() {
        if (!closed) {
            try {
                closed = true;
                outStream.close();
            } catch (IOException ex) {
                log.error("Error: close", ex);
            }
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void send(GameEvent ge) {
        if (!closed) {
            try {
                outStream.write(ge.getPayload());
                outStream.flush();
            } catch (IOException ex) {
                log.error("Error sending", ex);
                close();
            }
        }
    }
}
