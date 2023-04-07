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
import ru.yandex.practicum.filmorate.model.User;

import java.net.URI;
import java.util.stream.Stream;

@SpringBootTest(classes = FilmorateApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTests {

    @LocalServerPort
    private int port;

    static Stream<String> userWithWrongParameters() {
        return Stream.of(
                "{\"login\":\"\",\"name\":\"NickName\",\"email\":\"mail@mail.ru\",\"birthday\":\"1946-08-20\"}",
                "{\"login\":\"dol ore\",\"name\":\"NickName\",\"email\":\"mail@mail.ru\",\"birthday\":\"1946-08-20\"}",
                "{\"login\":\"dolore\",\"name\":\"NickName\",\"email\":\"@mail.ru\",\"birthday\":\"1946-08-20\"}",
                "{\"login\":\"dolore\",\"name\":\"NickName\",\"email\":\"mail@\",\"birthday\":\"1946-08-20\"}",
                "{\"login\":\"dolore\",\"name\":\"NickName\",\"email\":\"ab nv@mn\",\"birthday\":\"1946-08-20\"}",
                "{\"login\":\"dolore\",\"name\":\"NickName\",\"email\":\"mail@mail.ru\",\"birthday\":\"3000-08-20\"}"
                );
    }

    @Test
    public void NewUseNormal() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("{\"login\":\"dolore\",\"name\":\"NickName\",\"email\":\"mail@mail.ru\",\"birthday\":\"1946-08-20\"}", headers);
        URI uri = URI.create("http://localhost:" + port + "/users");
        ResponseEntity<User> response = new RestTemplate().postForEntity(uri, entity, User.class);
        Assertions.assertSame(response.getStatusCode(), HttpStatus.CREATED);
    }

    @ParameterizedTest
    @MethodSource("userWithWrongParameters")
    public void NewUserWrongParameters(String string) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(string, headers);
        URI uri = URI.create("http://localhost:" + port + "/users");
        HttpClientErrorException.BadRequest exception = Assertions.assertThrows(HttpClientErrorException.BadRequest.class, () -> {
                    new RestTemplate().postForEntity(uri, entity, User.class);
                }
        );
        Assertions.assertSame(exception.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
//
//    @Test
//    public void getFilmNormal() throws JsonProcessingException {
//        User user = new User("a@mail.ru", "ABC", "CBA", LocalDate.of(1986, Month.APRIL, 13));
//        ObjectMapper jsonMapper = new ObjectMapper();
//        jsonMapper.registerModule(new JavaTimeModule());
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        String string = jsonMapper.writeValueAsString(user);
//        HttpEntity<String> entity = new HttpEntity<>(string, headers);
//        URI uri = URI.create("http://localhost:" + port + "/users");
//        ResponseEntity<User> response = new RestTemplate().postForEntity(uri, entity, User.class);
//        User user2 = new User("a@mail.ru", "ABC", "CBA", LocalDate.of(1986, Month.APRIL, 13));
//
//        Assertions.assertSame(response.getStatusCode(), HttpStatus.CREATED);
//    }
}