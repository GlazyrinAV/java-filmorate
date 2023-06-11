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
import ru.yandex.practicum.filmorate.exceptions.exceptions.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.dao.FeedStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/dataForReviewTests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ReviewServiceTests {

    private final ReviewService reviewService;
    private final Validator validator;
    private final FeedStorage feedStorage;

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
    void saveNewNormal() {
        Assertions.assertEquals(reviewService
                        .saveNew(new Review("content", 1, 1, true, null, null)),
                new Review("content", 1, 1, true, 0, 1),
                "Ошибка при нормальном добавлении отзыва.");

        List<Feed> feeds = new ArrayList<>(feedStorage.findFeed(1));
        Assertions.assertEquals(1, feeds.size());
        Feed firstFeed = feeds.get(0);
        Assertions.assertEquals(1, firstFeed.getEventId());
        Assertions.assertEquals(1, firstFeed.getUserId());
        Assertions.assertEquals(1, firstFeed.getEntityId());
        Assertions.assertEquals(2, firstFeed.getEventType().getEventTypeId());
        Assertions.assertEquals(2, firstFeed.getOperation().getOperationId());
    }

    @ParameterizedTest
    @MethodSource("wrongIdReviewParameters")
    void saveNewWithIdErrors(Review review) {
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
    void saveNewWithErrors(Review review) {
        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
        Assertions.assertSame(1, violations.size(),
                "Ошибка при выявлении ошибок в данных новых отзывов.");
    }

    @Test
    void updateNormal() {
        reviewService.saveNew(new Review("content", 1, 1, true, null, null));
        Assertions.assertEquals(reviewService
                        .update(new Review("new content", 1, 1, true, null, 1)),
                new Review("new content", 1, 1, true, 0, 1),
                "Ошибка при нормальном обновлении отзыва.");

        List<Feed> feeds = new ArrayList<>(feedStorage.findFeed(1));
        Assertions.assertEquals(2, feeds.size());
        Feed firstFeed = feeds.get(0);
        Assertions.assertEquals(1, firstFeed.getEventId());
        Assertions.assertEquals(1, firstFeed.getUserId());
        Assertions.assertEquals(1, firstFeed.getEntityId());
        Assertions.assertEquals(2, firstFeed.getEventType().getEventTypeId());
        Assertions.assertEquals(2, firstFeed.getOperation().getOperationId());
        Feed secondFeed = feeds.get(1);
        Assertions.assertEquals(2, secondFeed.getEventId());
        Assertions.assertEquals(1, secondFeed.getUserId());
        Assertions.assertEquals(1, secondFeed.getEntityId());
        Assertions.assertEquals(2, secondFeed.getEventType().getEventTypeId());
        Assertions.assertEquals(3, secondFeed.getOperation().getOperationId());
    }

    @ParameterizedTest
    @MethodSource("wrongUpdateReviewParameters")
    void updateWithErrors(Review review) {
        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
        Assertions.assertSame(1, violations.size(),
                "Ошибка при выявлении ошибок в данных обновленных отзывов.");
    }

    @Test
    void removeNormal() {
        reviewService.remove(2);
        ReviewNotFoundException exception = Assertions.assertThrows(ReviewNotFoundException.class,
                () -> reviewService.findById(2));
        Assertions.assertEquals("Отзыв c ID 2 не найден.", exception.getMessage(),
                "Ошибка при нормальном удалении отзыва.");

        List<Feed> feeds = new ArrayList<>(feedStorage.findFeed(2));
        Assertions.assertEquals(1, feeds.size());
        Feed firstFeed = feeds.get(0);
        Assertions.assertEquals(1, firstFeed.getEventId());
        Assertions.assertEquals(2, firstFeed.getUserId());
        Assertions.assertEquals(2, firstFeed.getEntityId());
        Assertions.assertEquals(2, firstFeed.getEventType().getEventTypeId());
        Assertions.assertEquals(1, firstFeed.getOperation().getOperationId());
    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    void removeWithWrongId(int id) {
        ReviewNotFoundException exception = Assertions.assertThrows(ReviewNotFoundException.class,
                () -> reviewService.remove(id));
        Assertions.assertEquals(exception.getMessage(), "Отзыв c ID " + id + " не найден.",
                "Ошибка при удалении отзыва c ид " + id + ".");
    }

    @Test
    void findByIdNormal() {
        Assertions.assertEquals(reviewService.findById(2),
                new Review("other content", 2, 2, true, 0, 2),
                "Ошибка при нормальном поиске отзыва.");
    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    void findByIdWithWrongId(int id) {
        ReviewNotFoundException exception = Assertions.assertThrows(ReviewNotFoundException.class, () ->
                reviewService.findById(id));
        Assertions.assertEquals(exception.getMessage(), "Отзыв c ID " + id + " не найден.",
                "Ошибка при поиске отзыва c ид " + id + ".");
    }

    @Test
    void findAllNormal() {
        Assertions.assertEquals(reviewService.findAll(10).toString(),
                "[Review(content=with like content, userId=2, filmId=1, isPositive=true, useful=1, reviewId=4), " +
                        "Review(content=other content, userId=2, filmId=2, isPositive=true, useful=0, reviewId=2), " +
                        "Review(content=last content, userId=1, filmId=2, isPositive=false, useful=0, reviewId=3), " +
                        "Review(content=with like content, userId=1, filmId=3, isPositive=false, useful=-1, reviewId=5)]",
                "Ошибка при нормальном поиске всех отзывов.");
    }

    @Test
    void findAllWithNegativeCount() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                reviewService.findAll(-1));
        Assertions.assertEquals("Значение выводимых отзывов не может быть меньше или равно нулю.",
                exception.getMessage(),
                "Ошибка при поиске отзыва c ид " + -1 + ".");
    }

    @Test
    void findByFilmIdNormal() {
        Assertions.assertEquals("[Review(content=last content, userId=1, filmId=2, isPositive=false, useful=0, reviewId=3)]",
                reviewService.findByFilmId(2, 1).toString(),
                "Ошибка при нормальном поиске отзыва по ид.");
    }

    @Test
    void findNoneByFilmIdNormal() {
        Assertions.assertTrue(reviewService.findByFilmId(4, 1).isEmpty(),
                "Ошибка при нормальном поиске отзыва по ид.");
    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    void findByFilmIdWithWrongId(int id) {
        ReviewNotFoundException exception = Assertions.assertThrows(ReviewNotFoundException.class, () ->
                reviewService.findById(id));
        Assertions.assertEquals(exception.getMessage(), "Отзыв c ID " + id + " не найден.",
                "Ошибка при удалении отзыва c ид " + id + ".");
    }

    @Test
    void findByFilmIdWithNegativeCount() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                reviewService.findByFilmId(2, -1));
        Assertions.assertEquals("Значение выводимых отзывов не может быть меньше или равно нулю.",
                exception.getMessage(),
                "Ошибка при поиске отзыва c ид " + -1 + ".");
    }

    @Test
    void saveLikeNormal() {
        reviewService.saveLike(1, 2, Optional.of("like"));
        Assertions.assertEquals(1, (int) reviewService.findById(2).getUseful(),
                "Ошибка при нормальном добавлении лайка отзыву.");
    }

    @Test
    void saveDisLikeNormal() {
        reviewService.saveLike(1, 2, Optional.of("dislike"));
        Assertions.assertEquals(-1, (int) reviewService.findById(2).getUseful(),
                "Ошибка при нормальном добавлении дислайка отзыву.");
    }

    @Test
    void saveLikeTwice() {
        reviewService.saveLike(1, 2, Optional.of("like"));
        LikeAlreadyExistsException exception = Assertions.assertThrows(LikeAlreadyExistsException.class,
                () -> reviewService.saveLike(1, 2, Optional.of("like")));
        Assertions.assertEquals("Оценка отзыву уже поставлена.", exception.getMessage(),
                "Ошибка повторной установке лайка отзыву.");
    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    void saveLikeWithWrongUserId(int id) {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> reviewService.saveLike(id, 2, Optional.of("like")));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID " + id + " не найден.",
                "Ошибка при установке лайка отзыву c ид юзера " + id + ".");
    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    void saveLikeWrongReviewId(int id) {
        ReviewNotFoundException exception = Assertions.assertThrows(ReviewNotFoundException.class,
                () -> reviewService.saveLike(1, id, Optional.of("like")));
        Assertions.assertEquals(exception.getMessage(), "Отзыв c ID " + id + " не найден.",
                "Ошибка при установке лайка отзыву c ид отзыва " + id + ".");
    }

    @Test
    void saveLikeWithWrongOpinion() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> reviewService.saveLike(1, 2, Optional.of("other")));
        Assertions.assertEquals("Ошибка в виде оценке отзыва.", exception.getMessage(),
                "Ошибка при установке лайка отзыву c неправильным типом оценки.");
    }

    @Test
    void saveLikeWithNullOpinion() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                reviewService.saveLike(1, 2, Optional.empty()));
        Assertions.assertEquals("Ошибка в виде оценке отзыва.", exception.getMessage(),
                "Ошибка при установке лайка отзыву c неправильным типом оценки.");
    }

    @Test
    void removeLikeNormal() {
        reviewService.removeLike(1, 4, Optional.of("like"));
        Assertions.assertEquals(0, reviewService.findById(4).getUseful());
    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    void removeLikeWithWrongUserId(int id) {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () ->
                reviewService.removeLike(id, 4, Optional.of("like")));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID " + id + " не найден.",
                "Ошибка при удалении лайка отзыву c ид юзера " + id + ".");
    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    void removeLikeWrongReviewId(int id) {
        ReviewNotFoundException exception = Assertions.assertThrows(ReviewNotFoundException.class, () ->
                reviewService.removeLike(2, id, Optional.of("like")));
        Assertions.assertEquals(exception.getMessage(), "Отзыв c ID " + id + " не найден.",
                "Ошибка при установке лайка отзыву c ид отзыва " + id + ".");
    }

    @Test
    void removeLikeWithWrongOpinion() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                reviewService.removeLike(2, 4, Optional.of("other")));
        Assertions.assertEquals("Ошибка в виде оценке отзыва.", exception.getMessage(),
                "Ошибка при удалении лайка отзыву c неправильным типом оценки.");
    }

    @Test
    void removeLikeWithNullOpinion() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                reviewService.removeLike(2, 4, Optional.empty()));
        Assertions.assertEquals("Ошибка в форме запроса на удаление лайука у отзыва.", exception.getMessage(),
                "Ошибка при удалении лайка отзыву c неправильным типом оценки.");
    }

    @Test
    void removeDisLikeNormal() {
        reviewService.removeDislike(2, 5);
        Assertions.assertEquals(0, reviewService.findById(5).getUseful(),
                "Ошибка при нормальном удалении дизлайка.");
    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    void removeDisLikeWithWrongUserId(int id) {
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () ->
                reviewService.removeDislike(id, 5));
        Assertions.assertEquals(exception.getMessage(), "Пользователь c ID " + id + " не найден.",
                "Ошибка при удалении лайка отзыву c ид юзера " + id + ".");
    }

    @ParameterizedTest
    @MethodSource("wrongIdParameters")
    void removeDisLikeWrongReviewId(int id) {
        ReviewNotFoundException exception = Assertions.assertThrows(ReviewNotFoundException.class, () ->
                reviewService.removeDislike(2, id));
        Assertions.assertEquals(exception.getMessage(), "Отзыв c ID " + id + " не найден.",
                "Ошибка при установке лайка отзыву c ид отзыва " + id + ".");
    }

    @Test
    void isExistsTrue() {
        Assertions.assertTrue(reviewService.isExists(reviewService.findById(2)),
                "Ошибка при проверки наличия отзыва");
    }

    @Test
    void isExistsFalse() {
        Assertions.assertFalse(reviewService.isExists(new Review("c", 1, 2, true, null, 99)),
                "Ошибка при проверке несущетсвующего отзыва.");
    }
}