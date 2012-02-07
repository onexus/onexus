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

import org.apache.wicket.model.IModel;
import org.onexus.core.IEntity;
import org.onexus.core.IEntityTable;
import org.onexus.core.resources.Collection;
import org.onexus.ui.website.decorators.IDecorator;
import org.onexus.ui.website.viewers.tableviewer.headers.IHeader;

public class ViewTrack extends MatrixTrack {

    private Collection viewType;

    public ViewTrack(Collection viewType, IHeader headerDecorator,
	    IDecorator cellDecorator) {
	super(headerDecorator, cellDecorator);
	this.viewType = viewType;
    }

    @Override
    protected IModel<IEntity> getModelAdapter(IModel<IEntityTable> rowModel) {
	return new ModelAdapter(rowModel);
    }

    public class ModelAdapter implements IModel<IEntity> {
	private IModel<IEntityTable> rowModel;

	public ModelAdapter(IModel<IEntityTable> rowModel) {
	    this.rowModel = rowModel;
	}

	@Override
	public IEntity getObject() {
	    return rowModel.getObject().getEntity(viewType.getURI());
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
