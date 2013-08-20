package org.onexus.website.api.events;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.onexus.website.api.widgets.tableviewer.headers.FieldHeader;

public class EventFilterHeader extends AbstractEvent {

    private AjaxRequestTarget target;
    private FieldHeader header;

    public EventFilterHeader(AjaxRequestTarget target, FieldHeader header) {
        this.target = target;
        this.header = header;
    }

    public AjaxRequestTarget getTarget() {
        return target;
    }

    public FieldHeader getHeader() {
        return header;
    }
}
