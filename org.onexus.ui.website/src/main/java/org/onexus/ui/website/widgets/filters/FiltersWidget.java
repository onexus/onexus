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
package org.onexus.ui.website.widgets.filters;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.onexus.core.query.In;
import org.onexus.ui.website.events.EventAddFilter;
import org.onexus.ui.website.events.EventFiltersUpdate;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.events.EventRemoveFilter;
import org.onexus.ui.website.pages.browser.BrowserPage;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.pages.browser.IFilter;
import org.onexus.ui.website.utils.visible.VisiblePredicate;
import org.onexus.ui.website.widgets.Widget;

import java.util.Collection;
import java.util.List;

public class FiltersWidget extends Widget<FiltersWidgetConfig, FiltersWidgetStatus> {


    public FiltersWidget(String componentId, IModel<FiltersWidgetStatus> statusModel) {
        super(componentId, statusModel);
        onEventFireUpdate(EventQueryUpdate.class);

        // Filters list
        final Form<String> form = new Form<String>("form");
        form.setOutputMarkupId(true);
        form.add(new ListView<FilterConfig>("filters", new PropertyModel<List<? extends FilterConfig>>(statusModel, "filters")) {

            @Override
            protected void populateItem(final ListItem<FilterConfig> item) {

                final FilterConfig filterConfig = item.getModelObject();
                BrowserPageStatus browserStatus = getPageStatus();

                VisiblePredicate fixedPredicate = new VisiblePredicate(getReleaseUri(), browserStatus.getFilters());

                if (fixedPredicate.evaluate(filterConfig)) {

                    item.setOutputMarkupId(true);

                    // Add button
                    item.add(new AjaxLink<String>("add") {

                        @Override
                        public void onClick(AjaxRequestTarget target) {

                            List<IFilter> filters = findParent(BrowserPage.class).getStatus().getFilters();
                            filters.add(new BrowserFilter(filterConfig));
                            send(getPage(), Broadcast.BREADTH, EventAddFilter.EVENT);

                        }

                        @Override
                        public boolean isVisible() {

                            List<IFilter> filters = findParent(BrowserPage.class).getStatus().getFilters();

                            for (IFilter filter : filters) {
                                if (filterConfig.equals(filter.getFilterConfig())) {
                                    return false;
                                }
                            }
                            return true;

                        }
                    });

                    // Remove button
                    item.add(new AjaxLink<String>("remove") {

                        @Override
                        public void onClick(AjaxRequestTarget target) {

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
                            }

                            send(getPage(), Broadcast.BREADTH, EventRemoveFilter.EVENT);

                        }

                        @Override
                        public boolean isVisible() {

                            List<IFilter> filters = findParent(BrowserPage.class).getStatus().getFilters();

                            for (IFilter filter : filters) {
                                if (filterConfig.equals(filter.getFilterConfig())) {
                                    return true;
                                }
                            }
                            return false;

                        }
                    });

                    // Title
                    item.add(new Label("name", new TextFormaterPropertyModel(item.getModel(), "name", 65, true)));

                    if (item.getModelObject().getHelp()!=null) {
                        add(new AttributeModifier("title", new PropertyModel<String>(item.getModel(), "help")));
                        add(new AttributeModifier("rel", "tooltip"));
                        add(new AttributeModifier("data-placement", "right"));
                    }

                } else {
                    item.setVisible(false);
                }

            }
        });

        // Custom filter accordion
        UserFilterPanel customFilter = new UserFilterPanel("customFilter", getConfig().getFieldSelection()) {

            @Override
            public void recuperateFormValues(AjaxRequestTarget target, String filterName, FieldSelection field, Collection<String> values) {

                List<FilterConfig> filters = getStatus().getFilters();
                FilterConfig filter = new FilterConfig("user-filter-" + String.valueOf(filters.size() + 1), filterName);
                filter.setCollection(field.getCollection());
                filter.setDefine("fc='" + field.getCollection() + "'");
                In where = new In("fc", field.getFieldName());
                for (Object value : values) { where.addValue(value); }
                filter.setWhere(where.toString());
                filters.add(filter);
                target.add(form);
                sendEvent(EventFiltersUpdate.EVENT);

            }

            @Override
            public void cancel(AjaxRequestTarget target) {
            }

        };
        customFilter.setVisible(Boolean.TRUE.equals(getConfig().getUserFilters()));

        // Add components
        add(form);
        add(customFilter);
    }

    private BrowserPageStatus getPageStatus() {
        return findParent(BrowserPage.class).getStatus();
    }

}
