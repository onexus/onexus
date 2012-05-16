package org.onexus.core.query;

public class GreaterThan extends AtomicFilter {

    public GreaterThan() {
    }

    public GreaterThan(String collectionAlias, String fieldId, Object value) {
        super(collectionAlias, fieldId, value);
    }

    @Override
    public String getOperandSymbol() {
        return ">";
    }
}
