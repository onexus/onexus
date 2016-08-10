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
package org.onexus.website.widget.details;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.onexus.collection.api.*;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.EntityIterator;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.FilterConfig;
import org.onexus.website.api.IEntitySelection;
import org.onexus.website.api.MultipleEntitySelection;
import org.onexus.website.api.SingleEntitySelection;
import org.onexus.website.api.widget.Widget;
import org.onexus.website.widget.browser.BrowserPageStatus;
import org.onexus.website.widget.searchpage.boxes.FieldsPanel;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DetailsWidget extends Widget<DetailsWidgetConfig, DetailsWidgetStatus> {

    @Inject
    private ICollectionManager collectionManager;

    public DetailsWidget(String componentId, IModel<DetailsWidgetStatus> statusModel) {
        super(componentId, statusModel);

        ORI baseUri = getConfig().getWebsiteConfig().getORI().getParent();
        BrowserPageStatus pageStatus = findParentStatus(BrowserPageStatus.class);
        RepeatingView entities = new RepeatingView("entities");

        Map<ORI, String> templates = new HashMap<ORI, String>();
        for (DetailsEntity detail : this.getConfig().getEntities()) {
            templates.put(detail.getCollection().toAbsolute(baseUri), detail.getTemplate());
        }

        for (IEntitySelection selection : pageStatus.getEntitySelections()) {
            if (selection instanceof SingleEntitySelection) {
                IEntity entity = ((SingleEntitySelection) selection).getEntity(pageStatus.getORI());
                addEntity(entities, templates, entity);
            } else {
                Iterator<IEntity> values = getMultipleEntites(collectionManager, selection.getSelectionCollection(), baseUri, selection.getFilterConfig());
                while(values.hasNext()) {
                    IEntity entity = values.next();
                    addEntity(entities, templates, entity);
                }
            }
        }

        add(entities);
    }

    private void addEntity(RepeatingView entities, Map<ORI, String> templates, IEntity entity) {
        if (entity != null) {
            Collection collection = entity.getCollection();
            if (templates.size() == 0) {
                WebMarkupContainer box = new WebMarkupContainer(entities.newChildId());
                String labelField = collection.getProperty("FIXED_ENTITY_FIELD");
                String label = getLabel(entity, labelField);
                box.add(new Label("label", label).setEscapeModelStrings(false));
                box.add(new FieldsPanel("fields", labelField, entity));
                entities.add(box);
            } else {
                if (templates.containsKey(collection.getORI())) {
                    WebMarkupContainer box = new WebMarkupContainer(entities.newChildId());
                    String labelField = collection.getProperty("FIXED_ENTITY_FIELD");
                    String label = getLabel(entity, labelField);
                    box.add(new Label("label", label).setEscapeModelStrings(false));
                    box.add(new Label("fields", replaceEntityValues(templates.get(collection.getORI()), entity)).setEscapeModelStrings(false));
                    entities.add(box);
                }
            }
        }
    }

    private String getLabel(IEntity entity, String labelField) {
        return labelField == null ?
                StringUtils.replace(entity.getId(), "\t", "-") :
                String.valueOf(entity.get(labelField));
    }

    private static String replaceEntityValues(String template, IEntity entity) {

        Collection collection = entity.getCollection();

        for (Field field : collection.getFields()) {
            template = template.replaceAll("\\$\\[" + field.getId() + "\\]", String.valueOf(entity.get(field.getId())));
        }

        return template;
    }

    private static Iterator<IEntity> getMultipleEntites(ICollectionManager collectionManager, ORI collectionOri, ORI baseOri, FilterConfig filter) {

        Query query = new Query();
        query.setOn(baseOri);

        String collectionAlias = QueryUtils.newCollectionAlias(query, collectionOri);
        query.setFrom(collectionAlias);

        query.addSelect(collectionAlias, null);

        IEntitySelection selection = new MultipleEntitySelection(filter);
        query.setWhere(selection.buildFilter(query));

        return new EntityIterator(collectionManager.load(query), collectionOri);
    }
}
