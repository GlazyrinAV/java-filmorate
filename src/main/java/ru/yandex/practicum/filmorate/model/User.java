package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    @Email(message = "Неверно указан электронный адрес.")
    @NotBlank(message = "Неверно указан электронный адрес.")
    private final String email;

    @NotBlank(message = "Неверно указан логин.")
    private final String login;

    private String name;

    @Past(message = "Неверно указана дата рождения.")
    private final LocalDate birthday;

    private int id;
}