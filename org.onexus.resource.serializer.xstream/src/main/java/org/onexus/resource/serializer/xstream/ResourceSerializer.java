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
package org.onexus.resource.serializer.xstream;

import com.thoughtworks.xstream.XStream;
import org.onexus.core.IResourceSerializer;
import org.onexus.core.resources.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class ResourceSerializer implements IResourceSerializer {

    private Set<ClassLoader> registeredLoaders = new HashSet<ClassLoader>();

    private XStream xstream;

    public ResourceSerializer() {
        super();
        this.xstream = new XStream();
        this.xstream.setClassLoader(new RegisteredClassLoader());

        alias("resource", Resource.class);
        alias("workspace", Workspace.class);
        alias("project", Project.class);
        alias("release", Release.class);
        alias("collection", Collection.class);
        alias("source", Source.class);
        alias("tool", Tool.class);
        alias("task", Task.class);
        alias("field", Field.class);
        alias("link", Link.class);
        alias("parameter", Parameter.class);
        alias("parameter-value", ParameterValue.class);
        alias("property", Property.class);

    }

    @Override
    public String getMediaType() {
        return "text/xml";
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unserialize(Class<T> resourceType,
                             InputStream input) {
        return (T) xstream.fromXML(input);
    }

    @Override
    public void serialize(Object resource, OutputStream output) {
        xstream.toXML(resource, output);
    }

    private void alias(String alias, Class<?> resourceType) {
        xstream.alias(alias, resourceType);
        register(resourceType);
    }

    @Override
    public void register(Class<?> resourceType) {
        xstream.processAnnotations(resourceType);
        registeredLoaders.add(resourceType.getClassLoader());
    }

    private class RegisteredClassLoader extends ClassLoader {

        @SuppressWarnings({"rawtypes", "unchecked"})
        public Class loadClass(String name) throws ClassNotFoundException {

            for (ClassLoader loader : registeredLoaders) {
                try {
                    return loader.loadClass(name);
                } catch (ClassNotFoundException e) {
                    // Continue
                }
            }

            throw new ClassNotFoundException();
        }

    }

}
