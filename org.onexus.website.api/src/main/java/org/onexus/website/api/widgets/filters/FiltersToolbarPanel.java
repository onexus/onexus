package org.onexus.website.api.widgets.filters;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class FiltersToolbarPanel extends Panel {

    public FiltersToolbarPanel(String id, IModel<FiltersToolbarStatus> model) {
        super(id, model);
    }
}
