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
package org.onexus.website.widget.details;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.onexus.collection.api.IEntity;
import org.onexus.website.api.IEntitySelection;
import org.onexus.website.api.SingleEntitySelection;
import org.onexus.website.api.widget.Widget;
import org.onexus.website.widget.browser.BrowserPageStatus;
import org.onexus.website.widget.searchpage.boxes.FieldsPanel;

public class DetailsWidget extends Widget<DetailsWidgetConfig, DetailsWidgetStatus> {

    public DetailsWidget(String componentId, IModel<DetailsWidgetStatus> statusModel) {
        super(componentId, statusModel);

        BrowserPageStatus pageStatus = findParentStatus(BrowserPageStatus.class);

        RepeatingView entities = new RepeatingView("entities");

        for (IEntitySelection selection : pageStatus.getEntitySelections()) {
            if (selection instanceof SingleEntitySelection) {
                IEntity entity = ((SingleEntitySelection) selection).getEntity(pageStatus.getORI());
                if (entity != null) {
                    WebMarkupContainer box = new WebMarkupContainer(entities.newChildId());
                    String labelField = entity.getCollection().getProperty("FIXED_ENTITY_FIELD");
                    String label = getLabel(entity, labelField);
                    box.add(new Label("label", label).setEscapeModelStrings(false));
                    box.add(new FieldsPanel("fields", labelField, entity));
                    entities.add(box);
                }
            }
        }

        add(entities);
    }

    private String getLabel(IEntity entity, String labelField) {
        return labelField == null ?
                StringUtils.replace(entity.getId(), "\t", "-") :
                String.valueOf(entity.get(labelField));
    }
}
