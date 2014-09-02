package org.onexus.resource.api;

import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.resource.api.utils.AbstractMetadata;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ResourceAlias("service")
public class Service extends AbstractMetadata {

    @NotNull
    @Pattern(regexp=Resource.PATTERN_ID)
    private String id;

    @NotNull
    private String location;

    @NotNull
    private String mount;

    private String config;

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

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getMount() {
        return mount;
    }

    public void setMount(String mount) {
        this.mount = mount;
    }
}
