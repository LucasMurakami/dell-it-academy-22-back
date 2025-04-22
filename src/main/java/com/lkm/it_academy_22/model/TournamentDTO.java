package com.lkm.it_academy_22.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TournamentDTO {

    private Integer id;

    @NotNull
    @Size(max = 200)
    private String name;

    private OffsetDateTime createdAt;

    @Size(max = 50)
    private String status;

    private Integer champion;

}
