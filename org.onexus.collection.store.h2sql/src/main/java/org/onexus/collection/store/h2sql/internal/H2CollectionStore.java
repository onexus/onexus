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
package org.onexus.collection.store.h2sql.internal;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.h2.Driver;
import org.onexus.collection.store.sql.SqlCollectionStore;
import org.onexus.core.ICollectionStore;
import org.onexus.core.IResourceManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class H2CollectionStore extends SqlCollectionStore {

    private String database = "onexus-h2-database;LOG=0;CACHE_SIZE=65536;LOCK_MODE=0"; // ;TRACE_LEVEL_FILE=3;TRACE_LEVEL_SYSTEM_OUT=3";

    private String username = "sa";

    private String password = "";

    private String status;

    private IResourceManager resourceManager;

    public H2CollectionStore() {
        super(new H2Dialect());
    }

    protected DataSource newDataSource() {

        Driver.load();

        // Initialize the DataSource with a connection pool
        ConnectionFactory connectionFactory = new H2ConnectionFactory();
        ObjectPool connectionPool = new GenericObjectPool(null, GenericObjectPool.DEFAULT_MAX_ACTIVE,
                GenericObjectPool.WHEN_EXHAUSTED_GROW, GenericObjectPool.DEFAULT_MAX_WAIT);
        @SuppressWarnings("unused")
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,
                connectionPool, null, null, false, true);
        return new PoolingDataSource(connectionPool);
    }

    public void stop(ICollectionStore store, @SuppressWarnings("rawtypes") Map properties) {
        try {
            if (getDataSource() != null) {
                Connection conn = getDataSource().getConnection();
                Statement stat = conn.createStatement();

                stat.execute("SHUTDOWN");
                stat.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class H2ConnectionFactory implements ConnectionFactory {

        @Override
        public Connection createConnection() throws SQLException {
            return DriverManager.getConnection(
                    "jdbc:h2:" + database,
                    username,
                    password);
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
