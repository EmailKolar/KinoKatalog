package com.example.KinoKatalog.persistence.document.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    private String userId;
    private String text;
    private LocalDateTime createdAt;
}
