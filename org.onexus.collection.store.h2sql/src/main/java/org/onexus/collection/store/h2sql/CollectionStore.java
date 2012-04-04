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
package org.onexus.collection.store.h2sql;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.h2.Driver;
import org.onexus.core.*;
import org.onexus.core.query.Query;
import org.onexus.core.resources.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionStore implements ICollectionStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionStore.class);

    private static final int INSERT_BATCH_SIZE = 400;
    private static final int INSERT_BUFFER_SIZE = INSERT_BATCH_SIZE * 400;

    private String database = "onexus-h2-database;LOG=0;CACHE_SIZE=65536;LOCK_MODE=0"; // ;TRACE_LEVEL_FILE=3;TRACE_LEVEL_SYSTEM_OUT=3";

    private String username = "sa";

    private String password = "";

    private String status;

    private IResourceManager resourceManager;

    private DataSource dataSource;

    private Map<String, H2CollectionDDL> ddls;

    public CollectionStore() {
        super();
    }

    public void init() {

        if (!STATUS_ENABLED.equals(getStatus())) {
            return;
        }

        LOGGER.debug("Connecting to '{}' as '{}'.", database, username);

        Driver.load();

        // Initialize the DataSource with a connection pool
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory("jdbc:h2:" + database, username,
                password);
        ObjectPool connectionPool = new GenericObjectPool(null, GenericObjectPool.DEFAULT_MAX_ACTIVE,
                GenericObjectPool.WHEN_EXHAUSTED_GROW, GenericObjectPool.DEFAULT_MAX_WAIT);
        @SuppressWarnings("unused")
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,
                connectionPool, null, null, false, true);
        dataSource = new PoolingDataSource(connectionPool);

        // Create the store properties table if it's necessary
        try {
            SqlUtils.createSystemPropertiesTable(dataSource.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.ddls = new HashMap<String, H2CollectionDDL>();

    }

    @Override
    public boolean isRegistered(String collectionURI) {
        String tableName = null;
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            tableName = SqlUtils.loadProperty(conn, collectionURI);
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
        ddls.put(collectionURI, new H2CollectionDDL(getCollection(collectionURI)));

        H2CollectionDDL ddl = getDDL(collectionURI);
        Connection conn = null;
        try {

            conn = dataSource.getConnection();

            SqlUtils.execute(conn, ddl.getDropTable());
            SqlUtils.execute(conn, ddl.getCreateTable());
            SqlUtils.saveProperty(conn, collectionURI, ddl.getTableName());

        } catch (Exception e) {
            String msg = String.format("Registering collection '%s' with SQL create table '%s'", collectionURI,
                    ddl.getCreateTable());
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
            return SqlUtils.loadPropertyKeys(conn);
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
    public void unregisterCollection(String collectionURI) {
        LOGGER.debug("Unregistering collection {}", collectionURI);

        Connection conn = null;
        String tableName = null;
        try {

            conn = dataSource.getConnection();

            tableName = SqlUtils.loadProperty(conn, collectionURI);
            SqlUtils.removeProperty(conn, collectionURI);
            SqlUtils.execute(conn, "DROP TABLE `" + tableName + "`");

        } catch (Exception e) {
            String msg = String.format("Unregistering collection '%s' with table '%s'", collectionURI, tableName);
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
    public IEntityTable load(Query query) {
        LOGGER.debug("Loading query {}", query);

        try {

            H2EntityTable entitySet = new H2EntityTable(this, query, dataSource.getConnection());
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
            throw new RuntimeException("The collection '" + entitySet.getCollection().getURI()
                    + "' is NOT registered in this collection store.");
        }

        H2CollectionDDL ddl = getDDL(entitySet.getCollection().getURI());

        StringBuilder sql = new StringBuilder(INSERT_BUFFER_SIZE);

        SqlUtils.openInsert(sql, ddl);

        Connection conn = null;
        try {
            int batch = 0;
            conn = dataSource.getConnection();
            while (entitySet.next()) {
                if (batch > 0) {
                    sql.append(",");
                }
                SqlUtils.addValues(sql, ddl, entitySet);
                if (batch < INSERT_BATCH_SIZE) {
                    batch++;
                } else {
                    SqlUtils.execute(conn, sql.toString());
                    sql = new StringBuilder(INSERT_BUFFER_SIZE);
                    SqlUtils.openInsert(sql, ddl);
                    batch = 0;
                }
            }

            if (batch > 0) {
                SqlUtils.execute(conn, sql.toString());
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
            throw new RuntimeException("The collection '" + entity.getCollection().getURI()
                    + "' is NOT registered in this collection store.");
        }

        StringBuilder sql = new StringBuilder();

        H2CollectionDDL ddl = getDDL(entity.getCollection().getURI());

        // Open insert
        SqlUtils.openInsert(sql, ddl);
        SqlUtils.addValues(sql, ddl, entity);

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            SqlUtils.execute(conn, sql.toString());
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

    Collection getCollection(String collectionURI) {
        return resourceManager.load(Collection.class, collectionURI);
    }

    H2CollectionDDL getDDL(String collectionURI) {
        if (!ddls.containsKey(collectionURI)) {
            ddls.put(collectionURI, new H2CollectionDDL(getCollection(collectionURI)));
        }

        return ddls.get(collectionURI);
    }

    public void stop(ICollectionStore store, @SuppressWarnings("rawtypes") Map properties) {
        try {
            if (dataSource != null) {
                Connection conn = dataSource.getConnection();
                Statement stat = conn.createStatement();

                stat.execute("SHUTDOWN");
                stat.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

}
