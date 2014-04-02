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
import org.onexus.resource.api.*;
import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.resource.api.annotations.ResourceImplicitList;
import org.onexus.resource.api.annotations.ResourceRegister;
import org.onexus.resource.api.exceptions.UnserializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResourceSerializer implements IResourceSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceSerializer.class);
    public static final int MAX_FIRST_TAG_SIZE = 300;

    private Set<ClassLoader> registeredLoaders = new HashSet<ClassLoader>();

    private Map<String, XStream> xstreamMap = new HashMap<String, XStream>();
    private Map<Class, String> typeToAlias = new HashMap<Class, String>();

    private ThreadLocal<ResourceConverter.ResourceRef> resourceRef = new ThreadLocal<ResourceConverter.ResourceRef>();

    public ResourceSerializer() {
        super();

        register(Project.class);
        register(Folder.class);

    }

    @Override
    public String getMediaType() {
        return "text/xml";
    }

    private String readFirstTag(InputStream in) throws IOException {
        int c;
        StringBuilder firstTag = new StringBuilder(MAX_FIRST_TAG_SIZE);
        while (in.read() != '<') {
        }
        while ((c = in.read()) != '>') {
            firstTag.append((char) c);
        }

        return firstTag.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resource> T unserialize(Class<T> resourceType, ORI resourceOri, InputStream in) throws UnserializeException {

        T resource;
        XStream xstream;
        BufferedInputStream input = new BufferedInputStream(in, MAX_FIRST_TAG_SIZE);

        try {
            input.mark(MAX_FIRST_TAG_SIZE);
            String alias = readFirstTag(input);
            input.reset();

            Class<? extends Resource> type = getType(alias);

            if (type == null) {
                throw new UnsupportedOperationException("The tag '" + alias + "' is not a registered resource.");
            }

            if (!resourceType.isAssignableFrom(type)) {
                throw new UnsupportedOperationException("The resource of type '" + type.getCanonicalName() + " is not a valid '" + resourceType.getCanonicalName() + "' resource.");
            }

            resourceRef.set(new ResourceConverter.ResourceRef(resourceOri, type));
            xstream = getXStream(type);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            resource = (T) xstream.fromXML(input);
        } catch (ConversionException e) {

            String path = e.get("path");
            String line = e.get("line number");

            throw new UnserializeException(path, line, e);
        }

        resource.setORI(resourceOri);

        return resource;
    }

    @Override
    public void serialize(Resource resource, OutputStream output) {
        XStream xstream = getXStream(resource.getClass());
        resourceRef.set(new ResourceConverter.ResourceRef(resource));
        xstream.toXML(resource, output);
    }

    @Override
    public void register(Class<? extends Resource> resourceType) {

        // Register only once
        if (typeToAlias.containsKey(resourceType)) {
            return;
        }

        // Create a new XStream serializator
        XStream xstream = new CustomXStream();
        String alias = getAlias(resourceType);
        if (alias == null) {
            alias = resourceType.getCanonicalName();
        }

        // Report duplicated resource alias
        if (xstreamMap.containsKey(alias)) {
            LOGGER.error("The resource alias '" + alias + "' for '" + resourceType.getCanonicalName() + " it already in use for '" + getType(alias).getCanonicalName() + "'");
        }

        // Store the serializer
        xstreamMap.put(alias, xstream);
        typeToAlias.put(resourceType, alias);
        registeredLoaders.add(resourceType.getClassLoader());

        // Process type and all dependent types
        processAnnotations(xstream, resourceType);


    }

    private String getAlias(Class<?> resourceType) {
        ResourceAlias resourceAlias = resourceType.getAnnotation(ResourceAlias.class);
        return resourceAlias != null ? resourceAlias.value() : null;
    }

    Class<? extends Resource> getType(String alias) {
        for (Map.Entry<Class, String> entry : typeToAlias.entrySet()) {
            if (entry.getValue().equals(alias)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void processAnnotations(XStream xstream, Class<?> resourceType) {

        String alias = getAlias(resourceType);
        if (alias != null) {
            xstream.alias(alias, resourceType);
        }

        for (Field field : resourceType.getDeclaredFields()) {

            ResourceAlias resourceFieldAlias = field.getAnnotation(ResourceAlias.class);
            if (resourceFieldAlias != null) {
                xstream.aliasField(resourceFieldAlias.value(), resourceType, field.getName());
            }

            ResourceImplicitList implicitList = field.getAnnotation(ResourceImplicitList.class);
            if (implicitList != null) {
                xstream.addImplicitCollection(resourceType, field.getName(), implicitList.value(), getCollectionItemClass(field));
            }
        }

        ResourceRegister resourceRegister = resourceType.getAnnotation(ResourceRegister.class);
        if (resourceRegister != null) {
            for (Class type : resourceRegister.value()) {
                processAnnotations(xstream, type);
            }
        }

        if (resourceType.getSuperclass() != null) {
            processAnnotations(xstream, resourceType.getSuperclass());
        }

    }

    XStream getXStream(Class<?> resourceType) {
        return xstreamMap.get(typeToAlias.get(resourceType));
    }

    private Class<?> getCollectionItemClass(Field field) {

        Class<?> type = null;
        final Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            final Type typeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
            if (typeArgument instanceof ParameterizedType) {
                type = (Class<?>) ((ParameterizedType) typeArgument).getRawType();
            } else if (typeArgument instanceof Class) {
                type = (Class<?>) typeArgument;
            }
        }

        return type;
    }

    private class RegisteredClassLoader extends ClassLoader {

        @SuppressWarnings({"rawtypes", "unchecked"})
        public Class loadClass(String name) throws ClassNotFoundException {

            Class clazz = internalLoadClass(name);

            if (clazz == null) {
                
                // At startup some classes are slow to load, wait two seconds and repeat
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }

                clazz = internalLoadClass(name);
            }

            if (clazz == null) {
                throw new ClassNotFoundException();
            }

            return clazz;
        }

        private Class internalLoadClass(String name) {

            Class type = getType(name);
            if (type != null) {
                return type;
            }

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

    private class CustomXStream extends XStream {

        public CustomXStream() {
            super();
            setClassLoader(new RegisteredClassLoader());
            registerConverter(new ClassConverter());
            registerConverter(new ORIConverter());
            registerConverter(new ResourceConverter(ResourceSerializer.this.resourceRef, ResourceSerializer.this));
        }

        @Override
        protected MapperWrapper wrapMapper(MapperWrapper next) {
            return new MapperWrapper(next) {
                @Override
                public boolean shouldSerializeMember(Class definedIn,
                                                     String fieldName) {
                    if (definedIn == Object.class) {
                        LOGGER.warn("Tag '" + fieldName + "' not defined in '" + ResourceSerializer.this.resourceRef.get().getOri() + "'");
                        return false;
                    }
                    return super.shouldSerializeMember(definedIn, fieldName);
                }
            };
        }
    }


}
