package com.example.KinoKatalog.mapper;

import com.example.KinoKatalog.dto.MovieDTO;
import com.example.KinoKatalog.persistance.sql.entity.MovieEntity;

public class MovieMapper {


    public static MovieDTO toDTO(MovieEntity entity) {
        if (entity == null) return null;

        MovieDTO dto = new MovieDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setOverview(entity.getOverview());
        dto.setReleaseDate(entity.getReleaseDate());
        dto.setRuntime(entity.getRuntime());
        dto.setAverageRating(entity.getAverageRating());
        dto.setReviewCount(entity.getReviewCount());
        dto.setPosterUrl(entity.getPosterUrl());
        return dto;
    }

    public static MovieEntity toEntity(MovieDTO dto) {
        if (dto == null) return null;

        MovieEntity entity = new MovieEntity();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setOverview(dto.getOverview());
        entity.setReleaseDate(dto.getReleaseDate());
        entity.setRuntime(dto.getRuntime());
        entity.setPosterUrl(dto.getPosterUrl());

        // These two are calculated in DB — so we **don’t override them**
        // entity.setAverageRating(dto.getAverageRating());
        // entity.setReviewCount(dto.getReviewCount());

        return entity;
    }

}
