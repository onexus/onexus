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
package org.onexus.website.api.pages.search.boxes;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.resource.api.ORI;

import java.util.ArrayList;
import java.util.Set;

public abstract class DisambiguationPanel extends Panel {

    public DisambiguationPanel(String id, Set<String> notFoundValues) {
        super(id);

        add(new Label("message", "Unknown entries: "));

        RepeatingView links = new RepeatingView("links");

        WebMarkupContainer item = new WebMarkupContainer(links.newChildId());
        WebMarkupContainer comma = new WebMarkupContainer("comma");
        item.add(comma);
        comma.setVisible(false);

        AjaxLink<String> link = new AjaxLink<String>("link") {
            @Override
            public void onClick(AjaxRequestTarget target) {

            }
        };

        link.add(new Label("label", Strings.join(", ", new ArrayList<String>(notFoundValues))));
        item.add(link);
        links.add(item);

        add(links);
    }

    public DisambiguationPanel(String id, IEntityTable table, ORI collectionUri) {
        super(id);

        add(new Label("message", "Did you mean..."));

        RepeatingView links = new RepeatingView("links");

        addLink(links, table.getEntity(collectionUri), false);
        while (table.next()) {
            addLink(links, table.getEntity(collectionUri), true);
        }

        add(links);
    }


    private void addLink(RepeatingView links, IEntity entity, boolean showComma) {
        WebMarkupContainer item = new WebMarkupContainer(links.newChildId());
        WebMarkupContainer comma = new WebMarkupContainer("comma");
        item.add(comma);
        comma.setVisible(showComma);

        AjaxLink<String> link = new AjaxLink<String>("link", new Model(entity.getId())) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onSelection(target, getModelObject());
            }
        };

        link.add(new Label("label", getEntityLabel(entity)));
        item.add(link);

        links.add(item);
    }

    private String getEntityLabel(IEntity entity) {
        String labelField = entity.getCollection().getProperty("FIXED_ENTITY_FIELD");
        String label = (labelField == null ? StringUtils.replace(entity.getId(), "\t", "-") : String.valueOf(entity.get(labelField)));
        return label;
    }

    protected abstract void onSelection(AjaxRequestTarget target, String newSearch);
}
