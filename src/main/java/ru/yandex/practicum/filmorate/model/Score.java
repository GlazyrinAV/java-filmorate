package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class Score {

    @Positive
    @NotNull
    private final Integer filmId;

    @Positive
    @NotNull
    private final Integer userId;

    @Positive
    private final Integer score;
}