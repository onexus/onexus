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
package org.onexus.website.api.pages.browser;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;
import org.apache.wicket.model.Model;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteConfig;
import org.onexus.website.api.WebsiteStatus;
import org.onexus.website.api.events.EventAddFilter;
import org.onexus.website.api.events.EventFiltersUpdate;
import org.onexus.website.api.events.EventPanel;
import org.onexus.website.api.events.EventRemoveFilter;
import org.onexus.website.api.pages.PageConfig;
import org.onexus.website.api.pages.PageStatus;
import org.onexus.website.api.pages.search.SearchPage;
import org.onexus.website.api.pages.search.SearchPageConfig;
import org.onexus.website.api.pages.search.SearchPageStatus;
import org.onexus.website.api.widgets.Widget;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.util.Collection;

public class SelectionPanel extends EventPanel {

    @PaxWicketBean(name = "resourceManager")
    public transient IResourceManager resourceManager;

    @PaxWicketBean(name = "collectionManager")
    public transient ICollectionManager collectionManager;

    private WebMarkupContainer widgetModal;

    public SelectionPanel(String id, IModel<BrowserPageStatus> pageModel) {
        super(id, pageModel);

        // Update this component if this events are fired.
        onEventFireUpdate(EventAddFilter.class, EventRemoveFilter.class, EventFiltersUpdate.class);

        widgetModal = new WebMarkupContainer("widgetModal");
        widgetModal.setOutputMarkupId(true);

        widgetModal.add(new EmptyPanel("widget"));

        widgetModal.add(new AjaxLink<String>("close") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                Component widget = widgetModal.get("widget");
                if (widget instanceof Widget) {
                    ((Widget) widget).onClose(target);
                }
                target.appendJavaScript("$('#" + widgetModal.getMarkupId() + "').modal('hide')");
            }
        });

        add(widgetModal);

        widgetModal.add(new Label("modalHeader", "Selection details"));
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        RepeatingView filterRules = new RepeatingView("filter");

        Collection<IEntitySelection> filters = getBrowserPage().getEntitySelections();

        if (filters != null && !filters.isEmpty()) {

            Query query = getQuery();

            for (IEntitySelection filter : filters) {

                WebMarkupContainer container = new WebMarkupContainer(filterRules.newChildId());

                final String title = filter.getTitle(query);

                // Add new fixed entity
                BrowserPageLink<ORI> editLink = new BrowserPageLink<ORI>("edit") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {

                        SearchPageStatus status = new SearchPageStatus();
                        WebsiteStatus websiteStatus = getWebsiteStatus();
                        WebsiteConfig websiteConfig = websiteStatus.getConfig();

                        SearchPageConfig searchPageConfig = null;
                        for (PageConfig config : websiteConfig.getPages()) {
                            if (config instanceof SearchPageConfig) {
                                searchPageConfig = (SearchPageConfig) config;
                            }
                        }

                        status.setSearch(title);
                        status.setConfig(searchPageConfig);

                        widgetModal.addOrReplace(new SearchPage("widget", new Model<SearchPageStatus>(status), false, false));

                        target.add(widgetModal);

                        target.appendJavaScript("$('#" + widgetModal.getMarkupId() + "').modal('show')");
                    }
                };

                Label labelComponent = new Label("title", title);
                labelComponent.setEscapeModelStrings(false);
                editLink.add(labelComponent);
                container.add(editLink);


                BrowserPageLink<ORI> removeLink = new BrowserPageLink<ORI>("remove", Model.of(filter.getSelectionCollection())) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        getBrowserPageStatus().removeEntitySelection(getModelObject());
                        sendEvent(EventRemoveFilter.EVENT);
                    }

                };

                if (filter.isEnable()) {
                    container.add(new AttributeModifier("class", "btn btn-large"));
                } else {
                    container.add(new AttributeModifier("class", "btn btn-large disabled"));
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
        PageStatus pageStatus = findParentStatus(getDefaultModel(), PageStatus.class);
        return (pageStatus == null ? null : pageStatus.buildQuery(getBaseUri()));
    }

    protected ORI getBaseUri() {
        WebsiteStatus websiteStatus = getWebsiteStatus();
        return (websiteStatus == null ? null : websiteStatus.getConfig().getORI().getParent());
    }

    protected WebsiteStatus getWebsiteStatus() {
        return findParentStatus(getDefaultModel(), WebsiteStatus.class);
    }

    private static <T> T findParentStatus(IModel<?> model, Class<T> statusClass) {

        Object obj = model.getObject();

        if (obj != null && statusClass.isAssignableFrom(obj.getClass())) {
            return (T) obj;
        }

        if (model instanceof IWrapModel) {
            IModel<?> parentModel = ((IWrapModel) model).getWrappedModel();
            return findParentStatus(parentModel, statusClass);
        }

        return null;
    }

}
