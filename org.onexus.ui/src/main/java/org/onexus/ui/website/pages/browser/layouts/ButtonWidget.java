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
package org.onexus.ui.website.pages.browser.layouts;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.onexus.ui.website.widgets.Widget;

public class ButtonWidget extends Panel {

    public ButtonWidget(String id, String label, Widget<?, ?> widget) {
        super(id);

        Label button = new Label("button", label);

        button.add( new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                Component widget = ButtonWidget.this.get("widgetContainer");
                widget.setVisible(!widget.isVisible());
                target.add(widget);
            }
        });

        add(button);

        WebMarkupContainer widgetContainer = new WebMarkupContainer("widgetContainer");
        widgetContainer.setOutputMarkupPlaceholderTag(true);
        widgetContainer.setVisible(false);
        widgetContainer.add(widget);

        Image close = new Image("close", new PackageResourceReference(ButtonWidget.class, "close.png"));
        close.add( new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                Component widget = ButtonWidget.this.get("widgetContainer");
                widget.setVisible(!widget.isVisible());
                target.add(widget);
            }
        });

        widgetContainer.add(close);
        add(widgetContainer);

    }
}
