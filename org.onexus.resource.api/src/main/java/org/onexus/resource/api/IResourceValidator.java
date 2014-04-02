package org.onexus.resource.api;

import javax.validation.ConstraintViolation;
import java.util.Set;

public interface IResourceValidator {

    void init(IResourceManager resourceManager);
    
    <T extends Resource> Set<ConstraintViolation<T>> validate(T resource);
        
}
