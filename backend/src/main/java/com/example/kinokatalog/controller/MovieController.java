package com.example.kinokatalog.controller;


import com.example.kinokatalog.dto.MovieDTO;
import com.example.kinokatalog.exception.NotFoundException;
import com.example.kinokatalog.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {



    private final MovieService movieService;
/*
    @GetMapping
    public ResponseEntity<List<MovieDTO>> getAllMovies() {
        List<MovieDTO> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }*/

    @GetMapping
    public ResponseEntity<List<MovieDTO>> getMovies(
            @RequestParam(value = "q", required = false) String query) {

        System.out.println("Search query received: " + query);

        List<MovieDTO> movies;

        if (query != null && !query.isBlank()) {
            movies = movieService.searchMovies(query); // new method
        } else {
            movies = movieService.getAllMovies();
        }

        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Integer id) {
        MovieDTO movie = null;
        try {
            movie = movieService.getMovieById(id);
        } catch (NotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(movie);
    }

    @PostMapping
    public ResponseEntity<MovieDTO> createMovie(@Valid @RequestBody MovieDTO movieDTO) {
        movieDTO.setTitle(StringEscapeUtils.escapeHtml4(movieDTO.getTitle()));
        movieDTO.setOverview(StringEscapeUtils.escapeHtml4(movieDTO.getOverview()));
        movieDTO.setPosterUrl(StringEscapeUtils.escapeHtml4(movieDTO.getPosterUrl()));

        MovieDTO created = movieService.createMovie(movieDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable Integer id, @RequestBody MovieDTO updatedMovie) {
        MovieDTO existing = null;
        try {
            existing = movieService.getMovieById(id);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // overwrite editable fields
        existing.setTitle(updatedMovie.getTitle());
        existing.setOverview(updatedMovie.getOverview());
        existing.setRuntime(updatedMovie.getRuntime());
        existing.setReleaseDate(updatedMovie.getReleaseDate());
        existing.setPosterUrl(updatedMovie.getPosterUrl());

        MovieDTO saved = movieService.createMovie(existing);

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovieById(@PathVariable Integer id) {
        movieService.deleteMovieById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieDTO>> searchMovies(@RequestParam("q") String query) {
        List<MovieDTO> movies = movieService.getAllMovies().stream()
                .filter(m -> m.getTitle().toLowerCase().contains(query.toLowerCase()))
                .toList();
        return ResponseEntity.ok(movies);
    }



}
