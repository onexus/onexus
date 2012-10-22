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

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.resource.api.ParameterKey;
import org.onexus.ui.website.widgets.tableviewer.decorators.utils.FieldDecorator;

import java.util.ArrayList;
import java.util.Iterator;
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

            String currentColumnValue = String.valueOf(entity.get(getField().getId()));

            List<String> columnValues = new ArrayList<String>();
            if (parameters.containsKey(LinkDecoratorParameters.SEPARATOR)) {
                for (String value: currentColumnValue.split(parameters.get(LinkDecoratorParameters.SEPARATOR).trim())) {
                    columnValues.add(value.trim());
                }
            } else {
                columnValues.add(currentColumnValue);
            }

            StringBuilder content = new StringBuilder();
            Iterator<String> columnValueIt = columnValues.iterator();
            while (columnValueIt.hasNext()) {
                String columnValue = columnValueIt.next();

                String href = parameters.get(LinkDecoratorParameters.URL);

                href = replaceUrlParameters(getField(), columnValue, entity, href);

                content.append("<a href=\"").append(href).append("\"");
                if (parameters.containsKey(LinkDecoratorParameters.TARGET)) {
                    content.append(" target=\"").append(parameters.get(LinkDecoratorParameters.TARGET)).append("\"");
                }

                if (parameters.containsKey(LinkDecoratorParameters.LENGTH)) {
                    columnValue = StringUtils.abbreviate(columnValue, Integer.valueOf(parameters.get(LinkDecoratorParameters.LENGTH)));
                }
                content.append("\">").append(columnValue).append("</a>");

                if (columnValueIt.hasNext()) {
                        content.append(", ");
                }
            }

            cellContainer.add(new Label(componentId, content.toString()).setEscapeModelStrings(false));

        } else {
            cellContainer.add(new EmptyPanel(componentId));
        }

        cellContainer.add(new AttributeModifier("title", new Model<String>((description == null ? "No data" : description.toString()))));
    }

    public static String replaceUrlParameters(Field columnField, String columnValue, IEntity rowEntity, String href) {
        String columnPattern = "#{column.id}";
        if (href.contains(columnPattern)) {
            href = href.replaceAll(Pattern.quote(columnPattern), columnField.getId());
        }

        for (Field field : rowEntity.getCollection().getFields()) {
            String value = String.valueOf(rowEntity.get(field.getId()));

            if (field.equals(columnField)) {
                 value = columnValue;
            }

            String fieldPattern = "${" + field.getId() + "}";
            if (href.contains(fieldPattern)) {
                href = href.replaceAll(Pattern.quote(fieldPattern), value);
            }
        }
        return href;
    }

}
