package org.onexus.resource.api.session;

public interface IAuthenticatedSession {
    boolean authenticate(final String username, final String password);
}
