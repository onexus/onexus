/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.ui;

import org.onexus.core.IResourceSerializer;
import org.osgi.framework.ServiceReference;

import javax.swing.event.TreeModelListener;
import java.util.*;

public class DefaultResourceRegister implements IResourceRegister {

    private Set<ClassLoader> registeredLoaders = new HashSet<ClassLoader>();
    private ClassLoader classLoader = new RegisteredClassLoader();

    private IResourceSerializer serializer;
    private List<IResourceActivator> resourceActivators;
    
    private Map<Class<?>, Map<String, List<String>>> autocompleteMaps = new HashMap<Class<?>, Map<String, List<String>>>();

    @Override
    public void register(Class<?> resourceType) {
        serializer.register(resourceType);
        registeredLoaders.add(resourceType.getClassLoader());
    }

    @Override
    public ClassLoader getResourcesClassLoader() {
        return classLoader;
    }

    @Override
    public void addAutocompleteHint(Class<?> resourceType, String parentTag, String hint) {
        
        if (!autocompleteMaps.containsKey(resourceType)) {
            autocompleteMaps.put(resourceType, new HashMap<String, List<String>>());
        }
        
        Map<String, List<String>> autocompleteMap = autocompleteMaps.get(resourceType);

        if (!autocompleteMap.containsKey(parentTag)) {
            autocompleteMap.put(parentTag, new ArrayList<String>());
        }

        List<String> hints = autocompleteMap.get(parentTag);

        if (!hints.contains(hint)) {
            hints.add(hint);
        }

    }

    @Override
    public Map<String, List<String>> getAutocompleteMap(Class<?> resourceType) {
        return autocompleteMaps.get(resourceType);
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

            // Without this we have a ClassNotFoundException when
            // unserializating
            // the DefaultTreeModel object that uses WorkspaceTree. (may be the
            // problem was that we weren't importing javax.swing.event package)
            return TreeModelListener.class.getClassLoader().loadClass(name);
        }
    }
}
