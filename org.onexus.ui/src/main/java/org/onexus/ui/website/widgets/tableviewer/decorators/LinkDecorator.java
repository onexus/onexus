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
package org.onexus.ui.website.widgets.tableviewer.decorators;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.core.IEntity;
import org.onexus.core.resources.Field;
import org.onexus.ui.website.events.AbstractEvent;
import org.onexus.ui.website.events.EventFixEntity;
import org.onexus.ui.website.pages.browser.BrowserPageLink;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.utils.FixedEntity;

public class LinkDecorator extends FieldDecorator {

    private String collectionId;

    public LinkDecorator(String collectionId, Field field) {
        super(field);
        this.collectionId = collectionId;
    }

    @Override
    public void populateCell(WebMarkupContainer cellContainer,
                             String componentId, IModel<IEntity> data) {
        Object value = getValue(data.getObject());
        cellContainer.add(new LinkPanel(componentId, getFormatValue(data
                .getObject()), getLink(collectionId, data)));
        cellContainer.add(new AttributeModifier("title", new Model<String>(
                (value == null ? "No data" : value.toString()))));
    }

    protected WebMarkupContainer getLink(String collectionId,
                                         IModel<IEntity> data) {

        String entityId = null;
        IEntity entity = data.getObject();

        if (entity.getCollection().getURI().equals(collectionId)) {
            entityId = entity.getId();
        } else {
            entityId = String.valueOf(entity.get(getValueProperty().getId()));
        }

        return new BrowserPageLink<FixedEntity>(LinkPanel.LINK_ID, Model.of(new FixedEntity(collectionId, entityId))) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                BrowserPageStatus status = getBrowserPageStatus();
                FixedEntity rowEntity = getModelObject();

                AbstractEvent[] events = LinkDecorator.this.onClick(rowEntity, status);
                for (AbstractEvent event : events) {
                    sendEvent(event);
                }
            }

        };
    }

    protected AbstractEvent[] onClick(FixedEntity rowEntity, BrowserPageStatus status)  {

        // Fix current row entity
        status.getFixedEntities().add(rowEntity);

        return new AbstractEvent[] { EventFixEntity.EVENT };
    }


}
