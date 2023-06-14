package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
@Slf4j
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Mpa> findAll() {
        return mpaService.findAll();
    }

    @GetMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.OK)
    public Mpa findById(@PathVariable int ratingId) {
        return mpaService.findById(ratingId);
    }
}