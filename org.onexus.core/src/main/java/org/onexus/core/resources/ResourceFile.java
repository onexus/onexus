package org.onexus.core.resources;

public class ResourceFile extends Resource {

    private String localPath;

    public ResourceFile() {
        super();
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
