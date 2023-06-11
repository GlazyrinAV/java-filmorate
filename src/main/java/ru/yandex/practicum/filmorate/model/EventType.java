package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EventType {
    private final Integer eventTypeId;
    private final String eventTypeName;

    @JsonValue
    public String getEventTypeName() {
        return eventTypeName;
    }
}
