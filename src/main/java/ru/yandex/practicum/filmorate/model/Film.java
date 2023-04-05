package ru.yandex.practicum.filmorate.model;

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
}