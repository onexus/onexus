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
package org.onexus.ui.website.boxes;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Collection;
import org.onexus.ui.website.utils.panels.HelpMark;

/**
 * 
 * Collapsible Box panel container.
 * 
 */
public class BoxContainerPanel extends Panel {
    
    @Inject
    private IResourceManager resourceManager;

    public BoxContainerPanel(final String panelId, final AbstractBox box) {
	super(panelId);

	// Title of the box
	add(new Label("title", box.getTitle()));

	// Help text
	Collection collection = resourceManager.load(Collection.class, box.getCollectionId());
	String helpText = collection.getProperty("HELP_DESCRIPTION");

	// Help question mark
	add(new HelpMark("help", box.getTitle(), helpText));

	// Box content
	add(box);

    }

}