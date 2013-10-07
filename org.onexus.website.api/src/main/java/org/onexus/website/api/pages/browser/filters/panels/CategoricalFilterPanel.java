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
package org.onexus.website.api.pages.browser.filters.panels;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.collection.api.query.Contains;
import org.onexus.collection.api.query.Equal;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.NotEqual;
import org.onexus.website.api.widgets.selection.FilterConfig;
import org.onexus.website.api.widgets.tableviewer.headers.FieldHeader;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public abstract class CategoricalFilterPanel extends Panel {

    private static final String EQUAL = "equal";
    private static final String NOT_EQUAL = "not equal";
    private static final String CONTAINS = "contains";

    private static final List<String> OPERATIONS = Arrays.asList(new String[]{EQUAL, NOT_EQUAL, CONTAINS});

    private IModel<FilterOption> option;
    private FieldHeader header;

    public CategoricalFilterPanel(String id, FieldHeader fieldHeader, String options) {
        super(id);

        this.header = fieldHeader;

        List<String> values = Arrays.asList(StringUtils.split(options, ','));
        option = new CompoundPropertyModel<FilterOption>(new Model<FilterOption>(new FilterOption(EQUAL, values.get(0))));

        Form<FilterOption> form = new Form("form", option);

        form.add(new DropDownChoice<String>("operation", OPERATIONS).setNullValid(false));
        form.add(new DropDownChoice<String>("value", values).setNullValid(false));

        form.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                FilterOption fo = option.getObject();
                String value = fo.getValue();

                String title = header.getLabel() + " " + fo.getOperation() + " '" + value + "'";
                FilterConfig fc = new FilterConfig(title);

                fc.setCollection(header.getCollection().getORI());
                fc.setDefine("fc='" + fc.getCollection() + "'");

                Filter where;
                if (EQUAL.equals(fo.getOperation())) {
                    where = new Equal("fc", header.getField().getId(), value.trim());
                } else if (NOT_EQUAL.equals(fo.getOperation())) {
                    where = new NotEqual("fc", header.getField().getId(), value.trim());
                } else {
                    where = new Contains("fc", header.getField().getId(), value.trim());
                }

                fc.setWhere(where.toString());

                addFilter(target, fc);
            }
        });

        add(form);
    }

    protected abstract void addFilter(AjaxRequestTarget target, FilterConfig filterConfig);

    public static class FilterOption implements Serializable {
        private String operation;
        private String value;

        public FilterOption(String operation) {
            this.operation = operation;
        }

        public FilterOption(String operation, String value) {
            this.operation = operation;
            this.value = value;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "operation='" + operation + '\'' + ", value='" + value + '\'';
        }
    }
}
