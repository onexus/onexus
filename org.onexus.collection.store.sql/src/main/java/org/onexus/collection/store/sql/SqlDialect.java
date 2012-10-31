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

import org.onexus.collection.api.Field;
import org.onexus.collection.store.sql.SqlCollectionDDL.ColumnInfo;
import org.onexus.collection.store.sql.adapters.*;
import org.onexus.collection.store.sql.filters.*;
import org.onexus.collection.api.types.Text;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class SqlDialect {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(SqlDialect.class);

    static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS system_properties (name varchar(128), version VARCHAR(128), primary key(name))";
    static final String DELETE_OBJECT_SQL = "DELETE FROM system_properties WHERE name = ?";
    static final String INSERT_OBJECT_SQL = "INSERT INTO system_properties (name, version) VALUES (?, ?)";
    static final String SELECT_OBJECT_SQL = "SELECT version FROM system_properties WHERE name = ?";
    static final String SELECT_PROPERTIES_KEYS = "SELECT name FROM system_properties";


    private List<FilterBuilder> builders;
    private Map<Class<?>, SqlAdapter> sqlAdapters;
    private Map<Class<?>, String> columnTypes;

    public SqlDialect() {
        super();
        this.sqlAdapters = registerAdapters();
        this.builders = registerFilterBuilders();
        this.columnTypes = registerColumnTypes();
    }

    protected Map<Class<?>, String> registerColumnTypes() {
        Map<Class<?>, String> columnTypes = new HashMap<Class<?>, String>();
        columnTypes.put(String.class, "VARCHAR(128)");
        columnTypes.put(Text.class, "TEXT");
        columnTypes.put(Boolean.class, "TINYINT(1)");
        columnTypes.put(Date.class, "TIMESTAMP");
        columnTypes.put(Integer.class, "INT(11)");
        columnTypes.put(Long.class, "BIGINT");
        columnTypes.put(Double.class, "DOUBLE");
        return columnTypes;
    }

    protected Map<Class<?>, SqlAdapter> registerAdapters() {
        Map<Class<?>, SqlAdapter> sqlAdapters = new HashMap<Class<?>, SqlAdapter>();
        sqlAdapters.put(Double.class, new DoubleAdapter());
        sqlAdapters.put(Integer.class, new IntegerAdapter());
        sqlAdapters.put(Long.class, new LongAdapter());
        sqlAdapters.put(String.class, new StringAdapter(this));
        sqlAdapters.put(Boolean.class, new BooleanAdapter());
        sqlAdapters.put(Text.class, new TextAdapter(this));
        return sqlAdapters;
    }

    protected List<FilterBuilder> registerFilterBuilders() {
        List<FilterBuilder> builders = new ArrayList<FilterBuilder>();
        builders.add(new AtomicFilterBuilder(this));
        builders.add(new EqualIdFilterBuilder(this));
        builders.add(new BinaryFilterBuilder(this));
        builders.add(new NotFilterBuilder(this));
        builders.add(new InFilterBuilder(this));
        builders.add(new IsNullFilterBuilder(this));
        return builders;
    }

    public void createSystemPropertiesTable(Connection conn)
            throws SQLException {
        createSystemPropertiesTable(conn, true);
    }

    public void createSystemPropertiesTable(Connection conn,
                                            boolean closeConnection) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute(CREATE_TABLE_SQL);
        } finally {
            if (st != null)
                st.close();
            if (conn != null && closeConnection)
                conn.close();
        }
    }

    public List<String> loadPropertyKeys(Connection conn) throws Exception {
        List<String> keys = new ArrayList<String>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(SELECT_PROPERTIES_KEYS);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                keys.add(rs.getString("name"));
            }

        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();

        }
        return keys;
    }

    public void saveProperty(Connection conn, String name, String version)
            throws Exception {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(DELETE_OBJECT_SQL);
            pstmt.setString(1, name);
            pstmt.execute();
            pstmt.close();

            pstmt = conn.prepareStatement(INSERT_OBJECT_SQL);
            pstmt.setString(1, name);
            pstmt.setString(2, version);
            pstmt.executeUpdate();
        } finally {
            if (pstmt != null)
                pstmt.close();
        }
    }

    public void removeProperty(Connection conn, String name)
            throws Exception {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(DELETE_OBJECT_SQL);
            pstmt.setString(1, name);
            pstmt.execute();
            pstmt.close();
        } finally {
            if (pstmt != null)
                pstmt.close();
        }
    }

    public String loadProperty(Connection conn, String name)
            throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(SELECT_OBJECT_SQL);

            pstmt.setString(1, name);
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                return null;
            }

            String object = rs.getString(1);
            return object;
        } finally {
            if (pstmt != null)
                pstmt.close();
            if (rs != null)
                rs.close();
        }
    }

    public void execute(Connection conn, String query)
            throws SQLException {
        Statement st = null;
        try {
            LOGGER.trace("Execute: " + query);
            st = conn.createStatement();
            st.execute(query);
        } finally {
            if (st != null)
                st.close();
        }
    }

    public void openInsert(StringBuilder sql, SqlCollectionDDL ddl) {

        // Open insert
        sql.append("INSERT INTO `").append(ddl.getTableName())
                .append("` (`");

        // Column names
        Iterator<ColumnInfo> it = ddl.getColumnInfos().iterator();
        while (it.hasNext()) {
            sql.append(it.next().getColumnName());
            if (it.hasNext()) {
                sql.append("`, `");
            }
        }

        sql.append("`) VALUES ");
    }

    public void addValues(StringBuilder sql, SqlCollectionDDL ddl,
                          IEntity entity) {

        sql.append("(");
        Iterator<ColumnInfo> itColumns = ddl.getColumnInfos().iterator();
        while (itColumns.hasNext()) {
            ColumnInfo column = itColumns.next();
            Object value = entity.get(column.getField().getId());
            if (value == null) {

                if (column.getField().isPrimaryKey() != null && column.getField().isPrimaryKey()) {
                    if (column.getField().getType().equals(String.class)) {
                        sql.append("\"NONE\"");
                    } else {
                        sql.append("0");
                    }
                } else {
                    sql.append("NULL");
                }
            } else {
                SqlAdapter adapter = column.getAdapter();

                try {
                    adapter.append(sql, value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            if (itColumns.hasNext()) {
                sql.append(",");
            }
        }
        sql.append(")");
    }

    public SqlEntity loadEntity(Query query, SqlCollectionDDL ddl, ResultSet rs,
                                SqlEntity dstObj, String columnPrefix) {

        Set<String> columnWithError = new HashSet<String>();

        String collectionAlias = QueryUtils.newCollectionAlias(query, ddl.getCollection().getURI());
        List<String> queryFields = query.getSelect().get(collectionAlias);

        for (ColumnInfo column : ddl.getColumnInfos()) {

            if (queryFields != null && !queryFields.contains(column.getField().getId())) {
                continue;
            }

            String columnName = column.getColumnName();
            SqlAdapter adapter = column.getAdapter();
            try {
                String sqlColumnName = (columnPrefix == null ? columnName
                        : columnPrefix + "_" + columnName);
                dstObj.put(column.getField().getId(),
                        adapter.extract(rs, sqlColumnName));
            } catch (Exception e) {

                if (!columnWithError.contains(columnName)) {
                    LOGGER.error("Error loading column '" + columnName + "'");
                    try {
                        ResultSetMetaData m = rs.getMetaData();

                        List<String> columnNames = new ArrayList<String>();
                        for (int i = 1; i <= m.getColumnCount(); i++) {
                            columnNames.add(m.getColumnName(i));
                        }
                        LOGGER.error("Valid column names: " + columnNames + "'");

                    } catch (SQLException e1) {
                    }
                    columnWithError.add(columnName);
                }

            }
        }

        return dstObj;
    }

    public String nullValue() {
        return "NULL";
    }

    public String quoteString(Object value) {

        if (value == null) {
            return nullValue();
        }

        String s;

        if (value instanceof String) {
            s = (String) value;
        } else {
            s = String.valueOf(value);
        }

        int length = s.length();
        StringBuilder buff = new StringBuilder(length + 2);
        buff.append('\'');
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (c == '\'' || c == '\\') {
                buff.append(c);
            }
            buff.append(c);
        }
        buff.append('\'');
        return buff.toString();
    }


    public FilterBuilder getFilterBuilder(Filter filter) {

        for (FilterBuilder builder : builders) {
            if (builder.canBuild(filter)) {
                return builder;
            }
        }

        return new UnknownFilterBuilder(filter);

    }

    public SqlAdapter getAdapter(Class<?> classType) {
        return sqlAdapters.get(classType);
    }

    public String getColumnType(Class<?> type) {
        return columnTypes.get(type);
    }


}
