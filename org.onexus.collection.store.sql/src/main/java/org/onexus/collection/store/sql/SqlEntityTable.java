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
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.data.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlEntityTable implements IEntityTable {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(SqlEntityTable.class);

    private Query query;
    private Task task;

    private SqlQuery mysqlQuery;

    private SqlCollectionStore store;

    private Connection dataConn = null;
    private Statement dataSt = null;
    private ResultSet dataRS = null;
    private Long size = null;

    public SqlEntityTable(SqlCollectionStore store, Query query,
                          Connection conn) {

        this.store = store;
        this.query = query;
        this.mysqlQuery = store.newSqlQuery(query);
        this.dataConn = conn;

    }

    private void initDataRS() {
        String sql = null;
        try {
            dataSt = store.createReadStatement(dataConn);

            sql = mysqlQuery.toSelectSQL();
            LOGGER.debug(sql);
            dataRS = dataSt.executeQuery(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean next() {

        if (dataRS == null) {
            initDataRS();
        }

        try {
            boolean hasNext = (dataRS == null ? false : dataRS.next());

            if (!hasNext) {
                close();
                return hasNext;
            }

            return hasNext;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public long size() {

        if (size == null) {

            String sql = null;
            try {
                // Count all
                Statement dataSt = dataConn.createStatement();
                sql = mysqlQuery.toCountSQL();
                LOGGER.debug(sql);
                ResultSet dataRS = dataSt.executeQuery(sql);
                if (dataRS.next()) {
                    size = dataRS.getLong("size");
                } else {
                    size = Long.valueOf(0);
                }
                dataRS.close();
                dataSt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        return size;
    }

    @Override
    public void close() {
        try {
            if (dataConn != null && !dataConn.isClosed())
                dataConn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public IEntity getEntity(String collectionURI) {

        collectionURI = QueryUtils.getAbsoluteCollectionUri(query, collectionURI);

        Collection collection = store.getCollection(collectionURI);
        SqlEntity entity = new SqlEntity(collection);
        store.getSqlDialect().loadEntity(query, store.getDDL(collectionURI), dataRS, entity,
                QueryUtils.getCollectionAlias(query, collectionURI));

        return entity;
    }

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public void setTask(Task taskStatus) {
        this.task = taskStatus;
    }

}
