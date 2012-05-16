package org.onexus.core.query;

public class Equal extends AtomicFilter {

    public Equal() {
    }

    public Equal(String collectionAlias, String fieldId, Object value) {
        super(collectionAlias, fieldId, value);
    }

    @Override
    public String getOperandSymbol() {
        return "=";
    }
}
