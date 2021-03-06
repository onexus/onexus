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
package org.onexus.website.widget.tags;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.collection.api.IEntityTable;
import org.onexus.resource.api.ORI;
import org.onexus.website.widget.tableviewer.columns.AbstractColumn;

public class TagColumn extends AbstractColumn {

    private ORI collectionURI;

    public TagColumn(ORI collectionURI) {
        super();
        this.collectionURI = collectionURI;
    }

    @Override
    public void populateItem(Item<ICellPopulator<IEntityTable>> cellItem,
                             String componentId, IModel<IEntityTable> rowModel) {

        String rowValue = rowModel.getObject().getEntity(collectionURI).getId();

        cellItem.add(new TagColumnItem(componentId, Model.of(rowValue)));

    }


    @Override
    public void detach() {

    }

    @Override
    public Component getHeader(String componentId) {
        return new TagColumnHeader(componentId);
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
