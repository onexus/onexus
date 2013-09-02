package org.onexus.website.api.widgets.tableviewer.headers;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.panel.Panel;
import org.onexus.website.api.events.EventFilterHeader;

public class FilteredHeader extends Border {

    private FieldHeader header;

    public FilteredHeader(String id, FieldHeader fieldHeader) {
        super(id);

        this.header = fieldHeader;

        addToBorder(new AjaxLink<String>("filter") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BREADTH, new EventFilterHeader(target, header));
            }
        });

    }
}
