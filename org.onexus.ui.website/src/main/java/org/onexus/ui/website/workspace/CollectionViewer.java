/**
 *  Copyright 2012 Universitat Pompeu Fabra.
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
import org.apache.wicket.model.Model;
import org.onexus.resource.api.query.Query;
import org.onexus.resource.api.resources.Collection;
import org.onexus.resource.api.resources.Resource;
import org.onexus.ui.website.widgets.tableviewer.ColumnSet;
import org.onexus.ui.website.widgets.tableviewer.TableViewer;
import org.onexus.ui.website.widgets.tableviewer.TableViewerConfig;
import org.onexus.ui.website.widgets.tableviewer.TableViewerStatus;
import org.onexus.ui.website.widgets.tableviewer.columns.ColumnConfig;

public class CollectionViewer extends Panel {

    private static final String REGEXP_ALL_FIELDS = "*{(.*)}";

    public CollectionViewer(String id, IModel<? extends Resource> model) {
        super(id, model);

        Resource resource = model.getObject();

        if (resource != null && resource instanceof Collection) {

            Collection collection = (Collection) resource;

            TableViewerConfig viewerConfig = new TableViewerConfig("tableviewer", collection.getURI());

            ColumnSet columnSet = new ColumnSet();
            columnSet.getColumns().add(new ColumnConfig(collection.getURI(), REGEXP_ALL_FIELDS));
            viewerConfig.getColumnSets().add(columnSet);

            TableViewerStatus viewerStatus = new TableViewerStatus();
            viewerStatus.setCurrentColumnSet(0);
            viewerStatus.setConfig(viewerConfig);

            add(new TableViewer("table", Model.of(viewerStatus)) {
                @Override
                protected Query getQuery() {

                    Query query = new Query();

                    TableViewerStatus status = getStatus();

                    if (status != null) {
                        status.beforeQueryBuild(query);
                        status.onQueryBuild(query);
                        status.afterQueryBuild(query);
                    }

                    return query;

                }
            });

        } else {
            add(new EmptyPanel("table"));
        }

    }


}
