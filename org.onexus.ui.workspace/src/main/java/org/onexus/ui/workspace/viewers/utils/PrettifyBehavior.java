package org.onexus.ui.workspace.viewers.utils;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.resource.JQueryPluginResourceReference;


public class PrettifyBehavior extends Behavior {

    private final static HeaderItem CSS = CssHeaderItem.forReference(new CssResourceReference(PrettifyBehavior.class, "prettify.css"));
    private final static HeaderItem JS = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(PrettifyBehavior.class, "prettify.js"));

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(CSS);
        response.render(JS);
        response.render(OnLoadHeaderItem.forScript("prettyPrint()"));
    }
}
