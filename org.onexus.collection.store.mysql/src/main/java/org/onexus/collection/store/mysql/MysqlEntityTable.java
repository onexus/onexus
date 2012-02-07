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

import org.onexus.core.IEntity;
import org.onexus.core.IEntityTable;
import org.onexus.core.TaskStatus;
import org.onexus.core.query.Query;
import org.onexus.core.resources.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlEntityTable implements IEntityTable {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(MysqlEntityTable.class);

    private Query query;
    private TaskStatus taskStatus;

    private MysqlQuery mysqlQuery;

    private CollectionStore store;

    private Connection dataConn = null;
    private Statement dataSt = null;
    private ResultSet dataRS = null;
    private Long size = null;

    public MysqlEntityTable(CollectionStore store, Query query,
                            Connection conn) {

        this.store = store;
        this.query = query;
        this.mysqlQuery = new MysqlQuery(store, query);
        this.dataConn = conn;
    }

    private void initDataRS() {
        String sql = null;
        try {
            // Start the select
            dataSt = dataConn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);

            dataSt.setFetchDirection(ResultSet.FETCH_FORWARD);
            dataSt.setFetchSize(Integer.MIN_VALUE);

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

        collectionURI = mysqlQuery.getAbsoluteCollectionURI(collectionURI);

        Collection collection = store.getCollection(collectionURI);
        MysqlEntity entity = new MysqlEntity(collection);
        MysqlUtils.loadEntity(store.getDDL(collectionURI), dataRS, entity,
                mysqlQuery.getCollectionAlias(collectionURI));

        return entity;
    }

    @Override
    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    @Override
    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

}
