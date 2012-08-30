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

import org.apache.wicket.protocol.http.WebApplication;
import org.onexus.resource.api.IResourceActivator;
import org.onexus.resource.api.IResourceRegister;
import org.onexus.resource.api.IResourceService;
import org.onexus.ui.api.OnexusWebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebsiteActivator implements IResourceActivator {

    @Override
    public void bind(IResourceRegister resourceRegister) {
        resourceRegister.register(WebsiteConfig.class);

        for (IResourceService service : resourceRegister.getResourceServices()) {
            if (service instanceof WebApplication) {
                ((WebApplication)service).mountPage("web/${"+Website.PARAMETER_WEBSITE+"}/#{"+Website.PARAMETER_PAGE+"}/#{ptab}", Website.class);
            }
        }
    }

    @Override
    public void unbind(IResourceRegister resourceRegister) {
    }
}
