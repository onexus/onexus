package org.onexus.resource.api;

import java.util.Collection;

public interface IProfileManager {

    Collection<String> getKeys();

    String getValue(String key);

    String[] getValueArray(String key);

    void putValue(String key, String value);

    void putValueArray(String key, String[] values);

}
