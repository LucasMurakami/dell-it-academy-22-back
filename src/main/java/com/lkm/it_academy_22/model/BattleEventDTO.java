package com.lkm.it_academy_22.model;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BattleEventDTO {

    private Integer id;

    private OffsetDateTime createdAt;

    @NotNull
    private Integer battle;

    @NotNull
    private Integer startup;

    private Integer eventType;

}
