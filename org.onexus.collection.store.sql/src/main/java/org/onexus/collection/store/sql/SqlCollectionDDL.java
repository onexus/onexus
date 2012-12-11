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
import org.onexus.collection.api.Field;
import org.onexus.collection.api.Link;
import org.onexus.collection.api.utils.LinkUtils;
import org.onexus.collection.store.sql.adapters.SqlAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SqlCollectionDDL {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(SqlCollectionDDL.class);

    public final static char FIELD_SEPARATOR = '.';

    private Collection collection;
    private String tableName;
    private StringBuilder createTable;
    private Map<String, ColumnInfo> columnInfos;

    private SqlDialect sqlDialect;
    private List<String> createIndex;
    private List<String> dropIndex;

    public SqlCollectionDDL(SqlDialect sqlDialect, Collection collection, String tableName) {

        this.sqlDialect = sqlDialect;
        this.collection = collection;
        this.columnInfos = new HashMap<String, ColumnInfo>();
        this.tableName = (tableName == null ? convertURItoTableName(collection) : tableName);

        prepareFieldInfoMap(collection);
        prepareCreateTable(collection);
        prepareCreateIndex(collection);

    }

    private void prepareCreateIndex(Collection collection) {

        this.createIndex = new ArrayList<String>();
        this.dropIndex = new ArrayList<String>();
        if (collection.getLinks() != null) {
            StringBuilder linkSQL, dropIndexSQL;
            int i = 0;
            for (Link link : collection.getLinks()) {
                linkSQL = new StringBuilder();
                dropIndexSQL = new StringBuilder();

                String indexName = getTableName() + "-" + Integer.toString(i);
                linkSQL.append("CREATE INDEX `").append(indexName);
                linkSQL.append("` ON `").append(getTableName()).append("` (");

                dropIndexSQL.append("DROP INDEX `").append(indexName).append("`");

                Iterator<String> fieldIt = link.getFields().iterator();
                while (fieldIt.hasNext()) {
                    linkSQL.append("`").append(LinkUtils.getFromFieldName(fieldIt.next())).append("`");
                    if (fieldIt.hasNext()) {
                        linkSQL.append(", ");
                    }
                }
                linkSQL.append(")");
                this.createIndex.add(linkSQL.toString());
                this.dropIndex.add(dropIndexSQL.toString());
                i++;
            }
        }

    }

    private static String convertURItoTableName(Collection collection) {
        String hashCode = Integer.toHexString(collection.getURI().getProjectUrl().hashCode());
        String tableName = removeNonValidChars(collection.getURI().getPath());

        // Check that the table name is no longer than 64 characters (the
        // maximum allowed)
        int totalLength = tableName.length() + hashCode.length() + 1;
        if (totalLength > 64) {
            return tableName.substring(0, tableName.length()
                    - (totalLength - 64))
                    + "_" + hashCode;
        } else {
            return hashCode + "_" + tableName;
        }
    }

    public Collection getCollection() {
        return collection;
    }

    public String getTableName() {
        return tableName;
    }

    public String getCreateTable() {
        return createTable.toString();
    }

    public String getDropTable() {
        return "DROP TABLE IF EXISTS `" + tableName + "`";
    }

    public java.util.Collection<ColumnInfo> getColumnInfos() {
        return columnInfos.values();
    }

    public ColumnInfo getColumnInfo(String columnName) {
        return columnInfos.get(columnName);
    }

    public ColumnInfo getColumnInfoByFieldName(String fieldName) {
        for (ColumnInfo ci : columnInfos.values()) {
            if (ci.getField().getId().equals(fieldName)) {
                return ci;
            }
        }
        return null;
    }

    private void prepareFieldInfoMap(Collection collection) {
        if (collection == null) {
            return;
        }

        for (Field field : collection.getFields()) {
            ColumnInfo fi = new ColumnInfo(field);
            columnInfos.put(fi.getColumnName(), fi);
        }
    }

    private void prepareCreateTable(Collection collection) {
        createTable = new StringBuilder();
        createTable.append("CREATE TABLE IF NOT EXISTS `").append(tableName)
                .append("` (");

        Iterator<ColumnInfo> ifi = columnInfos.values().iterator();
        while (ifi.hasNext()) {
            ColumnInfo fi = ifi.next();
            createTable.append("`").append(fi.getColumnName()).append("` ")
                    .append(fi.getColumnType());

            if (ifi.hasNext()) {
                createTable.append(", ");
            }
        }

        // Add primary key

        // TODO Use common collections filters
        List<String> keys = new ArrayList<String>();
        for (Field field : collection.getFields()) {
            if (field.isPrimaryKey() != null && field.isPrimaryKey()) {
                keys.add(field.getId());
            }
        }

        if (!keys.isEmpty()) {
            createTable.append(", PRIMARY KEY (`");
            Iterator<String> keyFields = keys.iterator();
            while (keyFields.hasNext()) {
                String fieldURI = keyFields.next();
                String columnName = null;
                for (ColumnInfo column : columnInfos.values()) {
                    if (column.getField().getId().equals(fieldURI)) {
                        columnName = column.getColumnName();
                        break;
                    }
                }
                if (columnName == null) {
                    String msg = String.format(
                            "Key field '%s' not fount in collection '%s'.",
                            fieldURI, collection);
                    LOGGER.error(msg);
                    throw new RuntimeException(msg);
                }

                createTable.append(columnName);
                if (keyFields.hasNext()) {
                    createTable.append("`, `");
                }
            }
            createTable.append("`)");
        }

        createTable.append(")");
    }

    private static String removeNonValidChars(String id) {
        return id.toLowerCase().trim().replaceAll("[^a-z0-9]", "_");
    }

    public List<String> getCreateIndex() {
        return createIndex;
    }

    public List<String> getDropIndex() {
        return dropIndex;
    }

    public class ColumnInfo {

        private Field field;
        private String columnName;
        private String columnType = null;

        private ColumnInfo(Field field) {
            this.columnName = removeNonValidChars(field.getId());
            this.field = field;

            this.columnType = field.getProperty("SQL_COLUMN_TYPE");
            if (this.columnType == null) {
                this.columnType = sqlDialect.getColumnType(field.getType());
            }
            if (this.columnType == null) {
                String msg = "Column type not found for the field: '" + field
                        + "'";
                LOGGER.error(msg);
                throw new RuntimeException(msg);
            }
        }

        public Field getField() {
            return field;
        }

        public String getColumnName() {
            return columnName;
        }

        public String getColumnType() {
            return columnType;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ColumnInfo [field=");
            builder.append(field);
            builder.append(", columnName=");
            builder.append(columnName);
            builder.append(", columnType=");
            builder.append(columnType);
            builder.append("]");
            return builder.toString();
        }

        public SqlAdapter getAdapter() {
            return sqlDialect.getAdapter(field.getType());
        }

    }


}
