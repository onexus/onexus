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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;

public class BooleanExpressionEvaluator {

    private static final Logger LOGGER = LoggerFactory.getLogger(BooleanExpressionEvaluator.class);

    private Collection<String> trueValues;
    private StringTokenizer tokenizer;

    public BooleanExpressionEvaluator(String expression, String... trueValues) {
        this(expression, Arrays.asList(trueValues));
    }

    public BooleanExpressionEvaluator(String expression, Collection<String> trueValues) {
        this.trueValues = trueValues;
        this.tokenizer = new StringTokenizer(expression, getSingleOperands());
    }

    public boolean evaluate() {

        boolean value = internalEvaluate();

        if (tokenizer.hasNext()) {
            String msg = "Malformed logical expression: '" + tokenizer.getExpression() + "'";
            LOGGER.warn(msg);
        }

        return value;
    }

    private boolean internalEvaluate() {
        boolean value = true;
        boolean rightValue = true;

        boolean not = false;
        boolean andOperation = true;

        while (tokenizer.hasNext()) {
            String token = tokenizer.next();

            if (isCloseOperand(token)) {
                if (andOperation) {
                    return value && rightValue;
                } else {
                    return value || rightValue;
                }
            }

            if (isNotOperand(token)) {
                not = !not;
                continue;
            }

            if (isAndOperand(token)) {
                andOperation = true;
                continue;
            }

            if (isOrOperand(token)) {
                andOperation = false;
                continue;
            }

            if (isOpenOperand(token)) {
                rightValue = internalEvaluate();
            } else {
                rightValue = evaluateToken(token);
            }

            if (not) {
                rightValue = !rightValue;
                not = false;
            }

            if (andOperation) {
                value = value && rightValue;
            } else {
                value = value || rightValue;
            }
        }

        return value;
    }

    protected boolean evaluateToken(String token) {
        return trueValues.contains(token);
    }


    protected boolean isOpenOperand(String token) {
        return "(".equals(token);
    }

    protected boolean isCloseOperand(String token) {
        return ")".equals(token);
    }

    protected boolean isNotOperand(String token) {
        return "!".equals(token) || "NOT".equalsIgnoreCase(token);
    }

    protected boolean isAndOperand(String token) {
        return "AND".equalsIgnoreCase(token) || ",".equals(token);
    }

    protected boolean isOrOperand(String token) {
        return "OR".equalsIgnoreCase(token) || "|".equals(token);
    }

    protected String getSingleOperands() {
        return "!(),|#";
    }


}
