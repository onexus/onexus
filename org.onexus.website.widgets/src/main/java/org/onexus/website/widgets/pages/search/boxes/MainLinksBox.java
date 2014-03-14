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
package org.onexus.website.widgets.pages.search.boxes;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.onexus.website.api.Website;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.widgets.pages.search.SearchLink;

import java.util.List;

public class MainLinksBox extends Panel {

    private transient List<SearchLink> links;

    public MainLinksBox(String id, List<SearchLink> links) {
        super(id);

        this.links = links;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Prepare accordion containers
        WebMarkupContainer accordionToggle = new WebMarkupContainer("accordion-toggle");
        WebMarkupContainer accordionBody = new WebMarkupContainer("accordion-body");
        String bodyId = getMarkupId() + "-body";
        accordionBody.setMarkupId(bodyId);
        accordionToggle.add(new AttributeModifier("href", "#" + bodyId));
        accordionBody.add(new AttributeModifier("class", "accordion-body in collapse"));
        add(accordionToggle);
        add(accordionBody);

        // Label
        accordionToggle.add(new Label("label", "General links"));

        String prefix = getPage().getPageParameters().get(Website.PARAMETER_CURRENT_PAGE).isEmpty() ? WebsiteApplication.get().getWebPath() + "/" : "";

        // Links
        RepeatingView linksContainer = new RepeatingView("links");
        for (SearchLink searchLink : links) {
            WebMarkupContainer item = new WebMarkupContainer(linksContainer.newChildId());
            WebMarkupContainer link = new WebMarkupContainer("link");
            link.add(new AttributeModifier("href", prefix + searchLink.getUrl()));
            link.add(new Label("label", searchLink.getTitle()).setEscapeModelStrings(false));
            item.add(link);
            linksContainer.add(item);
        }

        accordionBody.add(linksContainer);
    }

}
