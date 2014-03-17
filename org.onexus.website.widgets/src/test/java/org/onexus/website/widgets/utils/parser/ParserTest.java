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
package org.onexus.website.widgets.utils.parser;

import org.junit.Test;
import org.onexus.website.api.utils.parser.BooleanExpressionEvaluator;
import org.onexus.website.api.utils.parser.StringTokenizer;

import static org.junit.Assert.*;

public class ParserTest {

    @Test
    public void testStringTokenizer() {

        StringTokenizer tokenizer = new StringTokenizer("  c1 (c2    AND c3),c4 OR (c5,c6)    ,     c7   ", "(),");

        assertTrue(tokenizer.hasNext());
        assertEquals(tokenizer.next(), "c1");
        assertEquals(tokenizer.next(), "(");
        assertEquals(tokenizer.next(), "c2");
        assertEquals(tokenizer.next(), "AND");
        assertEquals(tokenizer.next(), "c3");
        assertEquals(tokenizer.next(), ")");
        assertEquals(tokenizer.next(), ",");
        assertEquals(tokenizer.next(), "c4");
        assertEquals(tokenizer.next(), "OR");
        assertEquals(tokenizer.next(), "(");
        assertEquals(tokenizer.next(), "c5");
        assertEquals(tokenizer.next(), ",");
        assertEquals(tokenizer.next(), "c6");
        assertEquals(tokenizer.next(), ")");
        assertEquals(tokenizer.next(), ",");
        assertEquals(tokenizer.next(), "c7");
        assertFalse(tokenizer.hasNext());

    }

    @Test
    public void testBooleanExpressionEvaluator() {

        assertTrue(evaluate("c1 AND c2", "c1", "c2"));
        assertTrue(evaluate("c1, c2", "c1", "c2"));
        assertFalse(evaluate("c1 AND (NOT c2)", "c1", "c2"));
        assertFalse(evaluate("c1, !c2", "c1", "c2"));
        assertTrue(evaluate("(c1 AND c2)", "c1", "c2"));

        assertTrue(evaluate("c1 OR c2", "c2"));
        assertFalse(evaluate("c1 OR NOT c2", "c2"));
        assertTrue(evaluate("(c1 OR c2)", "c2"));

        assertTrue(evaluate("c1 AND c2 OR c3", "c3"));
        assertTrue(evaluate("(c1 AND c2) OR c3", "c3"));
        assertFalse(evaluate("c1 AND (c2 OR c3)", "c3"));
        assertFalse(evaluate("c1 AND c2 OR NOT c3", "c3"));

        assertTrue(evaluate("((c1 AND c2) OR (c3 AND (!c5 OR c6)))", "c2", "c5", "c6", "c3"));

        assertTrue(evaluate("NOT !c1", "c1"));
        assertTrue(evaluate("!!!c1"));
        assertFalse(evaluate("!!!(!!!)!!!!"));
        assertTrue(evaluate("", "c2"));

        assertTrue(evaluate("NOT c1 OR c2"));
        assertTrue(evaluate("NOT c1 OR c2", "c2"));

        assertTrue(evaluate("(c2 OR !c1)"));
        assertTrue(evaluate("(c2 OR !c1)", "c2"));

    }

    private boolean evaluate(String expression, String... trueValues) {
        BooleanExpressionEvaluator evaluator = new BooleanExpressionEvaluator(expression, trueValues);
        return evaluator.evaluate();
    }
}
