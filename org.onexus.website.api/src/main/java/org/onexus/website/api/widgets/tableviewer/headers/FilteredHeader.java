package org.onexus.website.api.widgets.tableviewer.headers;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.Panel;
import org.onexus.website.api.events.EventFilterHeader;

public class FilteredHeader extends Panel {

    public final static String CHILD_ID = "child";

    private FieldHeader header;

    public FilteredHeader(String id, Component childComponent, FieldHeader fieldHeader) {
        super(id);

        this.header = fieldHeader;

        add(new AjaxLink<String>("filter") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BREADTH, new EventFilterHeader(target, header));
            }
        });

        add(childComponent);
    }
}
