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
package org.onexus.website.api.pages.html;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.ResourceReference;
import org.onexus.data.api.IDataManager;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.WebsiteConfig;
import org.onexus.website.api.pages.Page;
import org.onexus.website.api.utils.HtmlDataResourceModel;
import org.ops4j.pax.wicket.api.PaxWicketBean;

public class HtmlPage extends Page<HtmlPageConfig, HtmlPageStatus> {

    @PaxWicketBean(name="dataManager")
    private IDataManager dataManager;

    public HtmlPage(String componentId, IModel<HtmlPageStatus> statusModel) {
        super(componentId, statusModel);

        HtmlPageConfig config = getConfig();
        String content = config.getContent();


        WebsiteConfig websiteConfig = config.getWebsiteConfig();
        ORI parentUri = (websiteConfig != null) ? websiteConfig.getURI().getParent() : null;
        ORI contentUri = new ORI(parentUri, content);

        add(new Label("content", new HtmlDataResourceModel(contentUri, this)).setEscapeModelStrings(false));
    }
}
