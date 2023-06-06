package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class Review {

    @NotBlank
    private final String content;

    @NotNull
    private final Integer userId;

    @NotNull
    private final Integer filmId;

    @NotNull
    @JsonRawValue
    private final Boolean isPositive;

    private Integer useful = 0;

    private Integer reviewId;
}