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
package org.onexus.ui.website.widgets.tableviewer.decorators.link;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.*;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.Field;
import org.onexus.resource.api.ParameterKey;
import org.onexus.ui.website.widgets.tableviewer.decorators.utils.FieldDecorator;
import org.onexus.ui.website.widgets.tableviewer.decorators.utils.LinkPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class LinkDecorator extends FieldDecorator {

    private Map<ParameterKey, String> parameters;

    public LinkDecorator(String collectionId, Field field, Map<ParameterKey, String> parameters) {
        super(field);
        this.parameters = parameters;
    }

    @Override
    public void populateCell(WebMarkupContainer cellContainer, String componentId, IModel<IEntity> entityModel) {

        IEntity entity = entityModel.getObject();
        Object description = getValue(entity);

        if (description != null) {

            RepeatingView links = new RepeatingView(componentId);

            String currentColumnValue = String.valueOf(entity.get(getField().getId()));

            List<String> columnValues = new ArrayList<String>();
            if (parameters.containsKey(LinkDecoratorParameters.SEPARATOR)) {
                for (String value: currentColumnValue.split(",")) {
                    columnValues.add(value.trim());
                }
            } else {
                columnValues.add(currentColumnValue);
            }

            for (String columnValue : columnValues) {
                String href = parameters.get(LinkDecoratorParameters.URL);

                for (Field field : entity.getCollection().getFields()) {
                    String value = String.valueOf(entity.get(field.getId()));

                    if (field.equals(getField())) {
                         value = columnValue;
                    }

                    String fieldPattern = "${" + field.getId() + "}";
                    if (href.contains(fieldPattern)) {
                        href = href.replaceAll(Pattern.quote(fieldPattern), value);
                    }
                }

                final String url = href;

                WebMarkupContainer link = new WebMarkupContainer(LinkPanel.LINK_ID) {

                    @Override
                    public void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);

                        String href = url;

                        // Add website URI
                        if (!href.startsWith("http://")) {
                            StringValue uri = getPage().getPageParameters().get("uri");
                            if (!uri.isEmpty()) {
                                href = href + "&uri=" + uri.toString();
                            }
                        }

                        tag.getAttributes().put("href", href);
                    }
                };


                if (parameters.containsKey(LinkDecoratorParameters.TARGET)) {
                    link.add(new AttributeModifier("target", parameters.get(LinkDecoratorParameters.TARGET)));
                }

                LinkPanel linkPanel = new LinkPanel(links.newChildId(), columnValue, link);
                links.add(linkPanel);
            }

            cellContainer.add(links);

        } else {
            cellContainer.add(new EmptyPanel(componentId));
        }

        cellContainer.add(new AttributeModifier("title", new Model<String>((description == null ? "No data" : description.toString()))));
    }

}
