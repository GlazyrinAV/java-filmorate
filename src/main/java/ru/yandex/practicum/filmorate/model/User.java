package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class User {

    @Email(message = "Неверно указан электронный адрес.")
    @NotBlank(message = "Неверно указан электронный адрес.")
    @EqualsAndHashCode.Exclude
    private final String email;

    @NotBlank(message = "Неверно указан логин.")
    @EqualsAndHashCode.Exclude
    private final String login;

    @EqualsAndHashCode.Exclude
    private String name;

    @Past(message = "Неверно указана дата рождения.")
    @EqualsAndHashCode.Exclude
    private final LocalDate birthday;

    @EqualsAndHashCode.Include
    private int id;
}