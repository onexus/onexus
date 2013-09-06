package org.onexus.website.api.pages.search.boxes;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.resource.api.ORI;

public abstract class DisambiguationPanel extends Panel {

    public DisambiguationPanel(String id, IEntityTable table, ORI collectionUri) {
        super(id);

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
