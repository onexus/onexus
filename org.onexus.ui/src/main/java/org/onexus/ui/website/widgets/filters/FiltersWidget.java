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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.onexus.core.query.In;
import org.onexus.ui.website.events.EventFiltersUpdate;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.pages.browser.BrowserPage;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.utils.panels.HelpMark;
import org.onexus.ui.website.utils.visible.VisiblePredicate;
import org.onexus.ui.website.widgets.Widget;

import java.util.Collection;
import java.util.List;

public class FiltersWidget extends Widget<FiltersWidgetConfig, FiltersWidgetStatus> {


    public FiltersWidget(String componentId, IModel<FiltersWidgetStatus> statusModel) {
        super(componentId, statusModel);

        onEventFireUpdate(EventQueryUpdate.class);

        String title = getConfig().getTitle();
        add(new Label("title", (title != null ? title : "Filters")));

        Form<String> form = new Form<String>("form");
        add(form);

        form.add(new ListView<FilterConfig>("filters", new PropertyModel<List<? extends FilterConfig>>(statusModel, "filters")) {

            @Override
            protected void populateItem(final ListItem<FilterConfig> item) {

                FilterConfig filter = item.getModelObject();
                BrowserPageStatus browserStatus = getPageStatus();

                VisiblePredicate fixedPredicate = new VisiblePredicate(getReleaseUri(), browserStatus.getFilters());

                if (!filter.getHidden() && fixedPredicate.evaluate(filter)) {

                    item.add(new CheckBoxItem("checkboxItem", item) {

                        @Override
                        public void onItemSelected(AjaxRequestTarget target, FilterConfig filter) {

                            send(getPage(), Broadcast.BREADTH, EventFiltersUpdate.EVENT);
                        }

                        @Override
                        protected void onItemDeleted(AjaxRequestTarget target, FilterConfig filter) {

                            getStatus().getFilters().remove(filter);

                            sendEvent(EventFiltersUpdate.EVENT);

                        }

                    });

                    // Help?
                    if (filter.getHtmlHelp() != null) {
                        item.add(new HelpMark("helpFilterPanel", "", filter.getHtmlHelp()));
                    } else {
                        item.add(new EmptyPanel("helpFilterPanel"));
                    }

                } else {
                    item.setVisible(false);
                }

            }
        });

        final ModalWindow modal = new ModalWindow("modalWindowAddFilter");
        modal.setContent(new ListItemsFilterPanel(ModalWindow.CONTENT_ID, getConfig().getFieldSelection()) {

            @Override
            public void recuperateFormValues(AjaxRequestTarget target, String filterName, FieldSelection field,
                                             Collection<String> values) {

                List<FilterConfig> filters = getStatus().getFilters();

                FilterConfig filter = new FilterConfig("user-filter-" + String.valueOf(filters.size() + 1), filterName, true);

                String collectionAlias = filterName;

                filter.setDefine(collectionAlias + '=' + field.getCollection());

                In where = new In(collectionAlias, field.getFieldName());
                for (Object value : values) {
                    where.addValue(value);
                }
                filter.setWhere(where.toString());
                filter.setDeletable(true);
                filters.add(filter);

                modal.close(target);

                sendEvent(EventFiltersUpdate.EVENT);

            }

            @Override
            public void cancel(AjaxRequestTarget target) {
                modal.close(target);
            }

        });
        add(modal);

        // Add Filter link - Only visible if there is fields to be viewed
        WebMarkupContainer addLink = new AjaxLink<String>("addFilter") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                modal.show(target);
            }

        };
        addLink.setOutputMarkupPlaceholderTag(true);
        form.add(addLink);
        addLink.setVisible(Boolean.TRUE.equals(getConfig().getUserFilters()));

    }

    private BrowserPageStatus getPageStatus() {
        return findParent(BrowserPage.class).getStatus();
    }

}
