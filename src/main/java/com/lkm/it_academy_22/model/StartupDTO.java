package com.lkm.it_academy_22.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StartupDTO {

    private Integer id;

    @NotNull
    @Size(max = 200)
    private String name;

    private String slogan;

    private Integer foundedYear;

    private String description;

    private OffsetDateTime createdAt;

}
