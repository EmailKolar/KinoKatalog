package com.example.kinokatalog.persistence.sql.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "people")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tmdb_id", unique = true)
    private Integer tmdbId;

    @Column(nullable = false)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 5000)
    private String bio;

    /*
    @OneToMany(mappedBy = "personEntity")
    private List<MovieCastEntity> castRoles;

    @OneToMany(mappedBy = "personEntity")
    private List<MovieCrewEntity> crewJobs;*/

}