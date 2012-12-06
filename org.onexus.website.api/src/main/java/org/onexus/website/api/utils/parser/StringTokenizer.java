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
