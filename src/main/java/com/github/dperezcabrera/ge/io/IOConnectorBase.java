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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
@Slf4j
@RequiredArgsConstructor
public class IOConnectorBase implements IOConnector {

    @NonNull
    private InputStream inStream;
    
    @NonNull
    private OutputStream outStream;
    private boolean closed = false;

    @Override
    public void send(GameEvent ge) {
        write(ge.toFrame());
    }

    @Override
    public GameEvent receive() {
        return new GameEvent(read());
    }

    @Override
    public GameEvent receive(long timeout) {
        return new GameEvent(read(timeout));
    }

    private void write(byte[] frame) {
        if (!closed) {
            try {
                byte[] length = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(frame.length).array();
                outStream.write(length);
                outStream.write(frame);
                outStream.flush();
            } catch (IOException ex) {
                log.error("Error sending", ex);
                close();
            }
        }
    }
    
    private void readFully(byte[] buffer) throws IOException{
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) inStream.read();
        }
    }

    private byte[] readFrame() throws IOException {
        byte[] sizeBuffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(0).array();
        readFully(sizeBuffer);
        int size = ByteBuffer.wrap(sizeBuffer).getInt();
        byte[] result = new byte[size];
        readFully(result);
        return result;
    }

    private byte[] read() {
        byte[] result = null;
        try {
            if (!closed) {
                result = readFrame();
            }
        } catch (IOException ex) {
            log.error("Error receiving", ex);
            close();
        }
        return result;
    }

    private byte[] read(long timeout) {
        byte[] result = null;
        if (!closed) {
            final Object mutex = new Object();
            final byte[][] cache = {null};
            Thread t = new Thread(() -> {
                try {
                    cache[0] = readFrame();
                } catch (IOException ex) {
                    log.error("Error receiving " + timeout, ex);
                    close();
                } finally {
                    synchronized (mutex) {
                        mutex.notify();
                    }
                }
            });
            synchronized (mutex) {
                try {
                    t.start();
                    mutex.wait(timeout);
                    result = cache[0];
                } catch (InterruptedException ex) {
                    // interrupted
                    log.debug("Interrupted read " + timeout, ex);
                    Thread.currentThread().interrupt();
                }
            }
            if (t.isAlive()) {
                t.interrupt();
            }
        }
        return result;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        if (!closed) {
            try {
                closed = true;
                outStream.close();
                inStream.close();
            } catch (IOException ex) {
                log.error("Error: close", ex);
            }
        }
    }
}
