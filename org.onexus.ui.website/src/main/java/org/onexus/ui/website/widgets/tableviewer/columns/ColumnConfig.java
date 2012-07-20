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
package org.onexus.ui.website.widgets.tableviewer.columns;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.onexus.collection.api.IEntityTable;
import org.onexus.resource.api.IResourceManager;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.resource.api.utils.ResourceUtils;
import org.onexus.ui.core.OnexusWebApplication;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecoratorManager;
import org.onexus.ui.website.widgets.tableviewer.headers.CollectionHeader;
import org.onexus.ui.website.widgets.tableviewer.headers.FieldHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@XStreamAlias("column")
public class ColumnConfig implements IColumnConfig {
    private final static Logger LOGGER = LoggerFactory.getLogger(ColumnConfig.class);

    private String collection;

    private String fields;

    private String decorator;

    private String visible;

    @Inject
    public IResourceManager resourceManager;

    @Inject
    public IDecoratorManager decoratorManager;

    public ColumnConfig(String collectionId) {
        this(collectionId, null, null);
    }

    public ColumnConfig(String collectionId, String fields) {
        this(collectionId, fields, null);
    }

    public ColumnConfig(String collectionURI, String fields, String decorator) {
        super();

        this.collection = collectionURI;
        this.fields = fields;
        this.decorator = decorator;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collectionURI) {
        this.collection = collectionURI;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getDecorator() {
        return decorator;
    }

    public void setDecorator(String decorator) {
        this.decorator = decorator;
    }

    @Override
    public void addColumns(List<IColumn<IEntityTable, String>> columns, String parentURI) {

        String collectionURI = ResourceUtils.getAbsoluteURI(parentURI, collection);
        Collection collection = getResourceManager().load(Collection.class, collectionURI);

        if (collection != null) {
            List<String> fields = getFields(collection);
            for (String fieldId : fields) {
                Field field = collection.getField(fieldId);
                columns.add(new CollectionColumn(collectionURI, new FieldHeader(collection, field, new CollectionHeader(
                        collection)), decoratorManager.getDecorator(decorator, collection, field)));
            }
        }
    }

    private List<String> getFields(Collection collection) {
        List<String> fields;

        if (this.fields == null) {
            fields = new ArrayList<String>();
            for (Field field : collection.getFields()) {
                fields.add(field.getId());
            }
        } else {
            fields = new ArrayList<String>();

            for (String fieldName : this.fields.split(",")) {
                if (fieldName.trim().startsWith("*{")) {
                    String regExp = fieldName.trim().substring(2);
                    regExp = regExp.substring(0, regExp.lastIndexOf("}"));
                    Pattern pattern = Pattern.compile(regExp);
                    for (Field field : collection.getFields()) {
                        if (pattern.matcher(field.getId()).matches()) {
                            fields.add(field.getId());
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
                        fields.add(field.getId());
                    }
                }

            }
        }
        return fields;
    }

    @Override
    public void buildQuery(Query query) {

        String collectionURI = ResourceUtils.getAbsoluteURI(query.getOn(), collection);
        String columnAlias = QueryUtils.newCollectionAlias(query, collectionURI);
        Collection collection = getResourceManager().load(Collection.class, collectionURI);

        if (collection != null) {
            query.addSelect(columnAlias, getFields(collection));
        }

        // The primary key fields must always be present
        List<String> currentFields = query.getSelect().get(columnAlias);
        for (Field field : collection.getFields()) {
            if (field.isPrimaryKey() != null && field.isPrimaryKey() && !currentFields.contains(field.getId())) {
                currentFields.add(field.getId());
            }
        }


    }

    private IResourceManager getResourceManager() {
        if (resourceManager == null) {
            OnexusWebApplication.inject(this);
        }

        return resourceManager;
    }

    @Override
    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }
}
