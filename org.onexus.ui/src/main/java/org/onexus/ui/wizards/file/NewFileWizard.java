/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.ui.wizards.file;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Bytes;
import org.onexus.core.ISourceManager;
import org.onexus.core.resources.Resource;
import org.onexus.core.resources.Source;
import org.onexus.core.utils.ResourceTools;
import org.onexus.ui.wizards.AbstractNewResourceWizard;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class NewFileWizard extends AbstractNewResourceWizard<Source> {

    @Inject
    public ISourceManager sourceManager;

    private transient List<FileUpload> tmpFile;
    private FileReference fileUpload = null;

    public NewFileWizard(String id, IModel<? extends Resource> resourceModel) {
        super(id, resourceModel);

        WizardModel model = new WizardModel();
        model.add(new UploadFile());
        model.add(new ResourceName());
        init(model);

        getForm().setMultiPart(true);
        getForm().setMaxSize(Bytes.megabytes(10));

    }

    @Override
    public void onFinish() {

        Source resource = getResource();
        String parentUri = getParentUri();

        if (resource != null && parentUri != null && fileUpload != null) {
            String resourceUri = ResourceTools.concatURIs(parentUri, resource.getName());
            sourceManager.store(resourceUri, new File(fileUpload.tempPath), true);

            super.onFinish();
        }

    }

    @Override
    public void onCancel() {

        // Remove temporal file on cancel
        if (fileUpload != null) {
            File tmpFile = new File(fileUpload.tempPath);
            tmpFile.delete();
        }

        setVisible(false);
    }

    @Override
    protected Source getDefaultResource() {
        return new Source();
    }

    private final class ResourceName extends WizardStep {

        public ResourceName() {
            super("New project", "Creates a new project inside the current workspace");

            add(getFieldResourceName());

        }

        @Override
        public boolean isComplete() {
            return getResource().getName() != null;
        }


    }

    private final class UploadFile extends WizardStep {

        public UploadFile() {
            super("New file", "Upload a file inside the current release");

            add(new FileUploadField("tmpFile") {

                @Override
                public boolean isEnabled() {
                    return fileUpload == null;
                }

            });

            add(new AjaxButton("sendFile") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                    List<FileUpload> files = NewFileWizard.this.getTmpFile();
                    if (files != null && !files.isEmpty()) {
                        FileUpload fileUpload = files.get(0);
                        NewFileWizard.this.fileUpload = new FileReference(fileUpload);
                    }

                    target.add(form);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(form);
                }

                @Override
                public boolean isEnabled() {
                    return fileUpload == null;
                }

            });

            add(new Label("selection", new PropertyModel<String>(NewFileWizard.this, "fileMsg")).setEscapeModelStrings(false));

        }

        @Override
        public void applyState() {

            if (fileUpload != null) {
                Source source = getResource();
                source.setName(fileUpload.fileName.replaceAll("[^\\w-.\\+]", "_"));
                source.setContentType(fileUpload.contentType);

            }
        }


        @Override
        public boolean isComplete() {
            return fileUpload != null;
        }

    }

    public List<FileUpload> getTmpFile() {
        return tmpFile;
    }

    public void setTmpFile(List<FileUpload> tmpFile) {
        this.tmpFile = tmpFile;
    }

    public String getFileMsg() {

        if (fileUpload == null) {
            return "Please choose a file and click on upload button.";
        }

        return String.format("File \"%s\" (%s) uploaded.",
                fileUpload.fileName,
                humanReadableByteCount(fileUpload.size, true)
        );
    }

    private final class FileReference implements Serializable {

        private String fileName;
        private Long size;
        private String contentType;
        private String tempPath;

        public FileReference(FileUpload upload) {
            this.fileName = upload.getClientFileName();
            this.size = upload.getSize();
            this.contentType = upload.getContentType();

            try {
                this.tempPath = upload.writeToTempFile().getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
