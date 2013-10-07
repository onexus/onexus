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
package org.onexus.ui.authentication.persona;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.onexus.resource.api.session.IAuthenticatedSession;

public class PersonaSignInPage extends WebPage {

    public static final CssResourceReference CSS = new CssResourceReference(PersonaSignInPage.class, "PersonaSignInPage.css");
    private static final PackageResourceReference LOGO = new PackageResourceReference(PersonaSignInPage.class, "persona-logo.png");

    private static final long serialVersionUID = 1L;

    public PersonaSignInPage(final PageParameters parameters) {
        super(parameters);

        add(new Image("logo", LOGO));
        add(new GuestPanel("browserId", GuestPanel.Style.GREEN) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSuccess(AjaxRequestTarget target) {
                BrowserId browserId = SessionHelper.getBrowserId(Session.get());

                if (browserId != null) {
                    if (((IAuthenticatedSession) WebSession.get()).authenticate(browserId.getEmail(), null)) {

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
