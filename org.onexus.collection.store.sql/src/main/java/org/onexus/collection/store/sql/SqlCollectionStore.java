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
import org.onexus.collection.api.ICollectionStore;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntitySet;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SqlCollectionStore implements ICollectionStore {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SqlCollectionStore.class);

    private static final int INSERT_BATCH_SIZE = 400;
    private static final int INSERT_BUFFER_SIZE = INSERT_BATCH_SIZE * 400;

    private DataSource dataSource;

    private Map<ORI, SqlCollectionDDL> ddls;

    private SqlDialect sqlDialect;

    public SqlCollectionStore() {
        this(new SqlDialect());
    }

    public SqlCollectionStore(SqlDialect sqlDialect) {
        super();

        this.sqlDialect = sqlDialect;
    }

    public void init() {

        dataSource = newDataSource();

        // Create the store properties table if it's necessary
        try {
            sqlDialect.createSystemPropertiesTable(dataSource.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.ddls = new HashMap<ORI, SqlCollectionDDL>();
    }

    protected abstract DataSource newDataSource();

    @Override
    public boolean isRegistered(ORI collectionURI) {

        String tableName = getProperty(collectionURI.toString());

        return (tableName != null);

    }

    private String getProperty(String propertyKey) {
        String property = null;
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            property = sqlDialect.loadProperty(conn, propertyKey);
        } catch (Exception e) {
            LOGGER.error("Error reading property " + propertyKey, e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error("Closing connection", e);
                }
        }

        return property;
    }


    @Override
    public void register(ORI collectionURI) {
        LOGGER.debug("Registering collection {}", collectionURI);

        // Rebuild always the DDL before registering
        ddls.put(collectionURI, new SqlCollectionDDL(sqlDialect,
                getCollection(collectionURI), getProperty(collectionURI.toString())));

        SqlCollectionDDL ddl = getDDL(collectionURI);
        Connection conn = null;
        try {
            conn = dataSource.getConnection();

            String dropTable = ddl.getDropTable();
            LOGGER.debug(dropTable);
            try {
                sqlDialect.execute(conn, dropTable);
            } catch (Exception e) {
                LOGGER.debug("Error droping table '" + dropTable + "' at register()", e);
            }

            List<String> dropIndex = ddl.getDropIndex();
            for (String indexSQL : dropIndex) {
                LOGGER.debug(indexSQL);
                try {
                    sqlDialect.execute(conn, indexSQL);
                } catch (Exception e) {
                    LOGGER.debug("Error creating index for table '" + dropTable + "' at register()", e);
                }
            }


            String createTable = ddl.getCreateTable();
            LOGGER.debug(createTable);
            sqlDialect.execute(conn, createTable);

            List<String> createIndex = ddl.getCreateIndex();
            for (String indexSQL : createIndex) {
                LOGGER.debug(indexSQL);
                sqlDialect.execute(conn, indexSQL);
            }

            sqlDialect.createSystemPropertiesTable(conn, false);
            sqlDialect.saveProperty(conn, collectionURI.toString(), ddl.getTableName());

        } catch (Exception e) {
            String msg = String.format(
                    "Registering collection '%s' with SQL create table '%s'",
                    collectionURI, ddl.getCreateTable());
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error("Closing connection", e);
                }
        }

    }

    @Override
    public void deregister(ORI collectionURI) {
        LOGGER.debug("Unregistering collection {}", collectionURI);

        Connection conn = null;
        String tableName = null;
        try {

            conn = dataSource.getConnection();

            tableName = sqlDialect.loadProperty(conn, collectionURI.toString());
            sqlDialect.removeProperty(conn, collectionURI.toString());
            sqlDialect.execute(conn, "DROP TABLE `" + tableName + "`");

        } catch (Exception e) {
            String msg = String.format(
                    "Unregistering collection '%s' with table '%s'",
                    collectionURI, tableName);
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error("Closing connection", e);
                }
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getRegistered() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return sqlDialect.loadPropertyKeys(conn);
        } catch (Exception e) {
            LOGGER.error("Error getRegistered()", e);
            return Collections.EMPTY_LIST;
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error("Closing connection", e);
                }
        }

    }

    @Override
    public IEntityTable load(Query query) {
        LOGGER.debug("Loading query\n --------------------------------------------------------\n {} \n --------------------------------------------------------\n", query);

        try {

            SqlEntityTable entitySet = new SqlEntityTable(this, query,
                    dataSource.getConnection());
            return entitySet;

        } catch (SQLException e) {
            String msg = "Error loading query '" + query + "'";
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    @Override
    public void insert(IEntitySet entitySet) {

        if (!isRegistered(entitySet.getCollection().getORI())) {
            throw new RuntimeException("The collection '"
                    + entitySet.getCollection().getORI()
                    + "' is NOT registered in this collection store.");
        }

        SqlCollectionDDL ddl = getDDL(entitySet.getCollection().getORI());

        StringBuilder sql = new StringBuilder(INSERT_BUFFER_SIZE);

        sqlDialect.openInsert(sql, ddl);

        Connection conn = null;
        try {
            int batch = 0;
            conn = dataSource.getConnection();
            while (entitySet.next()) {

                // Skip if the primary key is null
                boolean allPrimaryKeysNotNull = true;
                for (SqlCollectionDDL.ColumnInfo columnInfo : ddl.getColumnInfos()) {
                    Field field = columnInfo.getField();
                    if (field.isPrimaryKey() != null
                            && field.isPrimaryKey()
                            && entitySet.get(field.getId()) == null) {
                        allPrimaryKeysNotNull = false;
                        break;
                    }
                }

                if (!allPrimaryKeysNotNull) {
                    LOGGER.warn("Primary key value is NULL. Skipping row " + entitySet.toString());
                    continue;
                }

                if (batch > 0) {
                    sql.append(",");
                }
                sqlDialect.addValues(sql, ddl, entitySet);
                if (batch < INSERT_BATCH_SIZE) {
                    batch++;
                } else {
                    sqlDialect.execute(conn, sql.toString());
                    sql = new StringBuilder(INSERT_BUFFER_SIZE);
                    sqlDialect.openInsert(sql, ddl);
                    batch = 0;
                }
            }

            if (batch > 0) {
                sqlDialect.execute(conn, sql.toString());
                sql = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error("Closing connection", e);
                }
        }

    }

    @Override
    public void insert(IEntity entity) {

        if (!isRegistered(entity.getCollection().getORI())) {
            throw new RuntimeException("The collection '"
                    + entity.getCollection().getORI()
                    + "' is NOT registered in this collection store.");
        }

        StringBuilder sql = new StringBuilder();

        SqlCollectionDDL ddl = getDDL(entity.getCollection().getORI());

        // Open insert
        sqlDialect.openInsert(sql, ddl);
        sqlDialect.addValues(sql, ddl, entity);

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            sqlDialect.execute(conn, sql.toString());
        } catch (SQLException e) {
            String msg = "Error inserting entity: '" + entity + "'";
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error("Closing connection", e);
                }
        }

    }

    public Collection getCollection(ORI collectionURI) {
        return getResourceManager().load(Collection.class, collectionURI);
    }

    public SqlCollectionDDL getDDL(ORI collectionURI) {
        if (!ddls.containsKey(collectionURI)) {
            ddls.put(collectionURI, new SqlCollectionDDL(sqlDialect,
                    getCollection(collectionURI), getProperty(collectionURI.toString())));
        }

        return ddls.get(collectionURI);
    }

    public abstract IResourceManager getResourceManager();

    protected DataSource getDataSource() {
        return this.dataSource;
    }

    public SqlDialect getSqlDialect() {
        return sqlDialect;
    }

    public Statement createReadStatement(Connection dataConn) throws SQLException {
        return dataConn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
    }

    public SqlQuery newSqlQuery(Query query) {
        return new SqlQuery(this, query);
    }
}
