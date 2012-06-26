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
package org.onexus.ui.website.widgets.tableviewer.decorators.basic.decorators;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.core.IEntity;
import org.onexus.core.resources.Field;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.website.Website;
import org.onexus.ui.website.WebsiteConfig;
import org.onexus.ui.website.events.AbstractEvent;
import org.onexus.ui.website.events.EventFixEntity;
import org.onexus.ui.website.pages.browser.BrowserPageLink;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.pages.browser.FilterEntity;
import org.onexus.ui.website.widgets.tableviewer.decorators.FieldDecorator;
import org.onexus.ui.website.widgets.tableviewer.decorators.LinkPanel;

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

        return new BrowserPageLink<FilterEntity>(LinkPanel.LINK_ID, Model.of(new FilterEntity(collectionId, entityId))) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                BrowserPageStatus status = getBrowserPageStatus();
                FilterEntity rowEntity = getModelObject();

                // Try to make the link relative to the current website project
                Website website = findParent(Website.class);
                if (website != null) {
                    String projectUri = ResourceUtils.getProjectURI(website.getConfig().getURI());
                    String relativeUri = ResourceUtils.getRelativeURI(projectUri, rowEntity.getFilteredCollection());
                    rowEntity.setFilteredCollection(relativeUri);
                }

                AbstractEvent[] events = LinkDecorator.this.onClick(rowEntity, status);
                for (AbstractEvent event : events) {
                    sendEvent(event);
                }
            }

        };
    }

    protected AbstractEvent[] onClick(FilterEntity rowEntity, BrowserPageStatus status)  {

        // Fix current row entity
        status.getFilters().add(rowEntity);

        return new AbstractEvent[] { EventFixEntity.EVENT };
    }


}
