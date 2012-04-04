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
package org.onexus.ui.workspace.pages;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.resource.CssResourceReference;
import org.onexus.ui.OnexusWebSession;
import org.onexus.ui.workspace.pages.tools.AbstractTool;

@AuthorizeInstantiation("onexus-admin")
public class BasePage extends WebPage {

    public static final CssResourceReference DEFAULT_CSS = new CssResourceReference(ResourcesPage.class, "BasePage.css");
    public static final CssResourceReference STYLE_CSS = new CssResourceReference(ResourcesPage.class, "style.css");

    private RepeatingView toolbar;

    public BasePage() {
        super();

        RepeatingView menu = new RepeatingView("menu");

        addMenuItem(menu, ResourcesPage.class, "Resources");

        add(menu);

        // add(new TaskStatusProgress("progressbar"));
        add(new EmptyPanel("progressbar"));

        add(new Link<String>("signout") {

            @Override
            public void onClick() {
                OnexusWebSession.get().invalidate();
            }

        });

        add(new Label("username", OnexusWebSession.get().getUserToken()));

        this.toolbar = new RepeatingView("toolbar");
        add(toolbar);
    }

    protected void addTool(AbstractTool<?> tool) {
        WebMarkupContainer toolContainer = new WebMarkupContainer(toolbar.newChildId());
        toolContainer.add(tool);
        toolbar.add(toolContainer);
    }

    private void addMenuItem(RepeatingView menu, Class<ResourcesPage> pageClass, String label) {

        WebMarkupContainer childItem = new WebMarkupContainer(menu.newChildId());

        if (pageClass.isAssignableFrom(getClass())) {
            childItem.add(new AttributeModifier("id", "current"));
        }

        Link<String> link = new BookmarkablePageLink<String>("link", pageClass);
        link.add(new Label("label", label));
        childItem.add(link);

        menu.add(childItem);

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(DEFAULT_CSS);
        response.renderCSSReference(STYLE_CSS);
    }

}
