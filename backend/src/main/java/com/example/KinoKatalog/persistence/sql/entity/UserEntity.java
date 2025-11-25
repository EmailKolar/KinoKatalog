package com.example.KinoKatalog.persistence.sql.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private Boolean isVerified = false;

    @Column(columnDefinition = "ENUM('USER','ADMIN')")
    private String role = "USER";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
/*
    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<CollectionEntity> collections;*/

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
