package com.globallogic.microservice1.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Telefono {

    private Long    number;
    private Integer citycode;
    private String  contrycode;
}
