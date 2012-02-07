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
package org.onexus.ui.website.boxes;

import org.apache.wicket.model.AbstractWrapModel;
import org.apache.wicket.model.IModel;
import org.onexus.core.IEntity;

public class FieldValueModel<T> extends AbstractWrapModel<T> {

    private String fieldName;
    private IModel<? extends IEntity> complexModel;

    public FieldValueModel(String fieldName,
	    IModel<? extends IEntity> complexElementModel) {
	super();
	this.fieldName = fieldName;
	this.complexModel = complexElementModel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() {
	return (T) getWrappedModel().getObject().get(fieldName);
    }

    @Override
    public void setObject(T object) {
	getWrappedModel().getObject().put(fieldName, object);
    }

    @Override
    public IModel<? extends IEntity> getWrappedModel() {
	return complexModel;
    }

    @Override
    public void detach() {
	super.detach();
    }

}
