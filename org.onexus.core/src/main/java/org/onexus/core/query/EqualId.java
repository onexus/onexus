package org.onexus.core.query;

public class EqualId extends Filter {

    private String collectionAlias;

    private String id;


    public EqualId() {
        super();
    }

    public EqualId(String collectionAlias, String id) {
        this.collectionAlias = collectionAlias;
        this.id = id;
    }

    public String getCollectionAlias() {
        return collectionAlias;
    }

    public String getId() {
        return id;
    }

    public void setCollectionAlias(String collectionAlias) {
        this.collectionAlias = collectionAlias;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public StringBuilder toString(StringBuilder oql, boolean prettyPrint) {

        oql.append(collectionAlias);
        oql.append(" = ");
        oql.append(id);

        return oql;
    }
}
