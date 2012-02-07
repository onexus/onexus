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
package org.onexus.ui.website.boxes.forms;

import java.util.Date;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Field;

public class EntityFormFactory {

   
    public static Panel getFieldPanel(String id, Field field,
	    IModel<?> valueObjectModel) {
	return getFieldPanel(id, field, field.getDataType(), valueObjectModel);
    }

    @SuppressWarnings("unchecked")
    private static <T> Panel getFieldPanel(String id, Field field,
	    Class<?> valueClass, IModel<?> valueObjectModel) {

	if (Date.class.isAssignableFrom(valueClass)) {
	    return new ViewStringFieldPanel(id,
			(IModel<String>) valueObjectModel);
	}

	if (Number.class.isAssignableFrom(valueClass)) {
	    return new ViewNumericFieldPanel(id, valueObjectModel);
	}

	if (String.class.isAssignableFrom(valueClass)) {
	    if (field != null && field.getProperty("LINK") != null) {
		return new LinkStringFieldPanel(id,
			(IModel<String>) valueObjectModel,
			field.getProperty("LINK"));
	    }

	    return new ViewStringFieldPanel(id,
		    (IModel<String>) valueObjectModel);
	}

	throw new UnsupportedOperationException("Unknown field type "
		+ valueClass);

    }
    
    
}
