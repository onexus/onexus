package org.onexus.core.query;

public class EqualId extends Filter {

    private String collectionAlias;

    private Object id;


    public EqualId() {
        super();
    }

    public EqualId(String collectionAlias, Object id) {
        this.collectionAlias = collectionAlias;
        this.id = id;
    }

    public String getCollectionAlias() {
        return collectionAlias;
    }

    public Object getId() {
        return id;
    }

    public void setCollectionAlias(String collectionAlias) {
        this.collectionAlias = collectionAlias;
    }

    public void setId(Object id) {
        this.id = id;
    }

    @Override
    public StringBuilder toString(StringBuilder oql, boolean prettyPrint) {

        oql.append(collectionAlias);
        oql.append(" = ");
        oql.append(Filter.convertToOQL(id));

        return oql;
    }
}
