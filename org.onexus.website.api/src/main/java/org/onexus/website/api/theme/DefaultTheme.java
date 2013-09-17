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
package org.onexus.website.api.theme;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.resource.JQueryPluginResourceReference;

public class DefaultTheme extends Behavior {

    private final static HeaderItem BOOTSTRAP_CSS = CssHeaderItem.forReference(new CssResourceReference(DefaultTheme.class, "css/bootstrap.min.css"));
    private final static HeaderItem BOOTSTRAP_JS = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(DefaultTheme.class, "js/bootstrap.min.js"));
    private final static HeaderItem STYLE_CSS = CssHeaderItem.forReference(new CssResourceReference(DefaultTheme.class, "css/style.css"));

    private final static HeaderItem COLORBOX_JS = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(DefaultTheme.class, "colorbox/jquery.colorbox-min.js"));
    private final static HeaderItem COLORBOX_CSS = CssHeaderItem.forReference(new CssResourceReference(DefaultTheme.class, "colorbox/colorbox.css"));

    private final static HeaderItem THEME_JS = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(DefaultTheme.class, "defaulttheme.js"));

    @Override
    public void renderHead(Component component, IHeaderResponse response) {

        response.render(BOOTSTRAP_CSS);
        response.render(STYLE_CSS);
        response.render(COLORBOX_CSS);

        response.render(BOOTSTRAP_JS);
        response.render(COLORBOX_JS);

        response.render(THEME_JS);

        response.render(OnLoadHeaderItem.forScript(
                getTooltipJavascript() +
                getModalJavascript() +
                getPopoverJavascript() +
                getMoveFooter() +
                getColorBoxJavascript() +
                getSkipDoubleClickOnIPadAndIPhone())
        );
    }

    private static String getTooltipJavascript() {
        return "$(\"[rel=tooltip]\").tooltip({ placement: 'bottom'});";
    }

    private static String getTooltipHideJavascript() {
        return "$(\"[rel=tooltip]\").tooltip('hide');";
    }

    private static String getModalJavascript() {
        return "$(\"div.modal\").modal({ show: false });";
    }

    private static String getPopoverJavascript() {
        return "$(\"[rel=popover]\").popover({ placement: 'bottom'});";
    }

    private static String getMoveFooter() {
        return "moveFooter();";
    }

    private static String getColorBoxJavascript() {
        return "$(\".iframe\").colorbox({iframe:true, width:\"80%\", height:\"80%\"});";
    }

    private static String getSkipDoubleClickOnIPadAndIPhone() {
        return "$('a').on('click touchend', function(e) {\n" +
                "    var el = $(this);\n" +
                "    var link = el.attr('href');\n" +
                "    window.location = link;\n" +
                "});";
    }

    @Override
    public void onEvent(Component component, IEvent<?> event) {

        if (event.getPayload() instanceof AjaxRequestTarget) {
            AjaxRequestTarget target = (AjaxRequestTarget) event.getPayload();
            target.prependJavaScript(getTooltipHideJavascript());
            target.appendJavaScript(getTooltipJavascript());
            target.appendJavaScript(getMoveFooter());
            target.appendJavaScript(getColorBoxJavascript());
        }

    }
}
