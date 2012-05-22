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
package org.onexus.ui.website.pages.browser.layouts.topmain;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.pages.browser.ViewConfig;
import org.onexus.ui.website.pages.browser.layouts.AbstractLayout;
import org.onexus.ui.website.pages.browser.layouts.HorizontalWidgetBar;

public class TopmainLayout extends AbstractLayout {

    public static final CssResourceReference CSS = new CssResourceReference(TopmainLayout.class, "TopmainLayout.css");

    public TopmainLayout(String panelId, ViewConfig viewConfig, IModel<BrowserPageStatus> statusModel) {
        super(panelId, statusModel);

        // Add main widgets
        add(new HorizontalWidgetBar("topwidgets", filterWidgets(viewConfig.getTop()), statusModel));

        // Add main widgets
        add(new HorizontalWidgetBar("toprightwidgets", filterWidgets(viewConfig.getTopRight()), statusModel));


        // Add main widgets
        add(new HorizontalWidgetBar("main", filterWidgets(viewConfig.getMain()), statusModel));

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CSS));
    }
}