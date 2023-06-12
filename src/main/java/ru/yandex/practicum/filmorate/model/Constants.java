package ru.yandex.practicum.filmorate.model;

import java.util.Map;

public class Constants {
    public static final Map<String, Integer> eventTypes = Map.of(
            "LIKE", 1,
            "REVIEW", 2,
            "FRIEND", 3);
    public static final Map<String, Integer> operations = Map.of(
            "REMOVE", 1,
            "ADD", 2,
            "UPDATE", 3);
}
