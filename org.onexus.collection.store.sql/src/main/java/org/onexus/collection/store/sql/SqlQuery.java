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
package org.onexus.collection.store.sql;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Link;
import org.onexus.collection.api.query.*;
import org.onexus.collection.api.utils.FieldLink;
import org.onexus.collection.api.utils.LinkUtils;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.collection.store.sql.adapters.SqlAdapter;
import org.onexus.collection.store.sql.filters.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SqlQuery {

    private static final Logger log = LoggerFactory.getLogger(SqlQuery.class);
    private List<String> select = new ArrayList<String>();
    private String from;
    private String where;
    protected List<String> orderBy = new ArrayList<String>();
    private String limit;

    private List<String> leftJoins = new ArrayList<String>();

    private StringBuilder sqlCount;
    private StringBuilder sqlSelect;

    protected Query query;
    private SqlCollectionStore manager;

    public SqlQuery(SqlCollectionStore manager, Query query) {
        super();

        this.manager = manager;
        this.query = query;

        addSelect();
        addFrom();
        addJoins();
        addWhere();
        addOrderBy();
        addLimit();

    }

    protected void addSelect() {

        for (Map.Entry<String, List<String>> selectCollection : query.getSelect().entrySet()) {

            String collectionAlias = selectCollection.getKey();
            String collectionUri = QueryUtils.getCollectionUri(query, collectionAlias);
            SqlCollectionDDL collectionDDL = manager.getDDL(collectionUri);

            List<String> fields = selectCollection.getValue();
            if (fields == null) {
                fields = new ArrayList<String>();
                for (SqlCollectionDDL.ColumnInfo column : collectionDDL.getColumnInfos()) {
                    fields.add(column.getField().getId());
                }
            }
            for (String fieldId : fields) {
                String fieldName = collectionDDL.getColumnInfoByFieldName(fieldId).getColumnName();
                select.add("`" + collectionAlias + "`.`" + fieldName + "` " + collectionAlias + "_" + fieldName);
            }


        }

    }

    protected void addFrom() {

        String collectionAlias = query.getFrom();
        String collectionUri = QueryUtils.getCollectionUri(query, collectionAlias);
        String collectionTable = manager.getDDL(collectionUri).getTableName();

        this.from = "`" + collectionTable + "` AS " + collectionAlias;
    }

    protected void addJoins() {

        Collection fromCollection = manager.getCollection(QueryUtils.getCollectionUri(query, query.getFrom()));

        // Fixed entities
        List<EqualId> equalIds = new ArrayList<EqualId>();
        addEqualIdFilters(equalIds, query.getWhere());

        Map<String, List<String>> networkFixedCollection = new HashMap<String, List<String>>();
        Map<String, StringBuilder> networkFixedJoins = new HashMap<String, StringBuilder>();
        Map<String, List<FieldLink>> networkLinks = new HashMap<String, List<FieldLink>>();

        for (Map.Entry<String, String> define : query.getDefine().entrySet()) {

            String collectionAlias = define.getKey();
            String collectionUri = QueryUtils.getAbsoluteCollectionUri(query, define.getValue());

            // Skip from collection
            if (collectionUri.equals(fromCollection.getURI())) {
                continue;
            }

            String collectionTable = manager.getDDL(collectionUri).getTableName();

            Collection joinCollection = manager.getCollection(collectionUri);

            StringBuilder leftJoin = new StringBuilder(50);
            leftJoin.append(" LEFT JOIN `");
            leftJoin.append(collectionTable).append("` AS `").append(collectionAlias).append("` ON ");

            // Fixed entities
            List<String> collectionsFixed = new ArrayList<String>();
            for (EqualId se : equalIds) {
                if (joinCollection.getLinks() != null) {
                    for (Link colLink : joinCollection.getLinks()) {

                        String fixedCollection = QueryUtils.getCollectionUri(query, se.getCollectionAlias());
                        String linkCollection = QueryUtils.getAbsoluteCollectionUri(query, colLink.getCollection());

                        if (fixedCollection.equals(linkCollection)) {
                            collectionsFixed.add(fixedCollection);

                            List<String> fields = colLink.getFields();

                            Object id = se.getId();
                            //TODO manage composed keys
                            Object[] ids = new Object[]{id};

                            if (fields.size() != ids.length) {
                                throw new RuntimeException("Bad entity id: " + se);
                            }
                            for (int i = 0; i < ids.length; i++) {

                                leftJoin.append("`").append(collectionAlias).append("`.`")
                                        .append(Link.getToFieldName(fields.get(i)))
                                        .append("` = ");

                                SqlAdapter adapter = manager.getSqlDialect().getAdapter(joinCollection.getField(fields.get(i)).getType());
                                try {
                                    adapter.append(leftJoin, ids[i]);
                                } catch (Exception e) {
                                    //TODO
                                    log.error("Appending join value " + ids[i], e);
                                }

                                leftJoin.append(
                                        " AND ");
                            }
                        }
                    }
                }
            }

            // Add links
            String parentURI = query.getOn();
            List<FieldLink> linkFields = LinkUtils.getLinkFields(parentURI, joinCollection, fromCollection);

            // Add thirdParty joins
            if (linkFields.isEmpty()) {

                // Check if it's possible to link with another joined collection

                for (Map.Entry<String, String> tpDefine : query.getDefine().entrySet()) {

                    // Skip current joined collection
                    if (tpDefine.getKey().equals(define.getKey())) {
                        continue;
                    }

                    String tpCollectionUri = QueryUtils.getAbsoluteCollectionUri(query, tpDefine.getValue());

                    // Skip from collection
                    if (tpCollectionUri.equals(fromCollection.getURI())) {
                        continue;
                    }

                    Collection tpJoinCollection = manager.getCollection(tpCollectionUri);

                    linkFields.addAll(LinkUtils.getLinkFields(parentURI, joinCollection, tpJoinCollection));
                }
            }

            // Check if it's possible to join this collection
            if (linkFields.isEmpty()) {
                throw new UnsupportedOperationException("Impossible to link collection '" + collectionUri + "'.");
            }

            networkFixedJoins.put(collectionUri, leftJoin);
            networkFixedCollection.put(collectionUri, collectionsFixed);
            networkLinks.put(collectionUri, linkFields);


        }

        // Sort the network to include JOINS in the correct order

        List<String> keys = new ArrayList<String>(networkLinks.keySet());
        keys = sort(keys, new LinksNetworkComparator(networkLinks, fromCollection.getURI()));

        //TODO remove loops

        for (String collectionUri : keys) {

            StringBuilder leftJoin = networkFixedJoins.get(collectionUri);
            List<String> collectionsFixed = networkFixedCollection.get(collectionUri);

            for (FieldLink fieldLink : networkLinks.get(collectionUri)) {

                String linkCollection = QueryUtils.getAbsoluteCollectionUri(query, fieldLink.getToCollection());
                if (!collectionsFixed.contains(linkCollection)) {

                    String aCollection = fieldLink.getFromCollection();
                    SqlCollectionDDL aDDL = manager.getDDL(aCollection);
                    String aAlias = QueryUtils.getCollectionAlias(query, aCollection);
                    String aField = aDDL.getColumnInfoByFieldName(
                            fieldLink.getFromFieldName()).getColumnName();
                    leftJoin.append("`").append(aAlias).append("`.`")
                            .append(aField).append("` = `");

                    String bCollection = fieldLink.getToCollection();
                    SqlCollectionDDL bDDL = manager.getDDL(bCollection);
                    String bAlias = QueryUtils.getCollectionAlias(query, bCollection);
                    String bField = bDDL.getColumnInfoByFieldName(
                            fieldLink.getToFieldName()).getColumnName();
                    leftJoin.append(bAlias).append("`.`").append(bField)
                            .append("` AND ");
                }

            }

            // Remove last ' AND '
            leftJoin.delete(leftJoin.length() - 5, leftJoin.length() - 1);
            this.leftJoins.add(leftJoin.toString());

        }

    }

    private static List<String> sort(List<String> keys, LinksNetworkComparator comparator) {

        List<String> sortedKeys = new ArrayList<String>(keys);

        int l = keys.size();
        boolean swaped = true;
        while (swaped) {
            swaped = false;
            for (int i = 0; i < l - 1; i++) {
                for (int f = i + 1; f < l; f++) {
                    int value = comparator.compare(sortedKeys.get(i), sortedKeys.get(f));
                    if (value > 0) {
                        String vI = sortedKeys.get(f);
                        String vF = sortedKeys.get(i);
                        sortedKeys.set(i, vI);
                        sortedKeys.set(f, vF);
                        swaped = true;
                    }
                }
            }
        }

        return sortedKeys;
    }

    protected void addEqualIdFilters(List<EqualId> equalIds, Filter filter) {

        if (filter instanceof EqualId) {
            equalIds.add((EqualId) filter);
            return;
        }

        if (filter instanceof And) {
            addEqualIdFilters(equalIds, ((And) filter).getLeft());
            addEqualIdFilters(equalIds, ((And) filter).getRight());
        }
    }


    protected void addWhere() {

        Filter filter = query.getWhere();

        if (filter != null) {
            FilterBuilder builder = manager.getSqlDialect().getFilterBuilder(filter);

            StringBuilder where = new StringBuilder();
            builder.build(manager, query, where, filter);
            this.where = where.toString();
        }

    }


    protected void addOrderBy() {

        List<OrderBy> ordersOql = query.getOrders();

        if (ordersOql == null || ordersOql.isEmpty()) {
            return;
        }

        Iterator<OrderBy> orderIt = ordersOql.iterator();
        while (orderIt.hasNext()) {
            OrderBy order = orderIt.next();
            String field = "`" + order.getCollectionRef() + "`.`" + order.getFieldId() + "`";
            if (order.isAscendent()) {
                this.orderBy.add("ISNULL(" + field + ") ASC");
            }
            this.orderBy.add(field + (order.isAscendent() ? " ASC" : "DESC"));
        }

    }

    protected void addLimit() {

        if (query.getCount() != null) {
            Long offset = query.getOffset();
            this.limit = (offset == null ? "0" : offset) + ", " + query.getCount();
        }
    }

    protected String toSelectSQL() {
        if (sqlSelect == null) {
            sqlSelect = new StringBuilder();

            sqlSelect.append("SELECT ");

            if (select != null && !select.isEmpty()) {
                flatCollection(sqlSelect, select, ",");
            } else {
                sqlSelect.append("*");
            }

            sqlSelect.append(" FROM ").append(from);

            if (!leftJoins.isEmpty()) {
                flatCollection(sqlSelect, leftJoins, " ");
            }

            if (where != null) {
                sqlSelect.append(" WHERE ").append(where);
            }

            if (orderBy != null && !orderBy.isEmpty()) {
                sqlSelect.append(" ORDER BY ");
                flatCollection(sqlSelect, this.orderBy, ",");
            }

            if (limit != null) {
                sqlSelect.append(" LIMIT ").append(limit);
            }
        }

        return sqlSelect.toString();
    }

    protected String toCountSQL() {
        if (sqlCount == null) {
            sqlCount = new StringBuilder();
            sqlCount.append("SELECT COUNT(*) AS `size` FROM ").append(from);

            if (!leftJoins.isEmpty()) {
                flatCollection(sqlCount, leftJoins, " ");
            }

            if (where != null) {
                sqlCount.append(" WHERE ").append(where);
            }

        }

        return sqlCount.toString();
    }

    private static void flatCollection(StringBuilder builder, java.util.Collection<?> collection, String separator) {
        Iterator<?> it = collection.iterator();
        while (it.hasNext()) {
            builder.append(it.next());
            if (it.hasNext()) {
                builder.append(String.valueOf(separator));
            }
        }
    }


}
