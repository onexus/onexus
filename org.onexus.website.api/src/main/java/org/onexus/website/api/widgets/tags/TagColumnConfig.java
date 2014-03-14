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
package org.onexus.website.api.widgets.tags;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.website.api.widgets.tableviewer.columns.IColumnConfig;

import javax.validation.constraints.NotNull;
import java.util.List;

@ResourceAlias("column-tags")
public class TagColumnConfig implements IColumnConfig {

    @NotNull
    private ORI collection;

    private String visible;

    private String sortable;

    public TagColumnConfig() {
        super();
    }

    public TagColumnConfig(ORI collection) {
        super();
        this.collection = collection;
    }

    public ORI getCollection() {
        return collection;
    }

    public void setCollection(ORI collection) {
        this.collection = collection;
    }

    @Override
    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    @Override
    public String getSortable() {
        return sortable;
    }

    public void setSortable(String sortable) {
        this.sortable = sortable;
    }

    @Override
    public void buildQuery(Query query) {
        // Nothing to add
    }

    @Override
    public void addColumns(List<IColumn<IEntityTable, String>> columns, ORI parentURI, boolean sortable) {
        columns.add(new TagColumn(collection));
    }

}
