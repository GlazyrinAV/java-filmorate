package ru.yandex.practicum.filmorate.customConstraints.genreIdConstraint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class GenreIdConstraintValidator implements ConstraintValidator<GenreIdConstraint, List<Genre>> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreIdConstraintValidator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void initialize(GenreIdConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<Genre> genres, ConstraintValidatorContext constraintValidatorContext) {
        if (genres != null && !genres.isEmpty()) {
            String sqlQuery = "SELECT EXISTS (SELECT * FROM genres WHERE genre_id = ?)";
            for (Genre genre : genres) {
                if (Boolean.FALSE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, genre.getId()))) {
                    return false;
                }
            }
        }
        return true;
    }
}