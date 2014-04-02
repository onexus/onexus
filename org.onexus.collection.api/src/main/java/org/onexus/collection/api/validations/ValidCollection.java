package org.onexus.collection.api.validations;

import javax.validation.Constraint;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, METHOD, FIELD, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy=ValidCollectionValidator.class)
public @interface ValidCollection {

    String message() default "malformed collection";

    Class[] groups() default {};

    Class[] payload() default {};
}
