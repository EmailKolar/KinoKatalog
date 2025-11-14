package com.example.KinoKatalog.persistence.document.documents;


import com.example.KinoKatalog.persistence.document.embedded.CastMember;
import com.example.KinoKatalog.persistence.document.embedded.CompanyInfo;
import com.example.KinoKatalog.persistence.document.embedded.CrewMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("movies")
public class MovieDocument {

    @Id
    private ObjectId id;

    private Integer tmdbId;
    private String title;
    private String overview;
    private LocalDate releaseDate;
    private Integer runtime;

    private BigDecimal averageRating;
    private Integer reviewCount;

    private String posterUrl;
    private Instant createdAt;

    // Embedded lists for fast movie reads
    private List<String> genres;
    private List<String> tags;
    private List<CastMember> cast;
    private List<CrewMember> crew;
    private List<CompanyInfo> companies;

    // Reference IDs to global collections (OPTIONAL)
    private List<ObjectId> castPersonIds;
    private List<ObjectId> crewPersonIds;
    private List<ObjectId> companyIds;
}
