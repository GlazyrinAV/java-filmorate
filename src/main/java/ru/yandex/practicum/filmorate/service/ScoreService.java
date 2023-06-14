package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.dao.ScoreStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreService {

    private final ScoreStorage scoreStorage;

    public void saveScore(int filmId, int userId, int score) {
        scoreStorage.saveScore(filmId, userId, score);
    }

    public void removeScore(int filmId, int userId) {
        scoreStorage.removeScore(filmId, userId);
    }

    public Double findScore(int filmId) {
        return scoreStorage.findScore(filmId);
    }
}
