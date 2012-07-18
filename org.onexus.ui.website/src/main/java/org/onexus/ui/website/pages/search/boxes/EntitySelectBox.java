package org.onexus.ui.website.pages.search.boxes;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.util.string.StringValue;
import org.onexus.core.IEntity;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.website.pages.browser.FilterEntity;
import org.onexus.ui.website.pages.browser.IFilter;
import org.onexus.ui.website.pages.search.SearchLink;
import org.onexus.ui.website.pages.search.SearchPageStatus;
import org.onexus.ui.website.pages.search.SearchType;
import org.onexus.ui.website.utils.visible.VisiblePredicate;

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

        // Fields values
        RepeatingView fields = new RepeatingView("fields");
        for (Field field : collection.getFields()) {

            if (labelField != null && labelField.equals(field.getId())) {
                continue;
            }

            Object value = entity.get(field.getId());
            if (value != null && !StringUtils.isEmpty(value.toString())) {

                WebMarkupContainer fc = new WebMarkupContainer(fields.newChildId());
                fc.setRenderBodyOnly(true);
                fc.add(new Label("label", field.getLabel()).add(new AttributeModifier("title", field.getTitle())));
                fc.add(new Label("value", StringUtils.abbreviate(value.toString(), 50)));
                fields.add(fc);
            }
        }
        accordionBody.add(fields);

        // Prepare links variables
        SearchType searchType = status.getType();
        String entityId = entity.getId();

        // Links
        RepeatingView links = new RepeatingView("links");

        if (searchType.getLinks()!=null) {

            List<SearchLink> filteredLinks = new ArrayList<SearchLink>();
            VisiblePredicate predicate = new VisiblePredicate(ResourceUtils.getParentURI(collection.getURI()), Arrays.asList(new IFilter[] { new FilterEntity(entity) }));
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

    private String createLink(String url, String collection, String entityId) {

        String link = "../" + url;
        link = link.replace("$collection", collection);
        link = link.replace("$entity", entityId);

        StringValue uri = getPage().getPageParameters().get("uri");
        if (!uri.isEmpty()) {
            link = link + "&uri=" + uri.toString();
        }

        return link;
    }
}
