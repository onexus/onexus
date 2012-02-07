/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.ui.website.boxes.forms;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class LinkStringFieldPanel extends Panel {

    public LinkStringFieldPanel(String id, IModel<String> model, String href) {
	super(id);

	String linkValue = href
		+ (model.getObject() != null ? model.getObject() : "");
	ExternalLink link = new ExternalLink("link", linkValue);
	link.add(new Label("value", model).setRenderBodyOnly(true));

	add(link);

    }

}
