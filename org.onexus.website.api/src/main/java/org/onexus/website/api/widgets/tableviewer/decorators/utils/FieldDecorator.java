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
package org.onexus.website.api.widgets.tableviewer.decorators.utils;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.Strings;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.types.Text;
import org.onexus.website.api.widgets.tableviewer.decorators.IDecorator;
import org.onexus.website.api.widgets.tableviewer.formaters.DoubleFormater;
import org.onexus.website.api.widgets.tableviewer.formaters.ITextFormater;
import org.onexus.website.api.widgets.tableviewer.formaters.StringFormater;
import org.onexus.website.api.widgets.tableviewer.headers.FieldHeader;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Simple field decorator using a ITextFormater.
 *
 * @author Jordi Deu-Pons
 */
public class FieldDecorator implements IDecorator {

    private Field field;
    private ITextFormater textFormater;
    private String cssClass;
    private String template;

    /**
     * @param field The field to use to show the value.
     */
    public FieldDecorator(Field field) {
        this(field, null, null);
    }

    /**
     * @param field    The field to use to show the value.
     * @param cssClass The CSS class of the cell.
     */
    public FieldDecorator(Field field, String cssClass) {
        this(field, null, cssClass);
    }


    public FieldDecorator(Field field, ITextFormater textFormater) {
        this(field, textFormater, null);
    }


    public FieldDecorator(Field field, ITextFormater textFormater, String cssClass) {
        super();
        this.field = field;
        this.textFormater = textFormater== null ? getTextFormater(field) : textFormater;
        this.cssClass = cssClass;
    }

    @Deprecated
    private static ITextFormater getTextFormater(Field field) {
        if (field.getType().equals(String.class)) {
            return new StringFormater(FieldHeader.getMaxLength(field, 20), true);
        }

        if (field.getType().equals(Text.class)) {
            return new StringFormater(FieldHeader.getMaxLength(field, 50), true);
        }

        if (field.getType().equals(Double.class)
                || field.getType().equals(Long.class)
                || field.getType().equals(Integer.class)) {
            return DoubleFormater.INSTANCE;
        }

        return null;
    }

    protected Object getValue(IEntity data) {
        return getValue(data, field.getId());
    }

    protected Object getValue(IEntity data, String fieldId) {
        return data == null ? null : data.get(fieldId);
    }

    public String getFormatValue(final IEntity entity) {

        if (Strings.isEmpty(template)) {
            return getFormatValue(entity, field.getId());
        }

        if (getValue(entity) == null) {
            return "";
        }

        return replaceParameters(entity, template);

    }

    private String getFormatValue(final IEntity entity, String fieldId) {

        Object value = getValue(entity, fieldId);

        if (textFormater != null) {
            return textFormater.format(value);
        }

        if (value == null) {
            return "";
        }

        return value.toString();

    }

    @Override
    public String getColor(final IEntity data) {
        return "#000000";
    }

    @Override
    public String getTemplate() {
        return template;
    }

    @Override
    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    public void detach() {
    }

    @Override
    public void populateCell(WebMarkupContainer cellContainer, String componentId, IModel<IEntity> data) {
        Object value = getValue(data.getObject());

        cellContainer.add(new Label(componentId, getFormatValue(data.getObject())).setEscapeModelStrings(false));
        cellContainer.add(new AttributeModifier("title", new Model<String>(value == null ? "No data" : value.toString())));

        if (cssClass != null) {
            cellContainer.add(new AttributeModifier("class", new Model<String>(cssClass)));
        }
    }

    public Field getField() {
        return field;
    }

    protected String fixLinkUrl(String url) {

        if (Strings.isEmpty(url) || url.contains("://")) {
            return url;
        }

        List<String> segments = RequestCycle.get().getRequest().getUrl().getSegments();
        String lastSegment = segments.get(segments.size() - 1);

        if (segments.size() == 2) {
            url = lastSegment + '/' + url;
        }

        return url;
    }

    protected String replaceParameters(IEntity entity, String template) {
        return replaceParameters(null, null, entity, template, true);
    }

    protected String replaceParameters(Field columnField, String columnValue, IEntity entity, String template, boolean format) {


        String columnPattern = "$[column.id]";
        if (template.contains(columnPattern)) {
            template = template.replaceAll(Pattern.quote(columnPattern), columnField.getId());
        }

        for (Field field : entity.getCollection().getFields()) {
            String value;

            if (format) {
                value = getFormatValue(entity, field.getId());
            } else {
                value = String.valueOf(getValue(entity, field.getId()));
            }

            if (columnField != null && columnField.equals(field)) {
                value = columnValue;
            }

            String fieldPattern = "$[" + field.getId() + "]";
            if (template.contains(fieldPattern)) {
                template = template.replaceAll(Pattern.quote(fieldPattern), value);
            }
        }
        return template;
    }

}
