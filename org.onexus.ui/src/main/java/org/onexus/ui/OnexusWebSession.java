/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.ui;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.time.Duration;
import org.onexus.core.ICollectionManager;
import org.onexus.core.IResourceManager;

import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.security.Principal;
import java.util.Random;

public class OnexusWebSession extends AuthenticatedWebSession {

    @Inject
    private IResourceManager resourceManager;

    @Inject
    private ICollectionManager collectionManager;

    private String userName = null;
    private String userToken = null;


    public static final String APPLICATION_POLICY_NAME = "karaf";
    public static final String ONEXUS_COOKIE = "onexus-user-token";


    private final static synchronized String newUserToken() {
        return Long.toHexString(System.currentTimeMillis() + RANDOM.nextInt());
    }

    private final static Random RANDOM = new Random();

    private Subject subject;
    private Roles roles = new Roles();

    public OnexusWebSession(Request request) {
        super(request);

        Cookie userTokenCookie = ((WebRequest) request).getCookie(ONEXUS_COOKIE);
        String userToken;
        boolean newToken = (userTokenCookie == null);

        if (newToken) {
            userToken = newUserToken();
            userTokenCookie = new Cookie(ONEXUS_COOKIE, userToken);
            userTokenCookie.setMaxAge((int) (50 * Duration.ONE_WEEK.seconds()));
            userTokenCookie.setPath("/");

            ((WebResponse) RequestCycle.get().getResponse()).addCookie(userTokenCookie);
        } else {
            userToken = userTokenCookie.getValue();
        }

        this.userToken = userToken;
    }

    public String getUserToken() {
        return (isSignedIn() ? userName : userToken);
    }

    public String getUserName() {
        return userName;
    }

    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    public ICollectionManager getCollectionManager() {
        return collectionManager;
    }

    public static OnexusWebSession get() {
        return (OnexusWebSession) Session.get();
    }

    @Override
    public void signOut() {
        super.signOut();
        this.userName = null;
    }

    public boolean authenticate(String username, String password) {
        boolean authenticated = false;
        LoginCallbackHandler handler = new LoginCallbackHandler(username, password);
        try {
            LoginContext ctx = new LoginContext(APPLICATION_POLICY_NAME, handler);
            ctx.login();
            authenticated = true;
            subject = ctx.getSubject();
            userName = username;
            for (Principal p : subject.getPrincipals()) {
                roles.add(p.getName());
            }
        } catch (LoginException e) {
            // You'll get a LoginException on a failed username/password combo.
            authenticated = false;
        }
        return authenticated;
    }

    public Roles getRoles() {
        return roles;
    }

    private class LoginCallbackHandler implements CallbackHandler {

        private String username;
        private String password;

        public LoginCallbackHandler(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (int i = 0; i < callbacks.length; i++) {
                Callback callback = callbacks[i];
                if (callback instanceof NameCallback) {
                    ((NameCallback) callback).setName(username);
                } else if (callback instanceof PasswordCallback) {
                    PasswordCallback pwCallback = (PasswordCallback) callback;
                    pwCallback.setPassword(password.toCharArray());
                } else {
                    throw new UnsupportedCallbackException(callbacks[i], "Callback type not supported");
                }
            }
        }
    }


}
