package org.onexus.ui.workspace.internal.viewers.definition;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.api.Resource;
import org.onexus.ui.workspace.internal.viewers.utils.PrettifyBehavior;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.io.ByteArrayOutputStream;


public class DefinitionViewer extends Panel {

    @PaxWicketBean(name = "resourceSerializer")
    private IResourceSerializer resourceSerializer;

    public DefinitionViewer(String id, IModel<? extends Resource> model) {
        super(id);

        add(new PrettifyBehavior());

        Resource resource = model.getObject();

        String xml;

        if (resource == null) {
            xml = "";
        } else {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            resourceSerializer.serialize(resource, output);
            xml = output.toString();
        }

        add(new Label("xml", xml));
    }
}
