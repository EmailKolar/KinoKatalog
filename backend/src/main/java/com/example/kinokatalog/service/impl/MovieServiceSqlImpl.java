package com.example.kinokatalog.service.impl;

import com.example.kinokatalog.dto.MovieDTO;
import com.example.kinokatalog.exception.NotFoundException;
import com.example.kinokatalog.mapper.MovieMapper;
import com.example.kinokatalog.persistence.sql.entity.MovieEntity;
import com.example.kinokatalog.persistence.sql.repository.MovieSqlRepository;
import com.example.kinokatalog.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MovieServiceSqlImpl implements MovieService {

    private final MovieSqlRepository movieRepository;

    @Override
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(MovieMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public MovieDTO getMovieById(Integer id) {
        return movieRepository.findById(id)
                .map(MovieMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Movie not found"));
    }

    @Override
    @Transactional(transactionManager = "transactionManager", isolation = Isolation.READ_COMMITTED)
    public MovieDTO createMovie(MovieDTO movieDTO) {
        MovieEntity entity = MovieMapper.toEntity(movieDTO);
        MovieEntity saved = movieRepository.save(entity);
        return MovieMapper.toDTO(saved);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", isolation = Isolation.READ_COMMITTED)
    public void deleteMovieById(Integer id) {
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Movie not found");
        }
        movieRepository.deleteById(id);
    }
    @Override
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<MovieDTO> searchMovies(String query) {
        return movieRepository.findByTitleContainingIgnoreCase(query)
                .stream()
                .map(MovieMapper::toDTO)
                .toList();
    }

}
