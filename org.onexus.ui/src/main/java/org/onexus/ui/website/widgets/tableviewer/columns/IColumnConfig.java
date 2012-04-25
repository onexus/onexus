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
package org.onexus.ui.website.widgets.tableviewer.columns;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.onexus.core.IEntityTable;
import org.onexus.ui.website.widgets.tableviewer.columns.ColumnConfig.ExportColumn;

import java.io.Serializable;
import java.util.List;

public interface IColumnConfig extends Serializable {

    /**
     * @return Return all the collectionId that this column needs to add into
     *         the data query.
     */
    public String[] getQueryCollections(String releaseURI);

    /**
     * @param columns This method is called by the table viewer when it needs to
     *                construct all the columns. It's expected that the column
     *                config add all the columns at the end of the list.
     */
    public void addColumns(List<IColumn<IEntityTable>> columns, String releaseURI);

    public void addExportColumns(List<ExportColumn> columns, String releaseURI);

}
