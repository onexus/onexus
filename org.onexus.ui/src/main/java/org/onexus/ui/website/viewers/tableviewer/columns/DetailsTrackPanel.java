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

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.onexus.core.IEntity;
import org.onexus.core.IEntityTable;
import org.onexus.core.resources.Field;
import org.onexus.core.utils.ResourceTools;
import org.onexus.ui.website.boxes.AbstractBox;
import org.onexus.ui.website.boxes.BoxContainerPanel;
import org.onexus.ui.website.boxes.BoxFactory;
import org.onexus.ui.website.utils.EntityModel;

public class DetailsTrackPanel extends Panel {

    public DetailsTrackPanel(String id, IModel<IEntityTable> rowModel,
	    DetailsColumnConfig config) {
	super(id);

	RepeatingView items = new RepeatingView("items");

	IEntityTable rowEntities = rowModel.getObject();

	String releaseURI = rowEntities.getQuery().getMainNamespace();

	for (String collectionId : config.getCollections()) {

	    String collectionURI = ResourceTools.getAbsoluteURI(releaseURI,
		    collectionId);
	    IEntity entity = rowEntities.getEntity(collectionURI);
	    if (!isAllNull(entity)) {
		AbstractBox box = BoxFactory.createBox(collectionURI,
			new EntityModel(entity));
		items.add(new BoxContainerPanel(items.newChildId(), box));
	    }
	}

	add(items);

    }

    private boolean isAllNull(IEntity e) {
	for (Field f : e.getCollection().getFields()) {
	    if (e.get(f.getName()) != null) {
		return false;
	    }
	}
	return true;
    }

}
