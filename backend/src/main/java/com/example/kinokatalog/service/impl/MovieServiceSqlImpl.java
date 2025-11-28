package com.example.kinokatalog.service.impl;

import com.example.kinokatalog.dto.MovieDTO;
import com.example.kinokatalog.mapper.MovieMapper;
import com.example.kinokatalog.persistence.sql.entity.MovieEntity;
import com.example.kinokatalog.persistence.sql.repository.MovieSqlRepository;
import com.example.kinokatalog.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
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
