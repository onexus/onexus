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
package org.onexus.ui.website.internal.workspace;

import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.Resource;
import org.onexus.website.widget.tableviewer.ColumnSet;
import org.onexus.website.widget.tableviewer.TableViewer;
import org.onexus.website.widget.tableviewer.TableViewerConfig;
import org.onexus.website.widget.tableviewer.TableViewerStatus;
import org.onexus.website.widget.tableviewer.columns.ColumnConfig;

public class CollectionViewer extends Panel {

    private static final String REGEXP_ALL_FIELDS = "*{(.*)}";

    public CollectionViewer(String id, IModel<? extends Resource> model) {
        super(id, model);

        Resource resource = model.getObject();

        if (resource instanceof Collection) {

            Collection collection = (Collection) resource;

            TableViewerConfig viewerConfig = new TableViewerConfig("tableviewer", collection.getORI());

            ColumnSet columnSet = new ColumnSet();
            columnSet.getColumns().add(new ColumnConfig(collection.getORI(), REGEXP_ALL_FIELDS));
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
