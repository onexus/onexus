package org.onexus.ui.website;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.OnexusWebApplication;


public class CustomCssBehavior extends Behavior {

    private transient CssHeaderItem CSS;

    private String resourceUri;


    public CustomCssBehavior(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {

        if (CSS == null) {

            if (resourceUri != null) {

                ResourceReference webService = OnexusWebApplication.get().getWebService();

                if (webService != null) {
                    PageParameters parameters = new PageParameters();
                    parameters.add("data", ResourceUtils.getParentURI(resourceUri));
                    parameters.add("filename", ResourceUtils.getResourceName(resourceUri));
                    CSS = CssHeaderItem.forReference(webService, parameters, null);
                } else {
                    CSS = CssHeaderItem.forReference(new CssResourceReference(component.getClass(), component.getClass().getSimpleName() + ".css"));
                }

            } else {
                CSS = CssHeaderItem.forReference(new CssResourceReference(component.getClass(), component.getClass().getSimpleName() + ".css"));
            }
        }

        response.render(CSS);
    }


}
