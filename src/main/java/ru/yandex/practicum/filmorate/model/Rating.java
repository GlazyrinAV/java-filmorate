package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = "id")
@Builder
public class Rating {
    private final int id;
    private final String name;

    public Rating(int id, String name) {
        this.id = id;
        this.name = name;
    }
}