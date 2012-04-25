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
package org.onexus.ui.website.widgets.bookmark;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.ui.website.IWebsiteModel;
import org.onexus.ui.website.Website;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.widgets.IWidgetModel;
import org.onexus.ui.website.widgets.Widget;

import java.io.UnsupportedEncodingException;

public class BookmarkWidget extends Widget<BookmarkWidgetConfig, BookmarkWidgetStatus> {

    private transient StatusEncoder statusEncoder;

    public BookmarkWidget(String componentId, IWidgetModel<BookmarkWidgetStatus> statusModel) {
        super(componentId, statusModel);

        onEventFireUpdate(EventQueryUpdate.class);
    }

    private StatusEncoder getStatusEncoder() {
        if (statusEncoder == null) {
            statusEncoder = new StatusEncoder(getClass().getClassLoader());
        }
        return statusEncoder;
    }

    @Override
    protected void onBeforeRender() {

        IWebsiteModel websiteModel = getWebsiteModel();
        PageParameters params = new PageParameters();

        if (websiteModel != null) {
            String strStatus;
            try {
                strStatus = getStatusEncoder().encodeStatus(websiteModel.getObject());
            } catch (UnsupportedEncodingException e) {
                throw new WicketRuntimeException("Unable to encode the URL parameter 'status'", e);
            }

            params.add(Website.PARAMETER_STATUS, strStatus);
            params.add(Website.PARAMETER_WEBSITE, websiteModel.getConfig().getURI());
        }

        BookmarkablePageLink<String> link = new BookmarkablePageLink<String>("directLink", getPage().getClass(), params);
        link.add(new Image("image", "link.png"));
        addOrReplace(link);

        if (websiteModel == null) {
            link.setEnabled(false);
        }

        super.onBeforeRender();

    }

}
