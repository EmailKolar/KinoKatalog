package com.example.KinoKatalog.service;

public interface MovieService {
    List<MovieDTO> getAllMovies();
    MovieDTO getMovieById(Integer id);
    MovieDTO createMovie(MovieDTO movieDTO);
}
