package ru.yandex.practicum.filmorate.customConstraints;

import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Month;

public class ReleaseDateConstraintValidator implements ConstraintValidator<ReleaseDateConstraint, LocalDate> {
    private final LocalDate firstFilm = LocalDate.of(1895, Month.DECEMBER, 28);
    @Override
    public void initialize(ReleaseDateConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isAfter(firstFilm);
    }
}
