package com.lkm.it_academy_22.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "Startups")
@Getter
@Setter
public class Startup {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Integer id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column
    private String slogan;

    @Column
    private Integer foundedYear;

    @Column
    private String description;

    @Column
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "champion")
    private Set<Tournament> championTournaments;

    @OneToMany(mappedBy = "startup")
    private Set<TournamentStartup> startupTournamentStartups;

}
