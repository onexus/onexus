package ${package};

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.onexus.ui.website.widgets.Widget;

public class HelloWidget extends Widget<HelloWidgetConfig, HelloWidgetStatus> {

    public HelloWidget(String componentId, IModel<HelloWidgetStatus> statusModel) {
        super(componentId, statusModel);

        add(new Label("message", getConfig().getMessage()));

    }

}
