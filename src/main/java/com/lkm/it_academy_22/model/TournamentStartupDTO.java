package com.lkm.it_academy_22.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class TournamentStartupDTO {

    private Integer id;

    private Integer currentScore;

    private Boolean eliminated;

    @NotNull
    private Integer tournament;

    @NotNull
    private Integer startup;

    private OffsetDateTime createdAt;

}
