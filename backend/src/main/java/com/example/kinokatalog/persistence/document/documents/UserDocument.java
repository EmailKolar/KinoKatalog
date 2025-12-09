package com.example.kinokatalog.persistence.document.documents;

import com.example.kinokatalog.persistence.document.embedded.UserCollection;
import com.example.kinokatalog.persistence.document.embedded.Watchlist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("users")
@Builder
public class UserDocument {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String username;
    private String email;
    private String passwordHash;
    private Boolean isVerified;
    private String fullName;

    private LocalDateTime createdAt;
    private String role;

    private Watchlist watchlist;
    private List<UserCollection> collections;
}
