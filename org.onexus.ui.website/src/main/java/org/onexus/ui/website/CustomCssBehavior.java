package org.onexus.ui.website;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.resource.CssResourceReference;
import org.onexus.core.resources.Resource;
import org.onexus.core.utils.ResourceUtils;

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
