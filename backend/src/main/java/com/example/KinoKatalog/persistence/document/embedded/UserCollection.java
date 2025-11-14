package com.example.KinoKatalog.persistence.document.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCollection {

    private String name;
    private String description;
    private Instant createdAt;
    private List<String> movieIds;
}
