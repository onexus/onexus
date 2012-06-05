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
package org.onexus.ui.website.widgets.share;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.ui.website.Website;
import org.onexus.ui.website.WebsiteStatus;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.widgets.Widget;

public class ShareWidget extends Widget<ShareWidgetConfig, ShareWidgetStatus> {


    public ShareWidget(String componentId, IModel<ShareWidgetStatus> statusModel) {
        super(componentId, statusModel);

        onEventFireUpdate(EventQueryUpdate.class);
    }


    @Override
    protected void onBeforeRender() {

        Website website = findParent(Website.class);
        PageParameters params = new PageParameters();

        if (website != null) {
            WebsiteStatus status = website.getStatus();
            status.encodeParameters(params);

            // If the website URI is not defined at Application level then add it as a parameter.
            if (Application.get().getMetaData(Website.WEBSITE_CONFIG) == null) {
                params.add(Website.PARAMETER_WEBSITE, website.getConfig().getURI());
            }

        }

        BookmarkablePageLink<String> link = new BookmarkablePageLink<String>("directLink", getPage().getClass(), params);
        link.add(new Image("image", "link.png"));
        addOrReplace(link);

        PageParameters embedParams = new PageParameters(params);
        embedParams.set("embed", true);
        BookmarkablePageLink<String> elink = new BookmarkablePageLink<String>("embedLink", getPage().getClass(), embedParams);
        elink.add(new Image("image", "link.png"));
        addOrReplace(elink);

        super.onBeforeRender();

    }

}
