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
import org.onexus.resource.api.Folder;
import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.api.Loader;
import org.onexus.resource.api.Parameter;
import org.onexus.resource.api.Plugin;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.Property;
import org.onexus.resource.api.Resource;
import org.onexus.resource.api.exceptions.UnserializeException;
import org.onexus.resource.api.utils.AbstractMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class ResourceSerializer implements IResourceSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceSerializer.class);

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
                            LOGGER.warn("Tag '" + fieldName + "' not defined.");
                            return false;
                        }
                        return super.shouldSerializeMember(definedIn, fieldName);
                    }
                };
            }
        };

        this.xstream.setClassLoader(new RegisteredClassLoader());

        // Resource
        xstream.addImplicitCollection(Resource.class, "properties", "property", Property.class);
        xstream.addImplicitCollection(AbstractMetadata.class, "properties", "property", Property.class);

        // Project
        alias("project", Project.class);
        xstream.addImplicitCollection(Plugin.class, "parameters", "parameter", Parameter.class);
        xstream.omitField(Project.class, "name");

        // Plugin
        alias("plugin", Plugin.class);

        // Folder
        alias("folder", Folder.class);

        // Loader
        alias("parameter", Parameter.class);
        xstream.addImplicitCollection(Loader.class, "parameters", "parameter", Parameter.class);

        alias("property", Property.class);

        xstream.registerConverter(new ClassConverter());
        xstream.registerConverter(new ORIConverter());


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
