package org.onexus.collection.store.mysql;

import com.mysql.jdbc.Driver;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.onexus.collection.store.sql.SqlCollectionStore;
import org.onexus.core.IResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;


public class MysqlCollectionStore extends SqlCollectionStore {

    private static final Logger log = LoggerFactory.getLogger(MysqlCollectionStore.class);

    private String server;

    private String port;

    private String database;

    private String username;

    private String password;

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

        // Initialize the DataSource with a connection pool
        ConnectionFactory connectionFactory = new MysqlConnectionFactory();
        ObjectPool connectionPool = new GenericObjectPool(null,
                GenericObjectPool.DEFAULT_MAX_ACTIVE,
                GenericObjectPool.WHEN_EXHAUSTED_GROW,
                GenericObjectPool.DEFAULT_MAX_WAIT);
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

    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }
}
