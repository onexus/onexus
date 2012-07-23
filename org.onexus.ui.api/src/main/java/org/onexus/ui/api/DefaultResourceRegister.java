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
package org.onexus.ui.api;

import org.onexus.resource.api.IResourceSerializer;
import org.osgi.framework.ServiceReference;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultResourceRegister implements IResourceRegister {

    private Set<ClassLoader> registeredLoaders = new HashSet<ClassLoader>();
    private ClassLoader classLoader = new RegisteredClassLoader();

    private IResourceSerializer serializer;
    private List<IResourceActivator> resourceActivators;

    public DefaultResourceRegister() {
        registeredLoaders.add(getClass().getClassLoader());
    }

    @Override
    public void register(Class<?> resourceType) {
        serializer.register(resourceType);
        registeredLoaders.add(resourceType.getClassLoader());
    }

    @Override
    public ClassLoader getResourcesClassLoader() {
        return classLoader;
    }

    public IResourceSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(IResourceSerializer serializer) {
        this.serializer = serializer;
    }

    public List<IResourceActivator> getResourceActivators() {
        return resourceActivators;
    }

    public void setResourceActivators(List<IResourceActivator> resourceActivators) {
        this.resourceActivators = resourceActivators;
    }

    public void bindActivators(ServiceReference serviceRef) {

        if (resourceActivators != null) {
            for (IResourceActivator ra : resourceActivators) {
                ra.bind(this);
            }
        }

    }

    public void unbindActivators(ServiceReference serviceRef) {

        if (resourceActivators != null) {
            for (IResourceActivator ra : resourceActivators) {
                ra.unbind(this);
            }
        }

    }

    private class RegisteredClassLoader extends ClassLoader {

        @SuppressWarnings({"unchecked", "rawtypes"})
        public Class loadClass(String name) throws ClassNotFoundException {

            for (ClassLoader loader : registeredLoaders) {
                try {
                    return loader.loadClass(name);
                } catch (ClassNotFoundException e) {
                    // Continue
                }
            }

            return null;
        }
    }
}
