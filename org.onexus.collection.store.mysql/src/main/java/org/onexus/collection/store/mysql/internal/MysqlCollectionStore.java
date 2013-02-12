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
package org.onexus.collection.store.mysql.internal;

import com.mysql.jdbc.Driver;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.onexus.collection.api.ICollectionStore;
import org.onexus.collection.store.sql.SqlCollectionStore;
import org.onexus.resource.api.IResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MysqlCollectionStore extends SqlCollectionStore implements ICollectionStore {

    private static final Logger log = LoggerFactory.getLogger(MysqlCollectionStore.class);

    private String server;

    private String port;

    private String database;

    private String username;

    private String password;

    private String poolMaxActive;

    private String poolWhenExhausted;

    private String poolMaxWait;

    private IResourceManager resourceManager;

    public MysqlCollectionStore() {
        super();
    }

    protected DataSource newDataSource() {

        try {
            Class.forName(Driver.class.getName());
        } catch (Exception e) {
            log.error("Exception: " + e.getMessage());
        }

        // Config parameters
        int maxActive = 8;
        try {
            maxActive = Integer.valueOf(getPoolMaxActive()).intValue();
        } catch (Exception e) {
            log.error("Malformed config parameter 'poolMaxActive'");
        }

        byte whenExhausted = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
        try {
            if (getPoolWhenExhausted().equalsIgnoreCase("FAIL")) {
                whenExhausted = GenericObjectPool.WHEN_EXHAUSTED_FAIL;
            }
            if (getPoolWhenExhausted().equalsIgnoreCase("GROW")) {
                whenExhausted = GenericObjectPool.WHEN_EXHAUSTED_GROW;
            }
        } catch (Exception e) {
            log.error("Malformed config parameter 'poolWhenExhausted'");
        }

        long maxWait = GenericObjectPool.DEFAULT_MAX_WAIT;
        try {
            maxWait = Long.valueOf(getPoolMaxWait()).longValue();
        } catch (Exception e) {
            log.error("Malformed config parameter 'poolMaxWait'");
        }

        // Initialize the DataSource with a connection pool
        ConnectionFactory connectionFactory = new MysqlConnectionFactory();
        ObjectPool connectionPool = new GenericObjectPool(null,
                maxActive,
                whenExhausted,
                maxWait);
        @SuppressWarnings("unused")
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
                connectionFactory, connectionPool, null, null, false, true);
        return new PoolingDataSource(connectionPool);


    }

    @Override
    public Statement createReadStatement(Connection dataConn) throws SQLException {
        Statement st = super.createReadStatement(dataConn);

        st.setFetchDirection(ResultSet.FETCH_FORWARD);
        st.setFetchSize(Integer.MIN_VALUE);

        return st;
    }

    public class MysqlConnectionFactory implements ConnectionFactory {

        @Override
        public Connection createConnection() throws SQLException {
            return DriverManager.getConnection(
                    "jdbc:mysql://" + server + ":" + port + "/" + database,
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPoolMaxActive() {
        return poolMaxActive;
    }

    public void setPoolMaxActive(String poolMaxActive) {
        this.poolMaxActive = poolMaxActive;
    }

    public String getPoolWhenExhausted() {
        return poolWhenExhausted;
    }

    public void setPoolWhenExhausted(String poolWhenExhausted) {
        this.poolWhenExhausted = poolWhenExhausted;
    }

    public String getPoolMaxWait() {
        return poolMaxWait;
    }

    public void setPoolMaxWait(String poolMaxWait) {
        this.poolMaxWait = poolMaxWait;
    }

    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }
}
