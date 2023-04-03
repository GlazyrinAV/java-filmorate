package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class Film {
    @NotBlank(message = "Неверно указано название фильма.")
    @EqualsAndHashCode.Exclude
    private final String name;

    @Size(max = 200, message = "Описание не может быть больше 200 символов.")
    @EqualsAndHashCode.Exclude
    private final String description;

    @EqualsAndHashCode.Exclude
    private final LocalDate releaseDate;

    @Positive(message = "Неверно указана длительность фильма.")
    @EqualsAndHashCode.Exclude
    private final int duration;

    @EqualsAndHashCode.Include
    private int id;
}