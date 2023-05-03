package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.customConstraints.DurationConstraint;
import ru.yandex.practicum.filmorate.customConstraints.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode(exclude = "id")
@Builder
public class Film {

    private final Set<Integer> liked = new HashSet<>();

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

    private Collection<Genre> genres;

    private Rating mpa;

    public Film(String name, String description, LocalDate releaseDate, Duration duration, Integer id, Collection<Genre> genres, Rating mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.id = id;
        this.genres = genres;
        this.mpa = mpa;
    }
}