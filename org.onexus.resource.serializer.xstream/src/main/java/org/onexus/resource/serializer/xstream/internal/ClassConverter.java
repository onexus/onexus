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

import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.onexus.collection.api.types.Text;

import java.util.HashMap;
import java.util.Map;

public class ClassConverter implements SingleValueConverter {

    private static final Map<String, Class> STRING_TO_CLASS = new HashMap<String, Class>();
    private static final Map<Class, String> CLASS_TO_STRING = new HashMap<Class, String>();

    static {
        registerType("integer", Integer.class);
        registerType("string", String.class);
        registerType("double", Double.class);
        registerType("boolean", Boolean.class);
        registerType("text", Text.class);
    }

    public static void registerType(String text, Class type) {
        STRING_TO_CLASS.put(text, type);
        CLASS_TO_STRING.put(type, text);
    }

    @Override
    public boolean canConvert(Class type) {
        return Class.class.equals(type);
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }

        if (CLASS_TO_STRING.containsKey(obj)) {
            return CLASS_TO_STRING.get(obj);
        }

        return ((Class) obj).getCanonicalName();
    }

    @Override
    public Object fromString(String str) {
        if (str == null) {
            return null;
        }

        if (STRING_TO_CLASS.containsKey(str)) {
            return STRING_TO_CLASS.get(str);
        }

        try {
            return getClass().getClassLoader().loadClass(str);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}