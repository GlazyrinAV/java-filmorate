package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/dataForReviewTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ReviewServiceTests {

    private final ReviewService reviewService;

    private final Validator validator;

    static Stream<Review> wrongReviewParameters() {
        return Stream.of(
                new Review("", 1, 1, true, null, null),
                new Review(null, 1, 1, true, null, null),
                new Review("content", null, 1, true, null, null),
                new Review("content", 1, null, true, null, null),
                new Review("content", 1, 1, null, null, null)
        );
    }

    static Stream<Review> wrongUpdateReviewParameters() {
        return Stream.of(
                new Review("", 1, 1, true, null, 2),
                new Review(null, 1, 1, true, null, 2),
                new Review("content", null, 1, true, null, 2),
                new Review("content", 1, null, true, null, 2),
                new Review("content", 1, 1, null, null, 2)
        );
    }

    static Stream<Review> wrongIdReviewParameters() {
        return Stream.of(
                new Review("content", -1, 1, true, null, null),
                new Review("content", 0, 1, true, null, null),
                new Review("content", 1, -1, true, null, null),
                new Review("content", 1, 0, true, null, null),
                new Review("Content", 99, 1, true, null, null),
                new Review("Content", 1, 99, true, null, null)
        );
    }

    static Stream<Integer> wrongIdParameters() {
        return Stream.of(-1, 0, 99);
    }

    @Test
    public void saveNewNormal() {
        Assertions.assertEquals(reviewService
                        .saveNew(new Review("content", 1, 1, true, null, null)),
                new Review("content", 1, 1, true, 0, 1),
                "Ошибка при нормальном добавлении отзыва.");
    }

    @ParameterizedTest
    @MethodSource("wrongIdReviewParameters")
    public void saveNewWithIdErrors(Review review) {
        if (review.getUserId() <= 0 || review.getUserId() == 99) {
            UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () ->
                    reviewService.saveNew(review));
            Assertions.assertEquals(exception.getMessage(), "Пользователь c ID " + review.getUserId() +
                            " не найден.",
                    "Ошибка в получении ошибки при поиске отзыва с ID юзера " + review.getUserId());
        } else {
            FilmNotFoundException exception2 = Assertions.assertThrows(FilmNotFoundException.class, () ->
                    reviewService.saveNew(review));
            Assertions.assertEquals(exception2.getMessage(), "Фильм c ID " + review.getFilmId() + " не найден.",
                    "Ошибка в получении ошибки при поиске отзыва с ID фильма " + review.getFilmId());
        }
    }

    @ParameterizedTest
    @MethodSource("wrongReviewParameters")
    public void saveNewWithErrors(Review review) {
        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
        Assertions.assertSame(1, violations.size(),
                "Ошибка при выявлении ошибок в данных новых отзывов.");
    }

    @Test
    public void updateNormal() {
        reviewService.saveNew(new Review("content", 1, 1, true, null, null));
        Assertions.assertEquals(reviewService
                        .update(new Review("new content", 1, 1, true, null, 1)),
                new Review("new content", 1, 1, true, 0, 1),
                "Ошибка при нормальном обновлении отзыва.");
    }

    @ParameterizedTest
    @MethodSource("wrongUpdateReviewParameters")
    public void updateWithErrors(Review review) {
        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
        Assertions.assertSame(1, violations.size(),
                "Ошибка при выявлении ошибок в данных обновленных отзывов.");
    }

    @Test
    public void removeNormal() {
        reviewService.remove(2);
        ReviewNotFoundException exception = Assertions.assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.findById(2);
        });
        Assertions.assertEquals(exception.getMessage(), "Отзыв c ID 2 не найден.",
                "Ошибка при нормальном удалении отзыва.");
    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    public void removeWithWrongId(int id) {
        ReviewNotFoundException exception = Assertions.assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.findById(id);
        });
        Assertions.assertEquals(exception.getMessage(), "Отзыв c ID " + id + " не найден.",
                "Ошибка при удалении отзыва c ид " + id + ".");
    }

    @Test
    public void findByIdNormal() {
        Assertions.assertEquals(reviewService.findById(2),
                new Review("other content", 2, 2, true, 0, 2),
                "Ошибка при нормальном поиске отзыва.");
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
        Assertions.assertTrue(reviewService.isExists(reviewService.findById(2)),
                "Ошибка при проверки наличия отзыва");
    }

    @Test
    public void isExistsFalse() {
        Assertions.assertFalse(reviewService.isExists(new Review("c", 1, 2, true, null, 99)),
                "Ошибка при проверке несущетсвующего отзыва.");
    }
}