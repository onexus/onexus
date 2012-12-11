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
package org.onexus.website.api.utils.parser;

import java.util.Iterator;

public class StringTokenizer implements Iterator<String> {

    private int position;
    private int length;
    private CharSequence expression;
    private String singleTokens;

    public StringTokenizer(CharSequence expression) {
        this(expression, "()");
    }

    public StringTokenizer(CharSequence expression, String singleTokens) {
        this.expression = expression;
        this.position = 0;
        this.length = expression.length();
        this.singleTokens = singleTokens;
    }

    public CharSequence getExpression() {
        return expression;
    }

    @Override
    public boolean hasNext() {
        return position < length;
    }

    @Override
    public String next() {
        StringBuilder token = new StringBuilder();

        while (position < length) {
            char c = expression.charAt(position);

            // Blank
            if (c == ' ') {
                position++;
                skipWhitespaces();

                if (token.length() > 0) {
                    return token.toString();
                } else {
                    continue;
                }
            }

            if (singleTokens.indexOf(c) != -1) {

                if (token.length() > 0) {
                    return token.toString();
                } else {
                    position++;
                    token.append(c);
                    return token.toString();
                }

            }

            position++;
            token.append(c);
        }

        return token.toString();
    }

    private void skipWhitespaces() {
        while (position < length && Character.isWhitespace(expression.charAt(position))) {
            position++;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Read only iterator");
    }
}
