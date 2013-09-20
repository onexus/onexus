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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.StringValidator;
import org.onexus.collection.api.query.Contains;
import org.onexus.collection.api.query.Equal;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.website.api.pages.browser.filters.FiltersToolbarStatus;
import org.onexus.website.api.widgets.selection.FilterConfig;
import org.onexus.website.api.widgets.tableviewer.headers.FieldHeader;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public abstract class StringFilterPanel extends Panel {

    private static String EQUAL = "equal";
    private static String CONTAINS = "contains";

    private static List<String> OPERATIONS = Arrays.asList(new String[]{ EQUAL, CONTAINS });

    private IModel<FilterOption> option;
    private FieldHeader header;

    public StringFilterPanel(String id, FieldHeader fieldHeader) {
        super(id);

        this.header = fieldHeader;
        option = new CompoundPropertyModel<FilterOption>(new Model<FilterOption>(new FilterOption(EQUAL)));

        Form<FilterOption> form = new Form("form", option);

        form.add(new DropDownChoice<String>("operation", OPERATIONS).setNullValid(false));
        form.add(new TextField<String>("value").add(new StringValidator(1, 200)));

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
                    where = new Equal("fc", header.getField().getId(), value);
                } else {
                    where = new Contains("fc", header.getField().getId(), value);
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
