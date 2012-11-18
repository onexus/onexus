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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.pages.browser.FilterEntity;
import org.onexus.website.api.pages.browser.IFilter;
import org.onexus.website.api.pages.search.SearchLink;
import org.onexus.website.api.pages.search.SearchPageStatus;
import org.onexus.website.api.pages.search.SearchType;
import org.onexus.website.api.utils.visible.VisiblePredicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntitySelectBox extends Panel {

    private transient int position;
    private transient IEntity entity;
    private transient SearchPageStatus status;

    public EntitySelectBox(String id, int position, SearchPageStatus status, IEntity entity) {
        super(id);

        this.position = position;
        this.status = status;
        this.entity = entity;

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
        if (position == 0) {
            accordionBody.add(new AttributeModifier("class", "accordion-body in collapse"));
        }
        add(accordionToggle);
        add(accordionBody);

        // Label
        Collection collection = entity.getCollection();
        String labelField = collection.getProperty("FIXED_ENTITY_FIELD");

        String label = (labelField == null ?
                StringUtils.replace(entity.getId(), "\t", "-") :
                String.valueOf(entity.get(labelField))
        );
        accordionToggle.add(new Label("label", label));

        // Fields value
        if (status.getType().getTemplate() == null || status.getType().getTemplate().isEmpty()) {
            accordionBody.add(new FieldsPanel("fields", labelField, entity));
        } else {
            accordionBody.add(new Label("fields", replaceEntityValues(status.getType().getTemplate(), entity)).setEscapeModelStrings(false));
        }

        // Prepare links variables
        SearchType searchType = status.getType();
        String entityId = entity.getId();

        // Links
        RepeatingView links = new RepeatingView("links");

        if (searchType.getLinks()!=null) {

            List<SearchLink> filteredLinks = new ArrayList<SearchLink>();
            VisiblePredicate predicate = new VisiblePredicate(collection.getURI().getParent(), Arrays.asList(new IFilter[] { new FilterEntity(entity) }));
            CollectionUtils.select(searchType.getLinks(), predicate, filteredLinks);

            for (SearchLink searchLink : filteredLinks) {
                WebMarkupContainer item = new WebMarkupContainer(links.newChildId());
                WebMarkupContainer link = new WebMarkupContainer("link");
                link.add(new AttributeModifier("href", createLink(searchLink.getUrl(), searchType.getCollection(), entityId)));
                link.add(new Label("label", searchLink.getTitle()).setEscapeModelStrings(false));
                item.add(link);
                links.add(item);
            }
        }
        accordionBody.add(links);
    }

    private String createLink(String url, ORI collection, String entityId) {

        String link = url;
        link = link.replace("$collection", collection.toString());
        link = link.replace("$entity", entityId);

        /*StringValue uri = getPage().getPageParameters().get("uri");
        if (!uri.isEmpty()) {
            link = link + "&uri=" + uri.toString();
        } */

        return link;
    }

    private static String replaceEntityValues(String template, IEntity entity) {

        Collection collection = entity.getCollection();

        for (Field field : collection.getFields()) {
            template = template.replaceAll("\\$"+field.getId(), String.valueOf(entity.get(field.getId())));
        }

        return template;
    }
}