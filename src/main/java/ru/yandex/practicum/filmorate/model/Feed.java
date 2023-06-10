package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
public class Feed {
    private final Integer eventId;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private final Timestamp timestamp;
    private final Integer userId;
    private final EventType eventType;
    private final Operation operation;
    private final Integer entityId;
}
