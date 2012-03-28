package org.onexus.ui.website.widgets;

import org.onexus.ui.website.pages.IPageModel;
import org.onexus.ui.website.pages.PageStatus;

public class WidgetModel<S extends WidgetStatus> implements IWidgetModel<S> {

    private WidgetConfig config;
    private IPageModel<? extends PageStatus> pageModel;
    private S status;

    public WidgetModel(WidgetConfig config, IPageModel<? extends PageStatus> pageModel) {
        super();
        this.config = config;
        this.pageModel = pageModel;
    }

    @Override
    public WidgetConfig getConfig() {
        return config;
    }

    @Override
    public IPageModel getPageModel() {
        return pageModel;
    }

    @Override
    public S getObject() {

        if (status != null ) {
            return status;
        }

        status = (S) pageModel.getObject().getWidgetStatus(config.getId());
        
        if (status == null) {
            status = (S) config.getDefaultStatus();

            if (status == null) {
                status = (S) config.createEmptyStatus();
            }
            
            if (status != null) {
                setObject(status);
            }
        }
        
        return status;
    }

    @Override
    public void setObject(S object) {
        status = object;
        pageModel.getObject().setWidgetStatus(object);
    }

    @Override
    public void detach() {
    }
}
