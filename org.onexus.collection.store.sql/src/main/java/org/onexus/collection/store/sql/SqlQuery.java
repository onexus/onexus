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

import org.onexus.collection.store.sql.adapters.SqlAdapter;
import org.onexus.collection.store.sql.filters.FilterBuilder;
import org.onexus.core.query.*;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Link;
import org.onexus.core.utils.FieldLink;
import org.onexus.core.utils.LinkUtils;
import org.onexus.core.utils.QueryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SqlQuery {

    private static final Logger log = LoggerFactory.getLogger(SqlQuery.class);
    private List<String> select = new ArrayList<String>();
    private String from;
    private String where;
    private List<String> orderBy = new ArrayList<String>();
    private String limit;

    private List<String> leftJoins = new ArrayList<String>();

    private StringBuilder sqlCount;
    private StringBuilder sqlSelect;

    private Query query;
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

    private void addSelect() {

        for (Map.Entry<String, List<String>> selectCollection : query.getSelect().entrySet()) {

            String collectionAlias = selectCollection.getKey();
            String collectionUri = QueryUtils.getCollectionUri(query, collectionAlias);
            SqlCollectionDDL collectionDDL = manager.getDDL(collectionUri);

            List<String> fields = selectCollection.getValue();
            if (fields == null) {
                fields = new ArrayList<String>();
                for (SqlCollectionDDL.ColumnInfo column :collectionDDL.getColumnInfos()) {
                    fields.add(column.getField().getId());
                }
            }
            for (String fieldId : fields) {
                String fieldName = collectionDDL.getColumnInfoByFieldName(fieldId).getColumnName();
                select.add("`" + collectionAlias + "`.`" + fieldName + "` " + collectionAlias + "_" + fieldName);
            }


        }

    }

    private void addFrom() {

        String collectionAlias = query.getFrom();
        String collectionUri = QueryUtils.getCollectionUri(query, collectionAlias);
        String collectionTable = manager.getDDL(collectionUri).getTableName();

        this.from = "`" + collectionTable + "` AS " + collectionAlias;
    }

    private void addJoins() {

        Collection fromCollection = manager.getCollection(QueryUtils.getCollectionUri(query, query.getFrom()));

        // Fixed entities
        List<EqualId> equalIds = new ArrayList<EqualId>();
        addEqualIdFilters(equalIds, query.getWhere());

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
                for (Link colLink : joinCollection.getLinks()) {

                    String fixedCollection = QueryUtils.getCollectionUri(query, se.getCollectionAlias());
                    String linkCollection = QueryUtils.getAbsoluteCollectionUri(query, colLink.getCollection());

                    if (fixedCollection.equals(linkCollection)) {
                        collectionsFixed.add(fixedCollection);

                        List<String> fields = colLink.getFields();

                        Object id = se.getId();
                        //TODO manage composed keys
                        Object[] ids = new Object[] { id };

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

            // Add links
            String releaseURI = query.getOn();
            List<FieldLink> linkFields = LinkUtils.getLinkFields(releaseURI, joinCollection, fromCollection);

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

                    linkFields.addAll(LinkUtils.getLinkFields(releaseURI, joinCollection, tpJoinCollection));
                }
            }

            // Check if it's possible to join this collection
            if (linkFields.isEmpty()) {
                throw new UnsupportedOperationException("Impossible to link collection '" + collectionUri + "'.");
            }

            for (FieldLink fieldLink : linkFields) {

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
            leftJoins.add(leftJoin.toString());

        }

    }

    public void addEqualIdFilters(List<EqualId> equalIds, Filter filter) {

        if (filter instanceof EqualId) {
            equalIds.add((EqualId) filter);
            return;
        }

        if (filter instanceof And) {
            addEqualIdFilters(equalIds, ((And) filter).getLeft());
            addEqualIdFilters(equalIds, ((And) filter).getRight());
        }
    }


    private void addWhere() {

        Filter filter = query.getWhere();

        if (filter != null) {
            FilterBuilder builder = manager.getSqlDialect().getFilterBuilder(filter);

            StringBuilder where = new StringBuilder();
            builder.build(manager, query, where, filter);
            this.where = where.toString();
        }

    }


    private void addOrderBy() {

        List<OrderBy> ordersOql = query.getOrders();

        if (ordersOql == null || ordersOql.isEmpty()) {
            return;
        }

        for (OrderBy order : ordersOql) {
            this.orderBy.add("`" + order.getCollectionRef() + "`.`" + order.getFieldId() + "`" + (order.isAscendent() ? " ASC" : "DESC"));
        }

    }

    private void addLimit() {

        if (query.getCount() != null) {
            Long offset = query.getOffset();
            this.limit = (offset==null? "0" : offset) + ", " + query.getCount();
        }
    }

    public String toSelectSQL() {
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

    public String toCountSQL() {
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

    @Override
    public String toString() {
        return toSelectSQL();
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
