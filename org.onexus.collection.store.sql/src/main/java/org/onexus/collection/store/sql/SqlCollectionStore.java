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

import org.onexus.core.*;
import org.onexus.core.query.Query;
import org.onexus.core.resources.Collection;
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

    private String status;

    private DataSource dataSource;

    private Map<String, SqlCollectionDDL> ddls;

    private SqlDialect sqlDialect;

    public SqlCollectionStore() {
        this(new SqlDialect());
    }

    public SqlCollectionStore(SqlDialect sqlDialect) {
        super();

        this.sqlDialect = sqlDialect;
    }

    public void init() {

        if (!STATUS_ENABLED.equals(getStatus())) {
            return;
        }

        dataSource = newDataSource();

        // Create the store properties table if it's necessary
        try {
            sqlDialect.createSystemPropertiesTable(dataSource.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.ddls = new HashMap<String, SqlCollectionDDL>();
    }

    protected abstract DataSource newDataSource();

    @Override
    public boolean isRegistered(String collectionURI) {
        String tableName = null;
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            tableName = sqlDialect.loadProperty(conn, collectionURI);
        } catch (Exception e) {
            LOGGER.error("Error reading property " + collectionURI, e);
            return false;
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error("Closing connection", e);
                }
        }

        if (tableName == null) {
            return false;
        }

        return true;
    }

    @Override
    public void registerCollection(String collectionURI) {
        LOGGER.debug("Registering collection {}", collectionURI);

        // Rebuild always the DDL before registering
        ddls.put(collectionURI, new SqlCollectionDDL(sqlDialect,
                getCollection(collectionURI)));

        SqlCollectionDDL ddl = getDDL(collectionURI);
        Connection conn = null;
        try {
            conn = dataSource.getConnection();

                String dropTable = ddl.getDropTable();
                LOGGER.debug(dropTable);
                try { sqlDialect.execute(conn, dropTable); } catch (Exception e) {};

                List<String> dropIndex = ddl.getDropIndex();
                for (String indexSQL : dropIndex) {
                    LOGGER.debug(indexSQL);
                    try { sqlDialect.execute(conn, indexSQL); } catch (Exception e) {};
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
            sqlDialect.saveProperty(conn, collectionURI, ddl.getTableName());

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
    public void unregisterCollection(String collectionURI) {
        LOGGER.debug("Unregistering collection {}", collectionURI);

        Connection conn = null;
        String tableName = null;
        try {

            conn = dataSource.getConnection();

            tableName = sqlDialect.loadProperty(conn, collectionURI);
            sqlDialect.removeProperty(conn, collectionURI);
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
    public List<String> getRegisteredCollections() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return sqlDialect.loadPropertyKeys(conn);
        } catch (Exception e) {
            LOGGER.error("Error getRegisteredCollections()", e);
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

        if (!isRegistered(entitySet.getCollection().getURI())) {
            throw new RuntimeException("The collection '"
                    + entitySet.getCollection().getURI()
                    + "' is NOT registered in this collection store.");
        }

        SqlCollectionDDL ddl = getDDL(entitySet.getCollection().getURI());

        StringBuilder sql = new StringBuilder(INSERT_BUFFER_SIZE);

        sqlDialect.openInsert(sql, ddl);

        Connection conn = null;
        try {
            int batch = 0;
            conn = dataSource.getConnection();
            while (entitySet.next()) {
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

        if (!isRegistered(entity.getCollection().getURI())) {
            throw new RuntimeException("The collection '"
                    + entity.getCollection().getURI()
                    + "' is NOT registered in this collection store.");
        }

        StringBuilder sql = new StringBuilder();

        SqlCollectionDDL ddl = getDDL(entity.getCollection().getURI());

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

    public Collection getCollection(String collectionURI) {
        return getResourceManager().load(Collection.class, collectionURI);
    }

    public SqlCollectionDDL getDDL(String collectionURI) {
        if (!ddls.containsKey(collectionURI)) {
            ddls.put(collectionURI, new SqlCollectionDDL(sqlDialect,
                    getCollection(collectionURI)));
        }

        return ddls.get(collectionURI);
    }

    public abstract IResourceManager getResourceManager();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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
