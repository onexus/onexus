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
package org.onexus.ui.workspace.internal.viewers.wizards;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.onexus.resource.api.Resource;
import org.onexus.ui.api.utils.panels.HelpMark;
import org.onexus.ui.api.wizards.IWizardCreator;
import org.onexus.ui.api.wizards.IWizardsManager;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.util.List;

public class WizardViewer extends Panel {

    @PaxWicketBean(name="wizardsManager")
    private IWizardsManager wizardsManager;

    private IModel<? extends Resource> resourceModel;
    private WebMarkupContainer wizardContainer;

    public WizardViewer(String id, IModel<? extends Resource> model) {
        super(id);
        this.resourceModel = model;

        add(new ListView<IWizardCreator>("wizardList", new WizardCreatorsModel()) {

            @Override
            protected void populateItem(final ListItem<IWizardCreator> item) {

                AjaxLink<String> link = new AjaxLink<String>("link") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        IWizardCreator wizard = item.getModelObject();
                        wizardContainer.addOrReplace(wizard.getPanel("wizard", resourceModel));
                        target.add(wizardContainer);
                    }
                };

                IWizardCreator wizard = item.getModelObject();
                String label = wizard.getLabel();
                String title = wizard.getTitle();
                String description = wizard.getDescription();

                link.add(new Label("label", label));
                item.add(link);

                item.add(new Label("title", title));

                if ( description != null ) {
                    item.add(new HelpMark("description", title, description));
                } else {
                    item.add(new EmptyPanel("description"));
                }

            }
        });

        wizardContainer = new WebMarkupContainer("wizardContainer") {

            @Override
            public boolean isVisible() {
                Component wizard = get("wizard");

                if (wizard == null || !wizard.isVisible()) {
                    return false;
                }

                return true;
            }

        };
        wizardContainer.setOutputMarkupPlaceholderTag(true);
        wizardContainer.add(new EmptyPanel("wizard").setVisible(false));
        add(wizardContainer);
    }

    private class WizardCreatorsModel extends AbstractReadOnlyModel<List<? extends IWizardCreator>> {

        @Override
        public List<? extends IWizardCreator> getObject() {
            return wizardsManager.getWizardCreators(resourceModel.getObject());
        }

    }

}
