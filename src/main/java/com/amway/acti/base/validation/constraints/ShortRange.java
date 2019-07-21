package com.amway.acti.base.validation.constraints;

import com.amway.acti.base.validation.validators.ShortRangeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = {ShortRangeValidator.class})
@Target( {METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface ShortRange {
    String message() default "{value.range.error}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    short[] range() default {};
}
