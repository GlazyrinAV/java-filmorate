package ru.yandex.practicum.filmorate.storage.dao;

public interface ScoreStorage {

    void saveScore(int filmId, int userId, int score);

    void removeScore(int filmId, int userId);

    Double findScore(int filmId);

}