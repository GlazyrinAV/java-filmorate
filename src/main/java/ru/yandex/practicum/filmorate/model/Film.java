package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
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

    @EqualsAndHashCode.Exclude
    private final String description;

    @EqualsAndHashCode.Exclude
    private final LocalDate releaseDate;

    @EqualsAndHashCode.Exclude
    private final int duration;

    @EqualsAndHashCode.Include
    private int id;


}