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

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.widgets.tableviewer.decorators.IDecorator;
import org.onexus.website.api.widgets.tableviewer.headers.IHeader;

import java.util.List;

public class CollectionColumn extends AbstractColumn {


    private IHeader headerDecorator;
    private IDecorator cellDecorator;
    private List<IDecorator> cellActions;

    public CollectionColumn(ORI collectionId, IHeader headerDecorator, IDecorator cellDecorator, List<IDecorator> cellActions) {
        super(collectionId);

        this.headerDecorator = headerDecorator;
        this.cellDecorator = cellDecorator;
        this.cellActions = cellActions;

    }

    protected IModel<IEntity> getModelAdapter(IModel<IEntityTable> rowModel) {
        return new ModelAdapter(rowModel);
    }

    @Override
    public void populateItem(Item<ICellPopulator<IEntityTable>> cellItem,
                             String componentId, IModel<IEntityTable> rowModel) {

        if (cellActions == null || cellActions.isEmpty()) {
            cellDecorator.populateCell(cellItem, componentId, getModelAdapter(rowModel));
        } else {
            cellItem.add(new ActionPanel(componentId, cellDecorator, cellActions, getModelAdapter(rowModel)));
        }
    }

    public IDecorator getCellDecorator() {
        return cellDecorator;
    }

    @Override
    public Component getHeader(String componentId) {
        return headerDecorator.getHeader(componentId);
    }

    public IHeader getHeaderDecorator() {
        return headerDecorator;
    }

    @Override
    public String getSortProperty() {
        return headerDecorator.getSortProperty();
    }

    @Override
    public boolean isSortable() {
        return headerDecorator.isSortable();
    }

    public class ModelAdapter implements IModel<IEntity> {
        private IModel<IEntityTable> rowModel;

        public ModelAdapter(IModel<IEntityTable> rowModel) {
            this.rowModel = rowModel;
        }

        @Override
        public IEntity getObject() {
            return rowModel.getObject().getEntity(CollectionColumn.this.getCollectionId());
        }

        @Override
        public void setObject(IEntity object) {

            // Read only model
            throw new UnsupportedOperationException("Read-only model");
        }

        @Override
        public void detach() {
        }

    }

}
