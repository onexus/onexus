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
import org.onexus.ui.core.IResourceActivator;
import org.onexus.ui.core.IResourceRegister;

public class WebsiteActivator implements IResourceActivator {

    @Override
    public void bind(IResourceRegister resourceRegister) {
        resourceRegister.register(WebsiteConfig.class);
        resourceRegister.addAutoComplete(WebsiteConfig.class, "website", "<default><currentPage>[page-id]</currentPage></default>");
        resourceRegister.addAutoComplete(WebsiteConfig.class, "website", "<title>[title]</title>");
        resourceRegister.addAutoComplete(WebsiteConfig.class, "website", "<pages></pages>");
        resourceRegister.addAutoComplete(WebsiteConfig.class, "website", "<description>[description]</description>");
        resourceRegister.addAutoComplete(WebsiteConfig.class, "website", "<property><key>[key]</key><value>[value]</value></property>");
    }

    @Override
    public void unbind(IResourceRegister resourceRegister) {
    }
}
