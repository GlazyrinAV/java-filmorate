package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Score {
    private final Integer filmId;
    private final Integer userId;
    private final Integer score;
}