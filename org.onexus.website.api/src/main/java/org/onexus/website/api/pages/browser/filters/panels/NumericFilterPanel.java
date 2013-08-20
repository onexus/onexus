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

public abstract class NumericFilterPanel extends Panel {

    private static String EQ = "=";
    private static String NOT_EQ = "<>";
    private static String GT_EQ = ">=";
    private static String LT_EQ = "<=";
    private static String GT = ">";
    private static String LT = "<";

    private static List<String> OPERATIONS = Arrays.asList(new String[]{ EQ, NOT_EQ, GT, GT_EQ, LT, LT_EQ });

    private IModel<FilterOption> option;
    private FieldHeader header;

    public NumericFilterPanel(String id, FieldHeader fieldHeader) {
        super(id);

        this.header = fieldHeader;
        option = new CompoundPropertyModel<FilterOption>(new Model<FilterOption>(new FilterOption()));

        Form<FilterOption> form = new Form("form", option);

        form.add(new DropDownChoice<String>("operation", OPERATIONS));
        form.add(new TextField<String>("value"));

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
        });

        add(form);
    }

    protected abstract void addFilter(AjaxRequestTarget target, FilterConfig filterConfig);

    public static class FilterOption implements Serializable {
        private String operation;
        private String value;


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
