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
package org.onexus.website.api.widgets.filters.custom;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.query.*;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.exceptions.OnexusException;
import org.onexus.website.api.events.EventCloseModal;
import org.onexus.website.api.widgets.filters.FilterConfig;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class NumericCustomFilterPanel extends AbstractCustomFilterPanel {

    private CustomFilter customFilter;
    private ORI parentOri;

    private final static Random RANDOM = new Random();
    private final static List<String> OPERATIONS = Arrays.asList(new String[]{"<", ">", "<=", ">=", "=", "!="});

    @PaxWicketBean(name="resourceManager")
    private IResourceManager resourceManager;

    public NumericCustomFilterPanel(String id, CustomFilter customFilter, ORI parentOri) {
        super(id);

        this.customFilter = customFilter;
        this.parentOri = parentOri;

        final FeedbackPanel uploadFeedback = new FeedbackPanel("uploadFeedback");
        add(uploadFeedback);

        final FilterForm form = new FilterForm("form");
        add(form);

    }

    private void createFilter(AjaxRequestTarget target, String operation, String strValue) {

        String filterName = customFilter.getField() + " " + operation + " " + strValue;

        Object value;
        try {
            value = convertToNumber(strValue);
        } catch (NumberFormatException e) {
            throw new OnexusException(e.getMessage());
        }

        // Generate a pseudo random identifier
        String filterId = "user-filter-" + Integer.toHexString(filterName.hashCode()) + "-" + Integer.toHexString(RANDOM.nextInt());

        // Create the filter
        FilterConfig filter = new FilterConfig(filterName);
        filter.setDeletable(true);
        filter.setCollection(customFilter.getCollection());
        filter.setDefine("fc='" + customFilter.getCollection() + "'");

        Filter where;
        if (operation.equals("<")) {
            where = new LessThan("fc", customFilter.getField(), value);
        } else if (operation.equals(">")) {
            where = new GreaterThan("fc", customFilter.getField(), value);
        } else if (operation.equals("=")) {
            where = new Equal("fc", customFilter.getField(), value);
        } else if (operation.equals("<=")) {
            where = new LessThanOrEqual("fc", customFilter.getField(), value);
        } else if (operation.equals(">=")) {
            where = new GreaterThanOrEqual("fc", customFilter.getField(), value);
        } else {
            where = new NotEqual("fc", customFilter.getField(), value);
        }

        filter.setWhere(where.toString());

        addFilter(target, filter);

        send(getPage(), Broadcast.BREADTH, EventCloseModal.EVENT);
    }

    private Number convertToNumber(String strValue) throws NumberFormatException {

        if (strValue.contains(".")) {
            return Double.valueOf(strValue);
        } else {
            return Integer.valueOf(strValue);
        }

    }

    public void cancel(AjaxRequestTarget target) {

    }

    /**
     * Inner Form object
     */
    private class FilterForm extends Form<Object> {

        private String value;
        private String operation;

        public FilterForm(String name) {
            super(name);


            setOutputMarkupId(true);

            // Operation
            add(new DropDownChoice<String>("operation", new PropertyModel<String>(this, "operation"), OPERATIONS));

            // Value
            add(new TextField<String>("value", new PropertyModel<String>(this, "value")));

            final FeedbackPanel pfeedback = new FeedbackPanel("p-feedback");
            add(pfeedback);
            pfeedback.setOutputMarkupId(true);

            // Necessary to pass all information from form.
            add(new AjaxButton("okButton") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if (isDataFormOk()) {
                        try {
                            createFilter(target, operation, value);
                        } catch (Exception e) {
                            error(e.getMessage());
                        }
                    }
                    target.add(pfeedback);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(pfeedback);
                }

            });

            add(new AjaxButton("cancelButton") {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    cancel(target);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    // FIXME Auto-generated method stub

                }
            });
        }

        /**
         * Validates the form data.
         *
         * @return
         */
        public boolean isDataFormOk() {

            if (Strings.isEmpty(operation)) {
                error("Select an operation");
                return false;
            }

            if (Strings.isEmpty(value)) {
                error("Input a value");
                return false;
            }

            try {
                Number number = convertToNumber(value);

                Collection collection = resourceManager.load(Collection.class, customFilter.getCollection().toAbsolute(parentOri));
                Field field = collection.getField(customFilter.getField());
                if (Integer.class.isAssignableFrom(field.getType())) {
                    if (number instanceof Integer) {
                        return true;
                    } else {
                        error("Not a valid integer");
                        return false;
                    }
                }

            } catch (NumberFormatException e) {
                error("Not a valid number");
                return false;
            }

            //TODO Validate that the string can be parsed as a number
            return true;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }
    }


}
