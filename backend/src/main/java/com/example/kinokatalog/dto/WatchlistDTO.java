package com.example.kinokatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistDTO {
    private Integer id;
    private Integer movieId;
    private String username;
    private LocalDateTime addedAt;
}
