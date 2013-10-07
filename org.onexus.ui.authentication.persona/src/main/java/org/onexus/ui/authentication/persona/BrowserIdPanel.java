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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * This is the main panel which loads specific panels depending on whether there is a logged in user
 * or not.
 */
public class BrowserIdPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final String CONTENT_ID = "content";

    /**
     * The style of the default "Sign In" image button provided by https://browserid.org
     */
    private final GuestPanel.Style style;

    public BrowserIdPanel(String id) {
        this(id, GuestPanel.Style.BLUE);
    }

    public BrowserIdPanel(String id, GuestPanel.Style style) {
        super(id);

        this.style = style;

        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        if (SessionHelper.isLoggedIn(getSession())) {
            addOrReplace(getLoggedInPanel(CONTENT_ID));
        } else {
            addOrReplace(getGuestPanel(CONTENT_ID));
        }
    }

    protected Component getGuestPanel(String componentId) {
        return new GuestPanel(componentId, style) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSuccess(AjaxRequestTarget target) {
                super.onSuccess(target);

                target.add(BrowserIdPanel.this);
            }

            @Override
            protected void onFailure(AjaxRequestTarget target, final String failureReason) {
                super.onFailure(target, failureReason);

                error("The authentication failed: " + failureReason);
                target.addChildren(getPage(), IFeedback.class);
            }
        };
    }

    protected Component getLoggedInPanel(String componentId) {
        return new LoggedInPanel(componentId) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onLoggedOut(AjaxRequestTarget target) {
                super.onLoggedOut(target);
                target.add(BrowserIdPanel.this);
            }
        };
    }
}
