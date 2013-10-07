package org.onexus.website.api.mocks;

import org.onexus.resource.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockResourceManager implements IResourceManager {

    private Map<ORI, Resource> resources = new HashMap<ORI, Resource>();

    @Override
    public List<Project> getProjects() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Project getProject(String projectUrl) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void importProject(String projectName, String projectUrl) {
        throw new UnsupportedOperationException("Read only ResourceManager");
    }

    @Override
    public void syncProject(String projectUrl) {
        throw new UnsupportedOperationException("Read only ResourceManager");
    }

    @Override
    public void updateProject(String projectUrl) {
        throw new UnsupportedOperationException("Read only ResourceManager");
    }

    @Override
    public <T extends Resource> T load(Class<T> resourceType, ORI resourceOri) {
        return (T) resources.get(resourceOri);
    }

    @Override
    public <T extends Resource> List<T> loadChildren(Class<T> resourceType, ORI parentResourceOri) {
        if (resources.isEmpty()) {

        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void save(Resource resource) {
        resources.put(resource.getORI(), resource);
    }

    @Override
    public <T> T getLoader(Class<T> serviceClass, Plugin plugin, Loader loader) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addResourceListener(IResourceListener resourceListener) {
        throw new UnsupportedOperationException("Read only ResourceManager");
    }
}
