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

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.onexus.core.IEntityTable;

/*
 * Creates per each row, a corresponent detailsTrack
 */
public class DetailsTrack extends TableTrack {

    private final ResourceReference DETAILS_ICON = new PackageResourceReference(
	    DetailsTrack.class, "zoom.png");

    private ModalWindow modal;

    private DetailsColumnConfig configDetails;

    public DetailsTrack() {
	this(new DetailsColumnConfig());
    }

    public DetailsTrack(DetailsColumnConfig configModel) {
	super();
	this.configDetails = configModel;
    }

    public DetailsColumnConfig getConfiguration() {
	return configDetails;
    }

    @Override
    public Component getHeader(String componentId) {
	return new HeaderWithModal(componentId);
    }

    @Override
    public void populateItem(Item<ICellPopulator<IEntityTable>> cellItem,
	    String componentId, IModel<IEntityTable> rowModel) {

	cellItem.add(new DetailsTrackImage(componentId, rowModel, configDetails));

    }

    private class DetailsTrackImage extends Panel {

	public DetailsTrackImage(String componentId,
		IModel<IEntityTable> rowModel,
		DetailsColumnConfig configDetailsTrackModel) {
	    super(componentId);

	    Image image = new Image("imageDetails", DETAILS_ICON) {

		@Override
		protected boolean shouldAddAntiCacheParameter() {
		    return false;
		}

	    };
	    image.add(new OpenDetailWindowEvent(modal, rowModel,
		    configDetailsTrackModel));
	    add(image);
	}
    }

    private class HeaderWithModal extends Panel {

	public HeaderWithModal(String id) {
	    super(id);
	    add(modal = new ModalWindow("modalWindow"));
	}

    }

}
