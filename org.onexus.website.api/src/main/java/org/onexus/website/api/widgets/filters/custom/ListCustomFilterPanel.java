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
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;
import org.onexus.collection.api.query.In;
import org.onexus.website.api.events.EventCloseModal;
import org.onexus.website.api.widgets.filters.FilterConfig;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public abstract class ListCustomFilterPanel extends AbstractCustomFilterPanel {

    private IModel<String> filterNameModel;
    private CustomFilter customFilter;

    private final static Random RANDOM = new Random();

    public ListCustomFilterPanel(String id, CustomFilter customFilter) {
        super(id);

        this.filterNameModel = new Model<String>("");
        this.customFilter = customFilter;

        // Create feedback panels
        final FeedbackPanel uploadFeedback = new FeedbackPanel("uploadFeedback");

        // Add uploadFeedback to the page itself
        add(uploadFeedback);

        // Add simple upload form, which is hooked up to its feedback panel by
        // virtue of that panel being nested in the form.
        final FileUploadForm simpleUploadForm = new FileUploadForm("simpleUpload");
        add(simpleUploadForm);

    }

    private void recuperateFormValues(AjaxRequestTarget target, String filterName, CustomFilter field, Collection<String> values) {

        // Generate a pseudo random identifier
        String filterId = "user-filter-" + Integer.toHexString(filterName.hashCode()) + "-" + Integer.toHexString(RANDOM.nextInt());

        // Create the filter
        FilterConfig filter = new FilterConfig(filterId, filterName);
        filter.setDeletable(true);
        filter.setCollection(field.getCollection());
        filter.setDefine("fc='" + field.getCollection() + "'");
        In where = new In("fc", field.getField());
        for (Object value : values) {
            where.addValue(value);
        }
        filter.setWhere(where.toString());
        filter.setVisibleCollection(field.getVisibleCollection());

        addFilter(target, filter);

        send(getPage(), Broadcast.BREADTH, EventCloseModal.EVENT);

    }

    public void cancel(AjaxRequestTarget target) {

    }

    public IModel<String> getFilterNameModel() {
        return filterNameModel;
    }

    public String getFilterName() {
        return filterNameModel.getObject();
    }

    public void setFilterName(String filterName) {
        this.filterNameModel.setObject(filterName);
    }


    /**
     * Inner Form object
     */
    private class FileUploadForm extends Form<Object> {

        private FileUploadField fileUploadField;
        private Model<String> textArea = new Model<String>("");

        public FileUploadForm(String name) {
            super(name);

            setOutputMarkupId(true);

            // set this form to multipart mode (allways needed for uploads!)
            setMultiPart(true);

            TextField<String> filterName = new TextField<String>("filterName", getFilterNameModel());
            add(filterName);

            add(new TextArea<String>("textarea", textArea));

            // Add one file input field
            add(fileUploadField = new FileUploadField("fileInput"));

            final FeedbackPanel pfeedback = new FeedbackPanel("p-feedback");
            add(pfeedback);
            pfeedback.setOutputMarkupId(true);

            // Necessary to pass all information from form.
            add(new AjaxButton("okButton") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if (isDataFormOk()) {
                        try {
                            Collection<String> values = getValuesFromForm();
                            recuperateFormValues(target, getFilterName(), ListCustomFilterPanel.this.customFilter, values);
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

            // Set maximum size to 1K
            setMaxSize(Bytes.kilobytes(10));

        }

        /**
         * Validates the form data.
         *
         * @return
         */
        public boolean isDataFormOk() {

            if (getFilterName() == null || "".equals(getFilterName())) {
                error("Filter name must be specified");
                return false;

            }

            if ((fileUploadField.getFileUpload() == null && (textArea.getObject() == null || textArea.getObject().length() == 0))) {
                error("You must enter some values or upload a file");
                return false;
            }

            return true;
        }

        private Collection<String> getValuesFromForm() throws IOException {

            BufferedReader reader = null;
            InputStream input = null;
            try {
                final FileUpload upload = fileUploadField.getFileUpload();

                if (upload != null) {
                    input = upload.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(input));
                } else {
                    reader = new BufferedReader(new CharArrayReader(textArea.getObject().toCharArray()));
                }

                int c;
                Collection<String> values = new ArrayList<String>();
                String value = "";
                while ((c = reader.read()) != -1) {
                    if (c == ' ' || c == '\r' || c == '\t') {
                        continue;
                    }
                    if (c == ',' || c == '\n') {
                        if (!value.equals("")) {
                            values.add(value);
                            value = "";
                        }
                    } else {
                        value = value + ((char) c);
                    }
                }
                if (!value.equals("")) {
                    values.add(value);
                    value = "";
                }

                return values;
            } finally {
                if (reader != null) {
                    reader.close();
                }

                if (input != null) {
                    input.close();
                }
            }
        }

    }


}
