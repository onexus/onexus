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
package org.onexus.resource.authorization.internal;

import org.onexus.resource.api.IAuthorizationManager;
import org.onexus.resource.api.session.LoginContext;
import org.onexus.resource.api.ORI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class AuthorizationManager implements IAuthorizationManager {

    public static String ANONYMOUS_USER = "anonymous";

    private String authorizationFile;
    private Properties properties;

    @Override
    public boolean check(String privilege, ORI resourceOri) {

        // Service context always have read-only privilege
        LoginContext ctx = LoginContext.get();
        if (ctx==LoginContext.SERVICE_CONTEXT && READ.equals(privilege)) {
            return true;
        }

        // Check private projects
        String projectUrl = resourceOri.getProjectUrl();
        if (projectUrl.startsWith("private://")) {

            int end = projectUrl.indexOf('/',10);
            if (end == -1) {
                return false;
            }

            String userName = projectUrl.substring(10, end);
            if (userName==null || !userName.equals(ctx.getUserName())) {
                return false;
            }
        }

        // Check privileges
        return getPrivileges(resourceOri).contains(privilege);
    }

    @Override
    public Set<String> getPrivileges(ORI resourceOri) {

        LoginContext ctx = LoginContext.get();

        if (ctx == null) {
            return Collections.emptySet();
        }

        Set<String> result = new HashSet<String>();

        // First load roles privileges
        for (String role : ctx.getRoles()) {
            result.addAll(loadPrivileges("role-" + role, resourceOri));
        }

        // Load user privileges
        result.addAll(loadPrivileges(ctx.getUserName(), resourceOri));

        return result;
    }

    private Set<String> loadPrivileges(String key, ORI resourceOri) {

        Set<String> result = new HashSet<String>();

        if (key == null) {
            key = ANONYMOUS_USER;
        }

        String value = properties.getProperty(key);

        if (value == null || value.isEmpty()) {
            return result;
        }

        String items[] = value.split(",");
        for (String item : items) {
            String pair[] = item.split("\\|\\|");

            String regex = pair[0].trim();
            String privileges[] = pair[1].split("\\|");

            if (resourceOri.toString().matches(regex)) {
                for (String privilege : privileges) {
                    result.add(privilege.trim().toLowerCase());
                }
            }
        }

        return result;
    }

    public void load() {
        this.properties = new Properties();

        try {

            File file = new File(authorizationFile);

            if (!file.exists()) {
                this.properties.setProperty("role-admin", ".*||read|write|load|unload|grant");
                this.properties.setProperty(ANONYMOUS_USER, ".*||read");
                this.properties.setProperty("role-registered", ".*||read");
                this.properties.store(new FileOutputStream(file), "Syntax: username = [regular expression to match against ORI] || [privilege 1] | [privilege 2] | ... , more...");
            } else {
                properties.load(new FileInputStream(authorizationFile));
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getAuthorizationFile() {
        return authorizationFile;
    }

    public void setAuthorizationFile(String authorizationFile) {
        this.authorizationFile = authorizationFile;
    }
}
