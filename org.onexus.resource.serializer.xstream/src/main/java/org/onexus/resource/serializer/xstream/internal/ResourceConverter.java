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
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Resource;
import org.onexus.resource.api.exceptions.UnserializeException;

public class ResourceConverter implements Converter {

    private ThreadLocal<ResourceRef> resourceRef;
    private ResourceSerializer serializer;

    public ResourceConverter(ThreadLocal<ResourceRef> resourceRef, ResourceSerializer serializer) {
        this.resourceRef = resourceRef;
        this.serializer = serializer;
    }

    @Override
    public boolean canConvert(Class type) {
        return Resource.class.isAssignableFrom(type) && !resourceRef.get().getType().equals(type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {

        Resource resource = (Resource) source;
        ORI ori = resource.getORI();

        if (ori == null) {
            XStream xstream = serializer.getXStream(source.getClass());
            xstream.marshal(source, writer);
        } else {
            writer.setValue(ori.toRelative(resourceRef.get().getOri()).toString());
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

        if (reader.hasMoreChildren()) {

            Class<? extends Resource> type = context.getRequiredType();
            XStream xstream = serializer.getXStream(type);

            if (xstream == null) {
                throw new ConversionException("No '" + type + "' XStream serializer found");
            }

            ResourceRef ref = resourceRef.get();
            Class parentType = ref.getType();
            ref.type = type;
            try {
                xstream.alias(reader.getNodeName(), type);
                Object result = xstream.unmarshal(reader);
                return result;
            } catch (ConversionException e) {
                String path = e.get("path");
                String line = e.get("line number");
                throw new UnserializeException(path, line, e);
            } finally {
                ref.type = parentType;
            }
        }

        //TODO
        return null;
    }

    public static class ResourceRef {

        private ORI ori;
        private Class<? extends Resource> type;

        public ResourceRef(Resource resource) {
            this(resource.getORI(), resource.getClass());
        }

        public ResourceRef(ORI ori, Class<? extends Resource> type) {
            this.ori = ori;
            this.type = type;
        }

        public ORI getOri() {
            return ori;
        }

        public Class<? extends Resource> getType() {
            return type;
        }
    }
}
