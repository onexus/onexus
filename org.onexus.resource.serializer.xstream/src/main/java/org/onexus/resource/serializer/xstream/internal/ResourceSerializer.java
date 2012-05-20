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
package org.onexus.resource.serializer.xstream.internal;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import org.onexus.core.IResourceSerializer;
import org.onexus.core.exceptions.UnserializeException;
import org.onexus.core.resources.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class ResourceSerializer implements IResourceSerializer {

    private static final Logger log = LoggerFactory.getLogger(ResourceSerializer.class);
    private Set<ClassLoader> registeredLoaders = new HashSet<ClassLoader>();

    private XStream xstream;

    public ResourceSerializer() {
        super();
        this.xstream = new XStream() {
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MapperWrapper(next) {
                    @Override
                    public boolean shouldSerializeMember(Class definedIn,
                                                         String fieldName) {
                        if (definedIn == Object.class) {
                            log.warn("Tag '"+fieldName+"' not defined.");
                            return false;
                        }
                        return super.shouldSerializeMember(definedIn, fieldName);
                    }
                };
            }
        };

        this.xstream.setClassLoader(new RegisteredClassLoader());

        // Workspace
        alias("workspace", Workspace.class);
        xstream.addImplicitCollection(Workspace.class, "properties", "property", Property.class);

        // Project
        alias("project", Project.class);
        xstream.addImplicitCollection(Project.class, "properties", "property", Property.class);

        // Release
        alias("release", Release.class);
        xstream.addImplicitCollection(Release.class, "properties", "property", Property.class);

        // Folder
        alias("folder", Folder.class);

        // Source
        alias("source", Source.class);
        xstream.addImplicitCollection(Source.class, "paths", "path", String.class);
        xstream.addImplicitCollection(Source.class, "properties", "property", Property.class);

        // Collection
        alias("collection", Collection.class);
        xstream.addImplicitCollection(Collection.class, "properties", "property", Property.class);
        alias("task", Loader.class);
        alias("parameter", Parameter.class);
        alias("parameter-value", Parameter.class);
        xstream.addImplicitCollection(Loader.class, "parameters", "parameter", Parameter.class );
        alias("field", Field.class);
        xstream.addImplicitCollection(Field.class, "properties", "property", Property.class);
        xstream.aliasField("primary-key", Field.class, "primaryKey");
        xstream.registerConverter(new ClassConverter());
        alias("link", Link.class);
        xstream.addImplicitCollection(Link.class, "fields", "field", String.class);
        alias("property", Property.class);

    }

    @Override
    public String getMediaType() {
        return "text/xml";
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unserialize(Class<T> resourceType, InputStream input) throws UnserializeException {
        try {
            return (T) xstream.fromXML(input);
        } catch (ConversionException e) {

            String path = e.get("path");
            String line = e.get("line number");

            throw new UnserializeException(path, line, e);
        }
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
