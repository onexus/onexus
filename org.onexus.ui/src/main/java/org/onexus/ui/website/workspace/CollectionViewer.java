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
package org.onexus.ui.website.workspace;

import java.util.Iterator;

import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.core.resources.Resource;
import org.onexus.ui.website.tabs.topleft.TopleftTab;
import org.onexus.ui.website.tabs.topleft.TopleftTabConfig;
import org.onexus.ui.website.tabs.topleft.TopleftTabStatus;
import org.onexus.ui.website.viewers.tableviewer.TableViewerConfig;
import org.onexus.ui.website.viewers.tableviewer.columns.ColumnConfig;
import org.onexus.ui.website.widgets.export.ExportWidgetConfig;
import org.onexus.ui.website.widgets.search.SearchField;
import org.onexus.ui.website.widgets.search.SearchWidgetConfig;

public class CollectionViewer extends Panel {

    private static final String REGEXP_ALL_FIELDS = "*{(.*)}";

    public CollectionViewer(String id, IModel<? extends Resource> model) {
	super(id, model);

	Resource resource = model.getObject();

	if (resource != null && resource instanceof Collection) {

	    Collection collection = (Collection) resource;

	    // Tableviewer
	    TableViewerConfig viewerConfig = new TableViewerConfig("tableviewer", collection.getURI());
	    viewerConfig.addColumn(new ColumnConfig(collection.getURI(), REGEXP_ALL_FIELDS));

	    // Export widget
	    ExportWidgetConfig exportConfig = new ExportWidgetConfig("export", "left", collection.getURI());

	    // Search widget
	    SearchWidgetConfig searchConfig = new SearchWidgetConfig("search", "top");
	    Iterator<Field> fieldIt = collection.getFields().iterator();
	    StringBuilder fieldNames = new StringBuilder();
	    while (fieldIt.hasNext()) {
		Field field = fieldIt.next();
		if (String.class.isAssignableFrom(field.getDataType())) {
		    fieldNames.append(field.getName());
		    fieldNames.append(",");
		}
	    }
	    fieldNames.setCharAt(fieldNames.length() - 1, ' ');
	    searchConfig.addField(new SearchField(collection.getURI(), fieldNames.toString()));

	    // Layout
	    TopleftTabConfig tabConfig = new TopleftTabConfig("browser", "browser");
	    tabConfig.getViewers().add(viewerConfig);
	    tabConfig.getWidgets().add(exportConfig);
	    tabConfig.getWidgets().add(searchConfig);

	    IModel<TopleftTabStatus> statusModel = Model.of(tabConfig.getDefaultStatus());
	    add(new TopleftTab("table", tabConfig, statusModel));

	} else {
	    add(new EmptyPanel("table"));
	}

    }

}
