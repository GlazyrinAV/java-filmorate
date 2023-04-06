package ru.yandex.practicum.filmorate;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Stream;

@SpringBootTest(classes = FilmorateApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTests {

    @LocalServerPort
    private int port;

    static Stream<User> userWithWrongParameters() {
        return Stream.of(
                new User("aa.mail.ru", "ABC", "CBA", LocalDate.of(1986, Month.APRIL, 13)),
                new User("aa@", "ABC", "CBA", LocalDate.of(1986, Month.APRIL, 13)),
                new User("@aa", "ABC", "CBA", LocalDate.of(1986, Month.APRIL, 13)),
                new User("aa.mail.ru", "", "CBA", LocalDate.of(1986, Month.APRIL, 13)),
                new User("aa.mail.ru", "A C", "CBA", LocalDate.of(1986, Month.APRIL, 13)),
                new User("aa.mail.ru", "ABC", "CBA", LocalDate.of(2050, Month.APRIL, 13))
        );
    }

    @Test
    public void NewUseNormal() throws JsonProcessingException {
        User user = new User("a@mail.ru", "ABC", "CBA", LocalDate.of(1986, Month.APRIL, 13));
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String string = jsonMapper.writeValueAsString(user);
        HttpEntity<String> entity = new HttpEntity<>(string, headers);
        URI uri = URI.create("http://localhost:" + port + "/users");
        ResponseEntity<User> response = new RestTemplate().postForEntity(uri, entity, User.class);
        Assertions.assertSame(response.getStatusCode(), HttpStatus.CREATED);
    }

    @ParameterizedTest
    @MethodSource("userWithWrongParameters")
    public void NewUserWrongParameters(User user) throws JsonProcessingException {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String string = jsonMapper.writeValueAsString(user);
        HttpEntity<String> entity = new HttpEntity<>(string, headers);
        URI uri = URI.create("http://localhost:" + port + "/users");
        HttpClientErrorException.BadRequest exception = Assertions.assertThrows(HttpClientErrorException.BadRequest.class, () -> {
                    new RestTemplate().postForEntity(uri, entity, User.class);
                }
        );
        Assertions.assertSame(exception.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
}