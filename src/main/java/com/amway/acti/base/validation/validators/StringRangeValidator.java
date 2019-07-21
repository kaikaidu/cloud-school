package com.amway.acti.base.validation.validators;

import com.amway.acti.base.validation.constraints.StringRange;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

/**
 * @author ZHAN545
 */
public class StringRangeValidator implements ConstraintValidator<StringRange, String> {
    private String[] range;

    @Override
    public void initialize(StringRange constraintAnnotation) {
        range = constraintAnnotation.range();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(StringUtils.isEmpty(value)){
            return true;
        }
        for (String s : range) {
            if (s.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
