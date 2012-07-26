package org.onexus.collection.manager.internal;

import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;
import org.onexus.data.api.Progress;

public class ProgressEntityTable implements IEntityTable {

    Progress progress;

    IEntityTable entityTable;

    public ProgressEntityTable(Progress progress, IEntityTable entityTable) {
        this.progress = progress;
        this.entityTable = entityTable;
    }


    @Override
    public Query getQuery() {
        return entityTable.getQuery();
    }

    @Override
    public IEntity getEntity(String collectionURI) {
        return entityTable.getEntity(collectionURI);
    }

    @Override
    public boolean next() {
        return entityTable.next();
    }

    @Override
    public void close() {
        entityTable.close();
    }

    @Override
    public long size() {
        return entityTable.size();
    }

    @Override
    public Progress getProgress() {
        return progress;
    }
}
