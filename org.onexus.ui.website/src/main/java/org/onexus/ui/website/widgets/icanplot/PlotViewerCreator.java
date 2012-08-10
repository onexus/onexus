package org.onexus.ui.website.widgets.icanplot;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.Widget;

public class PlotViewerCreator extends AbstractWidgetCreator<PlotViewerConfig, PlotViewerStatus> {


    public PlotViewerCreator() {
        super(PlotViewerConfig.class, "viewer-plot", "Interactive plot viewer");
    }

    @Override
    protected Widget<?, ?> build(String componentId, IModel<PlotViewerStatus> statusModel) {
        return new PlotViewer(componentId, statusModel);
    }
}
