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
package org.onexus.website.api.utils.panels.ondomready;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public abstract class OnDomReadyPanel extends Panel {

    private static final CssReferenceHeaderItem CSS = CssHeaderItem.forReference(new PackageResourceReference(OnDomReadyPanel.class, "OnDomReadyPanel.css"));
    public static final ResourceReference LOADING_IMAGE = new PackageResourceReference(OnDomReadyPanel.class, "OnDomReadyLoading.gif");

    public OnDomReadyPanel(String id) {
        this(id, null);
    }

    public OnDomReadyPanel(String id, IModel<?> model) {
        super(id, model);
        setOutputMarkupId(true);

        add(new AjaxEventBehavior("domready") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {

                MarkupContainer parent = OnDomReadyPanel.this.getParent();

                Panel newPanel = onDomReadyPanel(OnDomReadyPanel.this.getId());
                newPanel.setOutputMarkupId(true);

                parent.addOrReplace(newPanel);
                target.add(newPanel);
            }
        });

        add(new Image("loading", LOADING_IMAGE));

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CSS);
    }

    protected abstract Panel onDomReadyPanel(String componentId);


}
