package org.onexus.ui.website.theme;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.*;
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
        response.render(OnLoadHeaderItem.forScript(
               getTooltipJavascript() +
               getModalJavascript() +
               getPopoverJavascript()));
    }

    private static String getTooltipJavascript() {
        return  "$(\"[rel=tooltip]\").tooltip({ placement: 'bottom'});";
    }

    private static String getModalJavascript() {
        return "$(\"div.modal\").modal({ show: false });";
    }

    private static String getPopoverJavascript() {
        return  "$(\"[rel=popover]\").popover({ placement: 'bottom'});";
    }

    @Override
    public void onEvent(Component component, IEvent<?> event) {

        if (event.getPayload() instanceof AjaxRequestTarget) {
            AjaxRequestTarget target = (AjaxRequestTarget) event.getPayload();

            target.appendJavaScript(getTooltipJavascript());
        }

    }
}
