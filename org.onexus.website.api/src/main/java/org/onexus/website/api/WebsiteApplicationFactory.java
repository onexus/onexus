package org.onexus.website.api;

import org.onexus.resource.api.ORI;
import org.ops4j.pax.wicket.api.WebApplicationFactory;

public class WebsiteApplicationFactory implements WebApplicationFactory<WebsiteApplication> {

    private String webPath;
    private String website;

    public WebsiteApplicationFactory() {
    }

    public String getWebPath() {
        return webPath;
    }

    public void setWebPath(String webPath) {
        this.webPath = webPath;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public Class<WebsiteApplication> getWebApplicationClass() {
        return WebsiteApplication.class;
    }

    @Override
    public void onInstantiation(WebsiteApplication application) {
        application.setWebsiteOri(new ORI(website));
        application.setWebPath(webPath);
    }
}
