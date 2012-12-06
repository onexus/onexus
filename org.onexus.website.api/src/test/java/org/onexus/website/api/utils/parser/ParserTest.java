package org.onexus.website.api.utils.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParserTest {

    @Test
    public void testStringTokenizer() {

        StringTokenizer tokenizer = new StringTokenizer("  c1 (c2     AND c3),c4 OR (c5,c6)    ,     c7   ", "(),");

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

    }

    private boolean evaluate(String expression, String... trueValues) {
        BooleanExpressionEvaluator evaluator = new BooleanExpressionEvaluator(expression, trueValues);
        return evaluator.evaluate();
    }
}
