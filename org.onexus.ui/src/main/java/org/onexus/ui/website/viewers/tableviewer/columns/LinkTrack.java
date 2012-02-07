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
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.onexus.core.IEntityTable;

public abstract class LinkTrack extends TableTrack {

    private String linkLabel;
    private boolean visibility;

    public LinkTrack(String linkLabel, boolean visibility) {
	super();
	this.linkLabel = linkLabel;
	this.visibility = visibility;
    }

    public abstract void onClick();

    @Override
    public Component getHeader(String componentId) {
	return new LinkPanel(componentId);
    }

    @Override
    public void populateItem(Item<ICellPopulator<IEntityTable>> cellItem,
	    String componentId, IModel<IEntityTable> rowModel) {
	Panel empty = new EmptyPanel(componentId);
	empty.setVisible(false);
	cellItem.add(empty);
    }

    public class LinkPanel extends Panel {
	public LinkPanel(String id) {
	    super(id);

	    Link<String> link = new Link<String>("link") {

		@Override
		public void onClick() {
		    LinkTrack.this.onClick();
		}

	    };

	    link.add(new Label("label", linkLabel));
	    link.setVisible(LinkTrack.this.visibility);

	    add(link);
	}
    }

}
