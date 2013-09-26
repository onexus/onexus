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
package org.onexus.website.api.pages.search.figures;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.string.Strings;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.Website;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.pages.browser.SingleEntitySelection;
import org.onexus.website.api.pages.search.SearchLink;
import org.onexus.website.api.pages.search.SearchPageStatus;
import org.onexus.website.api.pages.search.SearchType;
import org.onexus.website.api.pages.search.boxes.FieldsPanel;
import org.onexus.website.api.widgets.selection.FilterConfig;
import org.onexus.website.api.widgets.selection.MultipleEntitySelection;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LinksBox extends Panel {


    private static final int MAX_LABELS = 6;
    private transient IEntity entity;
    private transient Iterator<IEntity> entityIterator;
    private transient ORI collection;
    private transient FilterConfig filterConfig;
    private transient SearchPageStatus status;

    public LinksBox(String id, SearchPageStatus status, IEntity entity) {
        super(id);

        this.status = status;
        this.entity = entity;
        this.collection = entity.getCollection().getORI();
        this.filterConfig = null;

    }

    public LinksBox(String id, SearchPageStatus status, ORI collection, FilterConfig filterConfig, Iterator<IEntity> entityIterator) {
        super(id);

        this.status = status;
        this.filterConfig = filterConfig;
        this.collection = collection;
        this.entity = null;
        this.entityIterator = entityIterator;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Prepare accordion containers
        RepeatingView links = new RepeatingView("links");
        WebMarkupContainer accordionBody = new WebMarkupContainer("accordion-body");
        String bodyId = getMarkupId() + "-body";
        accordionBody.setMarkupId(bodyId);

        add(links);
        add(accordionBody);

        if (entityIterator != null) {
            entity = entityIterator.next();
        }

        // Label
        Set<String> labels = new HashSet<String>();
        String labelField = entity.getCollection().getProperty("FIXED_ENTITY_FIELD");

        WebMarkupContainer item = new WebMarkupContainer(links.newChildId());
        item.add(new AttributeModifier("class", "active"));
        links.add(item);

        WebMarkupContainer activeLink = new WebMarkupContainer("link");
        activeLink.add(new AttributeModifier("href", "#" + entity.getId()));
        activeLink.add(new AttributeModifier("data-toggle", "pill"));
        item.add(activeLink);

        String label = getLabel(entity, labelField);
        labels.add(label.toUpperCase());
        activeLink.add(new Label("label", label).setEscapeModelStrings(false));

        // Fields values
        RepeatingView fields = new RepeatingView("fields");
        accordionBody.add(fields);

        Component first = newFieldsBox(fields.newChildId(), labelField, entity, true);
        fields.add(first);

        // Complete the label
        if (entityIterator != null) {
            int count = 1;
            int remaining = 0;
            while (entityIterator.hasNext()) {

                entity = entityIterator.next();

                count++;

                label = getLabel(entity, labelField);
                labels.add(label.toUpperCase());

                if (count > MAX_LABELS) {
                    remaining++;
                    continue;
                }

                item = new WebMarkupContainer(links.newChildId());
                links.add(item);

                activeLink = new WebMarkupContainer("link");
                activeLink.add(new AttributeModifier("href", "#" + entity.getId()));
                activeLink.add(new AttributeModifier("data-toggle", "pill"));
                item.add(activeLink);
                activeLink.add(new Label("label", label));

                fields.add(newFieldsBox(fields.newChildId(), labelField, entity, false));

            }

            if (remaining > 0) {
                item = new WebMarkupContainer(links.newChildId());
                links.add(item);
                activeLink = new ExternalLink("link", "");
                activeLink.add(new Label("label", "and " + remaining + " more"));
                activeLink.setEnabled(false);
                item.add(activeLink);
            }
        }

        // Discover not matched labels
        Set<String> notFound = new HashSet<String>();
        String[] values = status.getSearch().split(",");
        for (String value : values) {
            if (!labels.contains(value.trim().toUpperCase())) {
                notFound.add(value);
            }
        }
        onNotFound(notFound);

        // Prepare links variables
        SearchType searchType = status.getType();
        String varEntity = filterConfig == null ? entity.getId() : "";

        String varFilter = createVarFilter(entity, filterConfig);

        // Links
        accordionBody.add(createLinks(collection, searchType, varEntity, varFilter));
    }

    private Component newFieldsBox(String componentId, String labelField, IEntity entity, boolean active) {

        Component box;
        if (status.getType().getTemplate() == null || status.getType().getTemplate().isEmpty()) {
            box = new FieldsPanel(componentId, labelField, entity);
        } else {
            box = new Label(componentId, replaceEntityValues(status.getType().getTemplate(), entity)).setEscapeModelStrings(false);
        }

        if (active) {
            box.add(new AttributeAppender("class", " active"));
        }

        box.setMarkupId(entity.getId());
        return box;

    }

    private static String createVarFilter(IEntity entity, FilterConfig filterConfig) {

        if (filterConfig != null) {
            MultipleEntitySelection browserSelection = new MultipleEntitySelection(filterConfig);
            return "pfc=" + UrlEncoder.QUERY_INSTANCE.encode(browserSelection.toUrlParameter(false, null), "UTF-8");
        }

        if (entity != null) {
            SingleEntitySelection singleEntitySelection = new SingleEntitySelection(entity);
            return "pf=" + UrlEncoder.QUERY_INSTANCE.encode(singleEntitySelection.toUrlParameter(false, null), "UTF-8");
        }

        return "";
    }

    protected void onNotFound(Set<String> valuesNotFound) {

    }


    private String getLabel(IEntity entity, String labelField) {
        return labelField == null ?
                StringUtils.replace(entity.getId(), "\t", "-") :
                String.valueOf(entity.get(labelField));
    }

    private WebMarkupContainer createLinks(ORI collectionORI, SearchType searchType, String varEntity, String varFilter) {
        WebMarkupContainer linksContainer = new WebMarkupContainer("linksContainer");
        RepeatingView links = new RepeatingView("links");

        if (searchType.getLinks() != null) {

            String prefix = getPage().getPageParameters().get(Website.PARAMETER_CURRENT_PAGE).isEmpty() ? WebsiteApplication.get().getWebPath() + "/" : "";

            linksContainer.setVisible(false);
            boolean isList = entityIterator != null || filterConfig != null;

            for (SearchLink searchLink : searchType.getLinks()) {

                String visible = searchLink.getVisible();
                if (!Strings.isEmpty(visible)) {

                    if (isList && !"LIST".equalsIgnoreCase(visible)) {
                        continue;
                    }

                    if (!isList && !"SINGLE".equalsIgnoreCase(visible)) {
                        continue;
                    }
                }

                linksContainer.setVisible(true);
                WebMarkupContainer item = new WebMarkupContainer(links.newChildId());
                WebMarkupContainer link = new WebMarkupContainer("link");
                link.add(new AttributeModifier("href", prefix + createLink(searchLink.getUrl(), searchType.getCollection(), varEntity, varFilter)));
                link.add(new Label("label", searchLink.getTitle()).setEscapeModelStrings(false));
                item.add(link);
                links.add(item);
            }
        } else {
            linksContainer.setVisible(false);
        }

        linksContainer.add(links);
        return linksContainer;
    }

    public static String createLink(String url, ORI collection, String varEntity, String varFilter) {

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
