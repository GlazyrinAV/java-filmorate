package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.net.URI;
import java.util.stream.Stream;

@SpringBootTest(classes = FilmorateApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmControllerTests {

    @LocalServerPort
    private int port;

    static Stream<String> filmWithWrongParameters() {
        return Stream.of(
                "{\"name\":\"\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100}",
                "{\"name\":\"nisieiusmod\",\"description\":\"Пятеро друзей ( комик-группа «Шарло»), приезжают в город" +
                        " Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                        "а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом " +
                        "Коломбани.\",\"releaseDate\":\"1967-03-25\",\"duration\":100}",
                "{\"name\":\"nisieiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1798-03-25\",\"duration\":100}",
                "{\"name\":\"nisieiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":-100}"
        );
    }

    @Test
    public void NewFilmNormal() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
                "{\"name\":\"nisieiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100}", headers);
        URI uri = URI.create("http://localhost:" + port + "/films");
        ResponseEntity<Film> response = new RestTemplate().postForEntity(uri, entity, Film.class);
        Assertions.assertSame(response.getStatusCode(), HttpStatus.CREATED);
    }

    @ParameterizedTest
    @MethodSource("filmWithWrongParameters")
    public void NewFilmWrongParameters(String string) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(string, headers);
        URI uri = URI.create("http://localhost:" + port + "/films");
        HttpClientErrorException.BadRequest exception = Assertions.assertThrows(HttpClientErrorException.BadRequest.class, () -> {
            new RestTemplate().postForEntity(uri, entity, Film.class);
                }
        );
        Assertions.assertSame(exception.getStatusCode(), HttpStatus.BAD_REQUEST);
    }


    @Test
    public void getFilmNormal() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
                "{\"name\":\"nisieiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100}", headers);
        URI uri = URI.create("http://localhost:" + port + "/films");
        new RestTemplate().postForEntity(uri, entity, User.class);
        ResponseEntity<String> response = new RestTemplate().getForEntity(uri, String.class);
        Assertions.assertSame(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals("[{\"name\":\"nisieiusmod\",\"description\":\"adipisicing\"," +
                "\"releaseDate\":\"1967-03-25\",\"duration\":100.000000000,\"id\":1}]", response.getBody());
    }
}