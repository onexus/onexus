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
