package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventType {
    SCORE(1),
    REVIEW(2),
    FRIEND(3);
    final int eventTypeId;
}