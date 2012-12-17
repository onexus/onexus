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
import org.onexus.collection.api.query.And;
import org.onexus.collection.api.query.EqualId;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.OrderBy;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.FieldLink;
import org.onexus.collection.api.utils.LinkUtils;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.collection.store.sql.adapters.SqlAdapter;
import org.onexus.collection.store.sql.filters.FilterBuilder;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.exceptions.OnexusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

        try {
            addSelect();
            addFrom();
            addJoins();
            addWhere();
            addOrderBy();
            addLimit();
        } catch (Exception e) {
            StringBuilder msg = new StringBuilder();
            msg.append(e.getMessage()).append(". Rendering query:\n");
            query.toString(msg, true);
            throw new OnexusException(msg.toString(), e);
        }

    }

    protected void addSelect() {

        for (Map.Entry<String, List<String>> selectCollection : query.getSelect().entrySet()) {

            String collectionAlias = selectCollection.getKey();
            ORI collectionUri = QueryUtils.getCollectionOri(query, collectionAlias);
            SqlCollectionDDL collectionDDL = manager.getDDL(collectionUri);

            List<String> fields = selectCollection.getValue();
            if (fields == null) {
                fields = new ArrayList<String>();
                for (SqlCollectionDDL.ColumnInfo column : collectionDDL.getColumnInfos()) {
                    fields.add(column.getField().getId());
                }
            }
            for (String fieldId : fields) {

                SqlCollectionDDL.ColumnInfo columnInfo = collectionDDL.getColumnInfoByFieldName(fieldId);
                if (columnInfo == null) {
                    throw new UnsupportedOperationException("Unknown field '" + fieldId + "'");
                }
                String fieldName = columnInfo.getColumnName();
                select.add("`" + collectionAlias + "`.`" + fieldName + "` " + collectionAlias + "_" + fieldName);
            }


        }

    }

    protected void addFrom() {

        String collectionAlias = query.getFrom();
        ORI collectionUri = QueryUtils.getCollectionOri(query, collectionAlias);
        String collectionTable = manager.getDDL(collectionUri).getTableName();

        this.from = "`" + collectionTable + "` AS " + collectionAlias;
    }

    protected void addJoins() {

        Collection fromCollection = manager.getCollection(QueryUtils.getCollectionOri(query, query.getFrom()));

        // Fixed entities
        List<EqualId> equalIds = new ArrayList<EqualId>();
        addEqualIdFilters(equalIds, query.getWhere());

        List<ORI> equalIdsCollections = new ArrayList<ORI>();
        for (EqualId se : equalIds) {
            equalIdsCollections.add(QueryUtils.getCollectionOri(query, se.getCollectionAlias()));
        }

        Map<ORI, List<ORI>> networkFixedCollection = new HashMap<ORI, List<ORI>>();
        Map<ORI, StringBuilder> networkFixedJoins = new HashMap<ORI, StringBuilder>();
        Map<ORI, List<FieldLink>> networkLinks = new HashMap<ORI, List<FieldLink>>();

        for (Map.Entry<String, ORI> define : query.getDefine().entrySet()) {

            String collectionAlias = define.getKey();
            ORI collectionUri = define.getValue().toAbsolute(query.getOn());

            // Skip from collection
            if (collectionUri.equals(fromCollection.getORI())) {
                continue;
            }

            String collectionTable = manager.getDDL(collectionUri).getTableName();

            Collection joinCollection = manager.getCollection(collectionUri);

            StringBuilder leftJoin = new StringBuilder(50);
            leftJoin.append(" LEFT JOIN `");
            leftJoin.append(collectionTable).append("` AS `").append(collectionAlias).append("` ON ");

            // Fixed entities
            List<ORI> collectionsFixed = new ArrayList<ORI>();
            for (EqualId se : equalIds) {
                if (joinCollection.getLinks() != null) {
                    for (Link colLink : joinCollection.getLinks()) {

                        ORI fixedCollection = QueryUtils.getCollectionOri(query, se.getCollectionAlias());
                        ORI linkCollection = colLink.getCollection().toAbsolute(query.getOn());

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
                                        .append(LinkUtils.getToFieldName(fields.get(i)))
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
            ORI parentURI = query.getOn();
            List<FieldLink> linkFields = LinkUtils.getLinkFields(parentURI, joinCollection, fromCollection, equalIdsCollections);

            // Add thirdParty joins
            if (linkFields.isEmpty()) {

                // Check if it's possible to link with another joined collection

                for (Map.Entry<String, ORI> tpDefine : query.getDefine().entrySet()) {

                    // Skip current joined collection
                    if (tpDefine.getKey().equals(define.getKey())) {
                        continue;
                    }

                    ORI tpCollectionUri = tpDefine.getValue().toAbsolute(query.getOn());

                    // Skip from collection
                    if (tpCollectionUri.equals(fromCollection.getORI())) {
                        continue;
                    }

                    Collection tpJoinCollection = manager.getCollection(tpCollectionUri);

                    linkFields.addAll(LinkUtils.getLinkFields(parentURI, joinCollection, tpJoinCollection, equalIdsCollections));
                }
            }

            // Check if it's possible to join this collection
            if (linkFields.isEmpty()) {
                throw new UnsupportedOperationException("Impossible to link collection '" + collectionUri + "' to '" + fromCollection.getORI() + "'.");
            }

            networkFixedJoins.put(collectionUri, leftJoin);
            networkFixedCollection.put(collectionUri, collectionsFixed);
            networkLinks.put(collectionUri, linkFields);


        }

        // Sort the network to include JOINS in the correct order

        List<ORI> keys = new ArrayList<ORI>(networkLinks.keySet());
        keys = sort(keys, new LinksNetworkComparator(networkLinks, fromCollection.getORI(), equalIdsCollections));

        for (ORI collectionUri : keys) {

            StringBuilder leftJoin = networkFixedJoins.get(collectionUri);
            List<ORI> collectionsFixed = networkFixedCollection.get(collectionUri);

            for (FieldLink fieldLink : networkLinks.get(collectionUri)) {

                ORI linkCollection = fieldLink.getToCollection().toAbsolute(query.getOn());
                if (!collectionsFixed.contains(linkCollection)) {

                    ORI aCollection = fieldLink.getFromCollection();
                    SqlCollectionDDL aDDL = manager.getDDL(aCollection);
                    String aAlias = QueryUtils.newCollectionAlias(query, aCollection);
                    String aField = aDDL.getColumnInfoByFieldName(
                            fieldLink.getFromFieldName()).getColumnName();
                    leftJoin.append("`").append(aAlias).append("`.`")
                            .append(aField).append("` = `");

                    ORI bCollection = fieldLink.getToCollection();
                    SqlCollectionDDL bDDL = manager.getDDL(bCollection);
                    String bAlias = QueryUtils.newCollectionAlias(query, bCollection);

                    SqlCollectionDDL.ColumnInfo columnInfo = bDDL.getColumnInfoByFieldName(fieldLink.getToFieldName());

                    if (columnInfo == null) {
                        throw new UnsupportedOperationException("Malformed link '" + fieldLink.toString() + "'");
                    }


                    String bField = columnInfo.getColumnName();
                    leftJoin.append(bAlias).append("`.`").append(bField)
                            .append("` AND ");
                }

            }

            // Remove last ' AND '
            leftJoin.delete(leftJoin.length() - 5, leftJoin.length() - 1);
            this.leftJoins.add(leftJoin.toString());

        }

    }

    private static List<ORI> sort(List<ORI> keys, LinksNetworkComparator comparator) {

        List<ORI> sortedKeys = new ArrayList<ORI>(keys);

        int l = keys.size();
        boolean swaped = true;
        while (swaped) {
            swaped = false;
            for (int i = 0; i < l - 1; i++) {
                for (int f = i + 1; f < l; f++) {
                    int value = comparator.compare(sortedKeys.get(i), sortedKeys.get(f));
                    if (value > 0) {
                        ORI vI = sortedKeys.get(f);
                        ORI vF = sortedKeys.get(i);
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

            String collectionAlias = order.getCollection();
            ORI collectionOri = QueryUtils.getCollectionOri(query, collectionAlias);
            SqlCollectionDDL collectionDDL = manager.getDDL(collectionOri);
            SqlCollectionDDL.ColumnInfo columnInfo = collectionDDL.getColumnInfoByFieldName(order.getField());

            String field = "`" + collectionAlias + "`.`" + columnInfo.getColumnName() + "`";
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
