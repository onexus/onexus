package org.onexus.resource.api;

import org.onexus.resource.api.IResourceRegister;

public class ResourceActivator {

    private IResourceRegister resourceRegister;

    private Class<?> resourceType;

    public void init() {
        resourceRegister.register(resourceType);
    }

    public Class<?> getResourceType() {
        return resourceType;
    }

    public void setResourceType(Class<?> resourceType) {
        this.resourceType = resourceType;
    }

    public IResourceRegister getResourceRegister() {
        return resourceRegister;
    }

    public void setResourceRegister(IResourceRegister resourceRegister) {
        this.resourceRegister = resourceRegister;
    }
}
