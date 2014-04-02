package org.onexus.resource.validator.hibernate.internal;

import org.hibernate.validator.HibernateValidator;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.IResourceValidator;
import org.onexus.resource.api.Resource;

import javax.validation.*;
import javax.validation.spi.ValidationProvider;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ResourceValidator implements IResourceValidator {

    private Validator validator;
    private IResourceManager resourceManager;

    public ResourceValidator() {
        super();

        // Create the validator
        Configuration<?> config = Validation.byDefaultProvider()
                .providerResolver(new ValidationProviderResolver() {
                    @Override
                    public List<ValidationProvider<?>> getValidationProviders() {
                        return (List) Arrays.asList(new HibernateValidator());
                    }
                })
                .configure();
        config.constraintValidatorFactory(new OsgiConstraintValidatorFactory());

        ValidatorFactory factory = config.buildValidatorFactory();
        this.validator = factory.getValidator();
    }

    public <T extends Resource> Set<ConstraintViolation<T>> validate(T resource) {
        return validator.validate(resource);
    }

    public void init(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    private class OsgiConstraintValidatorFactory implements ConstraintValidatorFactory {

        @Override
        public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {

            Method resourceManagerMethod = null;

            try {
                resourceManagerMethod = key.getMethod("initialize", IResourceManager.class);
            } catch (NoSuchMethodException e) {
            }

            T instance;
            try {
                instance = key.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (resourceManager != null && resourceManagerMethod != null) {
                try {
                    resourceManagerMethod.invoke(instance, resourceManager);
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                }
            }

            return instance;
        }

        @Override
        public void releaseInstance(ConstraintValidator<?, ?> instance) {

        }
    }
}
