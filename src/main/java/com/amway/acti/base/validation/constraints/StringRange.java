package com.amway.acti.base.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.amway.acti.base.validation.validators.StringRangeValidator;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author ZHAN545
 */
@Constraint(validatedBy = {StringRangeValidator.class})
@Target( {METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface StringRange {
    String message() default "{value.range.error}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] range() default {};

}
