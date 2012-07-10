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
package org.onexus.ui.website.widgets.filters;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simple lightweight form panel.
 *
 * @author armand
 */
public abstract class UserFilterPanel extends Panel {

    private IModel<String> filterNameModel;
    private FieldSelection fieldSelected;
    private List<FieldSelection> fieldSelectionList;

    public UserFilterPanel(String id, List<FieldSelection> filterableProps) {
        super(id);

        this.filterNameModel = new Model<String>("");
        this.fieldSelectionList = filterableProps;

        // Create feedback panels
        final FeedbackPanel uploadFeedback = new FeedbackPanel("uploadFeedback");

        // Add uploadFeedback to the page itself
        add(uploadFeedback);

        // Add simple upload form, which is hooked up to its feedback panel by
        // virtue of that panel being nested in the form.
        final FileUploadForm simpleUploadForm = new FileUploadForm("simpleUpload");
        add(simpleUploadForm);

    }

    // Methods to send result of user interaction
    public abstract void recuperateFormValues(AjaxRequestTarget target, String filterName, FieldSelection property,
                                              Collection<String> values);

    public abstract void cancel(AjaxRequestTarget target);

    public IModel<String> getFilterNameModel() {
        return filterNameModel;
    }

    public String getFilterName() {
        return filterNameModel.getObject();
    }

    public void setFilterName(String filterName) {
        this.filterNameModel.setObject(filterName);
    }

    public List<FieldSelection> getFieldSelectionList() {
        return fieldSelectionList;
    }

    public void setFieldSelectionList(List<FieldSelection> fieldSelectionList) {
        this.fieldSelectionList = fieldSelectionList;
    }

    public FieldSelection getFieldSelected() {
        return fieldSelected;
    }

    public void setFieldSelected(FieldSelection fieldSelectedModel) {
        this.fieldSelected = fieldSelectedModel;
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

            /* add(new Label("type", entityCollection.getName())); */

            TextField<String> filterName = new TextField<String>("filterName", getFilterNameModel());
            add(filterName);

            WebMarkupContainer properties = new WebMarkupContainer("properties");
            add(properties);

            PropertyModel propModel = new PropertyModel();
            fieldSelected = (getFieldSelectionList() == null || getFieldSelectionList().isEmpty()) ? null
                    : getFieldSelectionList().get(0);
            propModel.setObject(fieldSelected);
            properties.add(new DropDownChoice<FieldSelection>("select", propModel, getFieldSelectionList(),
                    new ChoiceRenderer<FieldSelection>("title")));

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
                            recuperateFormValues(target, getFilterName(), getFieldSelected(), values);

                        } catch (Exception e) {
                            // TODO
                            e.printStackTrace();
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

            } else {
                return getFieldSelected() != null
                        && getFilterName() != null
                        && !"".equals(getFilterName())
                        && (fileUploadField.getFileUpload() != null || (textArea.getObject() != null && textArea
                        .getObject().length() > 0));
            }
        }

        /**
         * Get Values From Form.
         *
         * @param upload
         * @return
         * @throws IOException
         */
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

    /*
     * Class to interact with property.
     */
    private class PropertyModel implements IModel<FieldSelection> {

        @Override
        public FieldSelection getObject() {
            return getFieldSelected();
        }

        @Override
        public void setObject(FieldSelection object) {
            setFieldSelected(object);
        }

        @Override
        public void detach() {
            // Nothing
        }
    }

}
