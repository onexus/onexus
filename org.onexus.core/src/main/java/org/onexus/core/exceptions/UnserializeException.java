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
package org.onexus.core.exceptions;

public class UnserializeException extends RuntimeException {

    private String path;
    private String line;

    public UnserializeException(String path, String line, Throwable e) {
        super("At line " + line + " on path " + path, e);
        this.line = line;
        this.path = path;
    }

    public String getLine() {
        return line;
    }

    public String getPath() {
        return path;
    }
}
