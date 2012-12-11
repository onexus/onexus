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
package org.onexus.ui.api.pages.resource.modals;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.resource.api.IResourceManager;
import org.onexus.ui.api.pages.resource.ResourcesPage;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportProjectModal extends Panel {

    private static final Logger log = LoggerFactory.getLogger(ImportProjectModal.class);

    @PaxWicketBean(name = "resourceManager")
    public IResourceManager resourceManager;

    private String projectURL;

    public ImportProjectModal(String id) {
        super(id);

        Form form = new Form<String>("form") {
            @Override
            protected void onSubmit() {

                try {
                    if (!projectURL.contains("://")) {
                        projectURL = "file://" + projectURL;
                    }

                    int lastSep = projectURL.lastIndexOf("/");
                    String projectName = projectURL.substring(lastSep);
                    resourceManager.importProject(projectName, projectURL);
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
