package com.sonet.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class MessageDto {

    @NotBlank
    private String message;

    private String test;
}
