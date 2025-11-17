package com.example.KinoKatalog.persistence.document.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    private String userId;
    private String text;
    private Instant createdAt; // Instant is better for timestamps than Date because it represents a point in time and not affected by time zones
}
