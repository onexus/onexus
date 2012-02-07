package org.onexus.ui.website.widgets;

import org.apache.wicket.model.IModel;

public interface IWidgetManager {

    Widget<?,?> create(String componentId, WidgetConfig config, IModel<WidgetStatus> statusModel);

}
