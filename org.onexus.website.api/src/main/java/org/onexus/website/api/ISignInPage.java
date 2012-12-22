package org.onexus.website.api;

import org.apache.wicket.markup.html.WebPage;

public interface ISignInPage {

    String getId();

    Class<? extends WebPage> getPageClass();
}
