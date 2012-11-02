package org.onexus.resource.api;

import java.util.Set;

public interface IAuthorizationManager {

    static String READ = "read";
    static String WRITE = "write";
    static String LOAD = "load";
    static String UNLOAD = "unload";
    static String GRANT = "grant";

    public boolean check(String privilege, ORI resourceOri);

    public Set<String> getPrivileges(ORI resourceOri);

}
