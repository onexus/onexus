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
package org.onexus.ui.website.pages.browser;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.core.ICollectionManager;
import org.onexus.core.IResourceManager;
import org.onexus.core.query.Query;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.website.WebsiteStatus;
import org.onexus.ui.website.events.EventFiltersUpdate;
import org.onexus.ui.website.events.EventFixEntity;
import org.onexus.ui.website.events.EventPanel;
import org.onexus.ui.website.events.EventUnfixEntity;
import org.onexus.ui.website.pages.PageStatus;
import org.onexus.ui.website.widgets.Widget;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class FiltersPanel extends EventPanel {

    @Inject
    public IResourceManager resourceManager;

    @Inject
    public ICollectionManager collectionManager;


    public FiltersPanel(String id, IModel<BrowserPageStatus> pageModel) {
        super(id, pageModel);

        // Update this component if this events are fired.
        onEventFireUpdate(EventFixEntity.class, EventUnfixEntity.class, EventFiltersUpdate.class);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        RepeatingView filterRules = new RepeatingView("fixedEntities");

        List<IFilter> filters = getBrowserPage().getFilters();

        if (filters != null && !filters.isEmpty()) {

            Query query = getQuery();

            for (int i=0; i < filters.size(); i++ ) {

                IFilter filter = filters.get(i);

                WebMarkupContainer container = new WebMarkupContainer(filterRules.newChildId());

                // Add new fixed entity
                container.add(new Label("collectionLabel", filter.getLabel(query)));
                Label labelComponent = new Label("entityLabel", filter.getTitle(query));
                labelComponent.setEscapeModelStrings(false);
                container.add(labelComponent);
                container.add(filter.getTooltip("box", query));

                BrowserPageLink<Integer> removeLink = new BrowserPageLink<Integer>("remove", Model.of(i)) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        Integer pos = getModelObject();
                        getBrowserPageStatus().getFilters().remove(pos.intValue());
                        sendEvent(EventUnfixEntity.EVENT);
                    }

                };
                removeLink.add(new Image("close", "close.png"));
                removeLink.setVisible(filter.isDeletable());

                if (filter.isEnable()) {
                    container.add(new AttributeModifier("class", "large awesome blue"));
                } else {
                    container.add(new AttributeModifier("class", "large awesome gray"));
                }

                container.add(removeLink);
                filterRules.add(container);
            }
        }

        addOrReplace(filterRules);

    }

    private BrowserPageStatus getBrowserPage() {
        return (BrowserPageStatus) getDefaultModelObject();
    }

    protected Query getQuery() {
        PageStatus pageStatus = Widget.findParentStatus(getDefaultModel(), PageStatus.class);
        return (pageStatus == null ? null : pageStatus.buildQuery(getBaseUri()));
    }

    protected String getBaseUri() {
        WebsiteStatus websiteStatus = Widget.findParentStatus(getDefaultModel(), WebsiteStatus.class);
        return (websiteStatus == null ? null : ResourceUtils.getParentURI(websiteStatus.getConfig().getURI()));
    }

}
