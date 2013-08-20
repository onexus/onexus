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

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.ui.authentication.persona.SignOutBehavior;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.theme.DefaultTheme;

public class NotAuthorizedPage extends WebPage {

    /**
     * Construct.
     */
    public NotAuthorizedPage() {
        this(null);
    }

    /**
     * Constructor
     *
     * @param parameters Page parameters (ignored since this is the home page)
     */
    public NotAuthorizedPage(final PageParameters parameters) {
        add(new DefaultTheme());

        if (WebsiteApplication.get().usePersonSignIn()) {
            add(new WebMarkupContainer("signout").add(new SignOutBehavior()));
        } else {
            add(new BookmarkablePageLink<String>("signout", SignOutPage.class));
        }
    }
}
