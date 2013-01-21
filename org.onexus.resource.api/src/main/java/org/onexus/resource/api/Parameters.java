package org.onexus.resource.api;

import java.util.HashMap;
import java.util.Map;

public class Parameters extends HashMap<ParameterKey, String> {

    public Parameters() {
        super();
    }

    public Parameters(Map<ParameterKey, String> map) {
        super(map);
    }

    public String get(Object key) {

        if (!(key instanceof ParameterKey)) {
            throw new UnsupportedOperationException("Key object must implement ParameterKey interface");
        }

        ParameterKey parameterKey = (ParameterKey) key;

        if (!parameterKey.isOptional() && !containsKey(key)) {
            throw new UnsupportedOperationException(
                    "Parameter '" + parameterKey.getKey() + " - " + parameterKey.getDescription() + "' is mandatory");
        }

        String value = super.get(key);

        if (value == null) {
            value = parameterKey.getDefault();
        }

        return value;
    }

}
