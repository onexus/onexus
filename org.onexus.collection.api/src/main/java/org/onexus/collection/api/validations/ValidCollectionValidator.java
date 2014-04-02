package org.onexus.collection.api.validations;

import org.onexus.collection.api.Collection;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidCollectionValidator implements ConstraintValidator<ValidCollection, Collection> {

    private IResourceManager resourceManager;

    public void initialize(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public void initialize(ValidCollection constraintAnnotation) {
    }

    @Override
    public boolean isValid(Collection collection, ConstraintValidatorContext context) {
        //TODO
        return true;
    }
}
