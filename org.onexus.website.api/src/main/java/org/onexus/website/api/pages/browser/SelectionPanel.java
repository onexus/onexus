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
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.In;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteConfig;
import org.onexus.website.api.WebsiteStatus;
import org.onexus.website.api.events.AbstractEvent;
import org.onexus.website.api.events.EventAddFilter;
import org.onexus.website.api.events.EventFiltersUpdate;
import org.onexus.website.api.events.EventPanel;
import org.onexus.website.api.events.EventRemoveFilter;
import org.onexus.website.api.pages.PageConfig;
import org.onexus.website.api.pages.PageStatus;
import org.onexus.website.api.pages.search.SearchPage;
import org.onexus.website.api.pages.search.SearchPageConfig;
import org.onexus.website.api.pages.search.SearchPageStatus;
import org.onexus.website.api.pages.search.SearchType;
import org.onexus.website.api.pages.search.boxes.BoxesPanel;
import org.onexus.website.api.widgets.Widget;
import org.onexus.website.api.widgets.selection.FilterConfig;
import org.onexus.website.api.widgets.selection.MultipleEntitySelection;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

public class SelectionPanel extends EventPanel {

    @Inject
    private IResourceManager resourceManager;

    @Inject
    private ICollectionManager collectionManager;

    private WebMarkupContainer widgetModal;

    public SelectionPanel(String id, IModel<BrowserPageStatus> pageModel) {
        super(id, pageModel);

        // Update this component if this events are fired.
        onEventFireUpdate(EventAddFilter.class, EventRemoveFilter.class, EventFiltersUpdate.class);

        // Create a new selection
        add(new BrowserPageLink<ORI>("new") {
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

                status.setConfig(searchPageConfig);

                widgetModal.addOrReplace(new Label("modalHeader", "New selection"));
                widgetModal.addOrReplace(
                        new SearchPage("widget", new Model<SearchPageStatus>(status), true, false) {
                            @Override
                            protected void onSubmit(SearchPageStatus status, ORI baseUri, FilterConfig filterConfig) {
                                SelectionPanel.this.onSubmit(status, baseUri, filterConfig);
                            }
                        }
                );

                target.add(widgetModal);

                target.appendJavaScript("$('#" + widgetModal.getMarkupId() + "').modal('show')");
            }
        });


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

    protected void onSubmit(SearchPageStatus status, ORI baseUri, FilterConfig filterConfig) {

        SearchType type = status.getType();

        if (status.getSearch() != null) {

            ORI collectionUri = type.getCollection().toAbsolute(baseUri);
            if (filterConfig == null && status.getSearch().indexOf(',') == -1) {
                IEntityTable table = BoxesPanel.getSingleEntityTable(collectionManager, type, collectionUri, baseUri, status.getSearch(), true);
                if (table.next()) {

                    // Single entity selection
                    IEntity entity = table.getEntity(collectionUri);
                    getBrowserPage().addEntitySelection(new SingleEntitySelection(entity));

                }
                table.close();
            } else {

                // Multiple entities selection
                if (filterConfig == null) {
                    filterConfig = new FilterConfig(status.getSearch());

                    filterConfig.setCollection(collectionUri);
                    filterConfig.setDefine("fc='" + collectionUri + "'");
                    String mainKey = type.getKeysList().get(0);
                    In where = new In("fc", mainKey);
                    String[] values = status.getSearch().split(",");
                    for (String value : values) {
                        where.addValue(value.trim());
                    }
                    filterConfig.setWhere(where.toString());

                }

                getBrowserPage().addEntitySelection(new MultipleEntitySelection(filterConfig));

            }
        }

        sendEvent(EventAddFilter.EVENT);

    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        WebMarkupContainer filtersContainer = new WebMarkupContainer("container");
        filtersContainer.setOutputMarkupId(true);

        RepeatingView filterRules = new RepeatingView("filter");
        filtersContainer.add(filterRules);

        final List<IEntitySelection> selections = getBrowserPage().getEntitySelections();

        if (selections != null && !selections.isEmpty()) {

            Query query = getQuery();

            for (IEntitySelection selection : selections) {

                WebMarkupContainer container = new WebMarkupContainer(filterRules.newChildId());


                final ORI filterORI = selection.getSelectionCollection();
                final String filterTitle = selection.getTitle(query);

                // Abbreviate at the 3th comma
                String abbreviateFilterTitle = filterTitle;
                int pos = filterTitle.indexOf(',');
                if (pos > -1) {
                    pos = filterTitle.indexOf(',', pos+1);
                    if (pos > -1) {
                        pos = filterTitle.indexOf(',', pos+1);
                        if (pos > -1) {
                            abbreviateFilterTitle = filterTitle.substring(0, pos) + "...";
                        }
                    }
                }

                // Edit a selection
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

                        for (SearchType searchType : searchPageConfig.getTypes()) {
                            if (searchType.getCollection().equals(filterORI)) {
                                status.setType(searchType);
                                break;
                            }
                        }

                        status.setSearch(filterTitle);
                        status.setConfig(searchPageConfig);

                        widgetModal.addOrReplace(new Label("modalHeader", "Selection details"));
                        widgetModal.addOrReplace(new SearchPage("widget", new Model<SearchPageStatus>(status), false, false) {
                            @Override
                            protected void onSubmit(SearchPageStatus status, ORI baseUri, FilterConfig filterConfig) {
                                SelectionPanel.this.onSubmit(status, baseUri, filterConfig);
                            }
                        });

                        target.add(widgetModal);

                        target.appendJavaScript("$('#" + widgetModal.getMarkupId() + "').modal('show')");
                    }
                };

                Label labelComponent = new Label("title", abbreviateFilterTitle);
                labelComponent.setEscapeModelStrings(false);
                editLink.add(labelComponent);
                container.add(editLink);

                // Remove link
                BrowserPageLink<IEntitySelection> removeLink = new BrowserPageLink<IEntitySelection>("remove", Model.of(selection)) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        getBrowserPageStatus().removeEntitySelection(getModelObject());
                        sendEvent(EventRemoveFilter.EVENT);
                    }

                };

                if (selection.isEnable()) {
                    container.add(new AttributeModifier("class", "btn btn-large"));
                } else {
                    container.add(new AttributeModifier("class", "btn btn-large disabled"));
                }

                container.add(removeLink);
                filterRules.add(container);
            }
        }

        addOrReplace(filtersContainer);


    }

    @Override
    protected void onRegisteredEvent(AjaxRequestTarget target, AbstractEvent event) {
        target.add(this.get("container"));
    }

    private BrowserPageStatus getBrowserPage() {
        return (BrowserPageStatus) getDefaultModelObject();
    }

    protected Query getQuery() {
        PageStatus pageStatus = findParentStatus(getDefaultModel(), PageStatus.class);
        return pageStatus == null ? null : pageStatus.buildQuery(getBaseUri());
    }

    protected ORI getBaseUri() {
        WebsiteStatus websiteStatus = getWebsiteStatus();
        return websiteStatus == null ? null : websiteStatus.getConfig().getORI().getParent();
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
