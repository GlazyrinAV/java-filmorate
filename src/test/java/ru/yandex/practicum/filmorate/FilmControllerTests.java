package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTests {

    @AfterEach
    public void setup() throws Exception {
        mockMvc.perform(delete("/resetFilms"));
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getFilmEmpty() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));
    }

    @Test
    public void postFimNormal() throws Exception {
        String film = "{\"name\":\"nisieiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\"," +
                "\"duration\":100}";
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(film))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("{\"name\":\"nisieiusmod\",\"description\":\"adipisicing\"," +
                        "\"releaseDate\":\"1967-03-25\",\"duration\":100,\"id\":1}")));
    }

    @Test
    public void getFilmNormal() throws Exception {
        String film = "{\"name\":\"nisieiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\"," +
                "\"duration\":100}";
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(film));
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[{\"name\":\"nisieiusmod\",\"description\":\"adipisicing\"," +
                        "\"releaseDate\":\"1967-03-25\",\"duration\":100,\"id\":1}]")));
    }

    @ParameterizedTest
    @MethodSource("filmWithWrongParameters")
    public void postFilmsWithErrorData(String string) throws Exception {
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(string))
                .andExpect(status().isBadRequest());
    }

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