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
package org.onexus.website.api.pages.browser;

import org.h2.util.StringUtils;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.EqualId;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.EntityIterator;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.utils.EntityModel;
import org.onexus.website.api.utils.SingleEntityQuery;
import org.onexus.website.api.utils.visible.VisibleRule;
import org.onexus.website.api.widgets.selection.FilterConfig;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.util.regex.Pattern;

public class SingleEntitySelection implements IEntitySelection {

    private ORI filteredCollection;
    private String entityId;
    private boolean deletable;
    private boolean enable;

    @PaxWicketBean(name = "resourceManager")
    private IResourceManager resourceManager;

    @PaxWicketBean(name = "collectionManager")
    private ICollectionManager collectionManager;

    public SingleEntitySelection() {
        super();
        WebsiteApplication.inject(this);
    }

    public SingleEntitySelection(IEntity entity) {
        this(entity, true);
    }

    public SingleEntitySelection(IEntity entity, boolean deletable) {
        this(entity.getCollection().getORI(), entity.getId(), deletable);
    }

    public SingleEntitySelection(ORI filteredCollection, String entityId) {
        this(filteredCollection, entityId, true);
    }

    public SingleEntitySelection(ORI filteredCollection, String entityId, boolean deletable) {
        super();
        WebsiteApplication.inject(this);

        this.filteredCollection = filteredCollection;
        this.entityId = entityId;
        this.deletable = deletable;
        this.enable = true;
    }

    @Override
    public ORI getSelectionCollection() {
        return filteredCollection;
    }

    @Override
    public FilterConfig getFilterConfig() {
        //TODO
        return null;
    }

    public void setFilteredCollection(ORI filteredCollection) {
        this.filteredCollection = filteredCollection;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityId() {
        return entityId;
    }

    @Override
    public boolean isDeletable() {
        return deletable;
    }

    @Override
    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public Filter buildFilter(Query query) {
        String collectionAlias = QueryUtils.newCollectionAlias(query, filteredCollection);
        return new EqualId(collectionAlias, entityId);
    }

    @Override
    public boolean match(VisibleRule rule) {

        //TODO Check projects

        if (rule.getType() == VisibleRule.SelectionType.LIST) {
            return false;
        }

        String filterPath = filteredCollection.getPath();
        String rulePath = rule.getFilteredCollection().getPath();

        boolean validCollection = (filterPath == null || rulePath == null) ? false : filterPath.endsWith(rulePath);


        if (rule.getField() == null) {
            return validCollection;
        }


        ORI collectionUri = filteredCollection.toAbsolute(rule.getParentURI());
        IEntity entity = (new EntityModel(collectionUri, entityId)).getObject();

        String fieldValue = String.valueOf(entity.get(rule.getField()));

        return StringUtils.equals(fieldValue, rule.getValue());
    }

    private static final String SEPARATOR = "::";
    private static final Pattern DOUBLE_COLON = Pattern.compile(SEPARATOR);

    @Override
    public String toUrlParameter(boolean global, ORI parentUri) {

        StringBuilder str = new StringBuilder();

        ORI uri;
        if (global) {
            uri = filteredCollection.toAbsolute(parentUri);
        } else {
            uri = filteredCollection;
        }

        str.append(uri).append(SEPARATOR);
        str.append(entityId).append(SEPARATOR);
        str.append(deletable ? "d" : "");
        str.append(enable ? "e" : "");

        return str.toString();
    }

    @Override
    public void loadUrlPrameter(String parameter) {

        String[] values = DOUBLE_COLON.split(parameter);

        this.filteredCollection = new ORI(values[0]);
        this.entityId = values[1];
        String flags = values[2];
        deletable = flags.contains("d");
        enable = flags.contains("e");
    }

    @Override
    public String getTitle(Query query) {

        // Make collection URI absolute
        ORI filteredCollection = getSelectionCollection().toAbsolute(query.getOn());
        Collection collection = getResourceManager().load(Collection.class, filteredCollection);

        String entityField = collection.getProperty("FIXED_ENTITY_FIELD");
        String entityLabel = getEntityId();
        if (entityField != null) {
            IEntityTable entityTable = getCollectionManager().load(new SingleEntityQuery(filteredCollection, getEntityId()));

            IEntity entity = new EntityIterator(entityTable, filteredCollection).next();

            entityLabel = String.valueOf(entity.get(entityField));

            if (entityLabel == null || entityLabel.isEmpty()) {
                entityLabel = getEntityId();
            }

            entityTable.close();
        }

        String entityPattern = collection.getProperty("FIXED_ENTITY_PATTERN");
        if (entityPattern != null) {
            IEntityTable entityTable = getCollectionManager().load(new SingleEntityQuery(filteredCollection, getEntityId()));

            IEntity entity = new EntityIterator(entityTable, filteredCollection).next();

            if (entity != null) {
                entityLabel = parseTemplate(entityPattern, entity);
            }

            entityTable.close();
        }

        return entityLabel;
    }

    private IResourceManager getResourceManager() {
        if (resourceManager == null) {
            WebsiteApplication.inject(this);
        }

        return resourceManager;
    }

    private ICollectionManager getCollectionManager() {
        if (collectionManager == null) {
            WebsiteApplication.inject(this);
        }

        return collectionManager;
    }

    private String parseTemplate(String entityPattern, IEntity entity) {


        for (Field field : entity.getCollection().getFields()) {
            String fieldName = field.getId();
            entityPattern = entityPattern.replaceAll(
                    Pattern.quote("${" + fieldName + "}"),
                    String.valueOf(entity.get(fieldName))
            );
        }

        return entityPattern;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FixedEntity [collectionId=");
        builder.append(filteredCollection);
        builder.append(", entityId=");
        builder.append(entityId);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((filteredCollection == null) ? 0 : filteredCollection.hashCode());
        result = prime * result
                + ((entityId == null) ? 0 : entityId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        SingleEntitySelection other = (SingleEntitySelection) obj;
        if (filteredCollection == null) {
            if (other.filteredCollection != null) {
                return false;
            }
        } else if (!filteredCollection.equals(other.filteredCollection)) {
            return false;
        }

        if (entityId == null) {
            if (other.entityId != null) {
                return false;
            }
        } else if (!entityId.equals(other.entityId)) {
            return false;
        }
        return true;
    }


}