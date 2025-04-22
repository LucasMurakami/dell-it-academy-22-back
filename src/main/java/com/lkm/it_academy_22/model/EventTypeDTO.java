package com.lkm.it_academy_22.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EventTypeDTO {

    private Integer id;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    private Integer scoreModifier;

}
