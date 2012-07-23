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
package org.onexus.ui.website.widgets.tags.tagstore;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.h2.Driver;
import org.onexus.ui.api.OnexusWebSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TagStoreManager implements ITagStoreManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagStoreManager.class);

    private String database = "onexus-h2-tags";
    private String username = "sa";
    private String password = "";

    private Map<String, TagStore> tagStores;
    private DataSource dataSource;

    public TagStoreManager() {
        super();
    }

    public void init() {
        LOGGER.debug("Connecting to '{}' as '{}'.", database, username);

        Driver.load();

        // Initialize the DataSource with a connection pool
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
                "jdbc:h2:" + database, username, password);
        ObjectPool connectionPool = new GenericObjectPool(null,
                GenericObjectPool.DEFAULT_MAX_ACTIVE,
                GenericObjectPool.WHEN_EXHAUSTED_GROW,
                GenericObjectPool.DEFAULT_MAX_WAIT);
        @SuppressWarnings("unused")
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
                connectionFactory, connectionPool, null, null, false, true);
        dataSource = new PoolingDataSource(connectionPool);


        this.tagStores = Collections.synchronizedMap(new HashMap<String, TagStore>());

    }


    @Override
    public TagStore getUserStore(String namespace) {

        String userToken = OnexusWebSession.get().getUserToken();

        return get(userToken + "_" + namespace);

    }


    private TagStore get(String namespace) {

        if (!tagStores.containsKey(namespace)) {
            tagStores.put(namespace, new TagStore(namespace, dataSource));
        }

        return tagStores.get(namespace);
    }

    public void stop() {
        try {
            Connection conn = dataSource.getConnection();
            Statement stat = conn.createStatement();

            stat.execute("SHUTDOWN");
            stat.close();

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


}
