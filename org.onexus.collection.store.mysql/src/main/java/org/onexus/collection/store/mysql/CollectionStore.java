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
package org.onexus.collection.store.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.onexus.core.ICollectionStore;
import org.onexus.core.IEntity;
import org.onexus.core.IEntitySet;
import org.onexus.core.IEntityTable;
import org.onexus.core.IResourceManager;
import org.onexus.core.query.Query;
import org.onexus.core.resources.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.Driver;

public class CollectionStore implements ICollectionStore {
    private static final Logger LOGGER = LoggerFactory
	    .getLogger(CollectionStore.class);

    private static final int INSERT_BATCH_SIZE = 400;
    private static final int INSERT_BUFFER_SIZE = INSERT_BATCH_SIZE * 400;

    private String uri;

    private String username;

    private String password;
    
    private String status;

    private IResourceManager resourceManager;

    private DataSource dataSource;

    private Map<String, MysqlCollectionDDL> ddls;

    public CollectionStore() {
	super();
    }
    
    public void init() {
	
	if (!STATUS_ENABLED.equals(getStatus())) {
	    return;
	}
	
	LOGGER.debug("Connecting to {} as {}.", uri, username);

	try {
	    Class.forName(Driver.class.getName());
	} catch (Exception e) {
	    LOGGER.error("Exception: " + e.getMessage());
	}

	// Initialize the DataSource with a connection pool
	ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
		uri, username, password);
	ObjectPool connectionPool = new GenericObjectPool(null,
		GenericObjectPool.DEFAULT_MAX_ACTIVE,
		GenericObjectPool.WHEN_EXHAUSTED_GROW,
		GenericObjectPool.DEFAULT_MAX_WAIT);
	@SuppressWarnings("unused")
	PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
		connectionFactory, connectionPool, null, null, false, true);
	dataSource = new PoolingDataSource(connectionPool);

	// Create the store properties table if it's necessary
	try {
	    MysqlUtils.createSystemPropertiesTable(dataSource.getConnection());
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}

	this.ddls = new HashMap<String, MysqlCollectionDDL>();
    }

    @Override
    public boolean isRegistered(String collectionURI) {
	String tableName = null;
	Connection conn = null;
	try {
	    conn = dataSource.getConnection();
	    tableName = MysqlUtils.loadProperty(conn, collectionURI);
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
	ddls.put(collectionURI, new MysqlCollectionDDL(
		    getCollection(collectionURI)));
	
	MysqlCollectionDDL ddl = getDDL(collectionURI);
	Connection conn = null;
	try {

	    conn = dataSource.getConnection();

	    MysqlUtils.execute(conn, ddl.getDropTable());
	    MysqlUtils.execute(conn, ddl.getCreateTable());
	    MysqlUtils.createSystemPropertiesTable(conn, false);
	    MysqlUtils.saveProperty(conn, collectionURI, ddl.getTableName() );

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

	    tableName = MysqlUtils.loadProperty(conn, collectionURI);
	    MysqlUtils.removeProperty(conn, collectionURI);
	    MysqlUtils.execute(conn, "DROP TABLE `"+tableName+"`");	    

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
	    return MysqlUtils.loadPropertyKeys(conn);
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
	LOGGER.debug("Loading query {}", query);

	try {

	    MysqlEntityTable entitySet = new MysqlEntityTable(this, query,
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

	MysqlCollectionDDL ddl = getDDL(entitySet.getCollection().getURI());

	StringBuilder sql = new StringBuilder(INSERT_BUFFER_SIZE);

	MysqlUtils.openInsert(sql, ddl);

	Connection conn = null;
	try {
	    int batch = 0;
	    conn = dataSource.getConnection();
	    while (entitySet.next()) {
		if (batch > 0) {
		    sql.append(",");
		}
		MysqlUtils.addValues(sql, ddl, entitySet);
		if (batch < INSERT_BATCH_SIZE) {
		    batch++;
		} else {
		    MysqlUtils.execute(conn, sql.toString());
		    sql = new StringBuilder(INSERT_BUFFER_SIZE);
		    MysqlUtils.openInsert(sql, ddl);
		    batch = 0;
		}
	    }

	    if (batch > 0) {
		MysqlUtils.execute(conn, sql.toString());
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

	MysqlCollectionDDL ddl = getDDL(entity.getCollection().getURI());

	// Open insert
	MysqlUtils.openInsert(sql, ddl);
	MysqlUtils.addValues(sql, ddl, entity);

	Connection conn = null;
	try {
	    conn = dataSource.getConnection();
	    MysqlUtils.execute(conn, sql.toString());
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

    MysqlCollectionDDL getDDL(String collectionURI) {
	if (!ddls.containsKey(collectionURI)) {
	    ddls.put(collectionURI, new MysqlCollectionDDL(
		    getCollection(collectionURI)));
	}

	return ddls.get(collectionURI);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    

}
