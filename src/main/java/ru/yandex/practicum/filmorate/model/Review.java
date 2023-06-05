package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class Review {

    @NotBlank
    private final String content;

    @NonNull
    @NumberFormat
    private final int userId;

    @NonNull
    @NumberFormat
    private final int filmId;

    @NonNull
    @JsonRawValue
    private final boolean isPositive;

    private final int useful;

    private int reviewId;
}