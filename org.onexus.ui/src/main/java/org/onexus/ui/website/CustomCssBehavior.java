package org.onexus.ui.website;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.core.util.string.UrlUtils;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.onexus.core.resources.Resource;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.OnexusWebApplication;

import java.util.regex.Pattern;


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
                String dataServiceUrl = "data" + Resource.SEPARATOR + Integer.toHexString(ResourceUtils.getProjectURI(resourceUri).hashCode()) + Resource.SEPARATOR + ResourceUtils.getResourcePath(resourceUri);
                CSS = CssHeaderItem.forUrl(dataServiceUrl);
            } else {
                CSS = CssHeaderItem.forReference(new CssResourceReference(component.getClass(), component.getClass().getSimpleName() + ".css"));
            }
        }

       response.render(CSS);
    }

}
