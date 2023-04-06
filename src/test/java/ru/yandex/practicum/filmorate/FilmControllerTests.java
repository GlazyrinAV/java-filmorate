package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.filmorate.model.Film;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Stream;

@SpringBootTest(classes = FilmorateApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmControllerTests {

    @LocalServerPort
    private int port;

    static Stream<Film> filmWithWrongParameters() {
        return Stream.of(
                new Film("", "1", LocalDate.now(), Duration.ofMinutes(100)),
                new Film("1", "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111",
                        LocalDate.now(), Duration.ofMinutes(100)),
                new Film("1", "1", LocalDate.of(1700, Month.APRIL, 6), Duration.ofMinutes(100)),
                new Film("1", "1", LocalDate.now(), Duration.ofMinutes(-100))
        );
    }

    @Test
    public void NewFilmNormal() throws JSONException, JsonProcessingException {
        Film film = new Film("1", "1", LocalDate.now(), Duration.ofMinutes(100));
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json");
        String string = jsonMapper.writeValueAsString(film);
        HttpEntity<String> entity = new HttpEntity<>(string, headers);
        URI uri = URI.create("http://localhost:" + port + "/films");
        ResponseEntity<Film> response = new RestTemplate().postForEntity(uri, entity, Film.class);
        Assertions.assertSame(response.getStatusCode(), HttpStatus.CREATED);
    }

    @ParameterizedTest
    @MethodSource("filmWithWrongParameters")
    public void NewFilmWrongParametrs(Film film) throws JsonProcessingException {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json");
        String string = jsonMapper.writeValueAsString(film);
        HttpEntity<String> entity = new HttpEntity<>(string, headers);
        URI uri = URI.create("http://localhost:" + port + "/films");
        HttpClientErrorException.BadRequest exception = Assertions.assertThrows(HttpClientErrorException.BadRequest.class, () -> {
            new RestTemplate().postForEntity(uri, entity, Film.class);
                }
        );
        Assertions.assertSame(exception.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
}