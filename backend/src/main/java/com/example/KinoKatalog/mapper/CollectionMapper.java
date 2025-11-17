package com.example.KinoKatalog.mapper;

import com.example.KinoKatalog.dto.CollectionDTO;
import com.example.KinoKatalog.persistance.sql.entity.CollectionEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CollectionMapper {

    public CollectionDTO toDTO(CollectionEntity entity) {
        CollectionDTO dto = new CollectionDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());

        // movieIds added later in service
        dto.setMovieIds(new ArrayList<>());

        return dto;
    }

    public CollectionEntity toEntity(CollectionDTO dto) {
        CollectionEntity entity = new CollectionEntity();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
