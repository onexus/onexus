package org.onexus.website.api.pages.search.figures;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.onexus.collection.api.IEntity;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.pages.search.FigureConfig;
import org.onexus.website.api.utils.HtmlDataResourceModel;

public class FigureBox extends Panel {

    public FigureBox(String id, FigureConfig config, ORI parentUri, IEntity entity) {
        super(id);

        WebMarkupContainer toggle = new WebMarkupContainer("accordion-toggle");
        toggle.add(new Label("label", config.getTitle()));

        WebMarkupContainer body = new WebMarkupContainer("accordion-body");

        body.add(new Label("content", new HtmlDataResourceModel(parentUri, config.getText())).setEscapeModelStrings(false));

        add(toggle);
        add(body);

    }
}
