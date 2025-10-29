package com.example.KinoKatalog.service.impl;

import com.example.KinoKatalog.dto.MovieDTO;
import com.example.KinoKatalog.mapper.MovieMapper;
import com.example.KinoKatalog.persistance.sql.entity.MovieEntity;
import com.example.KinoKatalog.persistance.sql.repository.MovieSqlRepository;
import com.example.KinoKatalog.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Profile("sql")
@RequiredArgsConstructor
public class MovieServiceSqlImpl implements MovieService {

    private final MovieSqlRepository movieRepository;

    @Override
    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(MovieMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MovieDTO getMovieById(Integer id) {
        return movieRepository.findById(id)
                .map(MovieMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    @Override
    public MovieDTO createMovie(MovieDTO movieDTO) {
        MovieEntity entity = MovieMapper.toEntity(movieDTO);
        MovieEntity saved = movieRepository.save(entity);
        return MovieMapper.toDTO(saved);
    }

}
