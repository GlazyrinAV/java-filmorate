package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.customConstraints.WhiteSpaceConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
public class User {

    private final Set<Integer> friends = new HashSet<>();

    @Email(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", message = "Неверно указан электронный адрес.")
    @NotBlank(message = "Неверно указан электронный адрес.")
    private final String email;

    @NotBlank(message = "Неверно указан логин.")
    @WhiteSpaceConstraint
    private final String login;

    private String name;

    @Past(message = "Неверно указана дата рождения.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate birthday;

    private int id;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.name = name;
        this.login = login;
        this.birthday = birthday;
        this.id = 0;
    }

    public User(String email, String login, String name, LocalDate birthday, int id) {
        this.email = email;
        this.name = name;
        this.login = login;
        this.birthday = birthday;
        this.id = id;
    }
}