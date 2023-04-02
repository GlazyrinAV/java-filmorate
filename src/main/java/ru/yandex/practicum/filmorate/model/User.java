package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class User {

    @EqualsAndHashCode.Exclude
    private final String email;
    @EqualsAndHashCode.Exclude
    private final String login;
    @EqualsAndHashCode.Exclude
    private String name;
    @EqualsAndHashCode.Exclude
    private final LocalDate birthday;
    @EqualsAndHashCode.Include
    private int id;

}