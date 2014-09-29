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
package org.onexus.website.api.widgets.tableviewer.decorators.box;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.utils.EntityModel;
import org.onexus.website.api.widgets.tableviewer.decorators.IDecorator;
import org.onexus.website.api.widgets.tableviewer.decorators.IDecoratorManager;
import org.onexus.website.api.widgets.tableviewer.decorators.link.LinkDecoratorParameters;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class BoxEntityPanel extends Panel {

    public static final CssReferenceHeaderItem CSS = CssHeaderItem.forReference(new PackageResourceReference(BoxEntityPanel.class, "BoxEntityPanel.css"));

    @Inject
    private IDecoratorManager decoratorManager;

    public BoxEntityPanel(String id, Field columnField, IEntity entity, List<String> fieldIds, Map<String, String> decorators) {
        super(id);

        Collection collection = entity.getCollection();

        List<Field> fields = new ArrayList<Field>();
        if (fieldIds == null || fieldIds.isEmpty()) {
            fields.addAll(collection.getFields());
        } else {
            for (String fieldId : fieldIds) {
                fields.add(collection.getField(fieldId));
            }
        }

        RepeatingView fieldsView = new RepeatingView("fields");
        boolean allValuesAreNull = true;
        for (Field field : fields) {

            Object value = entity.get(field.getId());
            if (value != null && !StringUtils.isEmpty(value.toString())) {

                allValuesAreNull = false;

                String externalLink = field.getProperty("EXTERNAL_LINK");
                String separator = field.getProperty("SEPARATOR");

                String val;
                if (separator != null) {
                    List<String> values = new ArrayList<String>();
                    for (String v : value.toString().split(separator.trim())) {
                        values.add(v.trim());
                    }

                    StringBuilder content = new StringBuilder();
                    Iterator<String> columnValueIt = values.iterator();

                    while (columnValueIt.hasNext()) {
                        String columnValue = columnValueIt.next();

                        String href = replaceParameters(field, columnValue, entity, externalLink, false);

                        content.append("<a href=\"").append(href).append("\"");
                        content.append(" target=\"_blank\"");

                        content.append(">").append(columnValue).append("</a>");

                        if (columnValueIt.hasNext()) {
                            content.append(", ");
                        }
                    }

                    val = content.toString();
                } else {
                    val = StringUtils.abbreviate(value.toString(), 50);
                }

                WebMarkupContainer fc = new WebMarkupContainer(fieldsView.newChildId());
                fc.setRenderBodyOnly(true);
                fc.add(new Label("label", field.getLabel()).add(new AttributeModifier("title", field.getTitle())));
                fc.add(new Label("value", val).setEscapeModelStrings(false));

                if (separator==null && !StringUtils.isEmpty(externalLink)) {
                    fc.add(new ExternalLink("link", replaceEntityValues(externalLink, entity)));
                } else {
                    fc.add(new WebMarkupContainer("link").setVisible(false));
                }

                fieldsView.add(fc);
            }
        }

        // Hide the box if it is empty
        if (allValuesAreNull) {
            setVisible(false);
        }

        add(fieldsView);


        WebMarkupContainer decoratorsContainer = new WebMarkupContainer("decoratorsContainer");
        add(decoratorsContainer);
        RepeatingView decoratorsView = new RepeatingView("decorators");

        for (Map.Entry<String, String> decorator : decorators.entrySet()) {
            WebMarkupContainer item = new WebMarkupContainer(decoratorsView.newChildId());

            IDecorator decoratorImpl = getDecoratorManager().getDecorator(decorator.getKey(), entity.getCollection(), columnField);
            decoratorImpl.populateCell(item, "decorator", new EntityModel(entity));
            item.add(new Label("label", decorator.getValue()));
            decoratorsView.add(item);
        }

        if (decorators.isEmpty()) {
            decoratorsContainer.setVisible(false);
        }

        decoratorsContainer.add(decoratorsView);

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CSS);
    }

    protected IDecoratorManager getDecoratorManager() {
        if (decoratorManager == null) {
            WebsiteApplication.inject(this);
        }
        return decoratorManager;
    }

    private static String replaceParameters(Field columnField, String columnValue, IEntity entity, String template, boolean format) {

        String columnPattern = "$[column.id]";
        if (template.contains(columnPattern)) {
            template = template.replaceAll(Pattern.quote(columnPattern), columnField.getId());
        }

        for (Field field : entity.getCollection().getFields()) {
            String value;

            value = String.valueOf(entity.get(field.getId()));

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

    private static String replaceEntityValues(String template, IEntity entity) {

        Collection collection = entity.getCollection();

        for (Field field : collection.getFields()) {
            template = template.replaceAll("\\$\\[" + field.getId() + "\\]", String.valueOf(entity.get(field.getId())));
        }

        return template;
    }
}
