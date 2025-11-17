package com.example.KinoKatalog.persistence.document.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Watchlist {

    private List<String> movieIds;
    private Instant updatedAt;
}
