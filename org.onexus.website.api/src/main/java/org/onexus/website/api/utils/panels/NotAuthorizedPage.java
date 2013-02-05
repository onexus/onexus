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
    public NotAuthorizedPage()
    {
        this(null);
    }

    /**
     * Constructor
     *
     * @param parameters
     *            Page parameters (ignored since this is the home page)
     */
    public NotAuthorizedPage(final PageParameters parameters)
    {
        add(new DefaultTheme());

        if (WebsiteApplication.get().usePersonSignIn()) {
            add(new WebMarkupContainer("signout").add(new SignOutBehavior()));
        } else {
            add(new BookmarkablePageLink<String>("signout", SignOutPage.class));
        }
    }
}
