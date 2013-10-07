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
package org.onexus.website.api.widgets.tableviewer.columns;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.onexus.collection.api.IEntityTable;
import org.onexus.resource.api.ORI;

public abstract class AbstractColumn implements IColumn<IEntityTable, String> {

    private ORI collectionId;

    public AbstractColumn() {
        super();
        this.collectionId = null;
    }

    public AbstractColumn(ORI collectionId) {
        super();
        this.collectionId = collectionId;
    }

    public ORI getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(ORI collectionId) {
        this.collectionId = collectionId;
    }

    @Override
    public void detach() {
    }

    @Override
    public String getSortProperty() {
        return null;
    }

    @Override
    public boolean isSortable() {
        return false;
    }

}
