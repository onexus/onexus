package org.onexus.resource.api;

import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.resource.api.utils.AbstractMetadata;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ResourceAlias("service")
public class Service extends Plugin {

    @NotNull
    private String mount;

    private String config;

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
