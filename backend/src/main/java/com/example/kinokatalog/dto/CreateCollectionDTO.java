package com.example.kinokatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectionDTO {
    private Integer userId;
    private String name;
    private String description;
    private String username;
}
