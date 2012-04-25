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

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.onexus.core.IEntity;
import org.onexus.core.IEntityTable;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecorator;
import org.onexus.ui.website.widgets.tableviewer.headers.IHeader;

/**
 * @author Jordi Deu-Pons
 */
public abstract class MatrixTrack extends TableTrack {

    private IHeader headerDecorator;
    private IDecorator cellDecorator;

    public MatrixTrack(IHeader headerDecorator, IDecorator cellDecorator) {
        super();
        this.headerDecorator = headerDecorator;
        this.cellDecorator = cellDecorator;
    }

    protected abstract IModel<IEntity> getModelAdapter(
            IModel<IEntityTable> rowModel);

    @Override
    public void populateItem(Item<ICellPopulator<IEntityTable>> cellItem,
                             String componentId, IModel<IEntityTable> rowModel) {
        cellDecorator.populateCell(cellItem, componentId,
                getModelAdapter(rowModel));
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

}
