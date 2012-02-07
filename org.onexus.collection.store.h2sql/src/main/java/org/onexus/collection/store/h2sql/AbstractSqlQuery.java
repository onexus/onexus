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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onexus.core.query.FixedEntity;
import org.onexus.core.query.Query;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.core.resources.Link;
import org.onexus.core.utils.ResourceTools;

public abstract class AbstractSqlQuery {

    private Query query;

    private transient int nextAliasPrefix = 0;
    private transient Map<String, String> collectionsAlias = new HashMap<String, String>();
    private transient Map<String, String> fixedEntities = new HashMap<String, String>();

    public AbstractSqlQuery(Query query) {
	super();
	this.query = query;
    }

    public Query getQuery() {
	return query;
    }

    private void addQuery() {
	addFields();
	addFrom();
	addWhere();
	addOrder();
	addLimit();
    }

    protected abstract void addFields();

    protected abstract void addFrom();

    protected abstract void addWhere();

    protected abstract void addOrder();

    protected abstract void addLimit();

    protected void init() {
	for (String collectionURI : query.getCollections()) {
	    addFixedEntities(collectionURI);
	}

	// Main collection
	addFixedEntities(getQuery().getMainCollection());

	for (FixedEntity fe : query.getFixedEntities()) {
	    Collection eCol = getCollection(fe.getCollectionURI());
	    for (Map.Entry<String, String> entry : getLinkValues(eCol, fe.getEntityId()).entrySet()) {
		this.fixedEntities.put(getAbsoluteCollectionURI(fe.getCollectionURI()) + ":" + entry.getKey(), "\""
			+ entry.getValue() + "\"");
	    }
	}

	addQuery();
    }

    private void addFixedEntities(String collectionURI) {
	Collection collection = getCollection(collectionURI);

	for (Field keyField : collection.getFields()) {
	    if (!keyField.isPrimaryKey())
		continue;

	    String linkCollection = collection.getURI();
	    Link link = SqlUtils.getLinkByField(collection, keyField.getName());
	    if (link != null) {
		linkCollection = link.getCollectionURI();
	    }
	    this.fixedEntities.put(linkCollection + ":" + keyField.getName(), "`" + getCollectionAlias(collectionURI)
		    + "`.`" + keyField.getName() + "`");
	}

	if (collection.getLinks() != null) {
	    for (Link link : collection.getLinks()) {
		for (String fieldLink : link.getFieldNames()) {
		    String fromField = Link.getFromFieldName(fieldLink);
		    this.fixedEntities.put(getAbsoluteCollectionURI(link.getCollectionURI()) + ":" + fromField, "`"
			    + getCollectionAlias(collectionURI) + "`.`" + fromField + "`");
		}
	    }
	}
    }

    public String getAbsoluteCollectionURI(String collectionURI) {
	return ResourceTools.getAbsoluteURI(getQuery().getMainNamespace(), collectionURI);
    }

    protected abstract Collection getCollection(String collectionURI);

    protected String getCollectionAlias(String collectionURI) {
	collectionURI = getAbsoluteCollectionURI(collectionURI);
	if (!collectionsAlias.containsKey(collectionURI)) {
	    String alias = "c" + Integer.toString(nextAliasPrefix);
	    collectionsAlias.put(collectionURI, alias);
	    nextAliasPrefix++;
	}
	return collectionsAlias.get(collectionURI);
    }

    protected String getFixedValue(String collectionURI, String fieldName) {
	return this.fixedEntities.get(getAbsoluteCollectionURI(collectionURI) + ":" + fieldName);
    }

    private static Map<String, String> getLinkValues(Collection collection, String entityId) {
	String[] ids = entityId.split("\t");

	List<String> collectionKeys = new ArrayList<String>();
	for (Field field : collection.getFields()) {
	    if (field.isPrimaryKey()) {
		collectionKeys.add(field.getName());
	    }
	}

	assert ids.length == collectionKeys.size() : "The total number of primary key values not match.";

	Map<String, String> keys = new HashMap<String, String>();
	for (int i = 0; i < collectionKeys.size(); i++) {
	    keys.put(collectionKeys.get(i), ids[i]);
	}

	return keys;
    }

}
