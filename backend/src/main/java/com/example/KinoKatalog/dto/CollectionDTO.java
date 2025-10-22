package com.example.KinoKatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionDTO {
    private Integer id;
    private String name;
    private String description;
    private String username; // owner
    private LocalDateTime createdAt;
}
