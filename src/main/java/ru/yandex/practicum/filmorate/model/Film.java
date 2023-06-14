package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.customconstraints.releasedateconstraint.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
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

    @Positive
    @NotNull
    private final Long duration;

    private Integer id;

    private List<Genre> genres;

    private Mpa mpa;

    private List<Director> directors;

    private Double score = 0.0;
}