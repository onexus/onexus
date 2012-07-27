/**
 *  Copyright 2012 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.ui.website;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.resource.CssResourceReference;
import org.onexus.resource.api.Resource;
import org.onexus.resource.api.utils.ResourceUtils;

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
