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
package org.onexus.ui.website.widgets.filters;

import org.apache.wicket.model.PropertyModel;
import org.onexus.ui.website.formaters.ITextFormater;

public class TextFormaterPropertyModel extends PropertyModel<String> {

    private ITextFormater formater;

    public TextFormaterPropertyModel(Object modelObject, String expression,
	    ITextFormater formater) {
	super(modelObject, expression);
	this.formater = formater;
    }

    @Override
    public String getObject() {
	Object value = super.getObject();
	return (value == null ? null : formater.format(value));
    }

}
