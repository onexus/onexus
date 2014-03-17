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
package org.onexus.website.widgets.html;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteConfig;
import org.onexus.website.api.utils.HtmlDataResourceModel;
import org.onexus.website.api.widgets.Widget;

public class HtmlPage extends Widget<HtmlPageConfig, HtmlPageStatus> {

    public HtmlPage(String componentId, IModel<HtmlPageStatus> statusModel) {
        super(componentId, statusModel);

        HtmlPageConfig config = getConfig();
        String content = config.getContent();

        WebsiteConfig websiteConfig = config.getWebsiteConfig();
        ORI parentUri = (websiteConfig != null) ? websiteConfig.getORI().getParent() : null;

        add(new Label("content", new HtmlDataResourceModel(parentUri, content)).setEscapeModelStrings(false));
    }
}
