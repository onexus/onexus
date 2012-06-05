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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderBy orderBy = (OrderBy) o;

        if (ascendent != orderBy.ascendent) return false;
        if (collectionRef != null ? !collectionRef.equals(orderBy.collectionRef) : orderBy.collectionRef != null)
            return false;
        if (fieldId != null ? !fieldId.equals(orderBy.fieldId) : orderBy.fieldId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = collectionRef != null ? collectionRef.hashCode() : 0;
        result = 31 * result + (fieldId != null ? fieldId.hashCode() : 0);
        result = 31 * result + (ascendent ? 1 : 0);
        return result;
    }
}
