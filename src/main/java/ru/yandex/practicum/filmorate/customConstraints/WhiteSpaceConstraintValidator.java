package ru.yandex.practicum.filmorate.customConstraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WhiteSpaceConstraintValidator implements ConstraintValidator<WhiteSpaceConstraint, String> {
    @Override
    public void initialize(WhiteSpaceConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !s.contains(" ");
    }
}