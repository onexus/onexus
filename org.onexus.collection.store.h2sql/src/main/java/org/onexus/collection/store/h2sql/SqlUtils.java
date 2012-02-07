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
package org.onexus.collection.store.h2sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.onexus.collection.store.h2sql.H2CollectionDDL.ColumnInfo;
import org.onexus.collection.store.h2sql.adapters.SQLAdapter;
import org.onexus.core.IEntity;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.core.resources.Link;
import org.onexus.core.utils.ResourceTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(SqlUtils.class);

    static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS system_properties (name varchar(128), version VARCHAR(128), primary key(name))";
    static final String DELETE_OBJECT_SQL = "DELETE FROM system_properties WHERE name = ?";
    static final String INSERT_OBJECT_SQL = "INSERT INTO system_properties (name, version) VALUES (?, ?)";
    static final String SELECT_OBJECT_SQL = "SELECT version FROM system_properties WHERE name = ?";
    static final String SELECT_PROPERTIES_KEYS = "SELECT name FROM system_properties";

    public static void createSystemPropertiesTable(Connection conn) throws SQLException {
	Statement st = null;
	try {
	    st = conn.createStatement();
	    st.execute(CREATE_TABLE_SQL);
	} finally {
	    if (st != null)
		st.close();
	    if (conn != null)
		conn.close();
	}
    }

    public static List<String> loadPropertyKeys(Connection conn) throws Exception {
	List<String> keys = new ArrayList<String>();
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	try {
	    pstmt = conn.prepareStatement(SELECT_PROPERTIES_KEYS);
	    rs = pstmt.executeQuery();

	    while (rs.next()) {
		keys.add(rs.getString("name"));
	    }

	} finally {
	    if (rs != null)
		rs.close();
	    if (pstmt != null)
		pstmt.close();

	}
	return keys;
    }

    public static void saveProperty(Connection conn, String name, String version) throws Exception {
	PreparedStatement pstmt = null;
	try {
	    pstmt = conn.prepareStatement(DELETE_OBJECT_SQL);
	    pstmt.setString(1, name);
	    pstmt.execute();
	    pstmt.close();

	    pstmt = conn.prepareStatement(INSERT_OBJECT_SQL);
	    pstmt.setString(1, name);
	    pstmt.setString(2, version);
	    pstmt.executeUpdate();
	} finally {
	    if (pstmt != null)
		pstmt.close();
	}
    }

    public static void removeProperty(Connection conn, String name) throws Exception {
	PreparedStatement pstmt = null;
	try {
	    pstmt = conn.prepareStatement(DELETE_OBJECT_SQL);
	    pstmt.setString(1, name);
	    pstmt.execute();
	    pstmt.close();
	} finally {
	    if (pstmt != null)
		pstmt.close();
	}
    }

    public static String loadProperty(Connection conn, String name) throws Exception {
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	try {
	    pstmt = conn.prepareStatement(SELECT_OBJECT_SQL);

	    pstmt.setString(1, name);
	    rs = pstmt.executeQuery();

	    if (!rs.next()) {
		return null;
	    }

	    String object = rs.getString(1);
	    return object;
	} finally {
	    if (pstmt != null)
		pstmt.close();
	    if (rs != null)
		rs.close();
	}
    }

    public static String removeNonValidChars(String id) {
	return id.toLowerCase().trim().replaceAll("[^(a-z|0-9)]", "_");
    }

    public static void execute(Connection conn, String query) throws SQLException {
	Statement st = null;
	try {
	    LOGGER.debug("Execute: " + query);
	    st = conn.createStatement();
	    st.execute(query);
	} finally {
	    if (st != null)
		st.close();
	}
    }

    public static void openInsert(StringBuilder sql, H2CollectionDDL ddl) {

	// Open insert
	sql.append("INSERT INTO `").append(ddl.getTableName()).append("` (`");

	// Column names
	Iterator<ColumnInfo> it = ddl.getColumnInfos().iterator();
	while (it.hasNext()) {
	    sql.append(it.next().getColumnName());
	    if (it.hasNext()) {
		sql.append("`, `");
	    }
	}

	sql.append("`) VALUES ");
    }

    public static void addValues(StringBuilder sql, H2CollectionDDL ddl, IEntity entity) {
	sql.append("(");
	Iterator<ColumnInfo> itColumns = ddl.getColumnInfos().iterator();
	while (itColumns.hasNext()) {
	    ColumnInfo column = itColumns.next();
	    Object value = entity.get(column.getField().getName());
	    if (value == null) {

		// Check if it's a key field (MySQL don't accept NULL values on
		// PRIMARY KEY fields)
		if (column.getField().isPrimaryKey()) {
		    if (column.getField().getDataType().equals(String.class)) {
			sql.append("'NONE'");
		    } else {
			sql.append("0");
		    }
		} else {
		    sql.append("NULL");
		}
	    } else {
		SQLAdapter adapter = column.getAdapter();

		try {
		    adapter.append(sql, value);
		} catch (Exception e) {
		    throw new RuntimeException(e);
		}

	    }

	    if (itColumns.hasNext()) {
		sql.append(",");
	    }
	}
	sql.append(")");
    }

    public static void flatCollection(StringBuilder builder, java.util.Collection<?> collection, String separator) {
	Iterator<?> it = collection.iterator();
	while (it.hasNext()) {
	    builder.append(it.next());
	    if (it.hasNext()) {
		builder.append(String.valueOf(separator));
	    }
	}
    }

    public static H2Entity loadEntity(H2CollectionDDL ddl, ResultSet rs, H2Entity dstObj, String columnPrefix) {

	for (ColumnInfo column : ddl.getColumnInfos()) {
	    String columnName = column.getColumnName();
	    SQLAdapter adapter = column.getAdapter();
	    try {
		String sqlColumnName = (columnPrefix == null ? columnName : columnPrefix + "_" + columnName);
		dstObj.put(column.getField().getName(), adapter.extract(rs, sqlColumnName));
	    } catch (Exception e) {
		LOGGER.error("Error loading column '" + column + "'");
		try {
		    ResultSetMetaData m = rs.getMetaData();

		    List<String> columnNames = new ArrayList<String>();
		    for (int i = 1; i <= m.getColumnCount(); i++) {
			columnNames.add(m.getColumnName(i));
		    }
		    LOGGER.debug("Column names: " + columnNames + "'");

		} catch (SQLException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}

	    }
	}

	return dstObj;
    }

    public static List<FieldLink> getLinkFields(String releaseURI, Collection a, Collection b) {
	List<FieldLink> fieldLinks = getLeftLinkFields(releaseURI, a, b);

	if (fieldLinks.isEmpty()) {
	    fieldLinks = getLeftLinkFields(releaseURI, b, a);
	}

	return fieldLinks;
    }

    public static List<FieldLink> getLeftLinkFields(String releaseURI, Collection a, Collection b) {

	List<FieldLink> fieldLinks = new ArrayList<FieldLink>();

	List<Link> linksA = a.getLinks();
	List<Link> linksB = b.getLinks();

	// Case 1: A has a direct link to B

	for (Link link : linksA) {
	    if (link.getCollectionURI().equals(b.getURI())) {
		for (String field : link.getFieldNames()) {
		    fieldLinks.add(new FieldLink(a.getURI(), Link.getFromFieldName(field), b.getURI(), Link
			    .getToFieldName(field)));
		}
		return fieldLinks;
	    }
	}

	// Case 2: All the primary fields of A has a link to collections linked
	// by B
	for (Field field : a.getFields()) {
	    if (field.isPrimaryKey()) {

		// The links that link to this field
		List<Link> keyLinks = new ArrayList<Link>();
		for (Link link : linksA) {
		    for (String fieldName : link.getFieldNames()) {
			String fromField = Link.getFromFieldName(fieldName);
			if (fromField.equals(field.getName())) {
			    keyLinks.add(link);
			}
		    }
		}

		// Look if there is any match with B links
		for (Link linkB : linksB) {
		    for (Link linkA : keyLinks) {
			String linkBCollection = ResourceTools.getAbsoluteURI(releaseURI, linkB.getCollectionURI());
			String linkACollection = ResourceTools.getAbsoluteURI(releaseURI, linkA.getCollectionURI());

			if (linkBCollection.equals(linkACollection)) {

			    // Try to match the field links
			    for (String fieldLinkA : linkA.getFieldNames()) {
				String toFieldA = Link.getToFieldName(fieldLinkA);

				for (String fieldLinkB : linkB.getFieldNames()) {
				    String toFieldB = Link.getToFieldName(fieldLinkB);
				    if (toFieldA.equals(toFieldB)) {
					String fromFieldA = Link.getFromFieldName(fieldLinkA);
					String fromFieldB = Link.getFromFieldName(fieldLinkB);
					fieldLinks.add(new FieldLink(a.getURI(), fromFieldA, b.getURI(), fromFieldB));
				    }
				}
			    }

			}
		    }
		}

	    }
	}

	return fieldLinks;

    }

    public static Link getLinkByField(Collection collection, String fieldName) {
	if (collection.getLinks() != null) {
	    for (Link link : collection.getLinks()) {
		for (String fieldLink : link.getFieldNames()) {
		    String fromField = Link.getFromFieldName(fieldLink);
		    if (fieldName.equals(fromField)) {
			return link;
		    }
		}
	    }
	}
	return null;
    }

}
