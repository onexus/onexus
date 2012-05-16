package org.onexus.collection.store.sql.filters;

import org.apache.commons.lang3.StringUtils;
import org.onexus.collection.store.sql.SqlCollectionStore;
import org.onexus.collection.store.sql.SqlDialect;
import org.onexus.collection.store.sql.adapters.SqlAdapter;
import org.onexus.core.IResourceManager;
import org.onexus.core.query.Filter;
import org.onexus.core.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFilterBuilder<T extends Filter> implements FilterBuilder {

    private static final Logger log = LoggerFactory.getLogger(AbstractFilterBuilder.class);
    private Class<T> filterClass;

    private SqlDialect dialect;

    public AbstractFilterBuilder(SqlDialect dialect, Class<T> filterClass) {
        this.filterClass = filterClass;
        this.dialect = dialect;
    }

    protected SqlDialect getDialect() {
        return this.dialect;
    }

    @Override
    public boolean canBuild(Filter filter) {

        if (filter == null) {
            return false;
        }

        return filterClass.isAssignableFrom(filter.getClass());
    }

    @Override
    public void build(IResourceManager resourceManager, Query query, StringBuilder where, Filter filter) {
        innerBuild(resourceManager, query, where, (T) filter);
    }

    protected void encodeValue(StringBuilder oql, Class<?> type, Object value) {

        SqlAdapter adapter = dialect.getAdapter(type);

        try {
            adapter.append(oql, value);
        } catch (Exception e) {
            log.error("Error encoding value", e);
        }

    }

    protected abstract void innerBuild(IResourceManager resourceManager, Query query, StringBuilder where, T filter);

}
