package org.onexus.resource.authorization.internal;

import org.onexus.resource.api.IAuthorizationManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.ResourceLoginContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class AuthorizationManager implements IAuthorizationManager {

    private String authorizationFile;
    private Properties properties;

    @Override
    public boolean check(String privilege, ORI resourceOri) {
        return getPrivileges(resourceOri).contains(privilege);
    }

    @Override
    public Set<String> getPrivileges(ORI resourceOri) {

        ResourceLoginContext ctx = ResourceLoginContext.get();

        if (ctx == null) {
            return Collections.EMPTY_SET;
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
        String value =  properties.getProperty(key);

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
                this.properties.setProperty("admin", ".*||read|write|load|unload|grant");
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
