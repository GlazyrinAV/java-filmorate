//package ru.yandex.practicum.filmorate.adapters;
//
//import com.fasterxml.jackson.core.JacksonException;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonDeserializer;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.Genre;
//
//import java.io.IOException;
//
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//public class FilmDeserializer extends JsonDeserializer<Film> {
//    ObjectMapper mapper = new ObjectMapper();
//    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//    @Override
//    public Film deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
//        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
//        String name = jsonNode.get("name").asText();
//        String description = jsonNode.get("description").asText();
//        Duration duration = Duration.ofMinutes(jsonNode.get("duration").asLong());
//        LocalDate releaseDate = LocalDate.parse(jsonNode.get("releaseDate").asText(), formatter);
//        List<Genre> myObjects = mapper.readValue(jsonNode.get("genres").asText(), mapper.getTypeFactory().constructCollectionType(List.class, Genre.class));
//        return new Film(name, description, releaseDate, duration, null);
//    }
//}
