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
package org.onexus.ui.website.pages.browser;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.core.ICollectionManager;
import org.onexus.core.IEntity;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.core.utils.EntityIterator;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.website.events.EventFixEntity;
import org.onexus.ui.website.events.EventPanel;
import org.onexus.ui.website.events.EventUnfixEntity;
import org.onexus.ui.website.pages.browser.boxes.GenericBox;
import org.onexus.ui.website.utils.EntityModel;
import org.onexus.ui.website.utils.FixedEntity;
import org.onexus.ui.website.utils.SingleEntityQuery;

import javax.inject.Inject;
import java.util.Set;
import java.util.regex.Pattern;

public class FixedEntities extends EventPanel {

    @Inject
    public IResourceManager resourceManager;

    @Inject
    public ICollectionManager collectionManager;

    private IModel<BrowserPageStatus> pageModel;

    public FixedEntities(String id, IModel<BrowserPageStatus> pageModel) {
        super(id);

        this.pageModel = pageModel;

        // Update this component if this events are fired.
        onEventFireUpdate(EventFixEntity.class, EventUnfixEntity.class);
    }

    @Override
    protected void onBeforeRender() {

        super.onBeforeRender();

        RepeatingView filterRules = new RepeatingView("fixedEntities");

        Set<FixedEntity> fixedEntities = pageModel.getObject().getFixedEntities();

        if (fixedEntities != null) {

            String releaseURI = pageModel.getObject().getRelease();

            for (FixedEntity fixedEntity : fixedEntities) {

                WebMarkupContainer container = new WebMarkupContainer(filterRules.newChildId());

                if (fixedEntity.isEnable()) {
                    container.add(new AttributeModifier("class", "large awesome blue"));
                } else {
                    container.add(new AttributeModifier("class", "large awesome gray"));
                }

                // Make collection URI absolute
                String absoluteCollectionURI = ResourceUtils.getAbsoluteURI(releaseURI, fixedEntity.getCollectionURI());
                fixedEntity.setCollectionURI(absoluteCollectionURI);

                Collection collection = resourceManager.load(Collection.class, fixedEntity.getCollectionURI());

                String collectionLabel = collection.getProperty("FIXED_COLLECTION_LABEL");
                if (collectionLabel == null) {
                    collectionLabel = collection.getName();
                }

                String entityField = collection.getProperty("FIXED_ENTITY_FIELD");
                String entityLabel = fixedEntity.getEntityId();
                if (entityField != null) {
                    IEntity entity = new EntityIterator(collectionManager.load(new SingleEntityQuery(fixedEntity.getCollectionURI(), fixedEntity
                            .getEntityId())), fixedEntity.getCollectionURI()).next();

                    entityLabel = String.valueOf(entity.get(entityField));

                    if (entityLabel == null || entityLabel.isEmpty()) {
                        entityLabel = fixedEntity.getEntityId();
                    }

                }

                String entityPattern = collection.getProperty("FIXED_ENTITY_PATTERN");
                if (entityPattern != null) {
                    IEntity entity = new EntityIterator(collectionManager.load(new SingleEntityQuery(fixedEntity.getCollectionURI(), fixedEntity
                            .getEntityId())), fixedEntity.getCollectionURI()).next();

                    if (entity != null) {
                        entityLabel = parseTemplate(entityPattern, entity);
                    }
                }


                // Add new fixed entity
                container.add(new Label("collectionLabel", collectionLabel));
                Label labelComponent = new Label("entityLabel", entityLabel);
                labelComponent.setEscapeModelStrings(false);
                container.add(labelComponent);
                container.add(new GenericBox("box", new EntityModel(fixedEntity)));

                BrowserPageLink<FixedEntity> removeLink = new BrowserPageLink<FixedEntity>("remove", Model.of(fixedEntity)) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {

                        Set<FixedEntity> fixedEntities = getBrowserPageStatus().getFixedEntities();
                        fixedEntities.remove(getModelObject());

                        sendEvent(EventUnfixEntity.EVENT);
                    }

                };
                removeLink.add(new Image("close", "close.png"));

                removeLink.setVisible(fixedEntity.isDeletable());
                container.add(removeLink);
                filterRules.add(container);
            }
        }

        addOrReplace(filterRules);

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

}
