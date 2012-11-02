package org.onexus.resource.api;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ResourceLoginContext implements Serializable {

    private static ThreadLocal<ResourceLoginContext> loginContext = new ThreadLocal<ResourceLoginContext>() {
        @Override
        protected ResourceLoginContext initialValue() {
            return new ResourceLoginContext();
        }
    };

    public static ResourceLoginContext get() {
        return loginContext.get();
    }

    public static void set(ResourceLoginContext ctx) {
        loginContext.set(ctx);
    }

    private String userName;
    private Set<String> roles = new HashSet<String>();

    public ResourceLoginContext() {
        this.userName = null;
    }

    public ResourceLoginContext(String userName) {
        this.userName = userName;
    }

    public void logout() {
        this.userName = null;
    }

    public String getUserName() {
        return userName;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void addRole(String role) {
        roles.add(role);
    }

    /**
     * Whether this context object containes the provided role.
     *
     * @param role the role to check
     * @return true if it contains the role, false otherwise
     */
    public boolean hasRole(final String role) {
        if (userName != null && role != null) {
            return roles.contains(role);
        }
        return false;
    }


}
