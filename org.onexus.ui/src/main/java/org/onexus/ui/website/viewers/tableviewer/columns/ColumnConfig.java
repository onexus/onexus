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
package org.onexus.ui.website.viewers.tableviewer.columns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.onexus.core.IEntityTable;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.core.utils.ResourceTools;
import org.onexus.ui.OnexusWebSession;
import org.onexus.ui.website.decorators.DecoratorFactory;
import org.onexus.ui.website.viewers.tableviewer.headers.CollectionHeader;
import org.onexus.ui.website.viewers.tableviewer.headers.FieldHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("column")
public class ColumnConfig implements IColumnConfig {
    private final static Logger LOGGER = LoggerFactory.getLogger(ColumnConfig.class);

    private String collection;

    private String fieldNames;

    private String decorator;

    public ColumnConfig() {
	super();
    }

    public ColumnConfig(String collectionId) {
	this(collectionId, null, null);
    }

    public ColumnConfig(String collectionId, String fieldNames) {
	this(collectionId, fieldNames, null);
    }

    public ColumnConfig(String collectionURI, String fieldNames, String decorator) {
	super();
	this.collection = collectionURI;
	this.fieldNames = fieldNames;
	this.decorator = decorator;
    }

    public String getCollection() {
	return collection;
    }

    public void setCollection(String collectionURI) {
	this.collection = collectionURI;
    }

    public String getFieldNames() {
	return fieldNames;
    }

    public void setFieldNames(String fieldNames) {
	this.fieldNames = fieldNames;
    }

    public String getDecorator() {
	return decorator;
    }

    public void setDecorator(String decorator) {
	this.decorator = decorator;
    }

    @Override
    public void addColumns(List<IColumn<IEntityTable>> columns, String releaseURI) {

	String collectionURI = ResourceTools.getAbsoluteURI(releaseURI, collection);
	Collection collection = OnexusWebSession.get().getResourceManager().load(Collection.class, collectionURI);

	if (collection != null) {
	    List<Field> fields = getFields(collection);
	    for (Field field : fields) {
		columns.add(new CollectionTrack(collectionURI, new FieldHeader(collection, field, new CollectionHeader(
			collection)), DecoratorFactory.getDecorator(decorator, collection, field)));
	    }
	}
    }

    @Override
    public void addExportColumns(List<ExportColumn> columns, String releaseURI) {

	String collectionURI = ResourceTools.getAbsoluteURI(releaseURI, collection);
	Collection collection = OnexusWebSession.get().getResourceManager().load(Collection.class, collectionURI);

	if (collection != null) {
	    List<Field> fields = getFields(collection);
	    StringBuilder fieldNames = new StringBuilder();
	    Iterator<Field> it = fields.iterator();

	    while (it.hasNext()) {
		fieldNames.append(it.next().getName());
		if (it.hasNext()) {
		    fieldNames.append(",");
		}
	    }

	    columns.add(new ExportColumn(this.collection, fieldNames.toString()));
	}

    }

    private List<Field> getFields(Collection collection) {
	List<Field> fields;

	if (fieldNames == null) {
	    fields = new ArrayList<Field>(collection.getFields());
	} else {
	    fields = new ArrayList<Field>();

	    for (String fieldName : fieldNames.split(",")) {
		if (fieldName.trim().startsWith("*{")) {
		    String regExp = fieldName.trim().substring(2);
		    regExp = regExp.substring(0, regExp.lastIndexOf("}"));
		    Pattern pattern = Pattern.compile(regExp);
		    for (Field field : collection.getFields()) {
			if (pattern.matcher(field.getName()).matches()) {
			    fields.add(field);
			}
		    }
		} else {

		    Field field = null;

		    if (collection != null && fieldName != null) {
			field = collection.getField(fieldName.trim());
		    }

		    if (field == null) {
			LOGGER.warn("Field '" + fieldName + "' not found on collection '" + collection.getURI() + "'.");
		    } else {
			fields.add(field);
		    }
		}

	    }
	}
	return fields;
    }

    @Override
    public String[] getQueryCollections(String releaseURI) {
	return new String[] { collection };
    }

    public static class ExportColumn implements Serializable {

	private String collectionURI;
	private String fieldNames;

	public ExportColumn(String collectionURI, String fieldNames) {
	    super();
	    this.collectionURI = collectionURI;
	    this.fieldNames = fieldNames;
	}

	public ExportColumn(String column) {

	    int separator = column.indexOf('|');

	    this.collectionURI = column.substring(0, separator);
	    this.fieldNames = column.substring(separator + 1);

	}

	public String getCollectionURI() {
	    return collectionURI;
	}

	public String[] getFieldNames() {
	    return fieldNames.split(",");
	}

	@Override
	public String toString() {
	    return collectionURI + "|" + fieldNames;
	}

    }

}
