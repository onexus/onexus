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
package org.onexus.website.api.pages.browser.filters;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.onexus.website.api.events.EventAddFilter;
import org.onexus.website.api.events.EventFilterHeader;
import org.onexus.website.api.events.EventRemoveFilter;
import org.onexus.website.api.pages.browser.BrowserPageStatus;
import org.onexus.website.api.pages.browser.filters.panels.CategoricalFilterPanel;
import org.onexus.website.api.pages.browser.filters.panels.DoubleFilterPanel;
import org.onexus.website.api.pages.browser.filters.panels.IntegerFilterPanel;
import org.onexus.website.api.pages.browser.filters.panels.StringFilterPanel;
import org.onexus.website.api.widgets.Widget;
import org.onexus.website.api.widgets.selection.FilterConfig;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FiltersToolbar extends Panel {

    private WebMarkupContainer widgetModal;

    private IModel<BrowserPageStatus> statusModel;

    public FiltersToolbar(String componentId, IModel<BrowserPageStatus> statusModel) {
        super(componentId);
        setOutputMarkupId(true);

        this.statusModel = statusModel;

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

        widgetModal.add(new Label("modalHeader", "Add filter"));

    }

    @Override
    public void onEvent(IEvent<?> event) {

        if (event.getPayload() instanceof EventFilterHeader) {
            EventFilterHeader e = ((EventFilterHeader) event.getPayload());

            String filter = e.getHeader().getFilter();
            String filterType;
            String options;

            int separator = filter.indexOf('[');
            if (separator != -1) {
                filterType = filter.substring(0, separator).trim();

                int endSeparator = filter.indexOf(']', separator);
                if (endSeparator != -1) {
                    options = filter.substring(separator+1, endSeparator).trim();
                } else {
                    options = filter.substring(separator+1).trim();
                }
            } else {
                filterType = filter.trim();
                options = "";
            }


            if ("STRING".equalsIgnoreCase(filterType)) {
                widgetModal.addOrReplace(new StringFilterPanel("widget", e.getHeader()) {
                    @Override
                    protected void addFilter(AjaxRequestTarget target, FilterConfig filterConfig) {
                        FiltersToolbar.this.addFilter(target, filterConfig);
                    }
                });
            } else if ("DOUBLE".equalsIgnoreCase(filterType)) {
                widgetModal.addOrReplace(new DoubleFilterPanel("widget", e.getHeader()) {

                    @Override
                    protected void addFilter(AjaxRequestTarget target, FilterConfig filterConfig) {
                        FiltersToolbar.this.addFilter(target, filterConfig);
                    }

                });
            } else if ("INTEGER".equalsIgnoreCase(filterType)) {
                widgetModal.addOrReplace(new IntegerFilterPanel("widget", e.getHeader()) {

                    @Override
                    protected void addFilter(AjaxRequestTarget target, FilterConfig filterConfig) {
                        FiltersToolbar.this.addFilter(target, filterConfig);
                    }

                });
            } else if ("CATEGORICAL".equalsIgnoreCase(filterType)) {

                List<String> values = Arrays.asList(StringUtils.split(options, ','));

                widgetModal.addOrReplace(new CategoricalFilterPanel("widget", e.getHeader(), values) {

                    @Override
                    protected void addFilter(AjaxRequestTarget target, FilterConfig filterConfig) {
                        FiltersToolbar.this.addFilter(target, filterConfig);
                    }

                });
            }

            widgetModal.addOrReplace(new Label("modalHeader", "Filter column '" + e.getHeader().getLabel() + "'"));

            e.getTarget().add(widgetModal);
            e.getTarget().appendJavaScript("$('#" + widgetModal.getMarkupId() + "').modal('show')");
        }

    }

    protected void addFilter(AjaxRequestTarget target, FilterConfig filterConfig) {

        statusModel.getObject().getCurrentFilters().add(filterConfig);

        target.add(this);
        target.prependJavaScript("$('#" + widgetModal.getMarkupId() + "').modal('hide')");
        send(getPage(), Broadcast.BREADTH, EventAddFilter.EVENT);
    }


    @Override
    protected void onBeforeRender() {

        RepeatingView filtersView = new RepeatingView("filters");
        Iterator<FilterConfig> itFilters = statusModel.getObject().getCurrentFilters().iterator();
        while (itFilters.hasNext()) {
            addFilter(filtersView, itFilters.next(), itFilters.hasNext());
        }

        addOrReplace(filtersView);

        super.onBeforeRender();
    }

    private void addFilter(RepeatingView filtersView, final FilterConfig filter, boolean hasNext) {

        WebMarkupContainer container = new WebMarkupContainer(filtersView.newChildId());
        WebMarkupContainer labelContainer = new WebMarkupContainer("container");
        labelContainer.add(new Label("title", filter.getName()));
        labelContainer.add(new AjaxLink<FilterConfig>("remove") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                statusModel.getObject().getCurrentFilters().remove(filter);
                target.add(FiltersToolbar.this);
                send(getPage(), Broadcast.BREADTH, EventRemoveFilter.EVENT);
            }
        });

        if (filter.isEnable()) {
            labelContainer.add(new AttributeModifier("class", "label label-important"));
        } else {
            labelContainer.add(new AttributeModifier("class", "label"));
        }

        container.add(labelContainer);
        container.add(new WebMarkupContainer("operator").setVisible(hasNext));

        filtersView.add(container);
    }

}
