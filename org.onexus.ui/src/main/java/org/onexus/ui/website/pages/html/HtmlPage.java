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
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.resource.CssResourceReference;
import org.onexus.ui.website.pages.IPageModel;
import org.onexus.ui.website.pages.Page;

public class HtmlPage extends Page<HtmlPageConfig, HtmlPageStatus> {

    public final static CssResourceReference CSS = new CssResourceReference(HtmlPage.class, "HtmlPage.css");

    public HtmlPage(String componentId, IPageModel<HtmlPageStatus> statusModel) {
        super(componentId, statusModel);
        
        add(new Label("content", getConfig().getContent()).setEscapeModelStrings(false));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CSS));
        
        if (getConfig().getCss() != null) {
            response.render(CssHeaderItem.forCSS(getConfig().getCss(), "page-css"));
        }
    }

}
