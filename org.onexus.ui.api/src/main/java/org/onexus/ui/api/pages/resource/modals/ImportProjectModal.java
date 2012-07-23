package org.onexus.ui.api.pages.resource.modals;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.resource.api.IResourceManager;
import org.onexus.ui.api.pages.resource.ResourcesPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class ImportProjectModal extends Panel {

    private static final Logger log = LoggerFactory.getLogger(ImportProjectModal.class);

    @Inject
    public IResourceManager resourceManager;

    private String projectURL;

    public ImportProjectModal(String id) {
        super(id);

        Form form = new Form<String>("form") {
            @Override
            protected void onSubmit() {

                try {

                    resourceManager.importProject(projectURL);
                    PageParameters parameters = new PageParameters();
                    parameters.set(ResourcesPage.PARAMETER_RESOURCE, projectURL);
                    setResponsePage(ResourcesPage.class, parameters);

                } catch (Exception e) {
                    error("Invalid Onexus project");
                    log.error("Invalid Onexus project", e);
                }

            }
        };

        form.add(new TextField<String>("text", new PropertyModel<String>(this, "projectURL")));

        add(form);
    }

    public String getProjectURL() {
        return projectURL;
    }

    public void setProjectURL(String projectURL) {
        this.projectURL = projectURL;
    }
}
