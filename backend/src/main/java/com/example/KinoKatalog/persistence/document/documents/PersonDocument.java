package com.example.KinoKatalog.persistence.document.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("people")
public class PersonDocument {

    @Id
    private ObjectId id;

    private Integer tmdbId;
    private String name;
    private String biography;
    private String birthday;
}
