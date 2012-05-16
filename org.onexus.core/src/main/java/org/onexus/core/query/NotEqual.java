package org.onexus.core.query;

public class NotEqual extends AtomicFilter {

    public NotEqual() {
    }

    public NotEqual(String collectionAlias, String fieldId, Object value) {
        super(collectionAlias, fieldId, value);
    }

    @Override
    public String getOperandSymbol() {
        return "!=";
    }
}
