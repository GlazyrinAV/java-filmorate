package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.customConstraints.durationConstraint.DurationConstraint;
import ru.yandex.practicum.filmorate.customConstraints.genreIdConstraint.GenreIdConstraint;
import ru.yandex.practicum.filmorate.customConstraints.ratingIdConstraint.RatingIdConstraint;
import ru.yandex.practicum.filmorate.customConstraints.releaseDateConstraint.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Film {

    @NotBlank(message = "Неверно указано название фильма.")
    private final String name;

    @Size(max = 200, message = "Описание не может быть больше 200 символов.")
    private final String description;

    @ReleaseDateConstraint
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate releaseDate;

    @DurationConstraint
    private final Duration duration;

    private Integer id;

    @GenreIdConstraint
    private List<Genre> genres;

    @RatingIdConstraint
    private Rating mpa;

    private List<Director> directors;
}