package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.service.RatingsService;

import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ReviewServiceTests {

    private final RatingsService ratingsService;

    static Stream<Integer> wrongIdParameters() {
        return Stream.of(-1, 0, 99);
    }

    @Test
    public void saveNewNormal() {

    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    public void saveNewWithErrors(int id) {

    }

    @Test
    public void updateNormal() {

    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    public void updateWithErrors(int id) {

    }

    @Test
    public void removeNormal() {

    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    public void removeWithWrongId(int id) {

    }

    @Test
    public void findByIdNormal() {

    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    public void findByIdWithWrongId(int id) {

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

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    public void findByFilmIdWithWrongId(int id) {

    }

    @Test
    public void findByFilmIdWithNegativeCount() {

    }

    @Test
    public void saveLikeNormal() {

    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    public void saveLikeWithWrongUserId(int id) {

    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    public void saveLikeWrongReviewId(int id) {

    }

    @Test
    public void saveLikeWithWrongOpinion() {

    }

    @Test
    public void removeLikeNormal() {

    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    public void removeLikeWithWrongUserId(int id) {

    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    public void removeLikeWrongReviewId(int id) {

    }

    @Test
    public void removeLikeWithWrongOpinion() {

    }

    @Test
    public void removeDisLikeNormal() {

    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    public void removeDisLikeWithWrongUserId(int id) {

    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    public void removeDisLikeWrongReviewId(int id) {

    }

    @Test
    public void isExistsTrue() {

    }

    @Test
    public void isExistsFalse() {

    }
}