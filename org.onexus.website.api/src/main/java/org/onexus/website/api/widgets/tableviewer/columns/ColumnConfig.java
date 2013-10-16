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
package org.onexus.website.api.widgets.tableviewer.columns;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.util.string.Strings;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Property;
import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.widgets.tableviewer.decorators.IDecorator;
import org.onexus.website.api.widgets.tableviewer.decorators.IDecoratorManager;
import org.onexus.website.api.widgets.tableviewer.headers.CollectionHeader;
import org.onexus.website.api.widgets.tableviewer.headers.FieldHeader;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@ResourceAlias("column")
public class ColumnConfig implements IColumnConfig {

    private String label;

    private String title;

    private ORI collection;

    private String fields;

    private String template;

    private String decorator;

    private String actions;

    private String visible;

    private String sortable;

    private String filter;

    private Integer length;

    public ColumnConfig() {
        super();
    }

    public ColumnConfig(ORI collectionId) {
        this(collectionId, null, null);
    }

    public ColumnConfig(ORI collectionId, String fields) {
        this(collectionId, fields, null);
    }

    public ColumnConfig(ORI collectionURI, String fields, String decorator) {
        super();

        this.collection = collectionURI;
        this.fields = fields;
        this.decorator = decorator;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ORI getCollection() {
        return collection;
    }

    public void setCollection(ORI collectionURI) {
        this.collection = collectionURI;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getDecorator() {
        return decorator;
    }

    public void setDecorator(String decorator) {
        this.decorator = decorator;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    @Override
    public String getSortable() {
        return sortable;
    }

    public void setSortable(String sortable) {
        this.sortable = sortable;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Override
    public void addColumns(List<IColumn<IEntityTable, String>> columns, ORI parentURI, boolean sortable) {

        ORI collectionURI = collection.toAbsolute(parentURI);
        Collection collection = getResourceManager().load(Collection.class, collectionURI);

        if (collection != null) {
            List<Field> fields = getFields(collection, parentURI);

            if (Strings.isEmpty(template)) {
                for (Field field : fields) {

                    if (length != null) {
                        if (field.getProperty("MAX_LENGTH") != null) {
                            for (Property property : field.getProperties()) {
                                if (property.getKey().equalsIgnoreCase("MAX_LENGTH")) {
                                    property.setValue(length.toString());
                                    break;
                                }
                            }
                        } else {
                            if (field.getProperties() == null) {
                                field.setProperties(new ArrayList<Property>());
                            }

                            field.getProperties().add(new Property("MAX_LENGTH", length.toString()));
                        }
                    }

                    IDecorator decoratorImpl = getDecoratorManager().getDecorator(decorator, collection, field);
                    List<IDecorator> actionsImpl = createActions(collection, field);

                    columns.add(new CollectionColumn(collectionURI, new FieldHeader(label, title, collection, field, new CollectionHeader(collection), filter, sortable), decoratorImpl, actionsImpl));
                }
            } else {
                Field field = fields.get(0);

                IDecorator decoratorImpl = getDecoratorManager().getDecorator(decorator, collection, field);
                List<IDecorator> actionsImpl = createActions(collection, field);
                decoratorImpl.setTemplate(template);

                columns.add(new CollectionColumn(collectionURI, new FieldHeader(label, title, collection, field, new CollectionHeader(collection), filter, sortable), decoratorImpl, actionsImpl));
            }
        }
    }

    private List<IDecorator> createActions(Collection collection, Field field) {

        if (actions == null) {
            return Collections.emptyList();
        }

        List<IDecorator> actionsImpl = new ArrayList<IDecorator>();
        for (String action : actions.split("::")) {
            IDecorator actionImpl = getDecoratorManager().getDecorator(action.trim(), collection, field);
            if (actionImpl != null) {
                actionsImpl.add(actionImpl);
            }
        }

        return actionsImpl;

    }

    protected List<Field> getFields(Collection collection, ORI parentOri) {
        List<Field> fields;

        if (this.fields == null) {
            fields = new ArrayList<Field>();
            for (Field field : collection.getFields()) {
                fields.add(field);
            }
        } else {
            fields = new ArrayList<Field>();

            for (String fieldName : this.fields.split(",")) {
                if (fieldName.trim().startsWith("*{")) {
                    String regExp = fieldName.trim().substring(2);
                    regExp = regExp.substring(0, regExp.lastIndexOf('}'));
                    Pattern pattern = Pattern.compile(regExp);
                    for (Field field : collection.getFields()) {
                        if (pattern.matcher(field.getId()).matches()) {
                            fields.add(field);
                        }
                    }
                } else {

                    Field field = null;

                    if (collection != null && fieldName != null) {
                        field = collection.getField(fieldName.trim());
                    }

                    if (field == null) {
                        throw new RuntimeException("Field '" + fieldName + "' not found on collection '" + collection.getORI() + "'.");
                    } else {
                        fields.add(field);
                    }
                }

            }
        }
        return fields;
    }

    @Override
    public void buildQuery(Query query) {

        ORI collectionURI = collection.toAbsolute(query.getOn());
        String columnAlias = QueryUtils.newCollectionAlias(query, collectionURI);
        Collection collection = getResourceManager().load(Collection.class, collectionURI);

        if (collection != null) {
            List<Field> fields = getFields(collection, query.getOn());
            Set<String> fieldIds = new HashSet<String>(fields.size());
            for (Field field : fields) {
                fieldIds.add(field.getId());

                for (IDecorator decorator : createActions(collection, field)) {
                    addExtraFields(fieldIds, decorator, collection);
                }

                if (decorator != null) {
                    IDecorator decorator = getDecoratorManager().getDecorator(this.decorator, collection, field);
                    addExtraFields(fieldIds, decorator, collection);
                }
            }
            query.addSelect(columnAlias, new ArrayList<String>(fieldIds));

        }
    }

    private static void addExtraFields(Set<String> fieldIds, IDecorator decorator, Collection collection) {
        List<String> decoratorExtraFields = decorator.getExtraFields(collection);
        if (decoratorExtraFields != null && !decoratorExtraFields.isEmpty()) {
            fieldIds.addAll(decoratorExtraFields);
        }
    }

    @Inject
    private IResourceManager resourceManager;

    @Inject
    private IDecoratorManager decoratorManager;

    protected IResourceManager getResourceManager() {
        if (resourceManager == null) {
            WebsiteApplication.inject(this);
        }
        return resourceManager;
    }

    protected IDecoratorManager getDecoratorManager() {
        if (decoratorManager == null) {
            WebsiteApplication.inject(this);
        }
        return decoratorManager;
    }


    @Override
    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }
}
