/**
 *  Copyright 2012 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.collection.api.utils;

import org.onexus.collection.api.query.And;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.Or;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.utils.ResourceUtils;

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
