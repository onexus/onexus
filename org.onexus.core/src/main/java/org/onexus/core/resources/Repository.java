package org.onexus.core.resources;

public class Repository extends AbstractMetadata {

    private String id;

    private String type;

    private String location;

    public Repository(String id, String type, String location) {
        this.id = id;
        this.type = type;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
