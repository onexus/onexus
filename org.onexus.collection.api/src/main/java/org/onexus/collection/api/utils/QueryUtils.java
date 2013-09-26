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
import org.onexus.resource.api.ORI;

import java.util.Iterator;
import java.util.Map;

public final class QueryUtils {

    private QueryUtils() {
    }

    public static String newCollectionAlias(Query query, ORI collectionUri) {

        if (!collectionUri.isAbsolute()) {
            collectionUri = collectionUri.toAbsolute(query.getOn());
        }

        String collectionAlias = null;
        for (Map.Entry<String, ORI> define : query.getDefine().entrySet()) {

            ORI defineUri = define.getValue().toAbsolute(query.getOn());

            if (collectionUri.equals(defineUri)) {
                collectionAlias = define.getKey();
                break;
            }
        }

        if (collectionAlias == null) {
            int size = query.getDefine().size();

            while (getCollectionOri(query, "c" + size) != null) {
                size++;
            }

            collectionAlias = "c" + size;
            query.addDefine(collectionAlias, collectionUri);
        }


        return collectionAlias;

    }

    public static ORI getCollectionOri(Query query, String collectionAlias) {
        ORI collectionUri = query.getDefine().get(collectionAlias);
        return collectionUri == null ? null : collectionUri.toAbsolute(query.getOn());

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
        return filters == null ? null : joinAnd(filters.iterator());
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
        return filters == null ? null : joinOr(filters.iterator());
    }


}
