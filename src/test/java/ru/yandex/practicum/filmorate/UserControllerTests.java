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
public class UserControllerTests {

    @AfterEach
    public void setup() throws Exception {
        mockMvc.perform(delete("/resetUsers"));
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getUsersEmpty() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));
    }

    @Test
    public void postFimNormal() throws Exception {
        String user = "{\"login\":\"dolore\",\"name\":\"NickName\",\"email\":\"mail@mail.ru\",\"birthday\":\"1946-08-20\"}";
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(user))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("{\"email\":\"mail@mail.ru\",\"login\":\"dolore\"," +
                        "\"name\":\"NickName\",\"birthday\":\"1946-08-20\",\"id\":1}")));
    }

    @Test
    public void getFilmNormal() throws Exception {
        String user = "{\"login\":\"dolore\",\"name\":\"NickName\",\"email\":\"mail@mail.ru\",\"birthday\":\"1946-08-20\"}";
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(user));
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"email\":\"mail@mail.ru\",\"login\":\"dolore\"," +
                        "\"name\":\"NickName\",\"birthday\":\"1946-08-20\",\"id\":1}")));
    }

    @ParameterizedTest
    @MethodSource("userWithWrongParameters")
    public void postFilmsWithErrorData(String string) throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(string))
                .andExpect(status().isBadRequest());
    }

    static Stream<String> userWithWrongParameters() {
        return Stream.of(
                "{\"login\":\"\",\"name\":\"NickName\",\"email\":\"mail@mail.ru\",\"birthday\":\"1946-08-20\"}",
                "{\"login\":\"dol ore\",\"name\":\"NickName\",\"email\":\"mail@mail.ru\",\"birthday\":\"1946-08-20\"}",
                "{\"login\":\"dolore\",\"name\":\"NickName\",\"email\":\"@mail.ru\",\"birthday\":\"1946-08-20\"}",
                "{\"login\":\"dolore\",\"name\":\"NickName\",\"email\":\"a@\",\"birthday\":\"1946-08-20\"}",
                "{\"login\":\"dolore\",\"name\":\"NickName\",\"email\":\"a a@mail.ru\",\"birthday\":\"1946-08-20\"}",
                "{\"login\":\"dolore\",\"name\":\"NickName\",\"email\":\"mail@mail.ru\",\"birthday\":\"3000-08-20\"}",
                "{\"login\":\"dolore\",\"name\":\"NickName\",\"email\":\"масо@mail.ru\",\"birthday\":\"3000-08-20\"}"
        );
    }
}