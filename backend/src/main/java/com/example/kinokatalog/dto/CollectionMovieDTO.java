package com.example.kinokatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionMovieDTO {
    private Integer collectionId;
    private Integer movieId;
}
