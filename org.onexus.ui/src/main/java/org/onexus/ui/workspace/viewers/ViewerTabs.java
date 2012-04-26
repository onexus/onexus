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
package org.onexus.ui.workspace.viewers;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.onexus.core.resources.Resource;
import org.onexus.ui.IViewerCreator;
import org.onexus.ui.workspace.events.EventResourceSelect;

import javax.inject.Inject;
import java.util.List;

public class ViewerTabs extends Panel {

    @Inject
    public IViewersManager viewersManager;

    private TabsListModel tabsModel;
    private IModel<Resource> resourceModel;

    private int currentTab = 0;

    public ViewerTabs(String id, IModel<Resource> model) {
        super(id, model);
        setOutputMarkupId(true);

        this.tabsModel = new TabsListModel();
        this.resourceModel = model;

        // Tabs row
        ListView<IViewerCreator> tabsRow = new ListView<IViewerCreator>("tabs", tabsModel) {

            @Override
            protected void populateItem(final ListItem<IViewerCreator> tab) {

                AjaxLink<String> link = new AjaxLink<String>("link") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {

                        IViewerCreator viewerTab = tab.getModelObject();

                        updatePanel(tab.getIndex(), viewerTab.getPanel("panel", resourceModel));

                        target.add(ViewerTabs.this);

                    }

                };

                link.add(new Label("title", Model.of(tab.getModelObject().getTitle())));
                tab.add(link);

                if (tab.getIndex() == currentTab) {
                    tab.add(new AttributeModifier("class", "selected"));
                }

            }
        };

        add(tabsRow);

        updateDefaultPanel();

    }

    private void updateDefaultPanel() {
        // Set first tab as default
        List<? extends IViewerCreator> tabs = tabsModel.getObject();
        if (tabs.isEmpty()) {
            updatePanel(0, new EmptyPanel("panel"));
        } else {
            IViewerCreator viewerTab = tabs.get(0);
            updatePanel(0, viewerTab.getPanel("panel", resourceModel));
        }
    }

    private void updatePanel(int position, Panel panel) {
        currentTab = position;
        addOrReplace(panel);
    }

    @Override
    public void onEvent(IEvent<?> event) {

        Object payLoad = event.getPayload();
        if (EventResourceSelect.EVENT == payLoad) {
            tabsModel.clear();
            updateDefaultPanel();
            RequestCycle.get().find(AjaxRequestTarget.class).add(this);
        }

    }

    private class TabsListModel extends AbstractReadOnlyModel<List<? extends IViewerCreator>> {

        private transient List<? extends IViewerCreator> viewerTabs;

        @Override
        public List<? extends IViewerCreator> getObject() {

            if (viewerTabs == null) {
                Resource resource = resourceModel.getObject();
                viewerTabs = viewersManager.getViewerCreators(resource);
            }

            return viewerTabs;

        }

        public void clear() {
            this.viewerTabs = null;
        }

    }


}
