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
package org.onexus.ui.authentication.jaas;

import org.apache.wicket.Session;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.onexus.resource.api.session.IAuthenticatedSession;

public class JaasSignInPage extends WebPage {
    public static final CssResourceReference CSS = new CssResourceReference(JaasSignInPage.class, "JaasSignInPage.css");
    private static final long serialVersionUID = 1L;

    /**
     * Construct
     */
    public JaasSignInPage() {
        this(null);
    }

    /**
     * Constructor
     *
     * @param parameters The page parameters
     */
    public JaasSignInPage(final PageParameters parameters) {
        super(parameters);

        if (parameters != null) {
            StringValue userName = parameters.get("username");
            StringValue password = parameters.get("password");
            StringValue redirect = parameters.get("redirect");

            if (!userName.isEmpty()) {
                boolean valid = getAuthenticatedSession().authenticate(userName.toString(), password.toString());

                if (valid) {
                    throw new RedirectToUrlException(redirect.toString());
                }
            }
        }

        SignInPanel signPanel = new SignInPanel("signInPanel");
        signPanel.setRememberMe(false);
        add(signPanel);

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CSS));
    }

    public static IAuthenticatedSession getAuthenticatedSession() {
        return (IAuthenticatedSession) Session.get();
    }
}
