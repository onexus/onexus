package org.onexus.ui.api.authentication.persona;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;


public class PersonaSignInPage extends WebPage {

    public static final CssResourceReference CSS = new CssResourceReference(PersonaSignInPage.class, "PersonaSignInPage.css");
    private static final String DEFAULT_PERSONA_PASSWORD = "persona";

    private static final long serialVersionUID = 1L;

    public PersonaSignInPage(final PageParameters parameters) {
        super(parameters);

        add(new GuestPanel("browserId", GuestPanel.Style.GREEN) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSuccess(AjaxRequestTarget target) {
                BrowserId browserId = SessionHelper.getBrowserId(Session.get());

                if (browserId != null) {
                    if (AuthenticatedWebSession.get().signIn(browserId.getEmail(), DEFAULT_PERSONA_PASSWORD)) {

                        // logon successful. Continue to the original destination
                        continueToOriginalDestination();
                        // Ups, no original destination. Go to the home page
                        throw new RestartResponseException(getSession().getPageFactory().newPage(
                                getApplication().getHomePage()));

                    } else {
                        onFailure(target, "You are not authorized");
                    }
                }
            }

            @Override
            protected void onFailure(AjaxRequestTarget target, final String failureReason) {
                error("The authentication failed: " + failureReason);
                target.addChildren(getPage(), IFeedback.class);
            }

        });

        add(new FeedbackPanel("feedback").setOutputMarkupId(true));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CSS));
    }

}
