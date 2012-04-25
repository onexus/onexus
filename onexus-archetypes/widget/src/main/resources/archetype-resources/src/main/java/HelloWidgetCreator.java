package ${package};

import org.apache.wicket.model.IModel;
import org.onexus.ui.IResourceRegister;
import org.onexus.ui.website.WebsiteConfig;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.IWidgetModel;
import org.onexus.ui.website.widgets.Widget;

public class HelloWidgetCreator extends AbstractWidgetCreator<HelloWidgetConfig, HelloWidgetStatus> {

    public HelloWidgetCreator() {
        super(HelloWidgetConfig.class, "widget-hello", "Hello world widget");
    }

    @Override
    protected Widget<?, ?> build(String componentId, IWidgetModel<HelloWidgetStatus> statusModel) {
        return new HelloWidget(componentId, statusModel);
    }

    @Override
    public void register(IResourceRegister resourceRegister) {
        super.register(resourceRegister);
        resourceRegister.addAutoComplete(WebsiteConfig.class, "widgets",
                "        <widget-hello>\n" +
                "          <id>hello-world</id>\n" +
                "          <message>This is my first Onexus widget!</message>\n" +
                "        </widget-hello>");
    }
}
