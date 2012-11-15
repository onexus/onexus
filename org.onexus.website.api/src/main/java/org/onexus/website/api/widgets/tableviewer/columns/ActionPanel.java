package org.onexus.website.api.widgets.tableviewer.columns;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.onexus.collection.api.IEntity;
import org.onexus.website.api.widgets.tableviewer.decorators.IDecorator;

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
