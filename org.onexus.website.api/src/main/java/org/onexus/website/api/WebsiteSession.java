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
package org.onexus.website.api;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.util.string.Strings;
import org.onexus.resource.api.LoginContext;
import org.onexus.ui.authentication.persona.PersonaRoles;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.security.Principal;

public class WebsiteSession extends AuthenticatedWebSession {

    private LoginContext ctx = new LoginContext();
    private Roles roles = new Roles();

    public static final String APPLICATION_POLICY_NAME = "onexus";

    public WebsiteSession(Request request) {
        super(request);

    }

    @Override
    public Roles getRoles() {
        return roles;
    }


    public String getUserName() {
        return ctx.getUserName();
    }

    public LoginContext getLoginContext() {
        return ctx;
    }

    public static WebsiteSession get() {
        return (WebsiteSession) Session.get();
    }

    @Override
    public void signOut() {
        super.signOut();
        this.ctx.logout();
        this.roles.clear();
    }

    public boolean authenticate(String username, String password) {

        if (WebsiteApplication.get().usePersonSignIn()) {
            return authenticatePersona(username);
        }

        boolean authenticated = false;
        LoginCallbackHandler handler = new LoginCallbackHandler(username, password);
        try {
            javax.security.auth.login.LoginContext javaCtx = new javax.security.auth.login.LoginContext(APPLICATION_POLICY_NAME, handler);
            javaCtx.login();
            authenticated = true;

            this.ctx = new LoginContext(username);

            Subject subject = javaCtx.getSubject();
            if (subject != null) {
                for (Principal p : subject.getPrincipals()) {
                    ctx.addRole(p.getName());
                    roles.add(p.getName());
                }
            }

        } catch (LoginException e) {
            // You'll get a LoginException on a failed username/password combo.
            authenticated = false;
        }
        return authenticated;
    }

    private boolean authenticatePersona(String username) {
        if (!Strings.isEmpty(username)) {
            this.ctx = new LoginContext(username);
            for (String role : PersonaRoles.getPersonaRoles(username)) {
                ctx.addRole(role);
                roles.add(role);
            }
            return true;
        }

        return false;
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
