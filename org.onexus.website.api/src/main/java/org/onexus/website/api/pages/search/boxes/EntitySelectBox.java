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
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.Website;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.pages.browser.FilterEntity;
import org.onexus.website.api.pages.browser.IFilter;
import org.onexus.website.api.pages.search.SearchLink;
import org.onexus.website.api.pages.search.SearchPageStatus;
import org.onexus.website.api.pages.search.SearchType;
import org.onexus.website.api.utils.visible.VisiblePredicate;
import org.onexus.website.api.widgets.filters.BrowserFilter;
import org.onexus.website.api.widgets.filters.FilterConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntitySelectBox extends Panel {

    private transient int position;
    private transient IEntity entity;
    private transient ORI collection;
    private transient FilterConfig filterConfig;
    private transient SearchPageStatus status;

    public EntitySelectBox(String id, int position, SearchPageStatus status, IEntity entity) {
        super(id);

        this.position = position;
        this.status = status;
        this.entity = entity;
        this.collection = entity.getCollection().getORI();
        this.filterConfig = null;

    }

    public EntitySelectBox(String id, int position, SearchPageStatus status, ORI collection, FilterConfig filterConfig) {
        super(id);

        this.position = position;
        this.status = status;
        this.filterConfig = filterConfig;
        this.collection = collection;
        this.entity = null;
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
        String label;
        if (entity != null) {
            String labelField = entity.getCollection().getProperty("FIXED_ENTITY_FIELD");
            label = (labelField == null ?
                    StringUtils.replace(entity.getId(), "\t", "-") :
                    String.valueOf(entity.get(labelField))
            );
        } else {
            label = filterConfig.getName();
        }
        accordionToggle.add(new Label("label", label));

        // Fields value
        if (entity != null) {
            String labelField = entity.getCollection().getProperty("FIXED_ENTITY_FIELD");
            if (status.getType().getTemplate() == null || status.getType().getTemplate().isEmpty()) {
                accordionBody.add(new FieldsPanel("fields", labelField, entity));
            } else {
                accordionBody.add(new Label("fields", replaceEntityValues(status.getType().getTemplate(), entity)).setEscapeModelStrings(false));
            }
        } else {
            //TODO FilterConfigPanel
            accordionBody.add(new EmptyPanel("fields"));
        }

        // Prepare links variables
        SearchType searchType = status.getType();
        String varEntity = (entity != null ? entity.getId() : "");
        String varFilter = createVarFilter();

        // Links
        accordionBody.add(createLinks(collection, searchType, varEntity, varFilter));
    }

    private RepeatingView createLinks(ORI collectionORI, SearchType searchType, String varEntity, String varFilter) {
        RepeatingView links = new RepeatingView("links");

        if (searchType.getLinks() != null) {

            List<SearchLink> filteredLinks = new ArrayList<SearchLink>();

            VisiblePredicate predicate;
            if (entity!=null) {
                predicate = new VisiblePredicate(collectionORI.getParent(), Arrays.asList(new IFilter[]{new FilterEntity(entity)}));
            } else {
                predicate = new VisiblePredicate(collectionORI.getParent(), Arrays.asList(new IFilter[]{new BrowserFilter(filterConfig)}));
            }

            CollectionUtils.select(searchType.getLinks(), predicate, filteredLinks);

            String prefix = (getPage().getPageParameters().get(Website.PARAMETER_CURRENT_PAGE).isEmpty()) ? WebsiteApplication.get().getWebPath() + "/" : "";

            for (SearchLink searchLink : filteredLinks) {
                WebMarkupContainer item = new WebMarkupContainer(links.newChildId());
                WebMarkupContainer link = new WebMarkupContainer("link");
                link.add(new AttributeModifier("href", prefix + createLink(searchLink.getUrl(), searchType.getCollection(), varEntity, varFilter)));
                link.add(new Label("label", searchLink.getTitle()).setEscapeModelStrings(false));
                item.add(link);
                links.add(item);
            }
        }

        return links;
    }

    private String createVarFilter() {

        if (entity != null) {
            FilterEntity filterEntity = new FilterEntity(entity);
            return "pf=" + UrlEncoder.QUERY_INSTANCE.encode(filterEntity.toUrlParameter(false, null), "UTF-8");
        }

        if (filterConfig != null) {
            BrowserFilter browserFilter = new BrowserFilter(filterConfig);
            return "pfc=" + UrlEncoder.QUERY_INSTANCE.encode(browserFilter.toUrlParameter(false, null), "UTF-8");
        }

        return "";
    }


    private String createLink(String url, ORI collection, String varEntity, String varFilter) {

        String link = url;
        link = link.replace("$collection", collection.toString());
        link = link.replace("$entity", varEntity);
        link = link.replace("$filter", varFilter);

        return link;
    }

    private static String replaceEntityValues(String template, IEntity entity) {

        Collection collection = entity.getCollection();

        for (Field field : collection.getFields()) {
            template = template.replaceAll("\\$" + field.getId(), String.valueOf(entity.get(field.getId())));
        }

        return template;
    }
}
