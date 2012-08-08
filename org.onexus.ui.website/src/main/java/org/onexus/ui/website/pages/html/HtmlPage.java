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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.io.IOUtils;
import org.onexus.data.api.IDataManager;
import org.onexus.data.api.IDataStreams;
import org.onexus.resource.api.utils.ResourceUtils;
import org.onexus.ui.website.WebsiteConfig;
import org.onexus.ui.website.pages.Page;

import javax.inject.Inject;
import java.io.IOException;

public class HtmlPage extends Page<HtmlPageConfig, HtmlPageStatus> {

    @Inject
    public IDataManager dataManager;

    public HtmlPage(String componentId, IModel<HtmlPageStatus> statusModel) {
        super(componentId, statusModel);

        add(new Label("content", new ContentModel()).setEscapeModelStrings(false));
    }

    public class ContentModel extends LoadableDetachableModel<String> {

        @Override
        protected String load() {

            HtmlPageConfig config = getConfig();
            String content = config.getContent();


            WebsiteConfig websiteConfig = config.getWebsiteConfig();
            String parentUri = (websiteConfig != null) ? ResourceUtils.getParentURI(websiteConfig.getURI()) : null;
            String contentUri = ResourceUtils.getAbsoluteURI(parentUri, content);

            IDataStreams stream = dataManager.load(contentUri);

            try {
                return IOUtils.toString(stream.iterator().next());
            } catch (IOException e) {
                return "";
            }
        }
    }

}
