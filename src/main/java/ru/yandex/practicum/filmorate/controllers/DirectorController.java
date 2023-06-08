package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorsService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/directors")
@Slf4j
public class DirectorController {

    private final DirectorsService directorsService;

    @Autowired
    public DirectorController(DirectorsService directorsService) {
        this.directorsService = directorsService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director saveNew(@Valid @RequestBody Director director) {
        return directorsService.saveNew(director);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director update(@Valid @RequestBody Director director) {
        return directorsService.update(director);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Director> findAll() {
        return directorsService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Director findById(@PathVariable int id) {
        return directorsService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeById(@PathVariable int id) {
        directorsService.removeById(id);
    }

}
