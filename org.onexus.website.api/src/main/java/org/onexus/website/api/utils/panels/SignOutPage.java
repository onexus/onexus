package org.onexus.website.api.utils.panels;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.website.api.WebsiteApplication;

public class SignOutPage extends WebPage {

    /**
     * Construct.
     */
    public SignOutPage()
    {
        this(null);
    }

    /**
     * Constructor
     *
     * @param parameters
     *            Page parameters (ignored since this is the home page)
     */
    public SignOutPage(final PageParameters parameters)
    {
        getSession().invalidate();
        setResponsePage(WebsiteApplication.get().getHomePage());
    }
}
