package org.onexus.collection.api.utils;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntitySet;

import java.util.Collections;
import java.util.Iterator;

public class EmptyEntitySet implements IEntitySet {

    private Collection collection;


    @Override
    public boolean next() {
        return false;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public void close() {
    }

    @Override
    public IEntity detachedEntity() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Collection getCollection() {
        return collection;
    }

    @Override
    public Object get(String fieldId) {
        return null;
    }

    @Override
    public void put(String fieldName, Object value) {
    }

    @Override
    public Iterator<IEntity> iterator() {
        return Collections.EMPTY_LIST.iterator();
    }
}
