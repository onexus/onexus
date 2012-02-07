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

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.onexus.core.IEntity;
import org.onexus.core.resources.Field;
import org.onexus.ui.website.boxes.forms.EntityFormFactory;

/**
 * Base class to instantiate (creates) new concrete panel.
 * <p/>
 * There is two possibilities. If there is not any viewerClass specified, a
 * generic way to display all data from an IEntity is used (in concrete
 * ElementObjectDisplayPanel)
 *
 * @author armand
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class GenericBox extends AbstractBox {

    public GenericBox(String collectionId, IModel<IEntity> entityModel) {
        super(collectionId, entityModel);

        RepeatingView fieldsRV = new RepeatingView("fields");
        for (Field field : entityModel.getObject().getCollection().getFields()) {

            // Skip fields with null value
            if (entityModel.getObject().get(field.getName()) == null) {
                continue;
            }

            String caption = field.getTitle();
            if (caption == null) {
                caption = field.getName();
            }

            // Create the field container
            WebMarkupContainer fieldContainer = new WebMarkupContainer(
                    fieldsRV.newChildId());
            fieldContainer.add(new Label("caption", caption));
            fieldContainer.add(EntityFormFactory.getFieldPanel("value", field,
                    new FieldValueModel(field.getName(), entityModel)));
            fieldsRV.add(fieldContainer);

        }
        add(fieldsRV);

    }

}
