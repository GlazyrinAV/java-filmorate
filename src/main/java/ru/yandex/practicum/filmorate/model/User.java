package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.customConstraints.WhiteSpaceConstraint;

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
    @WhiteSpaceConstraint
    private final String login;

    private String name;

    @Past(message = "Неверно указана дата рождения.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate birthday;

    private int id;

    @JsonCreator
    public User(@JsonProperty("email") String email, @JsonProperty("login") String login,
                @JsonProperty("name") String name, LocalDate birthday, @JsonProperty("id") int id) {
        this.email = email;
        this.name = name;
        this.login = login;
        this.birthday = birthday;
        this.id = id;
    }
}