package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Operation {
    REMOVE(1),
    ADD(2),
    UPDATE(3);
    final int operationId;
}