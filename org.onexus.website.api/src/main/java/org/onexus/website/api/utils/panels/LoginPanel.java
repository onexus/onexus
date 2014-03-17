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
package org.onexus.website.api.utils.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.onexus.resource.api.session.LoginContext;
import org.onexus.ui.authentication.persona.*;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.WebsiteSession;

public class LoginPanel extends Panel {

    public LoginPanel(String id) {
        super(id);
        setOutputMarkupId(true);
    }

    @Override
    protected void onBeforeRender() {

        LoginContext ctx = LoginContext.get();

        WebMarkupContainer link;

        if (!LoginContext.get().isAnonymous()) {
            if (WebsiteApplication.get().usePersonSignIn()) {
                link = new ExternalLink("account-details", "https://login.persona.org");
                link.add(new AttributeModifier("target", "_tab"));
            } else {
                //TODO Account manager on JAAS authentication system
                link = new WebMarkupContainer("account-details");
            }
        } else {
            if (WebsiteApplication.get().usePersonSignIn()) {
                link = new WebMarkupContainer("account-details");
                link.add(new VerifyBehavior() {
                    @Override
                    protected void onSuccess(AjaxRequestTarget target) {
                        BrowserId browserId = SessionHelper.getBrowserId(Session.get());
                        if (browserId != null && WebsiteSession.get().authenticate(browserId.getEmail(), null)) {
                            target.appendJavaScript("location.reload();");
                        }
                    }

                    @Override
                    protected void onFailure(AjaxRequestTarget target, final String failureReason) {
                    }
                });
            } else {
                link = new Link<String>("account-details") {
                    @Override
                    public void onClick() {
                        WebsiteApplication.get().restartResponseAtSignInPage();
                    }
                };
            }

        }

        link.add(new AttributeModifier("title", ctx.isAnonymous() ? "Sign in" : "Account Details"));
        link.add(new Label("username", ctx.isAnonymous() ? "Sign in" : ctx.getUserName()));

        Component signOut;
        if (!WebsiteApplication.get().usePersonSignIn() || ctx.isAnonymous()) {
            signOut = new BookmarkablePageLink<String>("signout", SignOutPage.class);
        } else {
            signOut = new WebMarkupContainer("signout").add(new SignOutBehavior());
        }
        signOut.setVisible(!ctx.isAnonymous());

        addOrReplace(signOut);
        addOrReplace(link);

        super.onBeforeRender();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        renderBrowserIdJavaScript(response);
    }

    /**
     * Renders a reference for external browserid.js (loaded from browserid.org). <br/>
     * Can be overridden with local reference to browserid.js if needed.
     *
     * @param response the current header response
     */
    protected void renderBrowserIdJavaScript(final IHeaderResponse response) {
        if (WebsiteApplication.get().usePersonSignIn()) {
            response.render(GuestPanel.INCLUDE);
        }
    }
}
