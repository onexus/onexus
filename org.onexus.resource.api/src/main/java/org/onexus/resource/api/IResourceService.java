package org.onexus.resource.api;

public interface IResourceService {

    /*
        Returns the URL mount point where the web service is mounted.
        Ex: http://localhost:8181/[service mount]/[service parameters]
     */
    String getMount();
}
