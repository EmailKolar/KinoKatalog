package com.example.kinokatalog.controller;

import com.example.kinokatalog.persistence.graph.nodes.MovieNode;
import com.example.kinokatalog.service.neo.MovieNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/neo/movies")
@RequiredArgsConstructor
public class MovieNodeController {

    private final MovieNodeService movieService;

    @PostMapping
    public MovieNode create(@RequestBody MovieNode movie) {
        return movieService.create(movie);
    }

    @PutMapping("/{id}")
    public MovieNode update(@PathVariable Long id, @RequestBody MovieNode movie) {
        movie.setId(id);
        return movieService.update(movie);
    }

    @GetMapping("/{id}")
    public MovieNode getById(@PathVariable Long id) {
        return movieService.getById(id);
    }

    @GetMapping("/tmdb/{tmdbId}")
    public MovieNode getByTmdb(@PathVariable Integer tmdbId) {
        return movieService.getByTmdbId(tmdbId);
    }

    @GetMapping
    public List<MovieNode> getAll() {
        return movieService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        movieService.delete(id);
    }
}
