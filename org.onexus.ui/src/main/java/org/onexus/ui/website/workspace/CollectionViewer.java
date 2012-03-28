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

import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.core.resources.Resource;
import org.onexus.ui.website.pages.PageModel;
import org.onexus.ui.website.pages.browser.BrowserPageConfig;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.pages.browser.layouts.topleft.TopleftLayout;
import org.onexus.ui.website.widgets.WidgetConfig;
import org.onexus.ui.website.widgets.WidgetModel;
import org.onexus.ui.website.widgets.export.ExportWidgetConfig;
import org.onexus.ui.website.widgets.search.SearchField;
import org.onexus.ui.website.widgets.search.SearchWidgetConfig;
import org.onexus.ui.website.widgets.tableviewer.ColumnSet;
import org.onexus.ui.website.widgets.tableviewer.TableViewer;
import org.onexus.ui.website.widgets.tableviewer.TableViewerConfig;
import org.onexus.ui.website.widgets.tableviewer.TableViewerStatus;
import org.onexus.ui.website.widgets.tableviewer.columns.ColumnConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CollectionViewer extends Panel {

    private static final String REGEXP_ALL_FIELDS = "*{(.*)}";

    public CollectionViewer(String id, IModel<? extends Resource> model) {
        super(id, model);

        Resource resource = model.getObject();

        if (resource != null && resource instanceof Collection) {

            Collection collection = (Collection) resource;

            TableViewerConfig viewerConfig = new TableViewerConfig("tableviewer", "main", collection.getURI());

            ColumnSet columnSet = new ColumnSet();
            columnSet.getColumns().add(new ColumnConfig(collection.getURI(), REGEXP_ALL_FIELDS));
            viewerConfig.getColumnSets().add(columnSet);

            add( new TableViewer("table", new WidgetModel<TableViewerStatus>(viewerConfig)));

        } else {
            add(new EmptyPanel("table"));
        }

    }


}
