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
package org.onexus.ui.website.widgets.tableviewer.decorators.tooltip;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.resource.api.ParameterKey;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecorator;

import java.util.Map;

public class TooltipDecorator implements IDecorator {

    private Field field;
    private Map<ParameterKey, String> parameters;

    public TooltipDecorator(Collection collection, Field field, Map<ParameterKey, String> parameters) {
        super();
        this.field = field;
        this.parameters = parameters;
    }

    @Override
    public void populateCell(WebMarkupContainer cellContainer, String componentId, IModel<IEntity> entity) {
        String value = getFormatValue(entity.getObject());


        int length = 15;
        if (parameters.containsKey(TooltipDecoratorParameters.LENGTH)) {
            length = Integer.valueOf(parameters.get(TooltipDecoratorParameters.LENGTH));
        }

        String tooltip = StringUtils.abbreviate(value, length) + "<i class=\"icon-plus\" rel=\"tooltip\" title=\"" + value + "\"></i>";
        cellContainer.add(new Label(componentId, tooltip).setEscapeModelStrings(false).setVisible(value != null));
    }

    @Override
    public String getFormatValue(IEntity entity) {
        if (entity == null) {
            return null;
        }

        Object value = entity.get(field.getId());
        if (value == null) {
            return null;
        }

        return String.valueOf(value);
    }



    @Override
    public String getColor(IEntity entity) {
        return "#000000";
    }

    @Override
    public String getTemplate() {
        return null;
    }

    @Override
    public void setTemplate(String template) {

    }

    @Override
    public void detach() {
    }
}
