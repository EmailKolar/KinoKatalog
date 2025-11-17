package com.example.KinoKatalog.persistence.document.documents;

import com.example.KinoKatalog.persistence.document.embedded.UserCollection;
import com.example.KinoKatalog.persistence.document.embedded.Watchlist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("users")
public class UserDocument {

    @Id
    private ObjectId id;

    private String username;
    private String email;
    private String passwordHash;
    private Boolean isVerified;
    private String fullName;

    private Instant createdAt;
    private String role;

    private Watchlist watchlist;
    private List<UserCollection> collections;
}
