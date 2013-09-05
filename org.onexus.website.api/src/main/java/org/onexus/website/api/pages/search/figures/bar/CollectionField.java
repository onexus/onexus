package org.onexus.website.api.pages.search.figures.bar;

import java.io.Serializable;

public class CollectionField implements Serializable {

    private String collection;

    private String field;

    public CollectionField() {
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
