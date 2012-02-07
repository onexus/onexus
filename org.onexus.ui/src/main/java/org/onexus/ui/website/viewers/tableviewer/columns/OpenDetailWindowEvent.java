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
package org.onexus.ui.website.viewers.tableviewer.columns;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.IEntityTable;

/**
 * Recreates modal window each time that is invoked.
 * 
 * @author armand
 */
public class OpenDetailWindowEvent extends AjaxEventBehavior {

    private ModalWindow modal;
    private Panel panel;

    public OpenDetailWindowEvent(ModalWindow modalWindow,
	    IModel<IEntityTable> rowModel,
	    DetailsColumnConfig configDetailsTrackModel) {

	super("onclick");

	this.modal = modalWindow;
	this.panel = new DetailsTrackPanel(modal.getContentId(), rowModel,
		configDetailsTrackModel);

    }

    @Override
    protected void onEvent(AjaxRequestTarget target) {

	if (modal != null) {
	    modal.setInitialWidth(790);
	    modal.setContent(panel);
	    modal.show(target);
	}
    }

}
