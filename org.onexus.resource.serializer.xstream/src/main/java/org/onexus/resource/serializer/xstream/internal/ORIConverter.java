package org.onexus.resource.serializer.xstream.internal;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.onexus.resource.api.ORI;

public class ORIConverter implements SingleValueConverter {
    @Override
    public String toString(Object obj) {
        return obj.toString();
    }

    @Override
    public Object fromString(String str) {
        return new ORI(str);
    }

    @Override
    public boolean canConvert(Class type) {
        return ORI.class.isAssignableFrom(type);
    }
}
