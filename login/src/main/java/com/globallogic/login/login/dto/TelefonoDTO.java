package com.globallogic.login.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TelefonoDTO {
    private Long number;
    private Integer citycode;
    private String contrycode;
}
