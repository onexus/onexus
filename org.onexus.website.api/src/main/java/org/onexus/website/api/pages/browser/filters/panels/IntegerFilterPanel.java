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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.collection.api.query.Equal;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.GreaterThan;
import org.onexus.collection.api.query.GreaterThanOrEqual;
import org.onexus.collection.api.query.LessThan;
import org.onexus.collection.api.query.LessThanOrEqual;
import org.onexus.collection.api.query.NotEqual;
import org.onexus.website.api.widgets.selection.FilterConfig;
import org.onexus.website.api.widgets.tableviewer.headers.FieldHeader;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public abstract class IntegerFilterPanel<T extends Number> extends Panel {

    private static final String EQ = "=";
    private static final String NOT_EQ = "<>";
    private static final String GT_EQ = ">=";
    private static final String LT_EQ = "<=";
    private static final String GT = ">";
    private static final String LT = "<";

    private static final List<String> OPERATIONS = Arrays.asList(new String[]{EQ, NOT_EQ, GT, GT_EQ, LT, LT_EQ});

    private IModel<FilterOption> option;
    private FieldHeader header;

    public IntegerFilterPanel(String id, FieldHeader fieldHeader) {
        super(id);

        this.header = fieldHeader;
        option = new CompoundPropertyModel<FilterOption>(new Model<FilterOption>(new FilterOption(EQ)));

        Form<FilterOption> form = new Form("form", option);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        form.add(new DropDownChoice<String>("operation", OPERATIONS).setNullValid(false));
        form.add(new TextField<Integer>("value"));

        form.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                FilterOption fo = option.getObject();
                String title = header.getLabel() + " " + fo.getOperation() + " '" + fo.getValue() + "'";
                FilterConfig fc = new FilterConfig(title);

                fc.setCollection(header.getCollection().getORI());
                fc.setDefine("fc='" + fc.getCollection() + "'");

                Filter where;
                String operation = fo.getOperation();

                if (EQ.equals(operation)) {
                    where = new Equal("fc", header.getField().getId(), fo.getValue());
                } else if (NOT_EQ.equals(operation)) {
                    where = new NotEqual("fc", header.getField().getId(), fo.getValue());
                } else if (GT_EQ.equals(operation)) {
                    where = new GreaterThanOrEqual("fc", header.getField().getId(), fo.getValue());
                } else if (LT_EQ.equals(operation)) {
                    where = new LessThanOrEqual("fc", header.getField().getId(), fo.getValue());
                } else if (GT.equals(operation)) {
                    where = new GreaterThan("fc", header.getField().getId(), fo.getValue());
                } else {
                    where = new LessThan("fc", header.getField().getId(), fo.getValue());
                }

                fc.setWhere(where.toString());

                addFilter(target, fc);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        add(form);
    }

    protected abstract void addFilter(AjaxRequestTarget target, FilterConfig filterConfig);

    public static class FilterOption implements Serializable {
        private String operation;
        private Integer value;

        public FilterOption(String operation) {
            super();

            this.operation = operation;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "operation='" + operation + '\'' + ", value='" + value + '\'';
        }
    }
}
