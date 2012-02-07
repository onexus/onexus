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
package org.onexus.ui.website.utils;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.onexus.core.IEntity;
import org.onexus.core.resources.Field;

public class EntityRenderer implements IChoiceRenderer<IEntity> {

    private String displayField;
    private String nullValue;

    public EntityRenderer(String displayField, String nullValue) {
	super();
	this.displayField = displayField;
	this.nullValue = nullValue;
    }

    public EntityRenderer(Field displayField, String nullValue) {
	super();
	this.displayField = displayField.getName();
	this.nullValue = nullValue;
    }

    @Override
    public Object getDisplayValue(IEntity entity) {
	return (entity == null ? nullValue : entity.get(displayField));
    }

    @Override
    public String getIdValue(IEntity object, int index) {
	return Integer.toString(index);
    }

}
