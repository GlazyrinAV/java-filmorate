package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = "id")
@Builder
public class Genre {
    private final Integer id;
    private final String name;

    public Integer getId() {
        if (id == null) {
            return -1;
        } else {
            return id;
        }
    }

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}