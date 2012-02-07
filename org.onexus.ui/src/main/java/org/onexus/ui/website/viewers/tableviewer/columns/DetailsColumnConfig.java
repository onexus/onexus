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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.onexus.core.IEntityTable;
import org.onexus.ui.website.viewers.tableviewer.columns.ColumnConfig.ExportColumn;

import java.util.Arrays;
import java.util.List;

@XStreamAlias("column-details")
public class DetailsColumnConfig implements IColumnConfig {

    private String[] collections;

    public DetailsColumnConfig() {
        super();
    }

    public DetailsColumnConfig(String... collections) {
        super();
        this.collections = collections;
    }

    public List<String> getCollections() {
        return Arrays.asList(collections);
    }

    @Override
    public void addColumns(List<IColumn<IEntityTable>> columns, String releaseURI) {
        columns.add(new DetailsTrack(this));
    }

    @Override
    public String[] getQueryCollections(String releaseURI) {
        return collections;
    }

    @Override
    public void addExportColumns(List<ExportColumn> columns, String releaseURI) {
        // Nothing to add
    }

}
