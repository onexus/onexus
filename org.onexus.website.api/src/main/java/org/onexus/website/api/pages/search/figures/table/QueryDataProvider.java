package org.onexus.website.api.pages.search.figures.table;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;

import java.util.Iterator;

public class QueryDataProvider implements IDataProvider<IEntityTable> {

    private Query query;

    public QueryDataProvider(Query query) {
        this.query = query;
    }

    @Override
    public Iterator<? extends IEntityTable> iterator(long first, long count) {
        return null;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public IModel<IEntityTable> model(IEntityTable object) {
        return null;
    }

    @Override
    public void detach() {

    }
}
