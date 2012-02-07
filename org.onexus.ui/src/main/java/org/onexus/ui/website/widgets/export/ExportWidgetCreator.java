package org.onexus.ui.website.widgets.export;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.Widget;

public class ExportWidgetCreator extends AbstractWidgetCreator<ExportWidgetConfig, ExportWidgetStatus> {
    
    public ExportWidgetCreator() {
	super(ExportWidgetConfig.class, "export-widget", "Download current table");
    }

    @Override
    protected Widget<?,?> build(String componentId, ExportWidgetConfig config, IModel<ExportWidgetStatus> statusModel) {
	return new ExportWidget(componentId, config, statusModel);
    }

}
