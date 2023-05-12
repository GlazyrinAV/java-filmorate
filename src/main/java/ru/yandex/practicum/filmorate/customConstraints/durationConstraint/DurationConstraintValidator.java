package ru.yandex.practicum.filmorate.customConstraints.durationConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;

public class DurationConstraintValidator implements ConstraintValidator<DurationConstraint, Duration> {

    @Override
    public void initialize(DurationConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext constraintValidatorContext) {
        return !duration.isNegative();
    }
}