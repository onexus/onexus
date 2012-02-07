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
package org.onexus.ui.website.widgets.tags;

import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.onexus.ui.website.events.EventViewChange;
import org.onexus.ui.website.pages.browser.BrowserPage;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.widgets.tags.tagstore.ITagStoreManager;
import org.onexus.ui.website.widgets.tags.tagstore.TagStore;

public class TagColumnItem extends Panel {
    
    @Inject
    public ITagStoreManager tagStoreManager;
    
    private WebMarkupContainer checkbox;

    public TagColumnItem(String id, final IModel<String> rowModel) {
	super(id);

	checkbox = new WebMarkupContainer("checkbox");

	checkbox.add(new AttributeModifier("value", rowModel));
	
	add( new ListView<String>("tags", new TagValuesModel(rowModel)) {

	    @Override
	    protected void populateItem(ListItem<String> item) {
		item.add(new Label("label", item.getModel()));
		item.add(new AjaxLink<String>("link", item.getModel()) {

		    @Override
		    public void onClick(AjaxRequestTarget target) {
			getTagStore().removeTagValue(getModelObject(), rowModel.getObject());
			
			send(getPage(), Broadcast.BREADTH, EventViewChange.EVENT);

		    }
		    
		});
	    }
	    
	});
	
	add(checkbox);

    }
    
    private TagStore getTagStore() {
	BrowserPageStatus status = findParent(BrowserPage.class).getStatus();
	String tagId = status.getCurrentTabId();
	String releaseURI = status.getReleaseURI();
	String namespace = releaseURI + "#" + tagId;
	
	return tagStoreManager.getUserStore(namespace);
    }
    
    protected WebMarkupContainer getCheckBox() {
	return checkbox;
    }
    
    protected String getTableId() {
	return findParent(DataTable.class).getMarkupId();
    }
    
    @Override
    protected void onBeforeRender() {
	
	checkbox.add(new AttributeModifier("onclick", "updateSelected('" + getTableId() + "');"));

	super.onBeforeRender();
    }

    private class TagValuesModel extends AbstractReadOnlyModel<List<String>> {

	private IModel<String> rowValue;
	
	public TagValuesModel(IModel<String> rowValue) {
	    super();
	    this.rowValue = rowValue;
	}

	@Override
	public List<String> getObject() {
	    return getTagStore().getTagKeysByValue(rowValue.getObject());
	}
	
    }
}
