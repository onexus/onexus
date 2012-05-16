package org.onexus.core.query;

import java.io.Serializable;

public class OrderBy implements Serializable {

    private String collectionRef;

    private String fieldId;

    private boolean ascendent = true;

    public OrderBy(String collectionRef, String fieldId) {
        this(collectionRef, fieldId, true);
    }

    public OrderBy(String collectionRef, String fieldId, boolean ascendent) {
        this.collectionRef = collectionRef;
        this.fieldId = fieldId;
        this.ascendent = ascendent;
    }

    public String getCollectionRef() {
        return collectionRef;
    }

    public void setCollectionRef(String collectionRef) {
        this.collectionRef = collectionRef;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public boolean isAscendent() {
        return ascendent;
    }

    public void setAscendent(boolean ascendent) {
        this.ascendent = ascendent;
    }

    public String toString() {
        return toString(new StringBuilder(), true).toString();
    }

    public StringBuilder toString(StringBuilder oql, boolean prettyPrint) {
        oql.append(collectionRef).append('.').append(fieldId);
        if (!ascendent) {
            oql.append(" DESC");
        }
        return oql;
    }
}
