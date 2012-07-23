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

import org.apache.wicket.model.IModel;
import org.onexus.ui.api.IResourceRegister;
import org.onexus.ui.website.WebsiteConfig;
import org.onexus.ui.website.pages.AbstractPageCreator;
import org.onexus.ui.website.pages.Page;

public class HtmlPageCreator extends AbstractPageCreator<HtmlPageConfig, HtmlPageStatus> {

    public HtmlPageCreator() {
        super(HtmlPageConfig.class, "html-browser", "A html page");
    }

    @Override
    protected Page<?, ?> build(String componentId, IModel<HtmlPageStatus> statusModel) {
        return new HtmlPage(componentId, statusModel);
    }

}
