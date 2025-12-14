package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.controller.params.SortBy;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    List<Film> findAll();

    Film findById(Integer id);

    void delete(Integer id);

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);

    List<Film> findPopularFilms(Integer limit, Integer year, Integer genreId);

    List<Film> getFilmsByDirector(Integer directorId, SortBy sortBy);

    List<Film> getCommonFilms(Integer userId,Integer friendId);

    List<Film> getRecommendations(Integer userId);
}
