package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class Film {

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