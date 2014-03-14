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
package org.onexus.website.widgets.pages.search.boxes;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;

public class FieldsPanel extends Panel {

    public FieldsPanel(String id, String labelField, IEntity entity) {
        super(id);

        Collection collection = entity.getCollection();

        // Fields values
        RepeatingView fields = new RepeatingView("fields");
        for (Field field : collection.getFields()) {

            if (labelField != null && labelField.equals(field.getId())) {
                continue;
            }

            Object value = entity.get(field.getId());
            if (value != null && !StringUtils.isEmpty(value.toString())) {

                WebMarkupContainer fc = new WebMarkupContainer(fields.newChildId());
                fc.setRenderBodyOnly(true);
                fc.add(new Label("label", field.getLabel()).add(new AttributeModifier("title", field.getTitle())));
                fc.add(new Label("value", StringUtils.abbreviate(value.toString(), 50)));
                fields.add(fc);
            }
        }

        add(fields);
    }

}
