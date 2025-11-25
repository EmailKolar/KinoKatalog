package com.example.KinoKatalog.persistence.document.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("people")
@Builder
public class PersonDocument {

    @Id
    private ObjectId id;

    private Integer tmdbId;
    private String name;
    private String biography;
    private LocalDate birthDate;
}
