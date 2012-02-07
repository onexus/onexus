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
package org.onexus.ui.website.utils.panels;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.onexus.ui.website.utils.panels.icons.Icons;

/**
 * 
 * Question mark panel with a modal window that shows the HELP_DESCRIPTION
 * property of the given entity collection.
 * 
 * @author Armand
 * 
 */
public class HelpMark extends Panel {

    /**
     * @param panelId
     *            Wicket component id.
     * @param title
     *            Modal window title.
     * @param helpText
     *            Text to show into the modal window.
     */
    public HelpMark(final String panelId, final String title,
	    final String helpText) {
	this(panelId, title, "", helpText);
    }

    public HelpMark(final String panelId, final String title,
	    final String displayLabel, final String helpText) {
	super(panelId);

	// Add modal window
	final ModalWindow modal = new ModalWindow("modalWindowEmbeeded");
	modal.setTitle(title);
	modal.setInitialWidth(700);
	modal.setInitialHeight(500);
	add(modal);

	// Add mark label
	final WebMarkupContainer container = new WebMarkupContainer(
		"displayLabel");
	container.add(new Label("label", displayLabel));
	add(container);

	// Add question mark icon
	Image img = null;
	container.add(img = new Image("imageHelp", Icons.HELP) {

	    @Override
	    protected boolean shouldAddAntiCacheParameter() {
		return false;
	    }

	});
	img.add(new AjaxEventBehavior("onclick") {

	    @Override
	    protected void onEvent(final AjaxRequestTarget target) {

		if (modal != null) {
		    modal.setInitialWidth(700);
		    modal.setInitialHeight(500);
		    modal.setContent(new HelpContentPanel(modal.getContentId(),
			    helpText));
		    modal.show(target);
		}

	    }

	});

	// Visible only if there is some
	setVisible(helpText != null);

    }
}
