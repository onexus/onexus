package ${package};

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.onexus.ui.website.widgets.WidgetConfig;

import java.lang.String;

@XStreamAlias("widget-hello")
public class HelloWidgetConfig extends WidgetConfig {

    private HelloWidgetStatus defaultStatus;
    private String message;

    public HelloWidgetConfig() {
        super();
    }

    public HelloWidgetConfig(String id, String message) {
        super(id);

        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public HelloWidgetStatus createEmptyStatus() {
        return new HelloWidgetStatus(getId());
    }

    public HelloWidgetStatus getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(HelloWidgetStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

}
