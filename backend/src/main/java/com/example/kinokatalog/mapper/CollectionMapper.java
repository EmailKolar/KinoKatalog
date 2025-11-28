package com.example.kinokatalog.mapper;

import com.example.kinokatalog.dto.CollectionDTO;
import com.example.kinokatalog.persistence.sql.entity.CollectionEntity;
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
