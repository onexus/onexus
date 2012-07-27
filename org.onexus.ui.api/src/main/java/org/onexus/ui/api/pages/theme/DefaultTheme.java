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
package org.onexus.ui.api.pages.theme;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.resource.JQueryPluginResourceReference;

public class DefaultTheme extends Behavior {

    public static final CssResourceReference BOOTSTRAP_CSS = new CssResourceReference(DefaultTheme.class, "css/bootstrap.min.css");
    public static final JQueryPluginResourceReference BOOTSTRAP_JS = new JQueryPluginResourceReference(DefaultTheme.class, "js/bootstrap.min.js");
    public static final CssResourceReference STYLE_CSS = new CssResourceReference(DefaultTheme.class, "css/style.css");

    @Override
    public void renderHead(Component component, IHeaderResponse response) {

        response.render(CssHeaderItem.forReference(BOOTSTRAP_CSS));
        response.render(CssHeaderItem.forReference(STYLE_CSS));

        response.render(JavaScriptHeaderItem.forReference(BOOTSTRAP_JS));
        response.render(JavaScriptHeaderItem.forScript("     $(document).ready(function () {\n" +
                "        $(\"[rel=tooltip]\").tooltip({ placement: 'bottom'});\n" +
                "        $(\"div.modal\").modal({ show: false }); \n" +
                "        });\n" +
                "   ", "bootstrap-init"));

    }
}
