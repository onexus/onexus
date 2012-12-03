package org.onexus.website.api.utils.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.onexus.resource.api.LoginContext;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.WebsiteSession;

public class LoginPanel extends Panel {

	public LoginPanel(String id) {
		super(id);
	}

	@Override
	protected void onBeforeRender() {

		LoginContext ctx = LoginContext.get();

		Link<String> link = new Link<String>("account-details") {
			@Override
			public void onClick() {

				if (LoginContext.get().isAnonymous()) {
					WebsiteApplication.get().restartResponseAtSignInPage();
				}

			}
		};

		link.add(new AttributeModifier("title", (ctx.isAnonymous() ? "Sign in" : "Account Details")));
		link.add(new Label("username", (ctx.isAnonymous() ? "Sign in" : ctx.getUserName())));

		Link<String> signOut = new Link<String>("signout") {
			@Override
			public void onClick() {
				WebsiteSession.get().signOut();
			}
		};
		signOut.setVisible(!ctx.isAnonymous());

		addOrReplace(signOut);
		addOrReplace(link);

		super.onBeforeRender();
	}
}
