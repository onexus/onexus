package org.onexus.resource.api;

import java.io.Serializable;

public interface ParameterKey extends Serializable {

    String getKey();

    String getDescription();

    boolean isOptional();

}
