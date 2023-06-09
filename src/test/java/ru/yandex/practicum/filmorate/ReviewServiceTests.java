package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.service.RatingsService;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ReviewServiceTests {

    private final RatingsService ratingsService;

    @Test
    public void saveNewNormal() {

    }

    @Test
    public void saveNewWithErrors() {

    }

    @Test
    public void updateNormal() {

    }

    @Test
    public void updateWithErros() {

    }

    @Test
    public void removeNormal() {

    }

    @Test
    public void removeWithNegativeId() {

    }

    @Test
    public void removeWithZeroId() {

    }

    @Test
    public void removeWrongId() {

    }

    @Test
    public void findByIdNormal() {

    }

    @Test
    public void findByIdWithNegativeId() {

    }

    @Test
    public void findByIdWithZeroId() {

    }

    @Test
    public void findByIdWithWrongId() {

    }

    @Test
    public void findAllNormal() {

    }

    @Test
    public void findAllWithNegativeCount() {

    }

    @Test
    public void findByFilmIdNormal() {

    }

    @Test
    public void findByFilmIdWithNegativeId() {

    }

    @Test
    public void findByFilmIdWithZeroId() {

    }

    @Test
    public void findByFilmIdWithWrongId() {

    }

    @Test
    public void findByFilmIdWithNegativeCount() {

    }

    public void saveLikeNormal() {

    }
}
