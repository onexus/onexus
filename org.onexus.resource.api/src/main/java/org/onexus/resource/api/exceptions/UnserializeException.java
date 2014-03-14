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
package org.onexus.resource.api.exceptions;

import java.util.Iterator;
import java.util.Set;

public class UnserializeException extends OnexusException {

    private String path;
    private String line;
    private Set<String> violations;

    public UnserializeException(Set<String> violations) {
        super("There are " + violations.size() + " syntax errors: " + join(violations));
        this.violations = violations;
    }

    public UnserializeException(String path, String line, Throwable e) {
        super("At line " + line + " on path " + path, e);
        this.line = line;
        this.path = path;
        this.violations = null;
    }

    public String getMessage(String resourcePath, String resourceContent) {

        if (violations == null) {
            return createMalformedXMLMessage(resourcePath, resourceContent, path, line);
        }

        StringBuilder msg = new StringBuilder();

        msg.append("Validating file " + resourcePath + " there are " + violations.size() + " syntax errors: \n");
        for (String violation : violations) {
            msg.append("\t - " + violation + "\n");
        }

        return msg.toString();
    }

    private static String createMalformedXMLMessage(String resourcePath, String resourceContent, String xmlPath, String xmlLine) {

        String msg = "Parsing file " + resourcePath + " at line " + xmlLine + " on " + xmlPath;

        if (resourceContent != null) {
            String[] lines = resourceContent.split(System.getProperty("line.separator"));

            int error = Integer.valueOf(xmlLine);
            int from = (error-1 > 0 ? error-1 : 0);
            int to = (error+1 < lines.length ? error+1 : lines.length);

            for (int i=from ; i <= to; i++) {
                msg = msg + "\n " + i + " >> " + lines[i];
            }
        }

        return msg;

    }

    private static String join(Iterable<String> values) {

        StringBuilder str = new StringBuilder();

        Iterator<String> it = values.iterator();
        while (it.hasNext()) {
            str.append(it.next()).append(it.hasNext() ? ", " : "");
        }

        return str.toString();
    }


}
