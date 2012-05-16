package org.onexus.core.utils;

import org.onexus.core.query.And;
import org.onexus.core.query.Filter;
import org.onexus.core.query.Or;
import org.onexus.core.query.Query;
import org.onexus.core.resources.Collection;

import java.util.Iterator;
import java.util.Map;

public class QueryUtils {

    public static String getAbsoluteCollectionUri(Query query, String collectionUri) {
        return ResourceUtils.getAbsoluteURI(query.getOn(), collectionUri);
    }

    public static String getCollectionUri(Query query, String collectionAlias) {
        String collectionUri = query.getDefine().get(collectionAlias);
        return (collectionUri == null ? null : getAbsoluteCollectionUri(query, collectionUri));

    }

    public static String getCollectionAlias(Query query, String collectionUri) {

        String sourceUri = getAbsoluteCollectionUri(query, collectionUri);

        if (sourceUri == null) {
            return null;
        }

        for (Map.Entry<String, String> define : query.getDefine().entrySet()) {

            String defineUri = getAbsoluteCollectionUri(query, define.getValue());

            if (sourceUri.equals(defineUri)) {
                return define.getKey();
            }

        }

        return null;
    }

    public static void and(Query query, Filter newFilter) {

        Filter currentWhere = query.getWhere();

        if (currentWhere != null) {
            query.setWhere(new And(currentWhere, newFilter));
        } else {
            query.setWhere(newFilter);
        }

    }

    public static Filter joinAnd(Iterator<Filter> filters) {

        if (filters == null || !filters.hasNext()) {
            return null;
        }

        Filter join = filters.next();

        while (filters.hasNext()) {
            join = new And(join, filters.next());
        }

        return join;

    }

    public static Filter joinAnd(Iterable<Filter> filters) {
        return (filters == null? null : joinAnd(filters.iterator()));
    }

    public static void or(Query query, Filter newFilter) {

        Filter currentWhere = query.getWhere();

        if (currentWhere != null) {
            query.setWhere(new Or(currentWhere, newFilter));
        } else {
            query.setWhere(newFilter);
        }

    }

    public static Filter joinOr(Iterator<Filter> filters) {

        if (filters == null || !filters.hasNext()) {
            return null;
        }

        Filter join = filters.next();

        while (filters.hasNext()) {
            join = new Or(join, filters.next());
        }

        return join;

    }

    public static Filter joinOr(Iterable<Filter> filters) {
        return (filters == null? null : joinOr(filters.iterator()));
    }

    public static String newCollectionAlias(Query query, String collectionUri) {

        String collectionAlias = getCollectionAlias(query, collectionUri);

        if (collectionAlias == null) {
            int size = query.getDefine().size();

            while ( getCollectionUri( query, "c" + size) != null) {
                size++;
            }

            collectionAlias = "c" + size;
            query.addDefine(collectionAlias, ResourceUtils.getRelativeURI(query.getOn(), collectionUri));
        }

        return collectionAlias;

    }

}
