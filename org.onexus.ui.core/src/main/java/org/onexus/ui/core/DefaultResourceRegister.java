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
package org.onexus.ui.core;

import org.onexus.core.IResourceSerializer;
import org.onexus.core.resources.*;
import org.onexus.core.resources.Collection;
import org.osgi.framework.ServiceReference;

import java.util.*;

public class DefaultResourceRegister implements IResourceRegister {

    private Set<ClassLoader> registeredLoaders = new HashSet<ClassLoader>();
    private ClassLoader classLoader = new RegisteredClassLoader();

    private IResourceSerializer serializer;
    private List<IResourceActivator> resourceActivators;
    
    private Map<Class<?>, Map<String, List<String>>> autocompleteMaps = new HashMap<Class<?>, Map<String, List<String>>>();
    
    public DefaultResourceRegister() {

        // Project
        addAutoComplete(Project.class, "project", "<title>[title]</title>");
        addAutoComplete(Project.class, "project", "<description>[description]</description>");
        addAutoComplete(Project.class, "project", "<property><key>[key]</key><value>[value]</value></property>");

        // Source
        addAutoComplete(Data.class, "data", "<title>[title]</title>");
        addAutoComplete(Data.class, "data", "<description>[description]</description>");
        addAutoComplete(Data.class, "data", "<content-type>[title]</content-type>");
        addAutoComplete(Data.class, "data", "<repository>[repository]</repository>");
        addAutoComplete(Data.class, "data", "<path>[path]</path>");
        addAutoComplete(Data.class, "data", "<property><key>[key]</key><value>[value]</value></property>");

        // Collection
        addAutoComplete(Collection.class, "collection", "<title>[title]</title>");
        addAutoComplete(Collection.class, "collection", "<description>[description]</description>");
        addAutoComplete(Collection.class, "collection", "<task><tool>[tool URL]</tool></task>");
        addAutoComplete(Collection.class, "collection", "<fields></fields>");
        addAutoComplete(Collection.class, "collection", "<links></links>");
        addAutoComplete(Collection.class, "task", "<tool>[tool URL]</tool>");
        addAutoComplete(Collection.class, "task", "<parameter><key>[parameter key]</key><value>[parameter value]</value></parameter>");
        addAutoComplete(Collection.class, "fields", "<field><id>[identifier]</id><label>[short label]</label><type>[data type]</type></field>");
        addAutoComplete(Collection.class, "field", "<name>[name]</name>");
        addAutoComplete(Collection.class, "field", "<label>[label]</label>");
        addAutoComplete(Collection.class, "field", "<title>[title]</title>");
        addAutoComplete(Collection.class, "field", "<description>[description]</description>");
        addAutoComplete(Collection.class, "field", "<type>[type]</type>");
        addAutoComplete(Collection.class, "field", "<primary-key>true</primary-key>");
        addAutoComplete(Collection.class, "field", "<property><key>[key]</key><value>[value]</value></property>");
        addAutoComplete(Collection.class, "type", "string");
        addAutoComplete(Collection.class, "type", "double");
        addAutoComplete(Collection.class, "type", "integer");
        addAutoComplete(Collection.class, "links", "<link><collection>[collection uri]</collection><field>[field-src-name] == [field-dst-name]</field></link>");
        addAutoComplete(Collection.class, "link", "<collection>[collection uri]</collection>");
        addAutoComplete(Collection.class, "link", "<field>[field-src-name//field-dst-name]</field>");

        registeredLoaders.add(getClass().getClassLoader());

    };

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
    public void addAutoComplete(Class<?> resourceType, String parentTag, String hint) {
        
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

            return null;
        }
    }
}
