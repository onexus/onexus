package org.onexus.collection.api.validations;

import org.onexus.collection.api.Collection;
import org.onexus.resource.api.IResourceManager;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidCollectionValidator implements ConstraintValidator<ValidCollection, Collection> {

    public void initialize(IResourceManager resourceManager) {
        //TODO Initialize the Collection validator.
    }

    @Override
    public void initialize(ValidCollection constraintAnnotation) {
    }

    @Override
    public boolean isValid(Collection collection, ConstraintValidatorContext context) {
        //TODO Validate the collection
        return true;
    }
}
