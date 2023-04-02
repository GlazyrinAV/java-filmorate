package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class User {

    @EqualsAndHashCode.Include
    private final String email;
    @EqualsAndHashCode.Include
    private final String login;
    @EqualsAndHashCode.Include
    private String name;
    @EqualsAndHashCode.Include
    private final LocalDate birthday;
    @EqualsAndHashCode.Exclude
    private int id;

}