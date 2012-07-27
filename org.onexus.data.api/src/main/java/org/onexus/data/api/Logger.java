/**
 *  Copyright 2012 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.data.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Logger implements Serializable {

    private int logLevel;
    private List<LogMessage> messages = new ArrayList<LogMessage>();

    public final static int TRACE = 0;
    public final static int DEBUG = 1;
    public final static int WARNING = 2;
    public final static int INFO = 3;
    public final static int ERROR = 4;

    public class LogMessage implements Serializable {

        private long time;
        private int level;
        private String message;

        public LogMessage(int level, String message) {
            this.time = System.currentTimeMillis();
            this.level = level;
            this.message = message;
        }

        public long getTime() {
            return time;
        }

        public int getLevel() {
            return level;
        }

        public String getMessage() {
            return message;
        }
    }

    public Logger() {
        this(WARNING);
    }

    public Logger(int logLevel) {
        this.logLevel = logLevel;
    }

    private void log(int level, String message) {
        if (logLevel <= level) {
            this.messages.add(new LogMessage(level, message));
        }
    }

    public void trace(String message) {
        log(TRACE, message);
    }

    public void debug(String message) {
        log(DEBUG, message);
    }

    public void warning(String message) {
        log(WARNING, message);
    }

    public void info(String message) {
        log(INFO, message);
    }

    public void error(String message) {
        log(ERROR, message);
    }

    public List<LogMessage> getMessages() {
        return messages;
    }
}
