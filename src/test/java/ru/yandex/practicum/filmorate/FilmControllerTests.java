package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;
import java.util.stream.Stream;

@SpringBootTest
public class FilmControllerTests {

    private FilmController filmController;

    private Validator validator;

    @BeforeEach
    public void start() {
        filmController = new FilmController();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createUserNormal() {
        User user = new User("mail@mail.ru", "login", "name", LocalDate.of(1986, Month.APRIL, 13));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        violations.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
        Assertions.assertSame(0, violations.size());

    }









//    @Test
//    public void getFilmEmpty() throws Exception {
//        mockMvc.perform(get("/films"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(containsString("[]")));
//    }
//
//    @Test
//    public void postFilmNormal() throws Exception {
//        String film = "{\"name\":\"nisieiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\"," +
//                "\"duration\":100}";
//        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(film))
//                .andExpect(status().isCreated())
//                .andExpect(content().string(containsString("{\"name\":\"nisieiusmod\",\"description\":" +
//                        "\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100.000000000,\"liked\":[],\"id\":1}")));
//    }
//
//    @Test
//    public void getFilmWithErrors() throws Exception {
//        String film = "{\"name\":\"nisieiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\"," +
//                "\"duration\":100}";
//        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(film));
//        mockMvc.perform(get("/films"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(containsString("[{\"name\":\"nisieiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100.000000000,\"liked\":[],\"id\":1}]")));
//    }
//
//    @ParameterizedTest
//    @MethodSource("filmWithWrongParameters")
//    public void postFilmsWithErrorData(String string) throws Exception {
//        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(string))
//                .andExpect(status().isBadRequest());
//    }

    static Stream<String> filmWithWrongParameters() {
        return Stream.of(
                "{\"name\":\"\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100}",
                "{\"name\":\"nisieiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1700-03-25\",\"duration\":100}",
                "{\"name\":\"nisieiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":-100}",
                "{\"name\":\"nisieiusmod\",\"description\":\"Пятеродрузей(комик-группа«Шарло»)," +
                        "приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова," +
                        " который задолжал им деньги, аименно 20миллионов. Куглов, который завремя «своего отсутствия»," +
                        " стал кандидатом Коломбани.\",\"releaseDate\":\"1967-03-25\",\"duration\":100}"
        );
    }
}