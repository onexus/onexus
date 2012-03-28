package org.onexus.ui.website.widgets;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.pages.IPageModel;

public interface IWidgetModel<S extends WidgetStatus> extends IModel<S> {

    public WidgetConfig getConfig();

    public IPageModel getPageModel();

}
