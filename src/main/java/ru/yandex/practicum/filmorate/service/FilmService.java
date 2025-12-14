package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.params.SortBy;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Operation;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final FeedService feedService;
    private final DirectorStorage directorStorage;

    /**
     * Создать фильм
     */
    public Film createFilm(Film film) {
        Film createdFilm = filmStorage.create(film);
        log.info("Создан фильм: id={}, name={}", createdFilm.getId(), createdFilm.getName());
        return createdFilm;
    }

    /**
     * Обновить фильм
     */
    public Film updateFilm(Film film) {
        Film updatedFilm = filmStorage.update(film);
        log.info("Обновлён фильм: id={}, name={}", updatedFilm.getId(), updatedFilm.getName());
        return updatedFilm;
    }

    /**
     * Получить все фильмы
     */
    public List<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    /**
     * Получить фильм по ID
     */
    public Film getFilmById(Integer id) {
        return filmStorage.findById(id);
    }

    /**
     * Поставить лайк фильму
     */
    public void addLike(Integer filmId, Integer userId) {
        userService.getUserById(userId); // Проверяем существование пользователя
        filmStorage.addLike(filmId, userId);

        Film film = filmStorage.findById(filmId);
        log.info("Пользователь {} поставил лайк фильму {} (всего лайков: {})",
                userId, filmId, film.getLikes().size());

        feedService.createEvent(userId, filmId, EventType.LIKE, Operation.ADD);
    }

    /**
     * Удалить лайк
     */
    public void removeLike(Integer filmId, Integer userId) {
        userService.getUserById(userId); // Проверяем существование пользователя
        filmStorage.removeLike(filmId, userId);

        Film film = filmStorage.findById(filmId);
        log.info("Пользователь {} удалил лайк у фильма {} (осталось лайков: {})",
                userId, filmId, film.getLikes().size());

        feedService.createEvent(userId, filmId, EventType.LIKE, Operation.REMOVE);
    }

    /**
     * Получить список из первых count фильмов по количеству лайков
     */
    public List<Film> getPopularFilms(Integer count, Integer year, Integer genreId) {
        return filmStorage.findPopularFilms(count, year, genreId);
    }

    /**
     * Получить список общих лайкнутых фильмов 2-ух пользователей
     */
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        userService.getUserById(userId);
        userService.getUserById(friendId);
        log.info("Запрос на общие фильмы пользователей {} и {}", userId, friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> getFilmsByDirector(Integer directorid, SortBy sortBy) {
        if (!directorStorage.isDirectorPresent(directorid)) {
            throw new NotFoundException("Режиссер с id " + directorid + " не найден");
        }
        return filmStorage.getFilmsByDirector(directorid, sortBy);
    }

    /**
     * Получить рекомендации по фильмам для пользователя
     */
    public List<Film> getRecommendations(Integer userId) {
        userService.getUserById(userId); // Проверяем существование пользователя
        log.info("Запрос рекомендаций для пользователя {}", userId);
        return filmStorage.getRecommendations(userId);
    }
}
