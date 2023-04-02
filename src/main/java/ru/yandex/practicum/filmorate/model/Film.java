package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class Film {

    @EqualsAndHashCode.Include
    private final String name;
    @EqualsAndHashCode.Include
    private final String description;
    @EqualsAndHashCode.Include
    private final LocalDate releaseDate;
    @EqualsAndHashCode.Include
    private final Duration duration;
    @EqualsAndHashCode.Exclude
    private int id;


}