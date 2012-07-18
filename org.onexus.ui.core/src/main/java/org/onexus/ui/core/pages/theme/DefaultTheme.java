package org.onexus.ui.core.pages.theme;

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
