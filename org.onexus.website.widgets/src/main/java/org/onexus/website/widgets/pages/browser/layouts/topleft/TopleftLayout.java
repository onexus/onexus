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
package org.onexus.website.widgets.pages.browser.layouts.topleft;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.onexus.website.widgets.pages.browser.BrowserPageStatus;
import org.onexus.website.widgets.pages.browser.ViewConfig;
import org.onexus.website.widgets.pages.browser.layouts.AbstractLayout;
import org.onexus.website.widgets.pages.browser.layouts.HorizontalWidgetBar;
import org.onexus.website.widgets.pages.browser.layouts.VerticalWidgetBar;

public class TopleftLayout extends AbstractLayout {

    public static final CssResourceReference CSS = new CssResourceReference(TopleftLayout.class, "TopleftLayout.css");


    public TopleftLayout(String panelId, ViewConfig viewConfig, IModel<BrowserPageStatus> statusModel) {
        super(panelId, statusModel);

        // Add left widgets
        add(new VerticalWidgetBar("leftwidgets", filterWidgets(viewConfig.getLeft()), statusModel));

        // Add main widgets
        add(new HorizontalWidgetBar("topwidgets", filterWidgets(viewConfig.getTop()), statusModel));

        // Add main widgets
        add(new HorizontalWidgetBar("toprightwidgets", filterWidgets(viewConfig.getTopRight()), statusModel));


        // Add main widgets
        add(new HorizontalWidgetBar("main", filterWidgets(viewConfig.getMain()), statusModel));

    }

    @Override
    protected void onBeforeRender() {
        StringValue embed = getPage().getPageParameters().get("embed");

        boolean visible = !embed.toBoolean(false);
        get("leftwidgets").setVisible(visible);
        get("topwidgets").setVisible(visible);
        get("toprightwidgets").setVisible(visible);

        super.onBeforeRender();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CSS));

        if (isEmbed()) {
            response.render(CssHeaderItem.forCSS("div.gridbar { display: none; }", "embed-layout"));
        }
    }
}
