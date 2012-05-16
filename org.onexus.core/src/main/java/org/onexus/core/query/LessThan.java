package org.onexus.core.query;

public class LessThan extends AtomicFilter {

    public LessThan() {
    }

    public LessThan(String collectionAlias, String fieldId, Object value) {
        super(collectionAlias, fieldId, value);
    }

    @Override
    public String getOperandSymbol() {
        return "<";
    }
}
