package org.onexus.ui.website.widgets.icanplot;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.onexus.ui.website.widgets.WidgetConfig;
import org.onexus.ui.website.widgets.WidgetStatus;

@XStreamAlias("viewer-plot")
public class PlotViewerConfig extends WidgetConfig {

    private PlotViewerStatus defaultStatus;

    @Override
    public WidgetStatus getDefaultStatus() {
        return defaultStatus;
    }

    @Override
    public WidgetStatus createEmptyStatus() {
        return new PlotViewerStatus(getId());
    }
}
