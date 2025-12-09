package com.example.kinokatalog.service.neo;

import com.example.kinokatalog.persistence.graph.nodes.MovieNode;
import com.example.kinokatalog.persistence.graph.repository.MovieNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieNodeService {


    private final MovieNodeRepository movieRepo;

    public MovieNode create(MovieNode movie) {
        return movieRepo.save(movie);
    }

    public MovieNode update(MovieNode movie) {
        return movieRepo.save(movie);
    }

    public MovieNode getById(Long id) {
        return movieRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    public MovieNode getByTmdbId(Integer tmdbId) {
        return movieRepo.findNodeOnlyByTmdbId(tmdbId);
    }

    public List<MovieNode> getAll() {
        return movieRepo.findAll();
    }

    public void delete(Long id) {
        movieRepo.deleteById(id);
    }

}
