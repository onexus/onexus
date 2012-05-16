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
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.onexus.core.IQueryParser;
import org.onexus.core.query.Filter;
import org.onexus.core.query.In;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;
import org.onexus.ui.website.events.EventFiltersUpdate;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.pages.IPageModel;
import org.onexus.ui.website.pages.browser.BrowserPageConfig;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.utils.panels.HelpMark;
import org.onexus.ui.website.utils.visible.FixedEntitiesVisiblePredicate;
import org.onexus.ui.website.widgets.IQueryContributor;
import org.onexus.ui.website.widgets.IWidgetModel;
import org.onexus.ui.website.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * FilterBoxPanel contain a list of tab filters that could be actived or
 * inactived.
 * <p/>
 * There are two kind of filters. A predefined ones, and custom filters (this
 * can be deleted).
 *
 * @author armand
 */
public class FiltersWidget extends Widget<FiltersWidgetConfig, FiltersWidgetStatus> implements IQueryContributor {

    private static final Logger log = LoggerFactory.getLogger(FiltersWidget.class);
    private FilterModel model;

    @Inject
    private IQueryParser queryParser;


    public FiltersWidget(String componentId, IWidgetModel<FiltersWidgetStatus> statusModel) {
        super(componentId, statusModel);

        onEventFireUpdate(EventQueryUpdate.class);


        String title = getConfig().getTitle();
        add(new Label("title", (title != null ? title : "Filters")));

        Form<String> form = new Form<String>("form");
        add(form);

        FiltersWidgetConfig config = getConfig();
        if (config.getFilters() != null) {
            FiltersWidgetStatus status = getStatus();
            if (status == null) {
                status = new FiltersWidgetStatus(config.getId());
                for (FilterConfig filter : config.getFilters()) {
                    status.updateFilter(filter);
                }
                statusModel.setObject(status);
            } else {
                for (FilterConfig filter : config.getFilters()) {
                    if (status.getActiveFilters().contains(filter.getId())) {
                        filter.setActive(true);
                    } else {
                        filter.setActive(false);
                    }
                }
            }
        }

        this.model = new FilterModel();

        form.add(new ListView<FilterConfig>("filters", this.model) {

            @Override
            protected void populateItem(final ListItem<FilterConfig> item) {

                FilterConfig filter = item.getModelObject();
                BrowserPageStatus browserStatus = getBrowserPageStatus();

                FixedEntitiesVisiblePredicate fixedPredicate = new FixedEntitiesVisiblePredicate(browserStatus
                        .getReleaseURI(), browserStatus.getFixedEntities());

                if (!filter.getHidden() && fixedPredicate.evaluate(filter)) {

                    item.add(new CheckBoxItem("checkboxItem", item) {

                        @Override
                        public void onItemSelected(AjaxRequestTarget target, FilterConfig filter) {
                            getStatus().updateFilter(filter);

                            send(getPage(), Broadcast.BREADTH, EventFiltersUpdate.EVENT);
                        }

                        @Override
                        protected void onItemDeleted(AjaxRequestTarget target, FilterConfig filter) {

                            getStatus().getUserFilters().remove(filter);

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

                List<FilterConfig> userFilters = getStatus().getUserFilters();

                FilterConfig filter = new FilterConfig("user-filter-" + String.valueOf(userFilters.size() + 1), filterName, true);

                String collectionAlias = filterName;

                filter.setDefine(collectionAlias + '=' + field.getCollection());

                In where = new In(collectionAlias, field.getFieldName());
                for (Object value : values) {
                    where.addValue(value);
                }
                filter.setWhere(where.toString());
                filter.setDeletable(true);
                userFilters.add(filter);

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

    @Override
    public void onQueryBuild(Query query) {

        BrowserPageStatus status = getBrowserPageStatus();

        FixedEntitiesVisiblePredicate fixedPredicate = new FixedEntitiesVisiblePredicate(status.getReleaseURI(), status.getFixedEntities());

        List<Filter> rules = new ArrayList<Filter>();
        for (FilterConfig filter : this.model.getObject()) {
            if (filter.getActive() && fixedPredicate.evaluate(filter)) {

                String oqlDefine = filter.getDefine();
                String oqlWhere = filter.getWhere();

                if (oqlDefine != null && oqlWhere != null) {
                    Map<String, String> define = queryParser.parseDefine(oqlDefine);
                    Filter where = queryParser.parseWhere(oqlWhere);

                if (define == null || where == null) {
                    log.error("Malformed filter definition\n DEFINE: " + filter.getDefine() + "\n WHERE: " + filter.getWhere() + "\n");

                } else {
                    for (Map.Entry<String, String> entry : define.entrySet()) {
                        query.addDefine(entry.getKey(), entry.getValue());
                    }

                    rules.add(where);
                }
                }


            }
        }

        if (!rules.isEmpty()) {

            boolean union = (getConfig().getUnion() != null && getConfig().getUnion().booleanValue());

            if (!union) {
                QueryUtils.and(query, QueryUtils.joinAnd(rules));
            } else {
                QueryUtils.and(query, QueryUtils.joinOr(rules));
            }

        }


    }

    private BrowserPageStatus getBrowserPageStatus() {
        IPageModel pageModel = getPageModel();

        if (pageModel != null && pageModel.getConfig() instanceof BrowserPageConfig) {
            return (BrowserPageStatus) pageModel.getObject();
        }

        return null;
    }

    public class FilterModel extends AbstractReadOnlyModel<List<? extends FilterConfig>> {

        @Override
        public List<? extends FilterConfig> getObject() {

            List<FilterConfig> filters = new ArrayList<FilterConfig>();
            List<FilterConfig> configFilters = FiltersWidget.this.getConfig().getFilters();
            List<FilterConfig> userFilters = FiltersWidget.this.getStatus().getUserFilters();

            if (configFilters != null) {
                filters.addAll(configFilters);
            }

            if (userFilters != null) {
                filters.addAll(userFilters);
            }

            return filters;
        }
    }
}
