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
package org.onexus.website.widget.tableviewer.decorators.list;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.resource.api.Parameters;
import org.onexus.website.widget.tableviewer.decorators.utils.FieldDecorator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListDecorator extends FieldDecorator {

    private Parameters parameters;

    public ListDecorator(Field field, Parameters parameters) {
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
            for (String value : currentColumnValue.split(parameters.get(ListDecoratorParameters.SEPARATOR).trim())) {
                columnValues.add(value.trim());
            }

            StringBuilder content = new StringBuilder();
            Iterator<String> columnValueIt = columnValues.iterator();

            int count = 0;
            int maxItems = Integer.valueOf(parameters.get(ListDecoratorParameters.ITEMS));

            while (columnValueIt.hasNext() && count < maxItems) {
                String columnValue = columnValueIt.next();

                String href = parameters.get(ListDecoratorParameters.URL);

                if (href != null) {
                    href = fixLinkUrl(href);
                    href = replaceParameters(getField(), columnValue, entity, href, false);
                    content.append("<a href=\"").append(href).append("\"");

                    if (parameters.containsKey(ListDecoratorParameters.TARGET)) {
                        content.append(" target=\"").append(parameters.get(ListDecoratorParameters.TARGET)).append("\"");
                    }

                    content.append(">");
                }

                content.append(columnValue);

                if (href != null) {
                    content.append("</a>");
                }

                count++;

                if (columnValueIt.hasNext() && count < maxItems) {
                    content.append(", ");
                }

            }

            if (columnValues.size() > maxItems) {
                content.append("...");
            }

            cellContainer.add(new Label(componentId, content.toString()).setEscapeModelStrings(false));

        } else {
            cellContainer.add(new EmptyPanel(componentId));
        }

        cellContainer.add(new AttributeModifier("title", new Model<String>(description == null ? "No data" : description.toString())));
    }


}
