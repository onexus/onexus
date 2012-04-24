package ${package};

import org.apache.wicket.markup.html.basic.Label;
import org.onexus.ui.website.widgets.IWidgetModel;
import org.onexus.ui.website.widgets.Widget;

public class HelloWidget extends Widget<HelloWidgetConfig, HelloWidgetStatus> {

    public HelloWidget(String componentId, IWidgetModel<HelloWidgetStatus> statusModel) {
        super(componentId, statusModel);

        add(new Label("message", getConfig().getMessage()));

    }

}
