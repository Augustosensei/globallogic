package com.globallogic.microservice1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelefonoDTO {


    private Long number;
    private Integer citycode;
    private String contrycode;
}
