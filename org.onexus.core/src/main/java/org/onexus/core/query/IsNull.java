package org.onexus.core.query;

public class IsNull extends Filter {

    private String collectionAlias;

    private String fieldId;

    public IsNull() {
        super();
    }

    public IsNull(String collectionAlias, String fieldId) {
        super();
        this.collectionAlias = collectionAlias;
        this.fieldId = fieldId;
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

    @Override
    public StringBuilder toString(StringBuilder oql, boolean prettyPrint) {

        oql.append(collectionAlias);
        oql.append('.');
        oql.append(fieldId);
        oql.append(" IS NULL");

        return oql;
    }
}
