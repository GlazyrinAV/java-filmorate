package ru.yandex.practicum.filmorate.customConstraints.ratingIdConstraint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Rating;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RatingIdConstraintValidator implements ConstraintValidator<RatingIdConstraint, Rating> {

    @Autowired
    public final JdbcTemplate jdbcTemplate;

    public RatingIdConstraintValidator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void initialize(RatingIdConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Rating rating, ConstraintValidatorContext constraintValidatorContext) {
        if (rating != null) {
            String sqlQuery = "SELECT EXISTS (SELECT * FROM ratings WHERE rating_id = ?)";
            return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, rating.getId()));
        }
        return true;
    }
}
