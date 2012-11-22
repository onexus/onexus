package org.onexus.website.api.widgets.filters.custom;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.onexus.website.api.widgets.filters.FilterConfig;

public abstract class AbstractCustomFilterPanel extends Panel {

    public AbstractCustomFilterPanel(String id) {
        super(id);
        setOutputMarkupId(true);
    }

    protected abstract void addFilter(AjaxRequestTarget target, FilterConfig filterConfig);

}
