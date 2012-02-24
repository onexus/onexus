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

import org.onexus.collection.store.h2sql.adapters.*;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.core.utils.ResourceTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public class H2CollectionDDL implements Serializable {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(H2CollectionDDL.class);

    private final static Map<Class<?>, String> columnTypes = new HashMap<Class<?>, String>();

    static {
        columnTypes.put(String.class, "VARCHAR_IGNORECASE(128)");
        columnTypes.put(Boolean.class, "TINYINT(1)");
        columnTypes.put(Date.class, "TIMESTAMP");
        columnTypes.put(Integer.class, "INT(11)");
        columnTypes.put(Long.class, "BIGINT");
        columnTypes.put(Double.class, "DOUBLE");
    }

    private final static Map<Class<?>, SQLAdapter> sqlAdapters = new HashMap<Class<?>, SQLAdapter>();

    static {
        sqlAdapters.put(Double.class, new DoubleAdapter());
        sqlAdapters.put(Integer.class, new IntegerAdapter());
        sqlAdapters.put(Long.class, new LongAdapter());
        sqlAdapters.put(String.class, new StringAdapter());
        sqlAdapters.put(Boolean.class, new BooleanAdapter());
    }

    public final static char FIELD_SEPARATOR = '.';

    private Collection collection;
    private String tableName;
    private StringBuilder createTable;
    private Map<String, ColumnInfo> columnInfos;

    public H2CollectionDDL(Collection collection) {

        this.collection = collection;
        this.columnInfos = new HashMap<String, ColumnInfo>();
        this.tableName = convertURItoTableName(collection);

        prepareFieldInfoMap(null, collection);
        prepareCreateTable(collection);

    }

    private static String convertURItoTableName(Collection collection) {
        String hashCode = Integer.toHexString(ResourceTools.getParentURI(collection.getURI()).hashCode());
        String tableName = SqlUtils.removeNonValidChars(collection.getName());

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
        return "CREATE TABLE IF NOT EXISTS `" + tableName + "`; " +
                "TRUNCATE TABLE `" + tableName + "`; " +
                "DROP TABLE IF EXISTS `" + tableName + "`";
    }

    public java.util.Collection<ColumnInfo> getColumnInfos() {
        return columnInfos.values();
    }

    public ColumnInfo getColumnInfo(String columnName) {
        return columnInfos.get(columnName);
    }

    public ColumnInfo getColumnInfoByFieldName(String fieldName) {
        for (ColumnInfo ci : columnInfos.values()) {
            if (ci.getField().getName().equals(fieldName)) {
                return ci;
            }
        }
        return null;
    }

    private void prepareFieldInfoMap(String prefix, Collection collection) {
        if (collection == null) {
            return;
        }

        for (Field field : collection.getFields()) {
            ColumnInfo fi = new ColumnInfo(prefix, field);
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
            if (field.isPrimaryKey()) {
                keys.add(field.getName());
            }
        }

        if (!keys.isEmpty()) {
            createTable.append(", PRIMARY KEY (`");
            Iterator<String> keyFields = keys.iterator();
            while (keyFields.hasNext()) {
                String fieldURI = keyFields.next();
                String columnName = null;
                for (ColumnInfo column : columnInfos.values()) {
                    if (column.getField().getName().equals(fieldURI)) {
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


    public static class ColumnInfo implements Serializable {

        private Field field;
        private String columnName;
        private String columnType = null;

        private ColumnInfo(String prefix, Field field) {
            this.columnName = SqlUtils.removeNonValidChars(field.getName());
            this.field = field;

            this.columnType = field.getProperty("SQL_COLUMN_TYPE");
            if (this.columnType == null) {
                this.columnType = columnTypes.get(field.getDataType());
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

        public SQLAdapter getAdapter() {
            return sqlAdapters.get(field.getDataType());
        }

    }


}
