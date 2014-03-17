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
package org.onexus.website.widgets.selection;

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
import org.onexus.website.api.FilterConfig;
import org.onexus.website.api.MultipleEntitySelection;
import org.onexus.website.api.events.EventAddFilter;
import org.onexus.website.api.events.EventCloseModal;
import org.onexus.website.api.widgets.Widget;
import org.onexus.website.widgets.browser.BrowserPage;
import org.onexus.website.widgets.browser.BrowserPageStatus;
import org.onexus.website.widgets.selection.custom.CustomFilter;
import org.onexus.website.widgets.selection.custom.ListCustomFilterPanel;
import org.onexus.website.widgets.selection.custom.NumericCustomFilterPanel;

public class FiltersWidget extends Widget<FiltersWidgetConfig, FiltersWidgetStatus> {

    private CustomFilter currentFilter;

    private static final Component EMPTY_CUSTOM_PANEL = new EmptyPanel("customPanel").setOutputMarkupId(true);

    public FiltersWidget(String componentId, IModel<FiltersWidgetStatus> statusModel) {
        super(componentId, statusModel);

        // Filters list
        final Form<String> filtersForm = new Form<String>("filtersForm");
        filtersForm.setOutputMarkupId(true);
        filtersForm.add(new ListView<FilterConfig>("filters", getConfig().getFilters()) {

            @Override
            protected void populateItem(final ListItem<FilterConfig> item) {

                final FilterConfig filterConfig = item.getModelObject();

                item.setOutputMarkupId(true);

                // Add button
                item.add(new AjaxLink<String>("apply") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        applyFilter(filterConfig, target);
                    }

                });

                // Title
                item.add(new Label("name", new TextFormaterPropertyModel(item.getModel(), "name", 65, true)));


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

        selector.add(new AjaxFormComponentUpdatingBehavior("onchange") {
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
                    FiltersWidget.this.addOrReplace(new NumericCustomFilterPanel("customPanel", customFilter, FiltersWidget.this.getBaseOri()) {
                        @Override
                        protected void addFilter(AjaxRequestTarget target, FilterConfig filterConfig) {
                            addCustomFilter(target, filterConfig);
                        }
                    });
                }

                target.add(FiltersWidget.this.get("customPanel"));

            }

            private void addCustomFilter(AjaxRequestTarget target, FilterConfig filter) {
                applyFilter(filter, target);
            }

        });
        selectForm.add(selector);
        add(selectForm);

        // Add custom filter panel
        add(EMPTY_CUSTOM_PANEL);


    }

    protected void applyFilter(FilterConfig filterConfig, AjaxRequestTarget target) {
        getPageStatus().addEntitySelection(new MultipleEntitySelection(filterConfig));
        send(getPage(), Broadcast.BREADTH, EventAddFilter.EVENT);
        send(getPage(), Broadcast.BREADTH, EventCloseModal.EVENT);
    }

    public CustomFilter getCurrentFilter() {
        return currentFilter;
    }

    public void setCurrentFilter(CustomFilter currentFilter) {
        this.currentFilter = currentFilter;
    }

    protected BrowserPageStatus getPageStatus() {
        return findParent(BrowserPage.class).getStatus();
    }
}
