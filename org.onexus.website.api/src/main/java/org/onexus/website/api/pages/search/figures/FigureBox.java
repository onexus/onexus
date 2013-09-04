package org.onexus.website.api.pages.search.figures;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.onexus.collection.api.IEntity;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.pages.search.FigureConfig;
import org.onexus.website.api.utils.HtmlDataResourceModel;
import org.onexus.website.api.widgets.selection.FilterConfig;

public class FigureBox extends Panel {

    /*
        Public constructor when only one entity is selected
     */
    public FigureBox(String id, FigureConfig config, ORI parentUri, IEntity entity) {
        this(id, config, parentUri);
    }

    /*
        Public constructor when multiple entities are selected
     */
    public FigureBox(String id, FigureConfig config, ORI parentUri, ORI collectionUri, FilterConfig filterConfig) {
        this(id, config, parentUri);
    }

    private FigureBox(String id, FigureConfig config, ORI parentUri) {
        super(id);

        WebMarkupContainer toggle = new WebMarkupContainer("accordion-toggle");
        WebMarkupContainer body = new WebMarkupContainer("accordion-body");
        String bodyId = getMarkupId() + "-body";
        body.setMarkupId(bodyId);
        toggle.add(new AttributeModifier("href", "#" + bodyId));

        toggle.add(new Label("label", config.getTitle()));
        body.add(new Label("content", new HtmlDataResourceModel(parentUri, config.getText())).setEscapeModelStrings(false));

        add(toggle);
        add(body);
    }
}
