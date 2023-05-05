package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Data
@EqualsAndHashCode(exclude = "id")
@Builder
public class Rating {
    private final Integer id;
    private final String name;

    public Rating(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}