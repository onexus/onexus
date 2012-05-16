package org.onexus.core.query;

public class LessThanOrEqual extends AtomicFilter {

    public LessThanOrEqual() {
    }

    public LessThanOrEqual(String collectionAlias, String fieldId, Object value) {
        super(collectionAlias, fieldId, value);
    }

    @Override
    public String getOperandSymbol() {
        return "<=";
    }
}
