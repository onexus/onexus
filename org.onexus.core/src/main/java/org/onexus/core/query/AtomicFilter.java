package org.onexus.core.query;

public abstract class AtomicFilter extends Filter {

    private String collectionAlias;

    private String fieldId;

    private Object value;

    public AtomicFilter() {
        super();
    }

    public AtomicFilter(String collectionAlias, String fieldId, Object value) {
        this.collectionAlias = collectionAlias;
        this.fieldId = fieldId;
        this.value = value;
    }

    public String getCollectionAlias() {
        return collectionAlias;
    }

    public void setCollectionAlias(String collectionAlias) {
        this.collectionAlias = collectionAlias;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public abstract String getOperandSymbol();

    @Override
    public StringBuilder toString(StringBuilder oql, boolean prettyPrint) {

        oql.append(collectionAlias);
        oql.append('.');
        oql.append(fieldId);
        oql.append(' ').append( getOperandSymbol() ).append(' ');
        oql.append(Filter.convertToOQL(value));

        return oql;
    }
}
