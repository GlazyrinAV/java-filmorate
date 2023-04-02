package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    @EqualsAndHashCode.Include
    private final int id;
    @EqualsAndHashCode.Exclude
    private final String name;
    @EqualsAndHashCode.Exclude
    private final String description;
    @EqualsAndHashCode.Exclude
    private final LocalDate releaseDate;
    @EqualsAndHashCode.Exclude
    private final Duration duration;
}