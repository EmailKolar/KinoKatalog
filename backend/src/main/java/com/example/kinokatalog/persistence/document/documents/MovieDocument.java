package com.example.kinokatalog.persistence.document.documents;


import com.example.kinokatalog.persistence.document.embedded.CastMember;
import com.example.kinokatalog.persistence.document.embedded.CompanyInfo;
import com.example.kinokatalog.persistence.document.embedded.CrewMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("movies")
public class MovieDocument {

    @Id
    private ObjectId id;

    private Integer tmdbId;

    @Indexed
    private String title;
    private String overview;
    private LocalDate releaseDate;
    private Integer runtime;

    private BigDecimal averageRating;
    private Integer reviewCount;

    private String posterUrl;
    private LocalDateTime createdAt;

    // Embedded lists for fast movie reads
    @Indexed
    private List<String> genres;
    @Indexed
    private List<String> tags;
    private List<CastMember> cast;
    private List<CrewMember> crew;
    private List<CompanyInfo> companies;

}
