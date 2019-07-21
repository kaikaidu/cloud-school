package com.amway.acti.base.validation.validators;

import com.amway.acti.base.validation.constraints.ShortRange;
import com.amway.acti.base.validation.constraints.StringRange;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ShortRangeValidator implements ConstraintValidator<ShortRange, Short> {
    private short[] range;

    @Override
    public void initialize(ShortRange shortRange) {
        range = shortRange.range();
    }

    @Override
    public boolean isValid(Short aShort, ConstraintValidatorContext constraintValidatorContext) {
        if(aShort==null){
            return true;
        }
        for(short s : range){
            if(aShort == s){
                return true;
            }
        }
        return false;
    }
}
