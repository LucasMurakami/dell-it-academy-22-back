package com.lkm.it_academy_22.model;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BattleDTO {

    private Integer id;

    private Integer battleNumber;

    private Integer roundNumber;

    private Boolean sharkFight;

    private Boolean completed;

    private OffsetDateTime createdAt;

    @NotNull
    private Integer tournament;

    private Integer startup1;

    private Integer startup2;

    private Integer winner;

}
