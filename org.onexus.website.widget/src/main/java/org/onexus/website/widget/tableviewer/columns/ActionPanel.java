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
package org.onexus.website.widget.tableviewer.columns;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.onexus.collection.api.IEntity;
import org.onexus.website.widget.tableviewer.decorators.IDecorator;

import java.util.List;

public class ActionPanel extends Panel {

    public ActionPanel(String id, IDecorator decorator, List<IDecorator> actions, IModel<IEntity> entityModel) {
        super(id);

        RepeatingView actionsView = new RepeatingView("actions");
        for (IDecorator action : actions) {
            action.populateCell(actionsView, actionsView.newChildId(), entityModel);
        }
        add(actionsView);

        decorator.populateCell(this, "cell", entityModel);
    }
}
