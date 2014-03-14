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
package org.onexus.website.widgets.tableviewer.decorators.box;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.resource.api.Parameters;
import org.onexus.website.widgets.tableviewer.decorators.IDecorator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class BoxDecorator implements IDecorator {

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private static final Pattern COLON_PATTERN = Pattern.compile(":");

    private Field field;
    private String fields;
    private Map<String, String> decorators = new HashMap<String, String>();

    public BoxDecorator(Field field, Parameters parameters) {
        super();
        this.field = field;
        this.fields = parameters.get(BoxDecoratorParameters.FIELDS);

        if (parameters.containsKey(BoxDecoratorParameters.DECORATORS)) {
            String decoParameters[] = COMMA_PATTERN.split(parameters.get(BoxDecoratorParameters.DECORATORS));

            for (String decoParam : decoParameters) {
                String[] values = COLON_PATTERN.split(decoParam);
                decorators.put(values[0], values[1]);
            }
        }
    }

    @Override
    public void populateCell(WebMarkupContainer cellContainer, String componentId, IModel<IEntity> entity) {
        cellContainer.add(new BoxEntityPanel(componentId, field, entity.getObject(), getFieldIds(fields), decorators));
    }

    private static List<String> getFieldIds(String fields) {

        if (Strings.isEmpty(fields)) {
            return new ArrayList<String>();
        }

        String[] fieldsSplit = fields.split(",");
        List<String> fieldIds = new ArrayList<String>(fieldsSplit.length);

        for (String id : fieldsSplit) {
            fieldIds.add(id.trim());
        }

        return fieldIds;
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
    public List<String> getExtraFields(Collection collection) {

        List<String> fields = getFieldIds(this.fields);

        if (fields.isEmpty()) {
            for (Field field : collection.getFields()) {
                fields.add(field.getId());
            }
        }

        return fields;
    }

    @Override
    public void detach() {
    }
}
