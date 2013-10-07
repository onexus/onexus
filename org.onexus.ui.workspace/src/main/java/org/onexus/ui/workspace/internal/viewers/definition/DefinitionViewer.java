/**
 *  Copyright 2012 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
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
