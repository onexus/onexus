package org.onexus.website.api.pages.search.figures.html;

import org.apache.wicket.markup.html.basic.Label;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.utils.HtmlDataResourceModel;

public class HtmlFigurePanel extends Label {

    public HtmlFigurePanel(String id, ORI parentUri, HtmlFigureConfig config) {
        super(id, new HtmlDataResourceModel(parentUri, config.getText()));

        setEscapeModelStrings(false);
    }
}
