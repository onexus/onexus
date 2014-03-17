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
package org.onexus.website.widgets.pages.search.figures;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.string.Strings;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.IEntitySelection;
import org.onexus.website.api.SingleEntitySelection;
import org.onexus.website.api.Website;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.widgets.pages.search.FigureConfig;
import org.onexus.website.widgets.pages.search.SearchLink;
import org.onexus.website.widgets.pages.search.figures.bar.BarFigureConfig;
import org.onexus.website.widgets.pages.search.figures.bar.BarFigurePanel;
import org.onexus.website.widgets.pages.search.figures.html.HtmlFigureConfig;
import org.onexus.website.widgets.pages.search.figures.html.HtmlFigurePanel;
import org.onexus.website.widgets.pages.search.figures.table.TableFigureConfig;
import org.onexus.website.widgets.pages.search.figures.table.TableFigurePanel;

public class FigureBox extends Panel {

    private ORI parentUri;
    private FigureConfig config;
    private IEntitySelection selection;

    public FigureBox(String id, FigureConfig config, ORI parentUri, IEntitySelection selection) {
        super(id);

        this.config = config;
        this.parentUri = parentUri;
        this.selection = selection;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        WebMarkupContainer toggle = new WebMarkupContainer("accordion-toggle");
        toggle.setOutputMarkupId(true);
        WebMarkupContainer body = new WebMarkupContainer("accordion-body");

        if (config.isOpen()) {
            body.add(new AttributeAppender("class", " in"));
        }

        String bodyId = getMarkupId() + "-body";
        body.setMarkupId(bodyId);
        toggle.add(new AttributeModifier("href", "#" + bodyId));

        toggle.add(new Label("label", config.getTitle()));

        Label description = new Label("description", config.getDescription());
        description.setEscapeModelStrings(false);
        description.setVisible(!Strings.isEmpty(config.getDescription()));
        body.add(description);

        if (config instanceof HtmlFigureConfig) {
            body.add(new HtmlFigurePanel("content", parentUri, (HtmlFigureConfig) config));
        } else if (config instanceof TableFigureConfig) {
            body.add(new TableFigurePanel("content", parentUri, selection, (TableFigureConfig) config));
        } else if (config instanceof BarFigureConfig) {
            body.add(new BarFigurePanel("content", parentUri, selection, (BarFigureConfig) config));
        } else {
            body.add(new Label("content", "Unknown figure type"));
        }

        SearchLink searchLink = config.getLink();
        WebMarkupContainer links = new WebMarkupContainer("links");
        if (searchLink != null) {

            // Create link url
            String prefix = getPage().getPageParameters().get(Website.PARAMETER_CURRENT_PAGE).isEmpty() ? WebsiteApplication.get().getWebPath() + "/" : "";
            String url = searchLink.getUrl();
            if (selection != null) {
                String parameter = selection instanceof SingleEntitySelection ? "pf=" : "pfc=";
                String varFilter = parameter + UrlEncoder.QUERY_INSTANCE.encode(selection.toUrlParameter(false, null), "UTF-8");
                url = url.replace("$filter", varFilter);
            }

            WebMarkupContainer link = new WebMarkupContainer("link");
            link.add(new AttributeModifier("href", prefix + url));
            link.add(new Label("label", searchLink.getTitle()).setEscapeModelStrings(false));
            links.add(link);

        } else {
            links.setVisible(false);
        }
        body.add(links);

        add(toggle);
        add(body);
    }
}
