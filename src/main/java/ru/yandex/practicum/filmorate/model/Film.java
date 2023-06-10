package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.time.DurationMin;
import ru.yandex.practicum.filmorate.customConstraints.releaseDateConstraint.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
@Builder
public class Film {

    @NotBlank(message = "Неверно указано название фильма.")
    private final String name;

    @Size(max = 200, message = "Описание не может быть больше 200 символов.")
    private final String description;

    @ReleaseDateConstraint
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate releaseDate;

    @DurationMin(minutes = 1)
    private final Duration duration;

    private Integer id;

    private List<Genre> genres;

    private Rating mpa;

    private List<Director> directors;
}