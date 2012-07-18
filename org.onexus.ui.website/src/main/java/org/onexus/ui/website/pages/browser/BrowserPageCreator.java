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
package org.onexus.ui.website.pages.browser;

import org.apache.wicket.model.IModel;
import org.onexus.ui.core.IResourceRegister;
import org.onexus.ui.website.WebsiteConfig;
import org.onexus.ui.website.pages.AbstractPageCreator;
import org.onexus.ui.website.pages.Page;

public class BrowserPageCreator extends AbstractPageCreator<BrowserPageConfig, BrowserPageStatus> {

    public BrowserPageCreator() {
        super(BrowserPageConfig.class, "fixed-browser", "A collection browser");
    }

    @Override
    protected Page<?, ?> build(String componentId, IModel<BrowserPageStatus> statusModel) {
        return new BrowserPage(componentId, statusModel);
    }

    @Override
    public void register(IResourceRegister resourceRegister) {
        super.register(resourceRegister);

        resourceRegister.addAutoComplete(WebsiteConfig.class, "pages",
                "<browser>" +
                        "<id>[page-id]</id>" +
                        "<label>[page-name]</label>" +
                        "<base>[base-uri]</base>" +
                        "<tabs></tabs>" +
                        "<widgets></widgets>" +
                        "</browser>");

        resourceRegister.addAutoComplete(WebsiteConfig.class, "tabs",
                "<tab>" +
                        "<id>[tab-id]</id>" +
                        "<title>[tab-title]</title>" +
                        "<view>" +
                        "<title>[view-title]</title>" +
                        "</view></tab>");

        resourceRegister.addAutoComplete(WebsiteConfig.class, "tab",  "<id>[tab-id]</id>");
        resourceRegister.addAutoComplete(WebsiteConfig.class, "tab",  "<title>[tab-title]</title>");
        resourceRegister.addAutoComplete(WebsiteConfig.class, "tab",  "<view><title>[view-title]</title></view>");

        resourceRegister.addAutoComplete(WebsiteConfig.class, "view", "<main>[widget-ids]</main>");
        resourceRegister.addAutoComplete(WebsiteConfig.class, "view", "<left>[widget-ids]</left>");
        resourceRegister.addAutoComplete(WebsiteConfig.class, "view", "<top>[widget-ids]</top>");
        resourceRegister.addAutoComplete(WebsiteConfig.class, "view", "<top-right>[widget-ids]</top-right>");
        resourceRegister.addAutoComplete(WebsiteConfig.class, "view", "<title>[view-title]</title>");

    }
}
