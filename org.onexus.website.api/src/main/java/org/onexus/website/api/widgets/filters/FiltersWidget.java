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
package org.onexus.website.api.widgets.filters;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.onexus.website.api.events.EventAddFilter;
import org.onexus.website.api.events.EventRemoveFilter;
import org.onexus.website.api.pages.browser.BrowserPage;
import org.onexus.website.api.pages.browser.BrowserPageStatus;
import org.onexus.website.api.pages.browser.IFilter;
import org.onexus.website.api.utils.visible.VisiblePredicate;
import org.onexus.website.api.widgets.Widget;
import org.onexus.website.api.widgets.filters.custom.CustomFilter;
import org.onexus.website.api.widgets.filters.custom.ListCustomFilterPanel;
import org.onexus.website.api.widgets.filters.custom.NumericCustomFilterPanel;

import java.util.List;

public class FiltersWidget extends Widget<FiltersWidgetConfig, FiltersWidgetStatus> {

    private CustomFilter currentFilter;

    private final static Component EMPTY_CUSTOM_PANEL = new EmptyPanel("customPanel").setOutputMarkupId(true);

    public FiltersWidget(String componentId, IModel<FiltersWidgetStatus> statusModel) {
        super(componentId, statusModel);

        // Filters list
        final Form<String> filtersForm = new Form<String>("filtersForm");
        filtersForm.setOutputMarkupId(true);
        filtersForm.add(new ListView<FilterConfig>("filters", new PropertyModel<List<? extends FilterConfig>>(statusModel, "filters")) {

            @Override
            protected void populateItem(final ListItem<FilterConfig> item) {

                final FilterConfig filterConfig = item.getModelObject();
                BrowserPageStatus browserStatus = getPageStatus();

                VisiblePredicate fixedPredicate = new VisiblePredicate(getPageBaseOri(), browserStatus.getFilters());

                if (fixedPredicate.evaluate(filterConfig)) {

                    item.setOutputMarkupId(true);

                    item.add(new AjaxLink<String>("remove") {

                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            FiltersWidget.this.getStatus().getFilters().remove(filterConfig);
                            unapplyFilter(filterConfig);
                            target.add(filtersForm);
                        }

                        @Override
                        public boolean isVisible() {
                            return filterConfig.isDeletable();
                        }
                    });

                    // Add button
                    item.add(new AjaxLink<String>("apply") {

                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            applyFilter(filterConfig);
                        }

                        @Override
                        public boolean isVisible() {
                            return !isFilterApplyed(filterConfig);
                        }

                    });

                    // Remove button
                    item.add(new AjaxLink<String>("unapply") {

                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            unapplyFilter(filterConfig);
                        }

                        @Override
                        public boolean isVisible() {
                            return isFilterApplyed(filterConfig);
                        }
                    });

                    // Title
                    item.add(new Label("name", new TextFormaterPropertyModel(item.getModel(), "name", 65, true)));

                    if (item.getModelObject().getHelp() != null) {
                        add(new AttributeModifier("title", new PropertyModel<String>(item.getModel(), "help")));
                        add(new AttributeModifier("rel", "tooltip"));
                        add(new AttributeModifier("data-placement", "right"));
                    }

                } else {
                    item.setVisible(false);
                }

            }
        });
        add(filtersForm);

        // Add custom filter select
        Form<String> selectForm = new Form<String>("selectForm");
        DropDownChoice<CustomFilter> selector = new DropDownChoice<CustomFilter>(
                "select",
                new PropertyModel<CustomFilter>(this, "currentFilter"),
                getConfig().getCustomFilters(),
                new ChoiceRenderer<CustomFilter>("title"));

        selector.add( new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                CustomFilter customFilter = getCurrentFilter();

                if (customFilter == null || customFilter.getType() == null) {
                    FiltersWidget.this.addOrReplace(EMPTY_CUSTOM_PANEL);
                } else if (customFilter.getType().equalsIgnoreCase("list")) {
                    FiltersWidget.this.addOrReplace(new ListCustomFilterPanel("customPanel", customFilter) {
                        @Override
                        protected void addFilter(AjaxRequestTarget target, FilterConfig filterConfig) {
                            addCustomFilter(target, filterConfig);
                        }
                    });
                } else if (customFilter.getType().equalsIgnoreCase("numeric")) {
                    FiltersWidget.this.addOrReplace(new NumericCustomFilterPanel("customPanel", customFilter) {
                        @Override
                        protected void addFilter(AjaxRequestTarget target, FilterConfig filterConfig) {
                           addCustomFilter(target, filterConfig);
                        }
                    });
                }

                target.add(FiltersWidget.this.get("customPanel"));

            }

            private void addCustomFilter(AjaxRequestTarget target, FilterConfig filter) {
                List<FilterConfig> filters = getStatus().getFilters();
                filters.add(filter);
                target.add(filtersForm);
                applyFilter(filter);
            }

        });
        selectForm.add(selector);
        add(selectForm);

        // Add custom filter panel
        add(EMPTY_CUSTOM_PANEL);


    }



    private boolean isFilterApplyed(FilterConfig filterConfig) {
        List<IFilter> filters = findParent(BrowserPage.class).getStatus().getFilters();

        for (IFilter filter : filters) {
            if (filterConfig.equals(filter.getFilterConfig())) {
                return true;
            }
        }
        return false;
    }

    private void unapplyFilter(FilterConfig filterConfig) {

        List<IFilter> filters = findParent(BrowserPage.class).getStatus().getFilters();

        IFilter removeMe = null;
        for (IFilter filter : filters) {
            if (filterConfig.equals(filter.getFilterConfig())) {
                removeMe = filter;
                break;
            }
        }

        if (removeMe != null) {
            filters.remove(removeMe);
            send(getPage(), Broadcast.BREADTH, EventRemoveFilter.EVENT);
        }
    }

    private void applyFilter(FilterConfig filterConfig) {
        List<IFilter> filters = findParent(BrowserPage.class).getStatus().getFilters();
        filters.add(new BrowserFilter(filterConfig));
        send(getPage(), Broadcast.BREADTH, EventAddFilter.EVENT);
    }

    private BrowserPageStatus getPageStatus() {
        return findParent(BrowserPage.class).getStatus();
    }

    public CustomFilter getCurrentFilter() {
        return currentFilter;
    }

    public void setCurrentFilter(CustomFilter currentFilter) {
        this.currentFilter = currentFilter;
    }
}
