package org.onexus.ui.authentication.persona;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;

import java.util.HashMap;
import java.util.Map;


public class SignOutBehavior extends AbstractDefaultAjaxBehavior {

    @Override
    protected void respond(AjaxRequestTarget target) {
        WebSession.get().invalidate();
        RequestCycle.get().setResponsePage(WebApplication.get().getHomePage());
    }

    @Override
    public void renderHead(final Component component, final IHeaderResponse response)
    {
        component.setOutputMarkupId(true);
        super.renderHead(component, response);

        final Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("componentId", component.getMarkupId());
        BrowserId personaId = SessionHelper.getBrowserId(Session.get());
        String userName = (personaId == null ? "" : personaId.getEmail() );
        variables.put("userName", userName);
        variables.put("callbackUrl", getCallbackUrl());

        final TextTemplate verifyTemplate = new PackageTextTemplate(VerifyBehavior.class, "signout.js.tmpl");
        String asString = verifyTemplate.asString(variables);

        response.render(JavaScriptHeaderItem.forUrl(GuestPanel.BROWSER_ID_JS));
        response.render(OnDomReadyHeaderItem.forScript(asString));

    }
}
