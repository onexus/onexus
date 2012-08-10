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
package org.onexus.ui.website.pages.html;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.resource.JQueryPluginResourceReference;
import org.onexus.data.api.IDataManager;
import org.onexus.resource.api.utils.ResourceUtils;
import org.onexus.ui.website.WebsiteConfig;
import org.onexus.ui.website.pages.Page;
import org.onexus.ui.website.utils.HtmlDataResourceModel;

import javax.inject.Inject;

public class HtmlPage extends Page<HtmlPageConfig, HtmlPageStatus> {

    @Inject
    public IDataManager dataManager;

    public HtmlPage(String componentId, IModel<HtmlPageStatus> statusModel) {
        super(componentId, statusModel);

        HtmlPageConfig config = getConfig();
        String content = config.getContent();


        WebsiteConfig websiteConfig = config.getWebsiteConfig();
        String parentUri = (websiteConfig != null) ? ResourceUtils.getParentURI(websiteConfig.getURI()) : null;
        String contentUri = ResourceUtils.getAbsoluteURI(parentUri, content);

        add(new Label("content", new HtmlDataResourceModel(contentUri)).setEscapeModelStrings(false));
    }
}
