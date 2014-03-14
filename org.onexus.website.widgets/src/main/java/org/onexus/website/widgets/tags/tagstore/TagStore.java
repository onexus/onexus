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
package org.onexus.website.widgets.tags.tagstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TagStore implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagStore.class);

    private String namespace;
    private String tableName;
    private DataSource dataSource;

    public TagStore(String namespace, DataSource dataSource) {
        super();

        this.namespace = namespace;
        this.tableName = namespace.trim().replaceAll("[^a-zA-Z0-9]", "_");
        this.dataSource = dataSource;

        createNamespaceTable();

    }

    public String getNamespace() {
        return namespace;
    }

    public Collection<String> getTagKeys() {

        String sql = "SELECT DISTINCT `tagKey` AS `tagKey` FROM `" + tableName + "`";

        List<String> tagKeys;

        try {
            tagKeys = executeCollection(sql, "tagKey");
        } catch (SQLException e) {
            String msg = "Error on getTagKeys() <" + sql + ">";
            LOGGER.error(msg);
            throw new RuntimeException(msg, e);
        }

        return tagKeys;
    }

    public Collection<String> getTagValues(String tagKey) {

        String sql = "SELECT `tagValue` FROM `" + tableName + "` WHERE `tagKey` = '" + tagKey + "'";

        List<String> tagValues;
        try {
            tagValues = executeCollection(sql, "tagValue");
        } catch (SQLException e) {
            String msg = "Error on getTagValues('" + tagKey + "') <" + sql + ">";
            LOGGER.error(msg);
            throw new RuntimeException(msg, e);
        }

        return tagValues;

    }

    public void putTagValue(String tagKey, String tagValue) {

        Collection<String> values = getTagValues(tagKey);

        // Skip already tagged values
        if (values.contains(tagValue)) {
            return;
        }

        String sql = "INSERT INTO `" + tableName + "` (`tagKey`, `tagValue`) VALUES ('" + tagKey + "', '" + tagValue
                + "')";

        try {
            executeSQL(sql);
        } catch (SQLException e) {
            String msg = "Error on putTagValue('" + tagKey + "', '" + tagValue + "') <" + sql + ">";
            LOGGER.error(msg);
            throw new RuntimeException(msg, e);
        }

    }

    public void removeTag(String tagKey) {

        String sql = "DELETE FROM `" + tableName + "` WHERE `tagKey` = '" + tagKey + "'";

        try {
            executeSQL(sql);
        } catch (SQLException e) {
            String msg = "Error on removeTag('" + tagKey + "') <" + sql + ">";
            LOGGER.error(msg);
            throw new RuntimeException(msg, e);
        }

    }

    public void putTagKey(String tagKey) {

        String sql = "INSERT INTO `" + tableName + "` (`tagKey`, `tagValue`) VALUES ('" + tagKey + "', NULL)";

        try {
            executeSQL(sql);
        } catch (SQLException e) {
            String msg = "Error on putTagKey('" + tagKey + "') <" + sql + ">";
            LOGGER.error(msg);
            throw new RuntimeException(msg, e);
        }

    }

    public List<String> getTagKeysByValue(String tagValue) {

        String sql = "SELECT `tagKey` FROM `" + tableName + "` WHERE `tagValue` = '" + tagValue + "'";

        List<String> tagKeys;
        try {
            tagKeys = executeCollection(sql, "tagKey");
        } catch (SQLException e) {
            String msg = "Error on getTagKeysByValues('" + tagValue + "') <" + sql + ">";
            LOGGER.error(msg);
            throw new RuntimeException(msg, e);
        }

        return tagKeys;
    }

    public void removeTagValue(String tagKey, String tagValue) {

        String sql = "DELETE FROM `" + tableName + "` WHERE `tagKey` = '" + tagKey + "' AND `tagValue` = '"
                + tagValue + "'";

        try {
            executeSQL(sql);
        } catch (SQLException e) {
            String msg = "Error on removeTagValue('" + tagKey + "', '" + tagValue + "') <" + sql + ">";
            LOGGER.error(msg);
            throw new RuntimeException(msg, e);
        }

    }

    private void createNamespaceTable() {

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS `")
                .append(tableName)
                .append("` ( `tagKey` varchar(128) NOT NULL, `tagValue` varchar(128) DEFAULT NULL, UNIQUE (`tagKey`, `tagValue`))");

        try {
            executeSQL(sql.toString());
        } catch (SQLException e) {
            String msg = "Error creating table for namespace '" + namespace + "' with SQL <" + sql.toString() + ">";
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    private void executeSQL(String sql) throws SQLException {

        Connection conn = dataSource.getConnection();
        Statement st = conn.createStatement();
        st.execute(sql);
        st.close();
        conn.close();

    }

    private List<String> executeCollection(String sql, String fieldName) throws SQLException {

        Connection conn = dataSource.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        List<String> result = new ArrayList<String>();
        while (rs.next()) {
            String value = rs.getString(fieldName);
            if (value != null) {
                result.add(value);
            }
        }

        rs.close();
        st.close();
        conn.close();

        return result;
    }

}
