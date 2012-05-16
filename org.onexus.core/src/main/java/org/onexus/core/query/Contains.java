package org.onexus.core.query;

public class Contains extends AtomicFilter {

    public Contains() {
    }

    public Contains(String collectionAlias, String fieldId, Object value) {
        super(collectionAlias, fieldId, value);
    }

    @Override
    public String getOperandSymbol() {
        return "CONTAINS";
    }
}
