package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.customConstraints.DurationConstraint;
import ru.yandex.practicum.filmorate.customConstraints.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class Film {
    @NotBlank(message = "Неверно указано название фильма.")
    private final String name;

    @Size(max = 200, message = "Описание не может быть больше 200 символов.")
    private final String description;

    @ReleaseDateConstraint
    private final LocalDate releaseDate;

    @DurationConstraint
    private final Duration duration;

    private int id;

    @JsonCreator
    public Film(@JsonProperty("name") String name, @JsonProperty("description") String description,
                @JsonProperty("releaseDate") LocalDate releaseDate, @JsonProperty("duration") Duration duration,
                @JsonProperty("id") int id) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.id = id;
    }
}