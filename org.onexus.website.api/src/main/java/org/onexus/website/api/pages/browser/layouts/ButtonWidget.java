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
package org.onexus.website.api.pages.browser.layouts;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.Strings;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.events.AbstractEvent;
import org.onexus.website.api.events.EventCloseModal;
import org.onexus.website.api.events.EventPanel;
import org.onexus.website.api.events.EventQueryUpdate;
import org.onexus.website.api.pages.browser.BrowserPageStatus;
import org.onexus.website.api.widgets.IWidgetManager;
import org.onexus.website.api.widgets.Widget;
import org.onexus.website.api.widgets.WidgetConfig;
import org.onexus.website.api.widgets.WidgetModel;
import org.onexus.website.api.widgets.WidgetStatus;

import javax.inject.Inject;

public class ButtonWidget extends EventPanel {

    @Inject
    private IWidgetManager widgetManager;

    private WidgetConfig widgetConfig;
    private IModel<BrowserPageStatus> pageModel;
    private WebMarkupContainer widgetModal;

    public ButtonWidget(String id, final WidgetConfig widgetConfig, final IModel<BrowserPageStatus> pageModel) {
        super(id);
        onEventFireUpdate(EventQueryUpdate.class);


        this.widgetConfig = widgetConfig;
        this.pageModel = pageModel;

        this.widgetModal = new WebMarkupContainer("widgetModal");
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

        if (!Strings.isEmpty(widgetConfig.getWidth())) {
            int width = Integer.valueOf(widgetConfig.getWidth());
            int marginLeft = width / 2;
            widgetModal.add(new AttributeModifier("style", "width: " + width + "px; margin-left: -" + marginLeft + "px;"));
        }

        add(widgetModal);


        Label button = new Label("button", new PropertyModel<String>(this, "buttonText"));
        button.setEscapeModelStrings(false);
        button.setOutputMarkupId(true);

        if (widgetConfig.getTitle() != null) {
            button.add(new AttributeModifier("title", widgetConfig.getTitle()));
            button.add(new AttributeModifier("rel", "tooltip"));
            widgetModal.add(new Label("modalHeader", widgetConfig.getTitle()));
        } else {
            widgetModal.add(new Label("modalHeader", ""));
        }

        button.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                Widget<?, ?> widgetPanel = getWidgetManager().create("widget", new WidgetModel(widgetConfig.getId(), pageModel));
                widgetModal.addOrReplace(widgetPanel);
                target.add(widgetModal);
                target.appendJavaScript("$('#" + widgetModal.getMarkupId() + "').modal('show')");
            }
        });

        add(button);

    }

    private IWidgetManager getWidgetManager() {

        if (widgetManager == null) {
            WebsiteApplication.inject(this);
        }

        return widgetManager;
    }

    public String getButtonText() {
        WidgetStatus status = pageModel.getObject().getWidgetStatus(widgetConfig.getId());
        String buttonText = widgetConfig.getButton();
        if (status != null) {
            buttonText = status.getButton();
        }
        return buttonText;
    }

    @Override
    protected void onRegisteredEvent(AjaxRequestTarget target, AbstractEvent event) {
        target.add(get("button"));
    }

    @Override
    public void onEvent(IEvent<?> event) {

        if (event.getPayload() instanceof EventCloseModal) {
            AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);

            if (target != null) {
                target.prependJavaScript("$('#" + widgetModal.getMarkupId() + "').modal('hide')");
            }
        }

        super.onEvent(event);
    }
}
