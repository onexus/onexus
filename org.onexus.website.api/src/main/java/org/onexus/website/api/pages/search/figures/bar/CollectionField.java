package org.onexus.website.api.pages.search.figures.bar;

import org.onexus.resource.api.ORI;

import java.io.Serializable;

public class CollectionField implements Serializable {

    private ORI collection;

    private String field;

    public CollectionField() {
    }

    public ORI getCollection() {
        return collection;
    }

    public void setCollection(ORI collection) {
        this.collection = collection;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
