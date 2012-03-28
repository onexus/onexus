package org.onexus.ui.website.pages;

import org.onexus.ui.website.IWebsiteModel;
import org.onexus.ui.website.WebsiteStatus;


public class PageModel<S extends PageStatus> implements IPageModel<S> {


    private PageConfig pageConfig;
    private IWebsiteModel websiteModel;
    private S status;

    public PageModel(PageConfig pageConfig) {
        this(pageConfig, null);
    }

    public PageModel(PageConfig pageConfig, IWebsiteModel websiteModel) {
        this.pageConfig = pageConfig;
        this.websiteModel = websiteModel;
    }

    @Override
    public PageConfig getConfig() {
        return pageConfig;
    }

    @Override
    public IWebsiteModel getWebsiteModel() {
        return websiteModel;
    }

    @Override
    public S getObject() {

        if (status != null) {
            return status;
        }

        WebsiteStatus websiteStatus = (websiteModel == null ? null : websiteModel.getObject());
        if (websiteStatus != null) {
            status = (S) websiteStatus.getPageStatus(pageConfig.getId());
        }

        if (status == null) {
            status = (S) pageConfig.getDefaultStatus();

            if (status == null) {
                status = (S) pageConfig.createEmptyStatus();
            }

            if (status != null) {
                setObject(status);
            }
        }

        return status;
    }

    @Override
    public void setObject(S object) {
        this.status = object;

        if (websiteModel != null) {
            WebsiteStatus websiteStatus = websiteModel.getObject();
            if (websiteStatus != null) {
                websiteStatus.setPageStatus(object);
            }
        }

    }

    @Override
    public void detach() {
        if (websiteModel != null) {
            status = null;
        }
    }
}
