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
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.onexus.website.api.widgets.Widget;
import org.onexus.website.api.widgets.WidgetModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FiltersToolbar extends Widget<FiltersToolbarConfig, FiltersToolbarStatus> {

    private WebMarkupContainer widgetModal;

    public FiltersToolbar(String componentId, IModel<FiltersToolbarStatus> statusModel) {
        super(componentId, statusModel);

        setOutputMarkupId(true);

        List<FilterConfig> filters = new ArrayList<FilterConfig>();
        filters.add(new FilterConfig("First filter"));
        filters.add(new FilterConfig("Second filter"));
        getStatus().setFilters(filters);

        widgetModal = new WebMarkupContainer("widgetModal");
        widgetModal.setOutputMarkupId(true);
        widgetModal.add(new FiltersToolbarPanel("widget", statusModel));

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

        add( new AjaxLink<String>("add") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(widgetModal);
                target.appendJavaScript("$('#" + widgetModal.getMarkupId() + "').modal('show')");
            }
        });

    }

    @Override
    protected void onBeforeRender() {

        RepeatingView filters = new RepeatingView("filters");
        for (FilterConfig filter : getStatus().getFilters()) {
            addFilter(filters, filter);
        }

        addOrReplace(filters);

        super.onBeforeRender();
    }

    private void addFilter(RepeatingView filtersView, final FilterConfig filter) {

        WebMarkupContainer container = new WebMarkupContainer(filtersView.newChildId());
        container.add(new Label("title", filter.getName()));
        container.add(new AjaxLink<FilterConfig>("remove") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                  getStatus().getFilters().remove(filter);
                  target.add(FiltersToolbar.this);
            }
        });
        filtersView.add(container);
    }

}
