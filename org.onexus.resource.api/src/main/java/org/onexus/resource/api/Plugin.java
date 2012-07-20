package org.onexus.resource.api;

import org.onexus.resource.api.utils.AbstractMetadata;

import java.util.List;

public class Plugin extends AbstractMetadata {

    private String id;

    private String location;

    private List<Parameter> parameters;

    public Plugin() {
        super();
    }

    public Plugin(String id, String location) {
        this.id = id;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
}
