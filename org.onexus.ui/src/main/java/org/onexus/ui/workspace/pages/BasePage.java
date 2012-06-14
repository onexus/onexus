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
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.resource.JQueryPluginResourceReference;
import org.onexus.ui.OnexusWebSession;
import org.onexus.ui.workspace.pages.tools.AbstractTool;
import org.onexus.ui.workspace.progressbar.ProgressBar;

@AuthorizeInstantiation("onexus-admin")
public class BasePage extends WebPage {

    public static final CssResourceReference BOOTSTRAP_CSS = new CssResourceReference(ResourcesPage.class, "css/bootstrap.min.css");
    public static final JQueryPluginResourceReference BOOTSTRAP_JS = new JQueryPluginResourceReference(ResourcesPage.class, "js/bootstrap.min.js");
    public static final CssResourceReference STYLE_CSS = new CssResourceReference(ResourcesPage.class, "css/style.css");

    public BasePage(PageParameters parameters) {
        super(parameters);

        Link<String> link = new Link<String>("account-details") {
            @Override
            public void onClick() {

            }
        };
        link.add(new Label("username", OnexusWebSession.get().getUserToken()));
        add(link);

        add(new Link<String>("signout") {
            @Override
            public void onClick() {
                OnexusWebSession.get().invalidate();
            }
        });

        WebMarkupContainer menuProjects = new WebMarkupContainer("menu-projects");
        add(menuProjects);

        if (ResourcesPage.class.isAssignableFrom(getClass())) {
            menuProjects.add(new AttributeModifier("class", "dropdown active"));
        } else {
            menuProjects.add(new AttributeModifier("class", "dropdown"));
        }

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(BOOTSTRAP_CSS));
        response.render(CssHeaderItem.forReference(STYLE_CSS));
        response.render(JavaScriptHeaderItem.forReference(BOOTSTRAP_JS));
        response.render(JavaScriptHeaderItem.forScript("     $(document).ready(function () {\n" +
                "        $(\"[rel=tooltip]\").tooltip({ placement: 'bottom'});\n" +
                "        });\n" +
                "   ", "bootstrap-tooltip"));
    }

}
