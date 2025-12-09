package com.example.kinokatalog.controller;

import com.example.kinokatalog.persistence.document.documents.MovieDocument;
import com.example.kinokatalog.service.document.MovieDocumentService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mongo/movies")
public class MovieDocumentController {


    private final MovieDocumentService movieService;


    @GetMapping
    public ResponseEntity<List<MovieDocument>> getAllMovies() {
        List<MovieDocument> movies = movieService.getAll();
        return ResponseEntity.ok(movies);
    }
    @GetMapping("/{id}")
    public ResponseEntity<MovieDocument> getMovieById(@PathVariable String id) {
        MovieDocument movie = movieService.getById(new ObjectId(id));
        return ResponseEntity.ok(movie);
    }
    @GetMapping("/search")
    public List<MovieDocument> searchByTitle(@RequestParam String title) {
        return movieService.findByTitle(title);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        movieService.delete(new ObjectId(id));
    }

    @PostMapping
    public MovieDocument create(@RequestBody MovieDocument movie) {
        return movieService.create(movie);
    }

}
