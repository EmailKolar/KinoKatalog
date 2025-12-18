package com.example.kinokatalog.service;

import com.example.kinokatalog.dto.MovieDTO;

import java.util.List;

public interface MovieService {
    List<MovieDTO> getAllMovies();
    MovieDTO getMovieById(Integer id);
    MovieDTO createMovie(MovieDTO movieDTO);
    void deleteMovieById(Integer id);
    List<MovieDTO> searchMovies(String query);
}
