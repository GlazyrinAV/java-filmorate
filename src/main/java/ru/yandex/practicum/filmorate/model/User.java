package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.customConstraints.WhiteSpaceConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = "id")
public class User {

    @Email(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
            ,message = "Неверно указан электронный адрес.")
    @NotBlank(message = "Неверно указан электронный адрес.")
    private final String email;

    @NotBlank(message = "Неверно указан логин.")
    @WhiteSpaceConstraint
    private final String login;

    private String name;

    @Past(message = "Неверно указана дата рождения.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate birthday;

    private transient int id;

    @JsonCreator
    public User(@JsonProperty("email") String email, @JsonProperty("login") String login,
                @JsonProperty("name") String name, @JsonProperty("birthday") LocalDate birthday) {
        this.email = email;
        this.name = name;
        this.login = login;
        this.birthday = birthday;
        this.id = 0;
    }
}