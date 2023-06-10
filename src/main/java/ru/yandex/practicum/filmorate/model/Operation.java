package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonTypeName("operation")
public class Operation {
    private final Integer operationId;
    private final String operation;

    @JsonValue
    public String getOperation() {
        return operation;
    }
}
