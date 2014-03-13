/**
 *  Copyright 2012 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.website.api.mocks;

import org.onexus.resource.api.IResourceListener;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.Loader;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Plugin;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.Resource;

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
        // Ignore
    }
}
