package org.onexus.core.query;

public class GreaterThanOrEqual extends AtomicFilter {

    public GreaterThanOrEqual() {
    }

    public GreaterThanOrEqual(String collectionAlias, String fieldId, Object value) {
        super(collectionAlias, fieldId, value);
    }

    @Override
    public String getOperandSymbol() {
        return ">=";
    }
}
