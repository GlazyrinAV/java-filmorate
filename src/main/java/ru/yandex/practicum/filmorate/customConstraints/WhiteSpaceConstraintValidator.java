package ru.yandex.practicum.filmorate.customConstraints;

import ru.yandex.practicum.filmorate.model.User;

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