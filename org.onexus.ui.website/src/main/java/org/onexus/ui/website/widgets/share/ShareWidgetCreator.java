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

import org.apache.wicket.model.IModel;
import org.onexus.ui.core.IResourceRegister;
import org.onexus.ui.website.WebsiteConfig;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.Widget;

public class ShareWidgetCreator extends AbstractWidgetCreator<ShareWidgetConfig, ShareWidgetStatus> {

    public ShareWidgetCreator() {
        super(ShareWidgetConfig.class, "share-widget", "Create linkable bookmarks.");
    }

    @Override
    protected Widget<?, ?> build(String componentId, IModel<ShareWidgetStatus> statusModel) {
        return new ShareWidget(componentId, statusModel);
    }

    @Override
    public void register(IResourceRegister resourceRegister) {
        super.register(resourceRegister);
        resourceRegister.addAutoComplete(WebsiteConfig.class, "widgets",  "<widget-share><id>share</id></widget-share>");
    }

}
