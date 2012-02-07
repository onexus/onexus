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

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.onexus.core.IEntityTable;
import org.onexus.ui.website.viewers.tableviewer.columns.IColumnConfig;
import org.onexus.ui.website.viewers.tableviewer.columns.ColumnConfig.ExportColumn;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("column-tags")
public class TagColumnConfig implements IColumnConfig {

    private String collection;

    public TagColumnConfig() {
	super();
    }
    
    public TagColumnConfig(String collection) {
	super();
	this.collection = collection;
    }

    public String getCollection() {
	return collection;
    }

    public void setCollection(String collection) {
	this.collection = collection;
    }

    @Override
    public String[] getQueryCollections(String releaseURI ) {
	return new String[0];
    }

    @Override
    public void addColumns(List<IColumn<IEntityTable>> columns, String releaseURI ) {
	columns.add(new TagColumn(collection));
    }

    @Override
    public void addExportColumns(List<ExportColumn> columns, String releaseURI ) {
	// Nothing to export
    }

}
