package com.example.kinokatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionDTO {
    private Integer id;
    private Integer userId;
    private String name;
    private String description;
    private String username; // owner
    private LocalDateTime createdAt;
    private List<Integer> movieIds;//can be upgraded to List<MovieDTO>

}
