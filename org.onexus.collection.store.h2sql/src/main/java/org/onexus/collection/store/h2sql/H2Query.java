/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.collection.store.h2sql;

import org.onexus.collection.store.h2sql.H2CollectionDDL.ColumnInfo;
import org.onexus.core.query.*;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.core.resources.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class H2Query extends AbstractSqlQuery {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(H2Query.class);

    private List<String> fields = new ArrayList<String>();
    private List<String> wheres = new ArrayList<String>();
    private List<String> orders = new ArrayList<String>();

    private Map<String, String> orderJoins = new HashMap<String, String>();
    private Map<String, String> filterJoins = new HashMap<String, String>();

    private String from = null;
    private String limit = null;

    private StringBuilder sqlCount;
    private StringBuilder sqlSelect;

    private CollectionStore manager;

    public H2Query(CollectionStore manager, Query query) {
        super(query);
        this.manager = manager;
        init();
    }

    protected Collection getCollection(String collectionURI) {
        return this.manager
                .getCollection(getAbsoluteCollectionURI(collectionURI));
    }

    private H2CollectionDDL getCollectionDDL(String collectionURI) {
        return this.manager.getDDL(getAbsoluteCollectionURI(collectionURI));
    }

    protected void addFields() {
        for (String collectionURI : getQuery().getCollections()) {
            addJoin(collectionURI, orderJoins);
            for (ColumnInfo ci : getCollectionDDL(collectionURI)
                    .getColumnInfos()) {
                String alias = getCollectionAlias(collectionURI);
                String name = ci.getColumnName();
                fields.add("`" + alias + "`.`" + name + "` AS `" + alias + "_"
                        + name + "`");
            }
        }
    }

    protected void addFrom() {
        this.from = "`"
                + getCollectionDDL(getQuery().getMainCollection())
                .getTableName() + "` AS `"
                + getCollectionAlias(getQuery().getMainCollection()) + "`";
    }

    protected void addWhere() {

        for (String filterKey : getQuery().getFilterKeys()) {
            for (Filter filter : getQuery().getFilters(filterKey)) {
                wheres.add(applyFilter(filter));
            }
        }

        // Fix fixed entities of the main collection
        String mainCollectionURI = getAbsoluteCollectionURI(getQuery()
                .getMainCollection());
        Collection mainCollection = getCollection(mainCollectionURI);
        String mainCollectionAlias = getCollectionAlias(mainCollectionURI);
        for (FixedEntity se : getQuery().getFixedEntities()) {
            for (Link colLink : mainCollection.getLinks()) {
                if (getAbsoluteCollectionURI(se.getCollectionURI()).equals(
                        getAbsoluteCollectionURI(colLink.getCollectionURI()))) {
                    List<String> fields = colLink.getFieldNames();
                    String[] ids = se.getEntityId().split("\t");
                    if (fields.size() != ids.length) {
                        throw new RuntimeException("Bad entity id: " + se);
                    }
                    for (int i = 0; i < ids.length; i++) {
                        wheres.add("`" + mainCollectionAlias + "`.`"
                                + SqlUtils.removeNonValidChars(
                                Link.getFromFieldName(fields.get(i))
                        )
                                + "` = '" + ids[i] + "'");

                    }
                }
            }
        }

    }

    protected String applyFilter(Filter filter) {
        if (filter == null) {
            return null;
        }

        String filterCollection = getAbsoluteCollectionURI(filter
                .getCollection());
        if (filterCollection != null
                && !getAbsoluteCollectionURI(getQuery().getMainCollection())
                .equals(filterCollection)) {
            addJoin(filterCollection, filterJoins);
        }

        if (filter instanceof NotNullCellCollections) {

            if (!getQuery().getCollections().isEmpty()) {
                for (String collectionId : getQuery().getCollections()) {
                    addJoin(collectionId, filterJoins);
                }
                StringBuilder where = new StringBuilder();
                where.append("COALESCE(");
                Iterator<String> it = getQuery().getCollections().iterator();
                while (it.hasNext()) {
                    String collectionId = getAbsoluteCollectionURI(it.next());
                    String prefix = getCollectionAlias(collectionId);

                    // TODO Support composed primary keys
                    String fieldName = null;
                    for (Field field : getCollection(collectionId).getFields()) {
                        if (field.isPrimaryKey()) {
                            fieldName = field.getName();
                            break;
                        }
                    }

                    where.append("`").append(prefix).append("`.`")
                            .append(SqlUtils.removeNonValidChars(fieldName)).append("`");
                    if (it.hasNext()) {
                        where.append(",");
                    }
                }
                where.append(") IS NOT NULL");
                return where.toString();
            }
            return null;

        }

        if (filter instanceof NotNull) {
            NotNull f = (NotNull) filter;
            // if
            // (getAbsoluteCollectionURI(f.getCollectionURI()).equals(getAbsoluteCollectionURI(getQuery().getMainCollection())))
            // {
            for (String collectionId : f.getCellCollections()) {
                addJoin(collectionId, filterJoins);
            }
            StringBuilder where = new StringBuilder();
            where.append("COALESCE(");
            Iterator<String> it = Arrays.asList(f.getCellCollections())
                    .iterator();
            while (it.hasNext()) {
                String collectionId = getAbsoluteCollectionURI(it.next());
                String prefix = getCollectionAlias(collectionId);

                // TODO Support composed primary keys
                String fieldName = null;
                for (Field field : getCollection(collectionId).getFields()) {
                    if (field.isPrimaryKey()) {
                        fieldName = field.getName();
                        break;
                    }
                }

                where.append("`").append(prefix).append("`.`")
                        .append(SqlUtils.removeNonValidChars(fieldName)).append("`");
                if (it.hasNext()) {
                    where.append(",");
                }
            }
            where.append(") IS NOT NULL");
            return where.toString();
            // }
            // return null;
        }

        if (filter instanceof Between) {
            Between f = (Between) filter;
            String prefix = getCollectionAlias(f.getCollection());
            String field = SqlUtils.removeNonValidChars(f.getFieldName());
            return "`" + prefix + "`.`" + field + "` BETWEEN "
                    + encodeSQL(f.getMin()) + " AND " + encodeSQL(f.getMax());
        }

        if (filter instanceof Equal) {
            Equal f = (Equal) filter;
            String prefix = getCollectionAlias(f.getCollection());
            String field = SqlUtils.removeNonValidChars(f.getFieldName());
            if (f.getValue() == null) {
                return "`" + prefix + "`.`" + field + "` IS NULL ";
            } else {
                return "`" + prefix + "`.`" + field + "` = "
                        + encodeSQL(f.getValue());
            }
        }

        if (filter instanceof EqualEntity) {
            EqualEntity f = (EqualEntity) filter;
            String collectionURI = getAbsoluteCollectionURI(filter
                    .getCollection());
            Collection collection = getCollection(collectionURI);
            String collectionAlias = getCollectionAlias(collectionURI);

            List<String> fields = new ArrayList<String>();
            for (Field field : collection.getFields()) {
                if (field.isPrimaryKey()) {
                    fields.add(field.getName());
                }
            }

            String[] ids = f.getEntityId().split("\t");
            if (fields.size() != ids.length) {
                throw new RuntimeException("Bad entity id: " + f);
            }

            StringBuilder where = new StringBuilder();
            for (int i = 0; i < ids.length; i++) {
                where.append("(`").append(collectionAlias).append("`.`");
                where.append(SqlUtils.removeNonValidChars(fields.get(i))).append("` = '").append(ids[i])
                        .append("')");

                if (i + 1 < ids.length) {
                    where.append(" AND ");
                }
            }

            return where.toString();
        }

        if (filter instanceof NotEqual) {
            NotEqual f = (NotEqual) filter;
            String prefix = getCollectionAlias(f.getCollection());
            String field = SqlUtils.removeNonValidChars(f.getFieldName());
            if (f.getValue() == null) {
                return "`" + prefix + "`.`" + field + "` IS NOT NULL ";
            } else {
                return "`" + prefix + "`.`" + field + "` <> "
                        + encodeSQL(f.getValue());
            }
        }

        if (filter instanceof GreaterThan) {
            GreaterThan f = (GreaterThan) filter;
            String prefix = getCollectionAlias(f.getCollection());
            String field = SqlUtils.removeNonValidChars(f.getFieldName());
            return "`" + prefix + "`.`" + field + "` > "
                    + encodeSQL(f.getValue());
        }

        if (filter instanceof GreaterThanOrEqual) {
            GreaterThanOrEqual f = (GreaterThanOrEqual) filter;
            String prefix = getCollectionAlias(f.getCollection());
            String field = SqlUtils.removeNonValidChars(f.getFieldName());
            return "`" + prefix + "`.`" + field + "` >= "
                    + encodeSQL(f.getValue());
        }

        if (filter instanceof LessThan) {
            LessThan f = (LessThan) filter;
            String prefix = getCollectionAlias(f.getCollection());
            String field = SqlUtils.removeNonValidChars(f.getFieldName());
            return "`" + prefix + "`.`" + field + "` < "
                    + encodeSQL(f.getValue());
        }

        if (filter instanceof LessThanOrEqual) {
            LessThanOrEqual f = (LessThanOrEqual) filter;
            String prefix = getCollectionAlias(f.getCollection());
            String field = SqlUtils.removeNonValidChars(f.getFieldName());
            return "`" + prefix + "`.`" + field + "` <= "
                    + encodeSQL(f.getValue());
        }

        if (filter instanceof Like) {
            Like f = (Like) filter;
            String prefix = getCollectionAlias(f.getCollection());
            String field = SqlUtils.removeNonValidChars(f.getFieldName());
            return "`" + prefix + "`.`" + field + "` LIKE '%" + f.getValue()
                    + "%'";
        }

        if (filter instanceof In) {
            In f = (In) filter;
            String prefix = getCollectionAlias(f.getCollection());
            String field = SqlUtils.removeNonValidChars(f.getFieldName());
            return "`" + prefix + "`.`" + field + "` IN ("
                    + f.listValuesToString(",") + ")";
        }

        if (filter instanceof Or) {
            Or f = (Or) filter;
            return "(" + applyFilter(f.getLeft()) + ") OR ("
                    + applyFilter(f.getRight()) + ")";
        }

        return null;

    }

    private static String encodeSQL(Object value) {
        if (value == null) {
            return "NULL";
        }

        if (value instanceof String) {
            return "'" + value + "'";
        }

        return String.valueOf(value);
    }

    protected void addOrder() {

        Order order = getQuery().getOrder();

        if (order == null) {
            return;
        }

        if (!isValidFieldName(order.getCollection(), order.getFieldName())) {
            return;
        }

        String mainCollection = getQuery().getMainCollection();
        if (!order.getCollection().equals(mainCollection)) {

            // Order a Cell collection
            if (!filterJoins.containsKey(order.getCollection())) {
                addJoin(order.getCollection(), orderJoins);
            }
        }

        for (ColumnInfo ci : getCollectionDDL(order.getCollection())
                .getColumnInfos()) {
            if (ci.getField().getName().equals(order.getFieldName())) {


                orders.add("`" + getCollectionAlias(order.getCollection())
                        + "`.`" + ci.getColumnName()
                        + (order.isAscending() ? "` ASC" : "` DESC")
                        + " NULLS LAST");
                break;
            }
        }

    }

    private boolean isValidFieldName(String collectionURI, String fieldName) {
        Collection collection = getCollection(collectionURI);
        if (collection == null) {
            return false;
        }

        for (Field field : collection.getFields()) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }

        return false;
    }

    private void addJoin(String collectionId, Map<String, String> joins) {

        collectionId = getAbsoluteCollectionURI(collectionId);

        if (!joins.containsKey(collectionId)
                && !getAbsoluteCollectionURI(getQuery().getMainCollection())
                .equals(collectionId)) {

            Collection mainCollection = getCollection(getQuery()
                    .getMainCollection());
            Collection joinCollection = getCollection(collectionId);

            String prefix = getCollectionAlias(collectionId);
            H2CollectionDDL ddl = manager.getDDL(collectionId);
            StringBuilder leftJoin = new StringBuilder(50);
            leftJoin.append(" LEFT JOIN `");
            leftJoin.append(ddl.getTableName()).append("` AS `").append(prefix)
                    .append("` ON ");

            // Fix fixed entities
            List<String> collectionsFixed = new ArrayList<String>();
            for (FixedEntity se : getQuery().getFixedEntities()) {
                for (Link colLink : joinCollection.getLinks()) {
                    if (getAbsoluteCollectionURI(se.getCollectionURI())
                            .equals(getAbsoluteCollectionURI(colLink.getCollectionURI()))) {
                        collectionsFixed.add(getAbsoluteCollectionURI(se.getCollectionURI()));
                        List<String> fields = colLink.getFieldNames();
                        String[] ids = se.getEntityId().split("\t");
                        if (fields.size() != ids.length) {
                            throw new RuntimeException("Bad entity id: " + se);
                        }
                        for (int i = 0; i < ids.length; i++) {
                            leftJoin.append("`").append(prefix)
                                    .append("`.`")
                                    .append(Link.getToFieldName(fields.get(i)))
                                    .append("` = ");
                            leftJoin.append("'" + ids[i] + "'").append(" AND ");
                        }
                    }
                }
            }

            // Add links
            String releaseURI = getQuery().getMainNamespace();
            List<FieldLink> linkFields = SqlUtils.getLinkFields(releaseURI, joinCollection, mainCollection);

            for (FieldLink fieldLink : linkFields) {

                if (!collectionsFixed.contains(getAbsoluteCollectionURI(fieldLink.getToCollection()))) {

                    String aCollection = fieldLink.getFromCollection();
                    H2CollectionDDL aDDL = getCollectionDDL(aCollection);
                    String aAlias = getCollectionAlias(aCollection);
                    String aField = aDDL.getColumnInfoByFieldName(fieldLink.getFromFieldName()).getColumnName();
                    leftJoin.append("`").append(aAlias).append("`.`").append(aField).append("` = `");

                    String bCollection = fieldLink.getToCollection();
                    H2CollectionDDL bDDL = getCollectionDDL(bCollection);
                    String bAlias = getCollectionAlias(bCollection);
                    String bField = bDDL.getColumnInfoByFieldName(fieldLink.getToFieldName()).getColumnName();
                    leftJoin.append(bAlias).append("`.`").append(bField).append("` AND ");
                }

            }

            leftJoin.append("0=0");
            joins.put(collectionId, leftJoin.toString());

        }
    }

    protected void addLimit() {
        long offset = getQuery().getFirstResult();
        long rowCount = getQuery().getMaxResults();
        if (offset > 0) {
            this.limit = Long.toString(offset) + ", " + Long.toString(rowCount);
        } else if (rowCount < Long.MAX_VALUE) {
            this.limit = Long.toString(rowCount);
        }
    }

    public String toSelectSQL() {
        if (sqlSelect == null) {
            sqlSelect = new StringBuilder();

            sqlSelect.append("SELECT ");
            Iterator<String> it = fields.iterator();
            while (it.hasNext()) {
                String field = it.next();
                sqlSelect.append(field);
                if (it.hasNext()) {
                    sqlSelect.append(", ");
                }
            }
            sqlSelect.append(" FROM ").append(from);

            if (!filterJoins.isEmpty()) {
                SqlUtils.flatCollection(sqlSelect, filterJoins.values(), " ");
            }

            if (!orderJoins.isEmpty()) {
                for (String collectionURI : filterJoins.keySet()) {
                    if (orderJoins.containsKey(collectionURI)) {
                        orderJoins.remove(collectionURI);
                    }
                }
                SqlUtils.flatCollection(sqlSelect, orderJoins.values(), "");
            }

            if (!wheres.isEmpty()) {
                sqlSelect.append(" WHERE (");
                SqlUtils.flatCollection(sqlSelect, wheres, ") AND (");
                sqlSelect.append(")");
            }

            if (!orders.isEmpty()) {
                sqlSelect.append(" ORDER BY ");
                SqlUtils.flatCollection(sqlSelect, orders, ", ");
            }

            if (limit != null) {
                sqlSelect.append(" LIMIT ").append(limit);
            }
        }

        LOGGER.debug(sqlSelect.toString());
        return sqlSelect.toString();
    }

    public String toCountSQL() {
        if (sqlCount == null) {
            sqlCount = new StringBuilder();
            sqlCount.append("SELECT COUNT(*) AS `size` FROM ").append(from);

            if (!filterJoins.isEmpty()) {
                SqlUtils.flatCollection(sqlCount, filterJoins.values(), " ");
            }

            if (!wheres.isEmpty()) {
                sqlCount.append(" WHERE (");
                SqlUtils.flatCollection(sqlCount, wheres, ") AND (");
                sqlCount.append(")");
            }
        }

        LOGGER.debug(sqlCount.toString());
        return sqlCount.toString();
    }

    @Override
    public String toString() {
        return toSelectSQL();
    }

}
