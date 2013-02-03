package org.onexus.ui.api.authentication.persona;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.onexus.ui.api.OnexusWebApplication;
import org.onexus.ui.api.OnexusWebSession;

import java.util.HashMap;
import java.util.Map;


public class SignOutBehavior extends AbstractDefaultAjaxBehavior {

    @Override
    protected void respond(AjaxRequestTarget target) {
        OnexusWebSession.get().invalidate();
        RequestCycle.get().setResponsePage(OnexusWebApplication.get().getHomePage());
    }

    @Override
    public void renderHead(final Component component, final IHeaderResponse response)
    {
        component.setOutputMarkupId(true);
        super.renderHead(component, response);

        final Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("componentId", component.getMarkupId());
        variables.put("userName", OnexusWebSession.get().getUserName());
        variables.put("callbackUrl", getCallbackUrl());

        final TextTemplate verifyTemplate = new PackageTextTemplate(VerifyBehavior.class, "signout.js.tmpl");
        String asString = verifyTemplate.asString(variables);

        response.render(JavaScriptHeaderItem.forUrl(GuestPanel.BROWSER_ID_JS));
        response.render(OnDomReadyHeaderItem.forScript(asString));

    }
}
