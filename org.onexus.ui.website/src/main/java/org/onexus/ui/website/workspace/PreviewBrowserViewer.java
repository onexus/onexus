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
package org.onexus.ui.website.workspace;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.link.InlineFrame;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.onexus.resource.api.Resource;
import org.onexus.website.api.Website;
import org.onexus.website.api.WebsiteModel;

public class PreviewBrowserViewer extends Panel {

    public PreviewBrowserViewer(String id, final IModel<? extends Resource> model) {
        super(id);

        WebApplication.get().mountPage("web"  + "/${"+Website.PARAMETER_PAGE+"}/#{ptab}" , Website.class);

        Session.get().setMetaData(WebsiteModel.WEBSITE_KEY, model.getObject().getURI());
        add(new InlineFrame("browser", Website.class));
    }

}
