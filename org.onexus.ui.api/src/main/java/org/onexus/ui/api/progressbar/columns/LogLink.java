package org.onexus.ui.api.progressbar.columns;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class LogLink extends Panel {

    public LogLink(String id) {
        super(id);

        AjaxLink<String> link = new AjaxLink<String>("link") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                LogLink.this.onClick(target);
            }
        };

        link.add(new Label("label", "<i class=\"icon-eye-open\"></i>").setEscapeModelStrings(false));
        add(link);
    }

    protected abstract void onClick(AjaxRequestTarget target);
}
